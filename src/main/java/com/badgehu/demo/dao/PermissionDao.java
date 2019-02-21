package com.badgehu.demo.dao;

import com.badgehu.demo.domain.Permission;

import java.util.List;

public interface PermissionDao {
    List<Permission> findAll();
    List<Permission> findByAdminUserId(int userId);
}
