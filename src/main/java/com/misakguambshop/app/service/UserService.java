package com.misakguambshop.app.service;

import com.misakguambshop.app.dto.UserDto;
import com.misakguambshop.app.model.User;
import org.springframework.web.multipart.MultipartFile;

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


    void deactivateUser(Long id);
    String forgotPassword(String email);

    String resetPassword(String token, String newPassword);
    User uploadProfileImage(Long id, MultipartFile file);
    User updateProfileImage(Long id, MultipartFile file);
    User deleteProfileImage(Long id);
    User reactivateAccount(String email) throws RuntimeException;
    void validateResetToken(String token);
    User becomeSeller(Long userId);

}
