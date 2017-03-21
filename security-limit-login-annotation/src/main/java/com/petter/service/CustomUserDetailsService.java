package com.petter.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;

/**
 * 查看JdbcDaoImpl的源码可以发现这是实现自定义的UserDetailsService
 * 添加上锁定和过期信息
 * @author hongxf
 * @since 2017-03-20 12:30
 */
@Service("userDetailsService")
public class CustomUserDetailsService extends JdbcDaoImpl {

    @Resource
    private DataSource dataSource;

    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }


    @Override
    @Value("select * from users where username = ?")
    public void setUsersByUsernameQuery(String usersByUsernameQueryString) {
        super.setUsersByUsernameQuery(usersByUsernameQueryString);
    }

    @Override
    @Value("select username, role from user_roles where username = ?")
    public void setAuthoritiesByUsernameQuery(String queryString) {
        super.setAuthoritiesByUsernameQuery(queryString);
    }

    @Override
    protected List<UserDetails> loadUsersByUsername(String username) {
        return getJdbcTemplate().query(super.getUsersByUsernameQuery(), new Object[]{username},
                (rs, rowNum) -> {
                    String username1 = rs.getString("username");
                    String password = rs.getString("password");
                    boolean enabled = rs.getBoolean("enabled");
                    boolean accountNonExpired = rs.getBoolean("accountNonExpired");
                    boolean credentialsNonExpired = rs.getBoolean("credentialsNonExpired");
                    boolean accountNonLocked = rs.getBoolean("accountNonLocked");

                    return new User(username1, password, enabled, accountNonExpired, credentialsNonExpired,
                            accountNonLocked, AuthorityUtils.NO_AUTHORITIES);
                });
    }

    @Override
    protected UserDetails createUserDetails(String username, UserDetails userFromUserQuery, List<GrantedAuthority> combinedAuthorities) {
        String returnUsername = userFromUserQuery.getUsername();

        if (!super.isUsernameBasedPrimaryKey()) {
            returnUsername = username;
        }

        return new User(returnUsername, userFromUserQuery.getPassword(),
                userFromUserQuery.isEnabled(),
                userFromUserQuery.isAccountNonExpired(),
                userFromUserQuery.isCredentialsNonExpired(),
                userFromUserQuery.isAccountNonLocked(), combinedAuthorities);
    }
}
