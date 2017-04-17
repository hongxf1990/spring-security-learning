package com.petter.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author hongxf
 * @since 2017-04-10 10:01
 */
public class PasswordEncoderGenerator {

    public static void main(String[] args) {
        String password = "123456";
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(password);
        System.out.println(hashedPassword);
    }
}
