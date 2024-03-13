package com.Ma.service;

import com.Ma.entity.Account;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthorizeService extends UserDetailsService {
    //发送验证邮件
    String sendValidateEmail(String email,String sessionID);


    String validateAndRegister(String username, String password, String email, String code, String sessionID);
}
