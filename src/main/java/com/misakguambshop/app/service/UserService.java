package com.misakguambshop.app.service;

import com.misakguambshop.app.dto.UserDto;
import com.misakguambshop.app.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(UserDto userDto);
    List<User> getAllUsers();
    User getUserById(Long id);
    User updateUser(Long id, UserDto userDto);
    void deleteUser(Long id);
    Optional<User> findByUsername(String testuser);
    Optional<User> findByEmail(String mail);
}
