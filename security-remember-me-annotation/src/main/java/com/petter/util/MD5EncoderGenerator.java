package com.petter.util;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

/**
 * @author hongxf
 * @since 2017-04-11 10:52
 */
public class MD5EncoderGenerator {
    public static void main(String[] args) {
        Md5PasswordEncoder encoder = new Md5PasswordEncoder();
        System.out.println(encoder.encodePassword("123456", "hongxf"));
    }
}
