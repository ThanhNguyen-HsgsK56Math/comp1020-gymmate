#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
MAGENTA='\033[0;35m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

BASE_URL="http://localhost:8080"

echo "GymMate Plan Generation API Testing Script"
echo "========================================="
echo

# Check if server is running
echo "Checking if server is running..."
if curl -s "$BASE_URL/api/users" > /dev/null; then
    echo -e "${GREEN}Server is running!${NC}"
else
    echo -e "${RED}Server is not running! Please start the server and try again.${NC}"
    exit 1
fi

run_plan_test() {
    local username=$1
    local email=$2
    local goal=$3
    local goal_description=$4
    
    echo
    echo -e "${BLUE}Testing plan generation for user with goal: ${goal_description}${NC}"
    echo "Creating test user..."
    
    REGISTER_RESPONSE=$(curl -s -X POST "$BASE_URL/api/users/register" \
      -H "Content-Type: application/json" \
      -d "{
        \"username\": \"${username}\",
        \"password\": \"password123\",
        \"email\": \"${email}\",
        \"goal\": \"${goal}\"
      }")
    
    if [[ $REGISTER_RESPONSE == *"$username"* ]]; then
        echo -e "${GREEN}✓ Test user created successfully!${NC}"
        # Extract user ID from the response
        USER_ID=$(echo $REGISTER_RESPONSE | grep -o '"id":"[^"]*' | cut -d'"' -f4)
        echo "User ID: $USER_ID"
        
        # Display user details
        echo -e "${MAGENTA}User Information:${NC}"
        echo -e "Username: ${username}"
        echo -e "Email: ${email}"
        echo -e "Goal: ${goal_description}"
        echo
        
        # Generate a plan for the user
        echo "Generating daily plan..."
        PLAN_RESPONSE=$(curl -s -X GET "$BASE_URL/api/dailyplans?userId=$USER_ID" \
          -H "Content-Type: application/json")
        
        # Save response to a temporary file for easier processing
        echo "$PLAN_RESPONSE" > /tmp/plan_response_${username}.json
        
        if [[ $PLAN_RESPONSE == *"exerciseSequence"* && $PLAN_RESPONSE == *"totalCalories"* ]]; then
            echo -e "${GREEN}✓ Plan generation successful!${NC}"
            
            # Extract and display the plan details
            TOTAL_CALORIES=$(echo $PLAN_RESPONSE | grep -o '"totalCalories":[0-9]*' | cut -d':' -f2)
            TOTAL_TIME=$(echo $PLAN_RESPONSE | grep -o '"totalTime":[0-9]*' | cut -d':' -f2)
            
            echo -e "${YELLOW}Plan Summary:${NC}"
            echo "Total Calories: $TOTAL_CALORIES"
            echo "Total Time (minutes): $TOTAL_TIME"
            
            # Calculate if goal was met
            if [[ $goal == burn_* ]]; then
                GOAL_CALORIES=$(echo $goal | sed 's/burn_\([0-9]*\)_calories/\1/')
                if [[ $TOTAL_CALORIES -ge $GOAL_CALORIES ]]; then
                    echo -e "${GREEN}Goal Status: Met (${TOTAL_CALORIES}/${GOAL_CALORIES} calories)${NC}"
                else
                    echo -e "${RED}Goal Status: Not Met (${TOTAL_CALORIES}/${GOAL_CALORIES} calories)${NC}"
                fi
            else
                echo "Goal Status: N/A (Non-calorie goal)"
            fi
            
            # Display enhanced exercise details
            echo -e "${YELLOW}Exercise Sequence:${NC}"
            
            # Extract exercises from the JSON response using pattern matching
            # Extract the entire exerciseSequence array
            EXERCISE_JSON=$(echo "$PLAN_RESPONSE" | sed 's/.*"exerciseSequence":\[\(.*\)\],.*/\1/')
            
            # Split into individual exercise objects and process each
            echo "$EXERCISE_JSON" | sed 's/},{/}\n{/g' | while read -r exercise; do
                # For each exercise, extract the name, description, calories, and time
                NAME=$(echo "$exercise" | grep -o '"name":"[^"]*' | cut -d'"' -f4)
                DESC=$(echo "$exercise" | grep -o '"description":"[^"]*' | cut -d'"' -f4)
                CALORIES=$(echo "$exercise" | grep -o '"caloriesBurned":[0-9]*' | cut -d':' -f2)
                TIME=$(echo "$exercise" | grep -o '"timeRequired":[0-9]*' | cut -d':' -f2)
                
                if [ -n "$NAME" ]; then
                    echo -e "${CYAN}Exercise:${NC} $NAME"
                    echo -e "${CYAN}Description:${NC} $DESC"
                    echo -e "${CYAN}Calories:${NC} $CALORIES"
                    echo -e "${CYAN}Time Required:${NC} $TIME minutes"
                    echo "----------------------------------------"
                fi
            done
            
            # If no exercises could be parsed, show a message
            if [ -z "$EXERCISE_JSON" ] || [ "$EXERCISE_JSON" = "{}" ]; then
                echo "  No exercises in sequence"
            fi
            
            # Return success
            return 0
        else
            echo -e "${RED}✗ Plan generation failed!${NC}"
            echo "Response: $PLAN_RESPONSE"
            # Return failure
            return 1
        fi
    else
        echo -e "${RED}✗ Failed to create test user!${NC}"
        echo "Response: $REGISTER_RESPONSE"
        # Return failure
        return 1
    fi
}

# Test with different user goals
echo "Running multiple plan generation tests..."

# Test 1: Standard calorie burning goal (500 calories)
TEST_USERNAME1="plantest_500cal_$(date +%s)" # Add timestamp to make username unique
TEST_EMAIL1="plantest_500cal_$(date +%s)@example.com"
run_plan_test "$TEST_USERNAME1" "$TEST_EMAIL1" "burn_500_calories" "Burn 500 calories"
TEST1_RESULT=$?

# Test 2: Higher calorie burning goal (800 calories)
TEST_USERNAME2="plantest_800cal_$(date +%s)" # Add timestamp to make username unique
TEST_EMAIL2="plantest_800cal_$(date +%s)@example.com"
run_plan_test "$TEST_USERNAME2" "$TEST_EMAIL2" "burn_800_calories" "Burn 800 calories"
TEST2_RESULT=$?

# Test 3: Lower calorie burning goal (300 calories)
TEST_USERNAME3="plantest_300cal_$(date +%s)" # Add timestamp to make username unique
TEST_EMAIL3="plantest_300cal_$(date +%s)@example.com"
run_plan_test "$TEST_USERNAME3" "$TEST_EMAIL3" "burn_300_calories" "Burn 300 calories"
TEST3_RESULT=$?

# Test 4: Invalid user ID test
echo
echo "Test 4: Generate a plan with invalid user ID"
echo "----------------------------------------"
INVALID_PLAN_RESPONSE=$(curl -s -X GET "$BASE_URL/api/dailyplans?userId=invalid_user_id" \
  -H "Content-Type: application/json")

if [[ $INVALID_PLAN_RESPONSE == *"error"* || $INVALID_PLAN_RESPONSE == *"not found"* ]]; then
    echo -e "${GREEN}✓ Test passed! Plan generation correctly failed with invalid user ID.${NC}"
    echo "Response: $INVALID_PLAN_RESPONSE"
    TEST4_RESULT=0
else
    echo -e "${RED}✗ Test failed! Plan generation should have failed with an invalid user ID.${NC}"
    echo "Response: $INVALID_PLAN_RESPONSE"
    TEST4_RESULT=1
fi

# Summary of all tests
echo
echo "========================================="
echo -e "${BLUE}Test Results Summary:${NC}"
echo "----------------------------------------"

[[ $TEST1_RESULT -eq 0 ]] && echo -e "${GREEN}✓ Test 1 (500 calories goal): Passed${NC}" || echo -e "${RED}✗ Test 1 (500 calories goal): Failed${NC}"
[[ $TEST2_RESULT -eq 0 ]] && echo -e "${GREEN}✓ Test 2 (800 calories goal): Passed${NC}" || echo -e "${RED}✗ Test 2 (800 calories goal): Failed${NC}"
[[ $TEST3_RESULT -eq 0 ]] && echo -e "${GREEN}✓ Test 3 (300 calories goal): Passed${NC}" || echo -e "${RED}✗ Test 3 (300 calories goal): Failed${NC}"
[[ $TEST4_RESULT -eq 0 ]] && echo -e "${GREEN}✓ Test 4 (Invalid user ID): Passed${NC}" || echo -e "${RED}✗ Test 4 (Invalid user ID): Failed${NC}"

# Calculate overall status
TESTS_PASSED=$(( 4 - TEST1_RESULT - TEST2_RESULT - TEST3_RESULT - TEST4_RESULT ))
echo
echo -e "${MAGENTA}Summary: ${TESTS_PASSED}/4 tests passed${NC}"
echo "=========================================" 