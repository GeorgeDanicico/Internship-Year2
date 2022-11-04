package com.montran.internship;

import com.montran.internship.util.PkceUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
public class InternshipApplication {

    public static void main(String[] args) {
//        try {
//
//            PkceUtil pkce = new PkceUtil();
//
//            String codeVerifier = pkce.generateCodeVerifier();
//            System.out.println("Code verifier = " + codeVerifier);
//
//            String codeChallenge = pkce.generateCodeChallange(codeVerifier);
//            System.out.println("Code challenge = " + codeChallenge);
//
//        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
//
//        }

        SpringApplication.run(InternshipApplication.class, args);
    }

}
