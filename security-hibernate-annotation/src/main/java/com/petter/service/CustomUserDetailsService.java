package com.petter.service;

import com.petter.dao.UserDao;
import com.petter.model.User;
import com.petter.model.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * security的验证过程会调用指定的UserDetailsService
 * 自定义的UserDetailsService查询数据库得到自己User后
 * 组装org.springframework.security.core.userdetails.User返回
 * @author hongxf
 * @since 2017-04-17 10:45
 */
@Service("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    @Resource
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("该用户不存在：" + username);
        }
        List<GrantedAuthority> authorities =  buildUserAuthority(user.getUserRole());
        return buildUserForAuthentication(user, authorities);
    }

    // 把自定义的User转换成org.springframework.security.core.userdetails.User
    private org.springframework.security.core.userdetails.User buildUserForAuthentication(
            User user,
            List<GrantedAuthority> authorities) {
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                user.isEnabled(), user.isAccountNonExpired(), user.isCredentialsNonExpired(), user.isAccountNonLocked(), authorities);
    }

    private List<GrantedAuthority> buildUserAuthority(Set<UserRole> userRoles) {

        Set<GrantedAuthority> setAuths = new HashSet<>();

        // Build user's authorities
        for (UserRole userRole : userRoles) {
            setAuths.add(new SimpleGrantedAuthority(userRole.getRole()));
        }

        return new ArrayList<>(setAuths);
    }

}
