package dev.vintrigue.gymmate.controller;

import dev.vintrigue.gymmate.model.User;
import dev.vintrigue.gymmate.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
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
        testUser.setGoal("burn_500_calories");

        // Mock the service response
        when(userService.register(any(User.class))).thenReturn(testUser);

        // Perform the request and validate the response
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"password123\",\"email\":\"test@example.com\",\"goal\":\"burn_500_calories\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":\"testId\",\"username\":\"testuser\"}"));
    }

    @Test
    public void testLoginUser_Success() throws Exception {
        // Mock the service response for successful login
        when(userService.login(anyString(), anyString())).thenReturn(true);

        // Perform the request and validate the response
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful"));
    }

    @Test
    public void testLoginUser_Failure() throws Exception {
        // Mock the service response for failed login
        when(userService.login(anyString(), anyString())).thenReturn(false);

        // Perform the request and validate the response
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }
} 