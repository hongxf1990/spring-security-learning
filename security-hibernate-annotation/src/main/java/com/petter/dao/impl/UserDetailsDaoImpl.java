package com.petter.dao.impl;

import com.petter.dao.UserDetailsDao;
import com.petter.model.UserAttempts;
import org.hibernate.SessionFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Date;
import java.util.List;

/**
 * @author hongxf
 * @since 2017-03-20 10:54
 */
@Repository
public class UserDetailsDaoImpl implements UserDetailsDao {

    @Resource
    private SessionFactory sessionFactory;

    private static final int MAX_ATTEMPTS = 3;

    @Transactional
    @Override
    public void updateFailAttempts(String username) {

        UserAttempts userAttempts = getUserAttempts(username);
        if (userAttempts == null) {
            if (isUserExists(username)) { //如果存在这个用户
                // 如果之前没有记录，添加一条
                userAttempts = new UserAttempts();
                userAttempts.setUsername(username);
                userAttempts.setAttempts(1);
                userAttempts.setLastModified(new Date());
                sessionFactory.getCurrentSession().save(userAttempts);
            }
        } else {
            if (isUserExists(username)) {
                userAttempts.setAttempts(userAttempts.getAttempts() + 1);
                userAttempts.setLastModified(new Date());
                sessionFactory.getCurrentSession().update(userAttempts);
                sessionFactory.getCurrentSession().flush();
            }

            if (userAttempts.getAttempts() >= MAX_ATTEMPTS) {
                // 大于尝试次数则锁定
                sessionFactory.getCurrentSession()
                        .createQuery("update User u set u.accountNonLocked =:accountNonLocked where u.username =:username")
                        .setParameter("accountNonLocked", false)
                        .setParameter("username", username)
                        .executeUpdate();
                // 并且抛出账号锁定异常
                throw new LockedException("用户账号已被锁定，请联系管理员解锁");
            }

        }

    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    @Override
    public UserAttempts getUserAttempts(String username) {

        List<UserAttempts> list = sessionFactory.getCurrentSession()
                .createQuery("from UserAttempts where username =:username")
                .setParameter("username", username)
                .list();
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }

    }

    @Transactional
    @Override
    public void resetFailAttempts(String username) {
        sessionFactory.getCurrentSession()
                .createQuery("update UserAttempts ua set ua.attempts = 0, ua.lastModified = null where ua.username =:username")
                .setParameter("username", username)
                .executeUpdate();
    }

    /**
     * 判断用户是否存在
     * @param username
     * @return
     */
    private boolean isUserExists(String username) {
        boolean result = false;

        Long count = (Long) sessionFactory.getCurrentSession()
                .createQuery("select count(*) from User u where u.username =:username")
                .setParameter("username", username)
                .iterate().next();

        if (count > 0) {
            result = true;
        }
        return result;
    }
}
