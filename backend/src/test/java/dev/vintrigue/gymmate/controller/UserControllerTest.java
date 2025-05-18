package dev.vintrigue.gymmate.controller;

import dev.vintrigue.gymmate.config.TestSecurityConfig;
import dev.vintrigue.gymmate.model.User;
import dev.vintrigue.gymmate.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void testRegisterUser_Success() throws Exception {
        // Create a test user
        User testUser = new User();
        testUser.setId("testId");
        testUser.setUsername("testuser");
        testUser.setPassword("encoded_password"); // This would be the encoded version
        testUser.setEmail("test@example.com");
        testUser.setGoal(Collections.singletonList("weight_loss"));
        testUser.setActivityLevel("moderately_active");
        testUser.setGender("male");

        // Mock the service response
        when(userService.register(any(User.class))).thenReturn(testUser);

        // Perform the request and validate the response
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"password123\",\"email\":\"test@example.com\",\"goal\":[\"weight_loss\"],\"activityLevel\":\"moderately_active\",\"gender\":\"male\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":\"testId\",\"username\":\"testuser\"}"));
    }

    @Test
    public void testRegisterUser_MissingActivityLevel() throws Exception {
        // Mock the service to throw exception for missing activity level
        when(userService.register(any(User.class)))
            .thenThrow(new IllegalArgumentException("Please put in your activityLevel with one of the following: sedentary, lightly_active, moderately_active, very_active, super_active"));

        // Perform the request and validate the response
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"password123\",\"email\":\"test@example.com\",\"goal\":[\"weight_loss\"],\"gender\":\"male\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Please put in your activityLevel with one of the following: sedentary, lightly_active, moderately_active, very_active, super_active"));
    }

    @Test
    public void testRegisterUser_InvalidActivityLevel() throws Exception {
        // Mock the service to throw exception for invalid activity level
        when(userService.register(any(User.class)))
            .thenThrow(new IllegalArgumentException("Invalid activity level. Must be one of: sedentary, lightly_active, moderately_active, very_active, super_active"));

        // Perform the request and validate the response
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"password123\",\"email\":\"test@example.com\",\"goal\":[\"weight_loss\"],\"activityLevel\":\"invalid_level\",\"gender\":\"male\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid activity level. Must be one of: sedentary, lightly_active, moderately_active, very_active, super_active"));
    }

    @Test
    public void testRegisterUser_MissingGender() throws Exception {
        // Mock the service to throw exception for missing gender
        when(userService.register(any(User.class)))
            .thenThrow(new IllegalArgumentException("Please specify your gender (male/female/other)"));

        // Perform the request and validate the response
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"password123\",\"email\":\"test@example.com\",\"goal\":[\"weight_loss\"],\"activityLevel\":\"moderately_active\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Please specify your gender (male/female/other)"));
    }

    @Test
    public void testRegisterUser_InvalidGender() throws Exception {
        // Mock the service to throw exception for invalid gender
        when(userService.register(any(User.class)))
            .thenThrow(new IllegalArgumentException("Invalid gender. Must be one of: male, female, other"));

        // Perform the request and validate the response
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"password123\",\"email\":\"test@example.com\",\"goal\":[\"weight_loss\"],\"activityLevel\":\"moderately_active\",\"gender\":\"invalid_gender\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid gender. Must be one of: male, female, other"));
    }

    @Test
    public void testLoginUser_Success() throws Exception {
        // Mock the service response for successful login
        when(userService.login(anyString(), anyString())).thenReturn(true);

        // Perform the request and validate the response
        mockMvc.perform(post("/api/users/login")
                .param("username", "testuser")
                .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful"));
    }

    @Test
    public void testLoginUser_Failure() throws Exception {
        // Mock the service response for failed login
        when(userService.login(anyString(), anyString())).thenReturn(false);

        // Perform the request and validate the response
        mockMvc.perform(post("/api/users/login")
                .param("username", "testuser")
                .param("password", "wrongpassword"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid credentials"));
    }
} 