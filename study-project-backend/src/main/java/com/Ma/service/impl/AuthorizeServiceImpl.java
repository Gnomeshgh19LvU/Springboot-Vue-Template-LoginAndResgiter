package com.Ma.service.impl;

import com.Ma.entity.auth.Account;
import com.Ma.mapper.UserMapper;
import com.Ma.service.AuthorizeService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AuthorizeServiceImpl implements AuthorizeService {

    @Value("${spring.mail.username}")
    String from;


    @Resource
    UserMapper userMapper;

    @Resource
    MailSender mailSender;

    @Resource
    StringRedisTemplate template;


    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null) {
            throw new UsernameNotFoundException("用户名不能为空");
        }
        Account account = userMapper.findAccountByNameOrEmail(username);
        if (account == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }

        return User
                .withUsername(account.getUsername())
                .password(account.getPassword())
                .roles("user")
                .build();
    }


    /**
     * 发送邮件
     * <p>
     * 1.先生成对应的验证码
     * 2.把邮箱和对应的验证码直接放到Redis中
     * (过期时间为三分钟，如果此时重新要求发送邮件，那么剩余时间低于两分钟，就可以重新发送一次，然后重复此流程)
     * 3.发送验证码到指定邮箱
     * 4.如果发送失败，热Redis里面的刚刚插入的删除
     * 5.用户在注册时，再从Redis里面去除对应键值对，然后看验证码是否一致
     */
    @Override
    public String sendValidateEmail(String email, String sessionID, boolean hasAccount) {
        String key = "email: " + sessionID + ":" + email + ":" + hasAccount;
        if (Boolean.TRUE.equals(template.hasKey(key))) {
            Long expire = Optional.ofNullable(template.getExpire(key, TimeUnit.SECONDS)).orElse(0L);

            if (expire > 120) {
                return "请求频繁，请稍后再试";
            }
        }
        Account account = userMapper.findAccountByNameOrEmail(email);
        if (hasAccount && account == null) {
            return "没有此邮件地址的用户";
        }
        if (!hasAccount && account != null) {
            return "此邮箱已被注册";
        }
        Random random = new Random();
        int code = random.nextInt(899999) + 100000;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("您的验证邮件");
        message.setText("您的验证码是：" + code);
        try {
            mailSender.send(message);
            template.opsForValue().set(key, String.valueOf(code), 3, TimeUnit.MINUTES);
            return null;
        } catch (MailException e) {
            e.printStackTrace();
            return "邮件发送失败或检查您的邮箱地址是否有效";
        }
    }


    //验证并注册
    @Override
    public String validateAndRegister(String username, String password, String email, String code, String sessionID) {
        String key = "email: " + sessionID + ":" + email + ":false";
        if (Boolean.TRUE.equals(template.hasKey(key))) {

            String s = template.opsForValue().get(key);
            if (s == null) return "验证码已过期，请重新请求";
            if (s.equals(code)) {
                Account account = userMapper.findAccountByNameOrEmail(username);
                if (account != null) {
                    return "此用户名已被注册，请更换用户名";
                }
                template.delete(key);
                password = encoder.encode(password);
                if (userMapper.createAccount(username, password, email) > 0) {
                    return null;
                } else {
                    return "网络错误，请联系管理员";
                }
            } else {
                return "验证码错误，请检查";
            }

        } else {
            return "请先请求验证码邮件";
        }

    }

    @Override
    public String validateOnly(String email, String code, String sessionID) {
        String key = "email: " + sessionID + ":" + email + ":true";
        if (Boolean.TRUE.equals(template.hasKey(key))) {

            String s = template.opsForValue().get(key);
            if (s == null) return "验证码已过期，请重新请求";
            if (s.equals(code)) {
                template.delete(key);
                return null;
            } else {
                return "验证码错误，请检查";
            }

        } else {
            return "请先请求验证码邮件";
        }
    }

    @Override
    public boolean resetPassword(String password, String email) {
        password = encoder.encode(password);
        return userMapper.resetPasswordByEmail(password, email) > 0;
    }

}
