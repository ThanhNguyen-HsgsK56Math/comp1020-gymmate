# API Testing Guide

Ensure you're in a Linux terminal environment. Open 2 terminal windows and run "cd backend" in both windows.

## Terminal 1: System Startup
- Run "mvn spring-boot:run" to initialize the system
- Wait for about 1-1.5 minutes until you see "Started GymmateApplication"
- Then proceed to Terminal 2

## Terminal 2: API Testing

### 1. User Registration
Test 1: Create a user with one goal
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser2",
    "password": "password123",
    "email": "new@example.com",
    "goal": ["weight_loss"],
    "activityLevel": "moderately_active",
    "gender": "male",
    "weight": 70.5,
    "height": 175.0,
    "age": 25
  }'
```
Expected: Returns user information and adds to MongoDB

Test 2: Create a user with two goals
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser3",
    "password": "password123",
    "email": "new3@example.com",
    "goal": ["weight_loss", "muscle_gain"],
    "activityLevel": "lightly_active",
    "gender": "female",
    "weight": 60.0,
    "height": 165.0,
    "age": 28
  }'
```
Expected: Returns user information and adds to MongoDB

Test 3: Create a user with more than two goals (should fail)
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser4",
    "password": "password123",
    "email": "new4@example.com",
    "goal": ["weight_loss", "muscle_gain", "endurance_building"],
    "activityLevel": "moderately_active",
    "gender": "male",
    "weight": 80.0,
    "height": 180.0,
    "age": 30
  }'
```
Expected: "Maximum of 2 goals allowed"

Test 4: Create a user with duplicate username AND password
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "password123",
    "email": "new3@example.com",
    "goal": ["weight_loss", "muscle_gain"],
    "activityLevel": "lightly_active",
    "gender": "female",
    "weight": 65.0,
    "height": 170.0,
    "age": 27
  }'
```
Expected: "User with this username and password already exists"
Note: If either username or password is different, a new user will be created

Test 5: Create a user with invalid goal
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser5",
    "password": "password123",
    "email": "new5@example.com",
    "goal": ["invalid_goal"],
    "activityLevel": "moderately_active",
    "gender": "male",
    "weight": 75.0,
    "height": 178.0,
    "age": 32
  }'
```
Expected: "Invalid goal. Must be one of: weight_loss, muscle_gain, endurance_building, healthy_lifestyle"

Test 6: Create a user with invalid activity level
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser6",
    "password": "password123",
    "email": "new6@example.com",
    "goal": ["weight_loss"],
    "activityLevel": "invalid_level",
    "gender": "male",
    "weight": 72.0,
    "height": 176.0,
    "age": 29
  }'
```
Expected: "Invalid activity level. Must be one of: sedentary, lightly_active, moderately_active, very_active, super_active"

Test 7: Create a user with missing activity level
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser7",
    "password": "password123",
    "email": "new7@example.com",
    "goal": ["weight_loss"],
    "gender": "male",
    "weight": 68.0,
    "height": 173.0,
    "age": 26
  }'
```
Expected: "Please put in your activityLevel with one of the following: sedentary, lightly_active, moderately_active, very_active, super_active"

Test 8: Create a user with missing gender
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser8",
    "password": "password123",
    "email": "new8@example.com",
    "goal": ["weight_loss"],
    "activityLevel": "moderately_active",
    "weight": 70.0,
    "height": 175.0,
    "age": 31
  }'
```
Expected: "Please specify your gender (male/female/other)"

Test 9: Create a user with invalid gender
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser9",
    "password": "password123",
    "email": "new9@example.com",
    "goal": ["weight_loss"],
    "activityLevel": "moderately_active",
    "gender": "invalid_gender",
    "weight": 73.0,
    "height": 177.0,
    "age": 33
  }'
```
Expected: "Invalid gender. Must be one of: male, female, other"

Test 10: Create a user with all valid goals
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser10",
    "password": "password123",
    "email": "new10@example.com",
    "goal": ["endurance_building", "healthy_lifestyle"],
    "activityLevel": "moderately_active",
    "gender": "male",
    "weight": 76.0,
    "height": 179.0,
    "age": 34
  }'
```
Expected: Returns user information and adds to MongoDB

### 2. User Login
Test 1: Valid login
```bash
curl -X POST "http://localhost:8080/api/users/login?username=newuser2&password=password123"
```
Expected: "Login successful"

Test 2: Invalid credentials
```bash
curl -X POST "http://localhost:8080/api/users/login?username=newuser2&password=wrongpassword"
```
Expected: "Invalid credentials"

### 3. Update Activity Level
Test 1: Update with valid activity level
```bash
curl -X PUT "http://localhost:8080/api/users/{userId}/activity-level?activityLevel=very_active"
```
Expected: Returns updated user information

Test 2: Update with invalid activity level
```bash
curl -X PUT "http://localhost:8080/api/users/{userId}/activity-level?activityLevel=invalid_level"
```
Expected: "Invalid activity level. Must be one of: sedentary, lightly_active, moderately_active, very_active, super_active"

Test 3: Update non-existent user
```bash
curl -X PUT "http://localhost:8080/api/users/nonexistentid/activity-level?activityLevel=moderately_active"
```
Expected: 404 Not Found

### 4. Get Exercise Plan
```bash
curl -X GET "http://localhost:8080/api/exerciseplans?userId=682a23b91cd96276a21204f9" \
  -H "Content-Type: application/json"
```
Note: Replace USER_ID with an actual user ID from your database
Expected: Returns exercise plan or "User not found" if ID is invalid

### 5. Get Meal Plan
Test 1: Single goal meal plan
```bash
curl -X GET "http://localhost:8080/api/mealplans?userId=USER_ID&targetCalories=2000&weightLoss=5&muscleBuilding=5&generalHealth=5" \
  -H "Content-Type: application/json"
```

Test 2: Two goals meal plan
```bash
curl -X GET "http://localhost:8080/api/mealplans?userId=USER_ID&targetCalories=1800&weightLoss=8&muscleBuilding=3&generalHealth=4" \
  -H "Content-Type: application/json"
```

Test 3: Two goals meal plan (different combination)
```bash
curl -X GET "http://localhost:8080/api/mealplans?userId=USER_ID&targetCalories=2500&weightLoss=2&muscleBuilding=9&generalHealth=4" \
  -H "Content-Type: application/json"
```

Test 4: Single goal meal plan (different goal)
```bash
curl -X GET "http://localhost:8080/api/mealplans?userId=USER_ID&targetCalories=2200&weightLoss=4&muscleBuilding=4&generalHealth=8" \
  -H "Content-Type: application/json"
```
Expected: Returns JSON containing meal plan details

### 6. Exercise List
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

## Exercise Plan Generation Tests

### Test Case 1: Basic Exercise Plan Generation
```http
POST /api/exercise/plan
Content-Type: application/json

{
    "targetCaloriesBurned": 500,
    "weightLossPreference": 8,
    "muscleBuildingPreference": 0,
    "endurancePreference": 0,
    "healthPreference": 0
}
```
Expected: Plan with exercises optimized for weight loss, total calories close to 500

### Test Case 2: Multiple Goals Exercise Plan
```http
POST /api/exercise/plan
Content-Type: application/json

{
    "targetCaloriesBurned": 600,
    "weightLossPreference": 7,
    "muscleBuildingPreference": 7,
    "endurancePreference": 0,
    "healthPreference": 0
}
```
Expected: Plan balancing weight loss and muscle building exercises

### Test Case 3: Health-Focused Exercise Plan
```http
POST /api/exercise/plan
Content-Type: application/json

{
    "targetCaloriesBurned": 400,
    "weightLossPreference": 0,
    "muscleBuildingPreference": 0,
    "endurancePreference": 0,
    "healthPreference": 8
}
```
Expected: Plan with exercises optimized for general health

### Test Case 4: Endurance-Focused Exercise Plan
```http
POST /api/exercise/plan
Content-Type: application/json

{
    "targetCaloriesBurned": 700,
    "weightLossPreference": 0,
    "muscleBuildingPreference": 0,
    "endurancePreference": 8,
    "healthPreference": 0
}
```
Expected: Plan with exercises optimized for endurance building

### Test Case 5: All Goals Exercise Plan
```http
POST /api/exercise/plan
Content-Type: application/json

{
    "targetCaloriesBurned": 800,
    "weightLossPreference": 6,
    "muscleBuildingPreference": 6,
    "endurancePreference": 6,
    "healthPreference": 6
}
```
Expected: Balanced plan considering all fitness goals

## Meal Plan Generation Tests

### Test Case 1: Basic Meal Plan Generation
```http
POST /api/meal/plan
Content-Type: application/json

{
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