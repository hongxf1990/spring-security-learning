package com.petter.dao.impl;

import com.petter.dao.UserDetailsDao;
import com.petter.model.UserAttempts;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Date;

/**
 * @author hongxf
 * @since 2017-03-20 10:54
 */
@Repository
public class UserDetailsDaoImpl extends JdbcDaoSupport implements UserDetailsDao {

    private static final String SQL_USERS_UPDATE_LOCKED = "UPDATE USERS SET accountNonLocked = ? WHERE username = ?";
    private static final String SQL_USERS_COUNT = "SELECT count(*) FROM USERS WHERE username = ?";

    private static final String SQL_USER_ATTEMPTS_GET = "SELECT * FROM USER_ATTEMPTS WHERE username = ?";
    private static final String SQL_USER_ATTEMPTS_INSERT = "INSERT INTO USER_ATTEMPTS (USERNAME, ATTEMPTS, LASTMODIFIED) VALUES(?,?,?)";
    private static final String SQL_USER_ATTEMPTS_UPDATE_ATTEMPTS = "UPDATE USER_ATTEMPTS SET attempts = attempts + 1, lastmodified = ? WHERE username = ?";
    private static final String SQL_USER_ATTEMPTS_RESET_ATTEMPTS = "UPDATE USER_ATTEMPTS SET attempts = 0, lastmodified = null WHERE username = ?";


    private static final int MAX_ATTEMPTS = 3;

    @Resource
    private DataSource dataSource;

    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }

    @Override
    public void updateFailAttempts(String username) {

        UserAttempts user = getUserAttempts(username);
        if (user == null) {
            if (isUserExists(username)) {
                // 如果之前没有记录，添加一条
                getJdbcTemplate().update(SQL_USER_ATTEMPTS_INSERT, username, 1, new Date());
            }
        } else {

            if (isUserExists(username)) {
                // 存在用户则失败一次增加一次尝试次数
                getJdbcTemplate().update(SQL_USER_ATTEMPTS_UPDATE_ATTEMPTS, new Date(), username);
            }

            if (user.getAttempts() + 1 >= MAX_ATTEMPTS) {
                // 大于尝试次数则锁定
                getJdbcTemplate().update(SQL_USERS_UPDATE_LOCKED, false, username);
                // 并且抛出账号锁定异常
                throw new LockedException("用户账号已被锁定，请联系管理员解锁");
            }

        }

    }

    @Override
    public UserAttempts getUserAttempts(String username) {

        try {

            UserAttempts userAttempts = getJdbcTemplate().queryForObject(SQL_USER_ATTEMPTS_GET,
                    new Object[] { username }, (rs, rowNum) -> {

                        UserAttempts user = new UserAttempts();
                        user.setId(rs.getInt("id"));
                        user.setUsername(rs.getString("username"));
                        user.setAttempts(rs.getInt("attempts"));
                        user.setLastModified(rs.getDate("lastModified"));

                        return user;
                    });
            return userAttempts;

        } catch (EmptyResultDataAccessException e) {
            return null;
        }

    }

    @Override
    public void resetFailAttempts(String username) {

        getJdbcTemplate().update(
                SQL_USER_ATTEMPTS_RESET_ATTEMPTS, username);

    }

    private boolean isUserExists(String username) {

        boolean result = false;

        int count = getJdbcTemplate().queryForObject(
                SQL_USERS_COUNT, new Object[] { username }, Integer.class);
        if (count > 0) {
            result = true;
        }

        return result;
    }
}
