package com.petter.handler;

import com.petter.dao.UserDetailsDao;
import com.petter.model.UserAttempts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 自定义验证程序，可以在这里进行其他附属操作，具体验证程序仍然调用上层接口即可
 * @author hongxf
 * @since 2017-03-20 14:28
 */
@Component("authenticationProvider")
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    @Resource
    private UserDetailsDao userDetailsDao;

    @Autowired
    @Qualifier("userDetailsService")
    @Override
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        super.setUserDetailsService(userDetailsService);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            //调用上层验证逻辑
            Authentication auth = super.authenticate(authentication);

            //如果验证通过登录成功则重置尝试次数， 否则抛出异常
            userDetailsDao.resetFailAttempts(authentication.getName());

            return auth;
        } catch (BadCredentialsException e) {
            //如果验证不通过，则更新尝试次数，当超过次数以后抛出账号锁定异常
            userDetailsDao.updateFailAttempts(authentication.getName());
            throw e;
        } catch (LockedException e){
            //该用户已经被锁定，则进入这个异常
            String error;
            UserAttempts userAttempts =
                    userDetailsDao.getUserAttempts(authentication.getName());

            if(userAttempts != null){
                Date lastAttempts = userAttempts.getLastModified();
                error = "用户已经被锁定，用户名 : "
                        + authentication.getName() + "最后尝试登陆时间 : " + lastAttempts;
            }else{
                error = e.getMessage();
            }

            throw new LockedException(error);
        }
    }
}
