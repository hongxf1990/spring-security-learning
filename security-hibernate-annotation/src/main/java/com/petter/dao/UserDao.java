package com.petter.dao;

import com.petter.model.User;

/**
 * @author hongxf
 * @since 2017-04-17 10:41
 */
public interface UserDao {
    User findByUserName(String username);
}
