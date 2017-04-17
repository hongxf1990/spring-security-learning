package com.petter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * 相当于spring-security.xml中的配置
 * @author hongxf
 * @since 2017-03-08 9:30
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private DataSource dataSource;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().dataSource(dataSource).passwordEncoder(passwordEncoder())
                .usersByUsernameQuery("select username,password, enabled from users where username = ?")
                .authoritiesByUsernameQuery("select username, role from user_roles where username = ?");
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
