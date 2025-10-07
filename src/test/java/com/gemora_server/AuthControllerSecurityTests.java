package com.gemora_server;

import com.gemora_server.controller.AuthController;
import com.gemora_server.dto.RegisterRequestDto;
import com.gemora_server.service.AuthService;
import com.gemora_server.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    void register_should_be_permitted_without_authentication() throws Exception {
        Mockito.when(authService.registerUser(Mockito.any(RegisterRequestDto.class)))
                .thenReturn("User registered successfully!");

        String json = "{" +
                "\"name\":\"Binoj\"," +
                "\"email\":\"binoj@gemora.com\"," +
                "\"password\":\"12345\"" +
                "}";

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully!"));
    }

    @Test
    void cors_preflight_should_be_permitted_for_register() throws Exception {
        mockMvc.perform(options("/api/auth/register")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"))
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }
}
