package com.petter.config;

import com.petter.handler.CustomAuthenticationProvider;
import com.petter.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

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
    @Resource
    private CustomAuthenticationProvider authenticationProvider;
    @Resource
    private CustomUserDetailsService userDetailsService;


    @Bean
    public PasswordEncoder passwordEncoder(){
        //return new CustomPasswordEncoder();
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        authenticationProvider.setPasswordEncoder(passwordEncoder());
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
                .formLogin().successHandler(savedRequestAwareAuthenticationSuccessHandler())
                .loginPage("/login") //指定自定义登录页
                .failureUrl("/login?error") //登录失败的跳转路径
                .loginProcessingUrl("/auth/login_check") //指定了登录的form表单提交的路径，需与表单的action值保存一致，默认是login
                .usernameParameter("user-name").passwordParameter("pwd")
            .and()
                .logout().logoutSuccessUrl("/login?logout")
            .and()
                .exceptionHandling().accessDeniedPage("/403")
            .and()
                .csrf()
            .and()
                //.rememberMe().rememberMeParameter("remember-me") //其实默认就是remember-me，这里可以指定更换
                //.tokenValiditySeconds(1209600)
                //.key("hongxf");
                .rememberMe().tokenRepository(persistentTokenRepository())
                .tokenValiditySeconds(1209600);
    }

    //如果采用持久化 token 的方法则需要指定保存token的方法
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
        db.setDataSource(dataSource);
        return db;
    }

    //使用remember-me必须指定UserDetailsService
    @Override
    protected UserDetailsService userDetailsService() {
        return userDetailsService;
    }

    /**
     * 这里是登录成功以后的处理逻辑
     * 设置目标地址参数为targetUrl
     * /auth/login_check?targetUrl=/admin/update
     * 这个地址就会被解析跳转到/admin/update，否则就是默认页面
     *
     * 本示例中访问update页面时候会判断用户是手动登录还是remember-me登录的
     * 如果是remember-me登录的则会跳转到登录页面进行手动登录再跳转
     * @return
     */
    @Bean
    public SavedRequestAwareAuthenticationSuccessHandler savedRequestAwareAuthenticationSuccessHandler() {

        SavedRequestAwareAuthenticationSuccessHandler auth = new SavedRequestAwareAuthenticationSuccessHandler();
        auth.setTargetUrlParameter("targetUrl");
        return auth;
    }

}
