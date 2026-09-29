package com.dairymart.dairyappserver.service;

import com.dairymart.dairyappserver.dao.UserDao;
import com.dairymart.dairyappserver.dao.UserLoginDao;
import com.dairymart.dairyappserver.dto.UserSessionDTO;
import com.dairymart.dairyappserver.repository.LoginRepository;
import com.dairymart.dairyappserver.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
//import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LoginService implements UserDetailsService {

    Logger logger = LoggerFactory.getLogger(LoginService.class);

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    @Lazy
    private NotificationService notificationService;

    public UserDao authenticate(String phoneNumber, String password) {
        List<UserDao> users = userRepo.findAll();
        for(UserDao user : users) {
            if(user.getPhoneNumber() != null && user.getPhoneNumber().equalsIgnoreCase(phoneNumber)) {
                String stored = user.getPassword() == null ? "" : user.getPassword().replace("{noop}", "");
                if (stored.equals(password) || (user.getPassword() != null && user.getPassword().equals(password))) {
                    return user;
                }
            }
        }
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDao> users = userRepo.findAll();
        for(UserDao user : users) {
            //System.out.println("user:" + user.getPhoneNumber());
            if(user.getPhoneNumber().equalsIgnoreCase(username)) {
                String password = user.getPassword();
                if (password != null && !password.startsWith("{")) {
                    password = "{noop}" + password;
                }
                return new org.springframework.security.core.userdetails.User(user.getPhoneNumber(), password, getAuthorities(user));
            }
        }
        throw new UsernameNotFoundException("User not found with username: " + username);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(UserDao user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        //System.out.println("role: " + user.getType().getUserTypeDesc());
        authorities.add(new SimpleGrantedAuthority(user.getType().getUserTypeDesc()));
        return authorities;
    }

    public UserLoginDao login(UserLoginDao userLoginDao) {
        UserLoginDao saved = loginRepository.save(userLoginDao);
        if (notificationService != null) {
            notificationService.recordActivity("LOGIN", "User signed in",
                    (saved.getPhoneNumber() == null ? "" : saved.getPhoneNumber())
                            + " (" + NotificationService.roleLabel(saved.getRole()) + ") signed in.",
                    saved.getUserId(), saved.getUserId());
        }
        return saved;
    }

    public UserLoginDao logout(UserLoginDao userLoginDao) {
        UserLoginDao saved = loginRepository.save(userLoginDao);
        if (notificationService != null) {
            notificationService.recordActivity("LOGOUT", "User signed out",
                    (saved.getPhoneNumber() == null ? "" : saved.getPhoneNumber())
                            + " (" + NotificationService.roleLabel(saved.getRole()) + ") signed out.",
                    saved.getUserId(), saved.getUserId());
        }
        return saved;
    }

    public UserLoginDao isLoggedIn(UserLoginDao userLoginDao) {
        if (userLoginDao == null) {
            return null;
        }
        String phone = userLoginDao.getPhoneNumber() == null ? "" : userLoginDao.getPhoneNumber().trim();
        if (phone.isEmpty() && userLoginDao.getUserId() <= 0) {
            return null;
        }
        List<UserLoginDao> daoList;
        if (!phone.isEmpty()) {
            daoList = loginRepository.findAll().stream()
                    .filter(x -> x.getPhoneNumber() != null
                            && x.getPhoneNumber().equalsIgnoreCase(phone)
                            && x.isActive())
                    .collect(Collectors.toCollection(ArrayList::new));
        } else {
            daoList = loginRepository.findAll().stream()
                    .filter(x -> x.getUserId() == userLoginDao.getUserId() && x.isActive())
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        return daoList.isEmpty() ? null : daoList.get(0);
    }

    public void endActiveSessions(String phoneNumber, int userId) {
        for (UserLoginDao row : loginRepository.findAll()) {
            boolean samePhone = phoneNumber != null && row.getPhoneNumber() != null
                    && row.getPhoneNumber().equalsIgnoreCase(phoneNumber);
            boolean sameUser = row.getUserId() == userId;
            if (row.isActive() && (samePhone || sameUser)) {
                row.setActive(false);
                row.setLoggedOut(new java.sql.Timestamp(System.currentTimeMillis()));
                loginRepository.save(row);
            }
        }
    }

    public Map<String, Object> getSessionOverview() {
        List<UserLoginDao> rows = new ArrayList<>(loginRepository.findAll());
        rows.sort(Comparator.comparing(UserLoginDao::getLoggedIn, Comparator.nullsLast(Comparator.reverseOrder())));
        Map<String, UserLoginDao> onlineByKey = new LinkedHashMap<>();
        List<UserSessionDTO> history = new ArrayList<>();
        for (UserLoginDao row : rows) {
            UserSessionDTO dto = toSession(row);
            history.add(dto);
            if (row.isActive()) {
                String key = (row.getPhoneNumber() == null ? "" : row.getPhoneNumber()) + "#" + row.getUserId();
                onlineByKey.putIfAbsent(key, row);
            }
        }
        List<UserSessionDTO> online = onlineByKey.values().stream().map(this::toSession).collect(Collectors.toCollection(ArrayList::new));
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("onlineCount", online.size());
        result.put("online", online);
        result.put("history", history.stream().limit(200).collect(Collectors.toCollection(ArrayList::new)));
        return result;
    }

    private UserSessionDTO toSession(UserLoginDao row) {
        UserSessionDTO dto = new UserSessionDTO();
        dto.setUserId(row.getUserId());
        dto.setPhoneNumber(row.getPhoneNumber());
        dto.setRole(row.getRole());
        dto.setRoleLabel(NotificationService.roleLabel(row.getRole()));
        dto.setLoggedIn(row.getLoggedIn());
        dto.setLoggedOut(row.getLoggedOut());
        dto.setActive(row.isActive());
        UserDao user = userRepo.findById(row.getUserId()).orElse(null);
        if (user == null && row.getPhoneNumber() != null) {
            user = userRepo.findAll().stream()
                    .filter(u -> u.getPhoneNumber() != null && u.getPhoneNumber().equalsIgnoreCase(row.getPhoneNumber()))
                    .findFirst()
                    .orElse(null);
        }
        String name = NotificationService.personName(user);
        dto.setName(name.isEmpty() ? (row.getPhoneNumber() == null ? ("User #" + row.getUserId()) : row.getPhoneNumber()) : name);
        if (dto.getRole() <= 0 && user != null) {
            dto.setRole(user.getTypeId());
            dto.setRoleLabel(NotificationService.roleLabel(user.getTypeId()));
        }
        return dto;
    }
}
