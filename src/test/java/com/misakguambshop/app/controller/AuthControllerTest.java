package com.misakguambshop.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.misakguambshop.app.dto.UserLoginDto;
import com.misakguambshop.app.dto.UserSignupDto;
import com.misakguambshop.app.model.ERole;
import com.misakguambshop.app.model.User;
import com.misakguambshop.app.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testAuthenticateUser() throws Exception {
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("password");

        when(authService.authenticateUser(any(UserLoginDto.class))).thenReturn("jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk());
    }

    @Test
    public void testRegisterUser() throws Exception {
        UserSignupDto signupDto = new UserSignupDto();
        signupDto.setUsername("testuser");
        signupDto.setEmail("test@example.com");
        signupDto.setPassword("password");
        signupDto.setPhone("1234567890");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDto)))
                .andExpect(status().isOk());
    }

    @Test
    public void testRegisterSeller() throws Exception {
        UserSignupDto signUpDto = new UserSignupDto();
        signUpDto.setUsername("testseller");
        signUpDto.setEmail("seller@example.com");
        signUpDto.setPassword("password");
        signUpDto.setPhone("9876543210");

        when(authService.registerUser(any(UserSignupDto.class), eq(ERole.SELLER))).thenReturn(new User());
        mockMvc.perform(post("/api/auth/signup/seller")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpDto)))
                .andExpect(status().isOk());
    }
}