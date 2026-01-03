package com.dairymart.dairyappserver.service;

import com.dairymart.dairyappserver.dao.UserDao;
import com.dairymart.dairyappserver.dao.UserLoginDao;
import com.dairymart.dairyappserver.repository.LoginRepository;
import com.dairymart.dairyappserver.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LoginService implements UserDetailsService {

    Logger logger = LoggerFactory.getLogger(LoginService.class);

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private LoginRepository loginRepository;

    public UserDao authenticate(String phoneNumber, String password) {
        List<UserDao> users = userRepo.findAll();
        for(UserDao user : users) {
            System.out.println("Auth user:" + user.getPhoneNumber());
            if(user.getPhoneNumber().equalsIgnoreCase(phoneNumber) && user.getPassword().equals(password)) {
                System.out.println("Found!!!");
                return user;
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
                //System.out.println("Found!!!");
                return new org.springframework.security.core.userdetails.User(user.getPhoneNumber(),user.getPassword(), getAuthorities(user));
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
        return loginRepository.save(userLoginDao);
    }

    public UserLoginDao logout(UserLoginDao userLoginDao) {
        return loginRepository.save(userLoginDao);
    }

    public UserLoginDao isLoggedIn(UserLoginDao userLoginDao) {
        if(userLoginDao.getPhoneNumber().isEmpty() && userLoginDao.getUserId() <= 0) {
            return null;
        }
        else if(!userLoginDao.getPhoneNumber().isEmpty()) {
            // get using phone number
            List<UserLoginDao> daoList =  loginRepository.findAll().stream().filter(x -> x.getPhoneNumber().equalsIgnoreCase(userLoginDao.getPhoneNumber()) && x.isActive()).collect(Collectors.toCollection(ArrayList::new));
            if(daoList.isEmpty()) {
                return null;
            }
            else {
                return daoList.get(0);
            }
        }
        else {
            // get by user id
            List<UserLoginDao> daoList = loginRepository.findAll().stream().filter(x-> x.getUserId() == userLoginDao.getUserId() && x.isActive()).collect(Collectors.toCollection(ArrayList::new));
            return daoList.get(0);
        }
    }
}
