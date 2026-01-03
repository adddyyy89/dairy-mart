package com.dairymart.dairyappserver.service;

import com.dairymart.dairyappserver.controller.LoginController;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioService {

    Logger logger = LoggerFactory.getLogger(TwilioService.class);

    @Value("${twilio.accountSid}")
    private String accountSid;

    @Value("${twilio.authToken}")
    private String authToken;

    @Value("${twilio.phoneNumber}")
    private String twilioPhoneNumber;

    public void sendOtp(String toPhoneNumber, String otp) {
        Twilio.init(accountSid, authToken);

        Message message = Message.creator(
                        new PhoneNumber(toPhoneNumber),
                        new PhoneNumber(twilioPhoneNumber),
                        "Your OTP for password reset is: " + otp)
                .create();

        logger.info("OTP sent successfully to phone number: {}", toPhoneNumber);
    }
}
