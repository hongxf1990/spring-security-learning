package com.petter.dao.impl;

import com.petter.dao.UserDao;
import com.petter.model.User;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hongxf
 * @since 2017-04-17 10:42
 */
@Repository
public class UserDaoImpl implements UserDao {

    @Resource
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    @Override
    public User findByUserName(String username) {

        List<User> users = sessionFactory.getCurrentSession()
                .createQuery("from User where username = ?")
                .setParameter(0, username)
                .list();

        if (users.size() > 0) {
            return users.get(0);
        } else {
            return null;
        }

    }
}
