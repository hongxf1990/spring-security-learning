package com.petter.config;

import com.petter.handler.CustomAuthenticationProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.annotation.Resource;

/**
 * 相当于spring-security.xml中的配置
 * @author hongxf
 * @since 2017-03-08 9:30
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Resource
    private CustomAuthenticationProvider authenticationProvider;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }

    /**
     * 配置权限要求
     * 采用注解方式，默认开启csrf
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/dba/**").hasAnyRole("ADMIN", "DBA")
            .and()
                .formLogin().loginPage("/login")
                .defaultSuccessUrl("/welcome").failureUrl("/login?error")
                .usernameParameter("user-name").passwordParameter("pwd")
            .and()
                .logout().logoutSuccessUrl("/login?logout")
            .and()
                .exceptionHandling().accessDeniedPage("/403")
            .and()
                .csrf();
    }


}
