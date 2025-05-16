# Testing Login and Signup API Endpoints

This document contains commands to test the login and signup functionality of the GymMate application.

## Prerequisites
- Ensure the backend server is running
- Use curl or Postman for testing
- The API is accessible at `http://localhost:8080`

## Register (Signup) Testing

### 1. Register a new user with curl

```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com",
    "goal": "burn_500_calories"
  }'
```

### 2. Register with missing fields (should fail)

```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "incompleteuser",
    "password": "password123"
  }'
```

### 3. Register with duplicate username (should fail)

First, create a user, then try to create another with the same username:

```bash
# Try registering again with the same username
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "different123",
    "email": "different@example.com",
    "goal": "improve_sleep"
  }'
```

## Login Testing

### 1. Login with correct credentials

```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

### 2. Login with incorrect password (should fail)

```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "wrongpassword"
  }'
```

### 3. Login with non-existent user (should fail)

```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "nonexistentuser",
    "password": "password123"
  }'
```

## Postman Testing

You can also use Postman to test these endpoints:

1. Create a new request in Postman
2. Set the HTTP method to POST
3. Enter the URL (e.g., `http://localhost:8080/api/users/register` or `http://localhost:8080/api/users/login`)
4. Go to the "Body" tab
5. Select "raw" and "JSON"
6. Enter the JSON payload (similar to the curl examples above)
7. Click "Send"

## Expected Responses

### Successful Registration
- HTTP Status: 200 OK
- Response Body: User object with generated ID and encoded password

### Successful Login
- HTTP Status: 200 OK
- Response Body: "Login successful"

### Failed Login
- HTTP Status: 401 Unauthorized
- Response Body: "Invalid credentials"

## Automated Testing with JUnit

For automated testing, you could create JUnit tests in a file like `UserControllerTest.java` to test these endpoints programmatically. 