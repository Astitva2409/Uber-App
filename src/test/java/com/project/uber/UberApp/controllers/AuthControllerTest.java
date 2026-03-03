package com.project.uber.UberApp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.uber.UberApp.dto.SignupDto;
import com.project.uber.UberApp.dto.UserDto;
import com.project.uber.UberApp.security.JwtService;
import com.project.uber.UberApp.services.AuthService;
import com.project.uber.UberApp.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Bypasses Spring Security filters during the HTTP request
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    // --- Add these two mocks to satisfy WebSecurityConfig ---
    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserService userService;
    // --------------------------------------------------------

    @Test
    void testSignUp_Success() throws Exception {
        SignupDto signupDto = new SignupDto("Jane Doe", "jane@test.com", "password123");
        UserDto responseDto = new UserDto();
        responseDto.setName("Jane Doe");
        responseDto.setEmail("jane@test.com");

        when(authService.signup(any(SignupDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.email").value("jane@test.com"))
                .andExpect(jsonPath("$.data.name").value("Jane Doe"));
    }

    @Test
    void testSignUp_ValidationFailure_MissingPassword() throws Exception {
        SignupDto signupDto = new SignupDto("Jane Doe", "jane@test.com", null);

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDto)))
                .andExpect(status().isBadRequest())
                // Updated JSON paths to match your GlobalResponseHandler wrapper!
                .andExpect(jsonPath("$.error.message").value("Input validation failed"))
                .andExpect(jsonPath("$.error.errorMessage").isArray());
    }
}