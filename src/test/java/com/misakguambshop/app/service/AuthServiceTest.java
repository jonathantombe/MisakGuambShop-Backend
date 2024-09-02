package com.misakguambshop.app.service;

import com.misakguambshop.app.dto.UserLoginDto;
import com.misakguambshop.app.dto.UserSignupDto;
import com.misakguambshop.app.model.ERole;
import com.misakguambshop.app.model.Role;
import com.misakguambshop.app.model.User;
import com.misakguambshop.app.repository.RoleRepository;
import com.misakguambshop.app.repository.UserRepository;
import com.misakguambshop.app.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAuthenticateUser() {
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("password");

        when(tokenProvider.generateToken(any())).thenReturn("jwtToken");

        String token = authService.authenticateUser(loginDto);

        assertNotNull(token);
        assertEquals("jwtToken", token);
    }

    @Test
    public void testRegisterUser() {
        UserSignupDto signUpDto = new UserSignupDto();
        signUpDto.setUsername("testuser");
        signUpDto.setEmail("test@example.com");
        signUpDto.setPassword("password");
        signUpDto.setPhone("1234567890");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByName(any(ERole.class))).thenReturn(Optional.of(new Role()));
        when(userRepository.save(any(User.class))).thenReturn(new User());

        User registeredUser = authService.registerUser(signUpDto, ERole.USER);

        assertNotNull(registeredUser);
        verify(userRepository, times(1)).save(any(User.class));
        verify(roleRepository, times(1)).findByName(ERole.USER);
    }

    @Test
    public void testRegisterSeller() {
        UserSignupDto signUpDto = new UserSignupDto();
        signUpDto.setUsername("testseller");
        signUpDto.setEmail("seller@example.com");
        signUpDto.setPassword("password");
        signUpDto.setPhone("1234567890");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByName(any(ERole.class))).thenReturn(Optional.of(new Role()));
        when(userRepository.save(any(User.class))).thenReturn(new User());

        User registeredSeller = authService.registerUser(signUpDto, ERole.SELLER);

        assertNotNull(registeredSeller);
        verify(userRepository, times(1)).save(any(User.class));
        verify(roleRepository, times(1)).findByName(ERole.SELLER);
    }

    @Test
    public void testRegisterUserUsernameExists() {
        UserSignupDto signUpDto = new UserSignupDto();
        signUpDto.setUsername("testuser");
        signUpDto.setEmail("test@example.com");
        signUpDto.setPassword("password");
        signUpDto.setPhone("9876543210");

        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authService.registerUser(signUpDto, ERole.USER));
    }

    @Test
    public void testRegisterUserEmailExists() {
        UserSignupDto signUpDto = new UserSignupDto();
        signUpDto.setUsername("testuser");
        signUpDto.setEmail("test@example.com");
        signUpDto.setPassword("password");
        signUpDto.setPhone("9876543210");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authService.registerUser(signUpDto, ERole.USER));
    }
}
