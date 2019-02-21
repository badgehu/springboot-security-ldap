package com.badgehu.demo.dao;

import com.badgehu.demo.domain.SysUser;


public interface UserDao {
    SysUser findByUserName(String username);
}
