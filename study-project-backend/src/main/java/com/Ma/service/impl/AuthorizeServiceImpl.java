package com.Ma.service.impl;

import com.Ma.entity.Account;
import com.Ma.mapper.UserMapper;
import com.Ma.service.AuthorizeService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthorizeServiceImpl implements AuthorizeService {

    @Resource
    UserMapper userMapper;



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


    //发送邮件
    @Override
    public boolean sendValidateEmail(String email) {
        /**
         * 1.先生成对应的验证码
         * 2.把邮箱和对应的验证码直接放到Redis中
         * (过期时间为三分钟，如果此时重新要求发送邮件，那么剩余时间低于两分钟，就可以重新发送一次，然后重复此流程)
         * 3.发送验证码到指定邮箱
         * 4.如果发送失败，热Redis里面的刚刚插入的删除
         * 5.用户在注册时，再从Redis里面去除对应键值对，然后看验证码是否一致
         */
        return false;
    }
}
