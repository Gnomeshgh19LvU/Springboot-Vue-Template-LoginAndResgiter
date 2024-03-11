package com.Ma.config;

import com.Ma.entity.RestBean;
import com.Ma.service.AuthorizeService;
import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    AuthorizeService authorizeService;


    /**
     * 配置安全过滤链
     *
     * @param httpSecurity 用于配置Spring Security的HttpSecurity对象
     * @return 返回配置好的DefaultSecurityFilterChain对象，用于定义请求如何被安全地处理
     * @throws Exception 抛出异常的情况主要发生在配置过程中出现错误
     */
    @Bean
    public DefaultSecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        /**
         * 配置请求授权：所有请求都需要认证
         */
        return httpSecurity
                .authorizeHttpRequests()
                .anyRequest().authenticated()
                .and()

                /**
                 * 配置表单登录：指定登录处理的URL
                 */
                .formLogin()
                .loginProcessingUrl("/api/auth/login")
                .successHandler(this::onAuthenticationSuccess)
                .failureHandler(this::onAuthenticationFailure)
                .and()

                /**
                 * 配置退出登录：指定退出登录的URL
                 */
                .logout()
                .logoutUrl("/api/auth/logout")
                .and()



                /**
                 * 禁用CSRF保护：基于当前配置需求，可能出于简化或特定安全考虑
                 */
                .csrf()
                .disable()

                /**
                 * 配置异常处理：指定异常处理的URL
                 */
                .exceptionHandling()
                .authenticationEntryPoint(this::onAuthenticationFailure)
                .and()
                .build();
    }

    /**
     * 创建并配置认证管理器。
     * 这个方法通过利用HttpSecurity来获取AuthenticationManagerBuilder实例，并基于授权服务(authorizeService)
     * 来配置用户详细服务(userDetailsService)，然后构建并返回一个认证管理器(AuthenticationManager)。
     *
     * @param security HttpSecurity对象，用于获取AuthenticationManagerBuilder实例以进行配置。
     * @return AuthenticationManager 一个配置好的认证管理器实例。
     * @throws Exception 如果在构建认证管理器过程中发生错误。
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity security) throws Exception {
        // 使用HttpSecurity配置AuthenticationManagerBuilder，并基于authorizeService配置用户详细服务
        return security
                .getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(authorizeService)
                .and()
                .build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * 登录成功后处理
     *
     * @param request        用于获取请求信息
     * @param response       用于获取响应信息
     * @param authentication 用于获取认证信息
     * @throws IOException      用于抛出IO异常
     * @throws ServletException 用于抛出Servlet异常
     */
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //对json字符的处理
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        response.getWriter().write(JSONObject.toJSONString(RestBean.success("登录成功")));

    }


    /**
     * 登录失败后处理
     *
     * @param request
     * @param response
     * @param exception
     * @throws IOException
     * @throws ServletException
     */

    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        response.getWriter().write(JSONObject.toJSONString(RestBean.failure(401, exception.getMessage())));

    }
}


