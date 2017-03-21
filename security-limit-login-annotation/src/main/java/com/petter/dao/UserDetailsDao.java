package com.petter.dao;

import com.petter.model.UserAttempts;

/**
 * @author hongxf
 * @since 2017-03-20 10:53
 */
public interface UserDetailsDao {

    void updateFailAttempts(String username);
    void resetFailAttempts(String username);
    UserAttempts getUserAttempts(String username);
}
