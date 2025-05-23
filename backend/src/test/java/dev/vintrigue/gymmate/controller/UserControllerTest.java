package dev.vintrigue.gymmate.controller;

import dev.vintrigue.gymmate.model.User;
import dev.vintrigue.gymmate.service.UserService;
import dev.vintrigue.gymmate.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
        testUser.setProfileCompleted(false);

        // Mock the service response
        when(userService.register(any(User.class))).thenReturn(testUser);

        // Perform the request and validate the response
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":\"testId\",\"username\":\"testuser\",\"profileCompleted\":false}"));
    }

    @Test
    public void testLoginUser_Success() throws Exception {
        // Create a test user for successful login
        User testUser = new User();
        testUser.setId("testId");
        testUser.setUsername("testuser");
        testUser.setProfileCompleted(true);

        // Mock the service response for successful login
        when(userService.login(anyString(), anyString())).thenReturn(testUser);

        // Perform the request and validate the response
        mockMvc.perform(post("/api/users/login")
                .param("username", "testuser")
                .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful"));
    }

    @Test
    public void testLoginUser_ProfileSetupRequired() throws Exception {
        // Create a test user with incomplete profile
        User testUser = new User();
        testUser.setId("testId");
        testUser.setUsername("testuser");
        testUser.setProfileCompleted(false);

        // Mock the service response for login with incomplete profile
        when(userService.login(anyString(), anyString())).thenReturn(testUser);

        // Perform the request and validate the response
        mockMvc.perform(post("/api/users/login")
                .param("username", "testuser")
                .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Profile setup required"));
    }

    @Test
    public void testLoginUser_Failure() throws Exception {
        // Mock the service response for failed login
        when(userService.login(anyString(), anyString())).thenReturn(null);

        // Perform the request and validate the response
        mockMvc.perform(post("/api/users/login")
                .param("username", "testuser")
                .param("password", "wrongpassword"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    public void testCompleteProfile_Success() throws Exception {
        // Create a test user
        User testUser = new User();
        testUser.setId("testId");
        testUser.setUsername("testuser");
        testUser.setProfileCompleted(true);
        testUser.setGoal(Collections.singletonList("weight_loss"));
        testUser.setActivityLevel("moderately_active");
        testUser.setGender("male");

        // Mock the service response
        when(userService.completeProfile(anyString(), any(User.class))).thenReturn(testUser);

        // Perform the request and validate the response
        mockMvc.perform(post("/api/users/testId/complete-profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"goal\":[\"weight_loss\"],\"activityLevel\":\"moderately_active\",\"gender\":\"male\",\"weight\":70.5,\"height\":175.0,\"age\":25}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":\"testId\",\"username\":\"testuser\",\"profileCompleted\":true}"));
    }
}