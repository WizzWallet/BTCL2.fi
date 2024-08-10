package com.wizz.fi.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wizz.fi.dao.mapper.UserMapper;
import com.wizz.fi.dao.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {
    // 获取用户
    public User getUser(String userAddress) {
        // 1. create user
        User user = getBaseMapper().byAddress(userAddress);
        if (user == null) {
            user = new User();
            user.setAddress(userAddress);
            save(user);
        }

        return user;
    }
}
