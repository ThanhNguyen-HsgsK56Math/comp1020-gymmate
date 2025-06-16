# API Testing Guide

Ensure you're in a Linux terminal environment. Open 2 terminal windows and run "cd backend" in both windows.

## Terminal 1: System Startup
- Run "mvn spring-boot:run" to initialize the system
- Wait for about 1-1.5 minutes until you see "Started GymmateApplication"
- Then proceed to Terminal 2

## Terminal 2: API Testing

### 1. Basic User Registration (Step 1)
Test 1: Create a user with just username and password
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "thanh-gay",
    "password": "password123"
  }'
```
Expected: Returns user information with profileCompleted=false

Test 2: Create a user with duplicate username
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser3",
    "password": "password123"
  }'
```
Expected: "User with this username and password already exists"

### 2. User Login
Test 1: Login with incomplete profile
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "thanh-gay",
    "password": "password123"
  }'
```
Expected: Returns JSON with username, both passwords, and status:
```json
{
    "username": "thanh-gay",
    "realPassword": "password123",
    "encodedPassword": "$2a$10$encoded_password_hash",
    "status": "Profile setup required"
}
```

### 3. Complete Profile (Step 2)
Test 1: Complete profile with valid data
```bash
curl -X POST http://localhost:8080/api/users/thanh-gay/complete-profile \
  -H "Content-Type: application/json" \
  -d '{
    "goal": ["weight_loss", "muscle_gain"],
    "activityLevel": "moderately_active",
    "gender": "male",
    "weight": 70.5,
    "height": 175.0,
    "age": 25
  }'
```
Expected: Returns updated user information with profileCompleted=true

Test 2: Complete profile with invalid activity level
```bash
curl -X POST http://localhost:8080/api/users/newuser1/complete-profile \
  -H "Content-Type: application/json" \
  -d '{
    "goal": ["weight_loss"],
    "activityLevel": "invalid_level",
    "gender": "male",
    "weight": 70.5,
    "height": 175.0,
    "age": 25
  }'
```
Expected: "Invalid activity level. Must be one of: sedentary, lightly_active, moderately_active, very_active, super_active"

Test 3: Complete profile with invalid gender
```bash
curl -X POST http://localhost:8080/api/users/newuser1/complete-profile \
  -H "Content-Type: application/json" \
  -d '{
    "goal": ["weight_loss"],
    "activityLevel": "moderately_active",
    "gender": "invalid_gender",
    "weight": 70.5,
    "height": 175.0,
    "age": 25
  }'
```
Expected: "Invalid gender. Must be one of: male, female, other"

Test 4: Complete profile with invalid goal
```bash
curl -X POST http://localhost:8080/api/users/newuser1/complete-profile \
  -H "Content-Type: application/json" \
  -d '{
    "goal": ["invalid_goal"],
    "activityLevel": "moderately_active",
    "gender": "male",
    "weight": 70.5,
    "height": 175.0,
    "age": 25
  }'
```
Expected: "Invalid goal. Must be one of: weight_loss, muscle_gain, endurance_building, healthy_lifestyle"

### 4. Login After Profile Completion
Test 1: Login with complete profile
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "dang",
    "password": "123"
  }'
```
Expected: Returns JSON with username, both passwords, and status:
```json
{
    "username": "thanh-gay",
    "realPassword": "password123",
    "encodedPassword": "$2a$10$encoded_password_hash",
    "status": "Login successful"
}
```

Test 2: Login with incorrect password
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "thanh-gay",
    "password": "wrongpassword"
  }'
```
Expected: "Invalid credentials"

### 5. Update Activity Level
Test 1: Update with valid activity level
```bash
curl -X PUT "http://localhost:8080/api/users/newuser1/activity-level?activityLevel=very_active"
```
Expected: Returns updated user information

Test 2: Update with invalid activity level
```bash
curl -X PUT "http://localhost:8080/api/users/newuser1/activity-level?activityLevel=invalid_level"
```
Expected: "Invalid activity level. Must be one of: sedentary, lightly_active, moderately_active, very_active, super_active"

Test 3: Update non-existent user
```bash
curl -X PUT "http://localhost:8080/api/users/nonexistentuser/activity-level?activityLevel=moderately_active"
```
Expected: 404 Not Found

### 6. Get Weekly Exercise Plan
Test 1: Get basic weekly exercise plan
```bash
curl -X GET "http://localhost:8080/api/exerciseplans?username=dang" \
  -H "Content-Type: application/json"
```
Expected: Returns a weekly exercise plan with the following structure:
```json
{
    "id": "string",
    "userId": "string",
    "startDate": "2024-03-20",
    "dailyExercises": {
        "2024-03-20": [
            {
                "id": "string",
                "name": "string",
                "caloriesBurned": "number",
                "duration": "number",
                "parameters": {
                    "suitability_weight_loss": "number",
                    "suitability_muscle_gain": "number",
                    "suitability_endurance_building": "number",
                    "suitability_healthy_lifestyle": "number"
                }
            }
        ],
        // ... exercises for other days of the week
    },
    "totalCaloriesBurned": "number",
    "totalDuration": "number"
}
```

Test 2: Get weekly exercise plan with specific goals
```bash
curl -X GET "http://localhost:8080/api/exerciseplans?username=newuser3&weightLoss=8&muscleBuilding=0&endurance=0&health=0" \
  -H "Content-Type: application/json"
```
Expected: Returns a weekly plan optimized for weight loss goals

Test 3: Get weekly exercise plan for non-existent user
```bash
curl -X GET "http://localhost:8080/api/exerciseplans?username=nonexistentuser" \
  -H "Content-Type: application/json"
```
Expected: 404 Not Found with error message "User not found"

### 7. Get Meal Plan
```bash
curl -X GET "http://localhost:8080/api/mealplans?username=newuser3" \
  -H "Content-Type: application/json"
```
Expected: Returns meal plan based on user's profile and goals
Note: User must have completed their profile and have valid goals set

### 8. Exercise List
Test 1: Get all available exercises
```bash
curl -X GET http://localhost:8080/api/exercises \
  -H "Content-Type: application/json"
```
Expected: Returns a JSON array of all exercises with their details:
```json
[
  {
    "id": "string",
    "name": "string",
    "caloriesBurned": "number",
    "duration": "number",
    "parameters": {
      "suitability_weight_loss": "number",
      "suitability_muscle_gain": "number",
      "suitability_endurance_building": "number",
      "suitability_healthy_lifestyle": "number"
    }
  }
]
```

Test 2: Get exercise by ID
```bash
curl -X GET http://localhost:8080/api/exercises/{exerciseId} \
  -H "Content-Type: application/json"
```
Expected: Returns a single exercise with the specified ID

Test 3: Get exercises by goal
```bash
curl -X GET "http://localhost:8080/api/exercises/goal?goal=weight_loss" \
  -H "Content-Type: application/json"
```
Expected: Returns exercises suitable for the specified goal

## Weekly Exercise Plan Generation Tests

### Test Case 1: Basic Weekly Exercise Plan
```http
GET /api/exerciseplans?username=newuser1&weightLoss=8&muscleBuilding=0&endurance=0&health=0
```
Expected: Weekly plan with exercises optimized for weight loss, distributed across 7 days

### Test Case 2: Multiple Goals Weekly Plan
```http
GET /api/exerciseplans?username=newuser1&weightLoss=7&muscleBuilding=7&endurance=0&health=0
```
Expected: Weekly plan balancing weight loss and muscle building exercises

### Test Case 3: Health-Focused Weekly Plan
```http
GET /api/exerciseplans?username=newuser1&weightLoss=0&muscleBuilding=0&endurance=0&health=8
```
Expected: Weekly plan with exercises optimized for general health

### Test Case 4: Endurance-Focused Weekly Plan
```http
GET /api/exerciseplans?username=newuser1&weightLoss=0&muscleBuilding=0&endurance=8&health=0
```
Expected: Weekly plan with exercises optimized for endurance building

### Test Case 5: All Goals Weekly Plan
```http
GET /api/exerciseplans?username=newuser1&weightLoss=6&muscleBuilding=6&endurance=6&health=6
```
Expected: Balanced weekly plan considering all fitness goals

### Test Case 6: Weekly Plan Validation
```http
GET /api/exerciseplans?username=newuser1
```
Expected: Verify that:
1. Plan contains exercises for all 7 days
2. Each day has appropriate number of exercises
3. Total weekly calories match target
4. Exercise variety across the week
5. Proper distribution of exercise types based on goals

### Test Case 7: Weekly Plan Edge Cases
Test 1: Very Low Activity Level
```http
GET /api/exerciseplans?username=newuser1&activityLevel=sedentary
```
Expected: Plan with appropriate exercise intensity and duration for sedentary users

Test 2: Very High Activity Level
```http
GET /api/exerciseplans?username=newuser1&activityLevel=super_active
```
Expected: Plan with higher intensity and duration exercises

Test 3: Multiple Goals with Different Priorities
```http
GET /api/exerciseplans?username=newuser1&weightLoss=9&muscleBuilding=3&endurance=5&health=7
```
Expected: Plan that prioritizes weight loss while maintaining balance with other goals

## Response Format Validation

### Weekly Exercise Plan Response Format
```json
{
    "id": "string",
    "userId": "string",
    "startDate": "date",
    "dailyExercises": {
        "date": [
            {
                "id": "string",
                "name": "string",
                "caloriesBurned": "number",
                "duration": "number",
                "parameters": {
                    "suitability_weight_loss": "number",
                    "suitability_muscle_gain": "number",
                    "suitability_endurance_building": "number",
                    "suitability_healthy_lifestyle": "number"
                }
            }
        ]
    },
    "totalCaloriesBurned": "number",
    "totalDuration": "number"
}
```

## Testing Instructions

1. For each test case:
   - Send the request to the appropriate endpoint
   - Verify the response status code is 200
   - Validate the response format matches the expected structure
   - Check that the total weekly calories match the target
   - Verify that each day has appropriate exercises
   - Ensure exercise variety across the week
   - Validate that exercises match user's goals and preferences

2. For edge cases:
   - Verify appropriate error handling
   - Check that the response provides meaningful error messages
   - Ensure the system handles extreme inputs gracefully

3. For response validation:
   - Check that all required fields are present
   - Verify that numerical values are within expected ranges
   - Ensure suitability scores are on the 1-5 scale
   - Validate that daily exercises are properly distributed

## Notes

- All preference values should be between 0-10
- Calorie targets should be positive numbers
- The system should handle missing or invalid parameters gracefully
- Response times should be reasonable (under 2 seconds for most requests)
- The generated weekly plans should be deterministic for the same inputs
- Exercise variety should be maintained across the week
- Rest days should be appropriately scheduled based on user's activity level

## Meal Plan Generation Tests

### Test Case 1: Basic Meal Plan Generation
```http
POST /api/meal/plan
Content-Type: application/json

{
    "username": "newuser1",
    "targetCalories": 2000,
    "weightLossPreference": 8,
    "muscleBuildingPreference": 0,
    "generalHealthPreference": 0
}
```
Expected: Plan with meals optimized for weight loss, total calories close to 2000

### Test Case 2: Muscle Building Meal Plan
```http
POST /api/meal/plan
Content-Type: application/json

{
    "username": "newuser1",
    "targetCalories": 2500,
    "weightLossPreference": 0,
    "muscleBuildingPreference": 8,
    "generalHealthPreference": 0
}
```
Expected: Plan with meals optimized for muscle building

### Test Case 3: Health-Focused Meal Plan
```http
POST /api/meal/plan
Content-Type: application/json

{
    "username": "newuser1",
    "targetCalories": 1800,
    "weightLossPreference": 0,
    "muscleBuildingPreference": 0,
    "generalHealthPreference": 8
}
```
Expected: Plan with meals optimized for general health

### Test Case 4: Combined Goals Meal Plan
```http
POST /api/meal/plan
Content-Type: application/json

{
    "username": "newuser1",
    "targetCalories": 2200,
    "weightLossPreference": 6,
    "muscleBuildingPreference": 6,
    "generalHealthPreference": 6
}
```
Expected: Balanced plan considering all dietary goals

## Edge Cases

### Test Case 1: Very Low Calorie Target
```http
POST /api/exercise/plan
Content-Type: application/json

{
    "username": "newuser1",
    "targetCaloriesBurned": 100,
    "weightLossPreference": 8,
    "muscleBuildingPreference": 0,
    "endurancePreference": 0,
    "healthPreference": 0
}
```
Expected: Should return a minimal exercise plan or appropriate error

### Test Case 2: Very High Calorie Target
```http
POST /api/meal/plan
Content-Type: application/json

{
    "username": "newuser1",
    "targetCalories": 5000,
    "weightLossPreference": 8,
    "muscleBuildingPreference": 0,
    "generalHealthPreference": 0
}
```
Expected: Should return a comprehensive meal plan or appropriate error

### Test Case 3: Zero Preferences
```http
POST /api/exercise/plan
Content-Type: application/json

{
    "username": "newuser1",
    "targetCaloriesBurned": 500,
    "weightLossPreference": 0,
    "muscleBuildingPreference": 0,
    "endurancePreference": 0,
    "healthPreference": 0
}
```
Expected: Should return a balanced plan or appropriate error

## Response Format Validation

### Exercise Plan Response Format
```json
{
    "exerciseSequence": [
        {
            "name": "string",
            "caloriesBurned": "number",
            "duration": "number",
            "parameters": {
                "suitability_weight_loss": "number",
                "suitability_muscle_gain": "number",
                "suitability_endurance_building": "number",
                "suitability_healthy_lifestyle": "number"
            }
        }
    ],
    "totalCaloriesBurned": "number",
    "totalDuration": "number"
}
```

### Meal Plan Response Format
```json
{
    "mealSequence": [
        {
            "name": "string",
            "type": "string",
            "calories": "number",
            "prepTime": "number",
            "parameters": {
                "suitability_weight_loss": "number",
                "suitability_muscle_gain": "number",
                "suitability_endurance_building": "number",
                "suitability_healthy_lifestyle": "number"
            }
        }
    ],
    "totalCalories": "number",
    "totalPrepTime": "number"
}
```

## Testing Instructions

1. For each test case:
   - Send the request to the appropriate endpoint
   - Verify the response status code is 200
   - Validate the response format matches the expected structure
   - Check that the total calories match the target (within 50 calories)
   - Verify that the sequence items have appropriate suitability scores based on preferences

2. For edge cases:
   - Verify appropriate error handling
   - Check that the response provides meaningful error messages
   - Ensure the system doesn't crash with extreme inputs

3. For response validation:
   - Check that all required fields are present
   - Verify that numerical values are within expected ranges
   - Ensure suitability scores are on the 1-5 scale
   - Validate that sequences are properly ordered

## Notes

- All preference values should be between 0-10
- Calorie targets should be positive numbers
- The system should handle missing or invalid parameters gracefully
- Response times should be reasonable (under 2 seconds for most requests)
- The generated plans should be deterministic for the same inputs 