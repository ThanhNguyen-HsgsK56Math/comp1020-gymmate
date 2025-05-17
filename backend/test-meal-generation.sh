#!/bin/bash

# Test meal plan generation endpoint
echo "Testing meal plan generation..."

# Generate meal plan for test user
response=$(curl -s -X POST "http://localhost:8080/api/meal-plans/generate/test-user-id")

# Check if response contains expected fields
if echo "$response" | grep -q "totalCalories" && \
   echo "$response" | grep -q "mealSequence"; then
    echo "✓ Meal plan generated successfully"
    echo "Response: $response"
else
    echo "✗ Meal plan generation failed"
    echo "Response: $response"
    exit 1
fi

# Make script executable
chmod +x test-meal-generation.sh

echo "All tests passed!"
