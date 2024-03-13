package com.Ma.service;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthorizeService extends UserDetailsService {
    //发送验证邮件
    String sendValidateEmail(String email,String sessionID,boolean hasAccount);

    String validateAndRegister(String username, String password, String email, String code, String sessionID);

    String validateOnly(String email, String code, String sessionID);

    boolean resetPassword(String password, String email);

}
