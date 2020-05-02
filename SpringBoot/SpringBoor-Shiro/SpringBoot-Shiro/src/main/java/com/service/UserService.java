package com.service;

import com.pojo.User;

public interface UserService {
    User queryUserByName(String name);
}
