package com.gemora_server;

import com.gemora_server.config.SecurityConfig;
import com.gemora_server.controller.AuthController;
import com.gemora_server.dto.RegisterResponseDto;
import com.gemora_server.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService; // injected mock from TestConfig

    @Test
    void register_should_be_permitted_without_authentication() throws Exception {
        Mockito.when(authService.registerUserWithFiles(
                        Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                        Mockito.<org.springframework.web.multipart.MultipartFile>any(), Mockito.<org.springframework.web.multipart.MultipartFile>any(), Mockito.<org.springframework.web.multipart.MultipartFile>any()))
                .thenReturn(new RegisterResponseDto("User registered successfully!", "mock-token", "USER"));

        MockMultipartHttpServletRequestBuilder multipartRequest = multipart("/api/auth/register")
                .param("name", "Binoj")
                .param("email", "binoj@gemora.com")
                .param("password", "12345");

        mockMvc.perform(multipartRequest)
                .andExpect(status().isOk());
    }

    @Test
    void cors_preflight_should_be_permitted_for_register() throws Exception {
        mockMvc.perform(options("/api/auth/register")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"));
    }

    @org.springframework.boot.test.context.TestConfiguration
    static class TestConfig {
        @Bean
        public AuthService authService() {
            return Mockito.mock(AuthService.class);
        }
    }
}
