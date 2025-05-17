#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

BASE_URL="http://localhost:8080"
TEST_USERNAME="testuser_$(date +%s)" # Add timestamp to make username unique
TEST_EMAIL="test_$(date +%s)@example.com"

echo "GymMate Authentication API Testing Script"
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

echo
echo "Running tests with unique username: $TEST_USERNAME"
echo

# Test 1: Register a new user
echo "Test 1: Register a new user"
echo "-------------------------"
REGISTER_RESPONSE=$(curl -s -X POST "$BASE_URL/api/users/register" \
  -H "Content-Type: application/json" \
  -d "{
    \"username\": \"$TEST_USERNAME\",
    \"password\": \"password123\",
    \"email\": \"$TEST_EMAIL\",
    \"goal\": \"burn_500_calories\"
  }")

if [[ $REGISTER_RESPONSE == *"$TEST_USERNAME"* ]]; then
    echo -e "${GREEN}✓ Registration successful!${NC}"
    echo "Response: $REGISTER_RESPONSE"
else
    echo -e "${RED}✗ Registration failed!${NC}"
    echo "Response: $REGISTER_RESPONSE"
fi
echo

# Test 2: Login with correct credentials
echo "Test 2: Login with correct credentials"
echo "------------------------------------"
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/users/login" \
  -H "Content-Type: application/json" \
  -d "{
    \"username\": \"$TEST_USERNAME\",
    \"password\": \"password123\"
  }")

if [[ $LOGIN_RESPONSE == "Login successful" ]]; then
    echo -e "${GREEN}✓ Login successful!${NC}"
else
    echo -e "${RED}✗ Login failed!${NC}"
    echo "Response: $LOGIN_RESPONSE"
fi
echo

# Test 3: Login with incorrect password
echo "Test 3: Login with incorrect password"
echo "-----------------------------------"
INCORRECT_LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/users/login" \
  -H "Content-Type: application/json" \
  -d "{
    \"username\": \"$TEST_USERNAME\",
    \"password\": \"wrongpassword\"
  }")

if [[ $INCORRECT_LOGIN_RESPONSE == "Invalid credentials" ]]; then
    echo -e "${GREEN}✓ Test passed! Login correctly failed with wrong password.${NC}"
else
    echo -e "${RED}✗ Test failed! Login should have failed but didn't.${NC}"
    echo "Response: $INCORRECT_LOGIN_RESPONSE"
fi
echo

# Test 4: Login with non-existent user
echo "Test 4: Login with non-existent user"
echo "----------------------------------"
NONEXISTENT_LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/users/login" \
  -H "Content-Type: application/json" \
  -d "{
    \"username\": \"nonexistentuser_$(date +%s)\",
    \"password\": \"password123\"
  }")

if [[ $NONEXISTENT_LOGIN_RESPONSE == "Invalid credentials" ]]; then
    echo -e "${GREEN}✓ Test passed! Login correctly failed with non-existent user.${NC}"
else
    echo -e "${RED}✗ Test failed! Login should have failed but didn't.${NC}"
    echo "Response: $NONEXISTENT_LOGIN_RESPONSE"
fi
echo

# Test 5: Register with duplicate username
echo "Test 5: Register with duplicate username"
echo "-------------------------------------"
DUPLICATE_REGISTER_RESPONSE=$(curl -s -X POST "$BASE_URL/api/users/register" \
  -H "Content-Type: application/json" \
  -d "{
    \"username\": \"$TEST_USERNAME\",
    \"password\": \"different123\",
    \"email\": \"different@example.com\",
    \"goal\": \"improve_sleep\"
  }")

# Note: The expected response depends on how your API handles duplicates
# Adjust this condition based on your implementation
if [[ $DUPLICATE_REGISTER_RESPONSE == *"error"* || $DUPLICATE_REGISTER_RESPONSE == *"already exists"* || $DUPLICATE_REGISTER_RESPONSE == *"duplicate"* ]]; then
    echo -e "${GREEN}✓ Test passed! Registration correctly failed with duplicate username.${NC}"
else
    echo -e "${RED}✗ Test might have failed! Check if your API properly handles duplicate usernames.${NC}"
    echo "Response: $DUPLICATE_REGISTER_RESPONSE"
fi
echo

echo "========================================="
echo "All tests completed!"
echo "----------------------------------------"
echo "Note: This script tests basic functionality."
echo "For more comprehensive testing, use JUnit tests"
echo "or integrate these tests into your CI pipeline." 