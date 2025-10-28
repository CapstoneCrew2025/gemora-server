package com.gemora_server;

import com.gemora_server.controller.AuthController;
import com.gemora_server.controller.TestController;
import com.gemora_server.dto.LoginRequestDto;
import com.gemora_server.dto.LoginResponseDto;
import com.gemora_server.dto.RegisterResponseDto;
import com.gemora_server.service.AuthService;
import com.gemora_server.filter.JwtAuthenticationFilter;
import com.gemora_server.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {AuthController.class, TestController.class})
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter; // prevent real bean

    @MockBean
    private JwtUtil jwtUtil; // prevent real bean

    @Test
    @DisplayName("Public endpoint /public/test should be accessible and return body")
    void publicTestAccessible() throws Exception {
        mockMvc.perform(get("/public/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("public ok"));
    }

    @Test
    @DisplayName("Login should return token and role JSON when service succeeds")
    void loginPermitted() throws Exception {
        Mockito.when(authService.loginUser(any(LoginRequestDto.class)))
                .thenReturn(new LoginResponseDto("dummy.jwt.token", "USER"));

        String body = "{" +
                "\"email\":\"user@example.com\"," +
                "\"password\":\"secret\"}";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("dummy.jwt.token"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @DisplayName("Register (multipart) should return success JSON when service succeeds")
    void registerPermitted() throws Exception {
        Mockito.when(authService.registerUserWithFiles(anyString(), anyString(), anyString(), anyString(), any(), any(), any()))
                .thenReturn(new RegisterResponseDto("User registered successfully!", "reg.jwt.token", "USER"));

        MockMultipartFile name = new MockMultipartFile("name", "", "text/plain", "Binoj".getBytes());
        MockMultipartFile email = new MockMultipartFile("email", "", "text/plain", "binoj@gemora.com".getBytes());
        MockMultipartFile password = new MockMultipartFile("password", "", "text/plain", "12345".getBytes());
        MockMultipartFile contact = new MockMultipartFile("contactNumber", "", "text/plain", "0771234567".getBytes());
        MockMultipartFile idFront = new MockMultipartFile("idFrontImage", "front.jpg", "image/jpeg", new byte[]{1,2,3});
        MockMultipartFile idBack = new MockMultipartFile("idBackImage", "back.jpg", "image/jpeg", new byte[]{1,2,3});
        MockMultipartFile selfie = new MockMultipartFile("selfieImage", "selfie.jpg", "image/jpeg", new byte[]{1,2,3});

        mockMvc.perform(multipart("/api/auth/register")
                        .file(name)
                        .file(email)
                        .file(password)
                        .file(contact)
                        .file(idFront)
                        .file(idBack)
                        .file(selfie)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"))
                .andExpect(jsonPath("$.token").value("reg.jwt.token"))
                .andExpect(jsonPath("$.role").value("USER"));
    }
}
