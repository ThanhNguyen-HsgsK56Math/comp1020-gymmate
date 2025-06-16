// Dashboard JavaScript
$(document).ready(function(){
    
	document.getElementById("user-name").innerHTML = readCookie();
    // Prevent form submission from reloading the page
    $("#profile-update").on("submit", function(e) {
        e.preventDefault();
        const form = e.target;
        if (!form.checkValidity()) {
            form.reportValidity();
            return;
        }
        generatePlan();
    });
    
    $("#gen-btn").click((e) => {
        e.preventDefault();
        const form = document.getElementById('profile-update');
        if (!form.checkValidity()) {
            form.reportValidity();
            return;
        }
        generatePlan();
    });
})

// Function to generate plan
async function generatePlan() {
    console.log("Generating plan...");
    
    function getFromDropDown(id) {
        const e = document.getElementById(id);
        if (!e) {
            console.error(`Element with id '${id}' not found in the DOM`);
            return null;
        }
        if (!e.value) {
            console.error(`Element with id '${id}' has no value selected`);
            return null;
        }
        return e.value; // Return the value directly since our select options already have the correct backend values
    }

    try {
        const username = readCookie();
        if (!username) {
            window.alert("Username not found. Please sign in again.");
            return;
        }

        // Get form elements
        const form = document.getElementById('profile-update');
        if (!form) {
            console.error('Profile form not found');
            window.alert('Profile form not found. Please refresh the page and try again.');
            return;
        }

        // Validate form
        if (!form.checkValidity()) {
            form.reportValidity();
            return;
        }

        // Get form values
        const weight = form.querySelector('#weight')?.value;
        const height = form.querySelector('#height')?.value;
        const age = form.querySelector('#age')?.value;
        const goal = getFromDropDown('goal');
        const activity = getFromDropDown('activity');
        const gender = getFromDropDown('gender');

        // Validate all required fields
        if (!weight || !height || !age || !goal || !activity || !gender) {
            const missingFields = [];
            if (!weight) missingFields.push('weight');
            if (!height) missingFields.push('height');
            if (!age) missingFields.push('age');
            if (!goal) missingFields.push('goal');
            if (!activity) missingFields.push('activity level');
            if (!gender) missingFields.push('gender');
            window.alert(`Please fill in all required fields: ${missingFields.join(', ')}`);
            return;
        }

        // Store username in localStorage for later use
        localStorage.setItem('username', username);
        
        const requestBody = {
            goal: [goal], // Use the goal value directly since it's already in the correct format
            activityLevel: activity, // Use the activity value directly since it's already in the correct format
            gender: gender,
            weight: Number(weight),
            height: Number(height),
            age: Number(age),
        };
        
        console.log("Sending request with body:", requestBody);
        await completeProfile(username, requestBody);

    } catch (error) {
        console.error("Error generating plan:", error);
        if (error.message.includes("Failed to fetch")) {
            window.alert("Cannot connect to the server. Please make sure the backend server is running at http://localhost:8080");
        } else {
            window.alert("Error generating plan: " + error.message);
        }
    }
}

// Function to display the exercise plan
function displayExercisePlan(plan) {
    const container = document.getElementById('workout-plan');
    if (!container) return;

    // Create calendar container
    const calendarContainer = document.createElement('div');
    calendarContainer.className = 'weekly-calendar';
    calendarContainer.style.width = '100%';
    calendarContainer.style.padding = '20px';

    // Add header
    const header = document.createElement('div');
    header.className = 'calendar-header';
    header.innerHTML = `
        <h3>Weekly Workout Schedule</h3>
        <div class="week-navigation">
            <button class="nav-button" id="prevWeek">
                <i class="fas fa-chevron-left"></i>
            </button>
            <span class="current-week">Week of ${formatDate(new Date())}</span>
            <button class="nav-button" id="nextWeek">
                <i class="fas fa-chevron-right"></i>
            </button>
        </div>
    `;
    calendarContainer.appendChild(header);

    // Add weekly summary
    const weeklySummary = document.createElement('div');
    weeklySummary.className = 'weekly-summary';
    
    // Calculate weekly totals
    let totalWeeklyCalories = 0;
    let totalWeeklyDuration = 0;
    let totalWorkouts = 0;
    
    Object.values(plan.dailyExercises).forEach(dayExercises => {
        if (dayExercises && dayExercises.length > 0) {
            totalWorkouts++;
            dayExercises.forEach(exercise => {
                // Calculate calories based on exercise type and duration
                let caloriesPerMinute = 0;
                const exerciseType = exercise.type?.toLowerCase() || '';
                const duration = exercise.duration || 0;
                
                switch(exerciseType) {
                    case 'cardio':
                        caloriesPerMinute = 10; // Average calories burned per minute for cardio
                        break;
                    case 'strength':
                        caloriesPerMinute = 7; // Average calories burned per minute for strength training
                        break;
                    case 'hiit':
                        caloriesPerMinute = 12; // Average calories burned per minute for HIIT
                        break;
                    case 'bodyweight':
                        caloriesPerMinute = 8; // Average calories burned per minute for bodyweight exercises
                        break;
                    default:
                        caloriesPerMinute = 6; // Default calories burned per minute
                }
                
                // Calculate total calories for this exercise
                const exerciseCalories = Math.round(caloriesPerMinute * duration);
                totalWeeklyCalories += exerciseCalories;
                totalWeeklyDuration += duration;
            });
        }
    });

    weeklySummary.innerHTML = `
        <div class="summary-item">
            <i class="fas fa-fire"></i>
            <span>Weekly Calories: ${totalWeeklyCalories}</span>
        </div>
        <div class="summary-item">
            <i class="fas fa-clock"></i>
            <span>Total Duration: ${totalWeeklyDuration} minutes</span>
        </div>
        <div class="summary-item">
            <i class="fas fa-dumbbell"></i>
            <span>Workout Days: ${totalWorkouts-1}</span>
        </div>
    `;
    calendarContainer.appendChild(weeklySummary);

    // Create calendar grid
    const grid = document.createElement('div');
    grid.style.display = 'flex';
    grid.style.flexDirection = 'row';
    grid.style.justifyContent = 'space-between';
    grid.style.gap = '10px';
    grid.style.width = '100%';
    grid.style.overflowX = 'auto';

    // Add each day's exercises
    const daysOfWeek = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];
    
    daysOfWeek.forEach(day => {
        const dayColumn = document.createElement('div');
        dayColumn.style.flex = '1';
        dayColumn.style.minWidth = '0';
        dayColumn.style.background = '#f8f9fa';
        dayColumn.style.borderRadius = '8px';
        dayColumn.style.padding = '10px';
        dayColumn.style.minHeight = '200px';
        
        // Add day header
        const dayHeader = document.createElement('div');
        dayHeader.style.fontWeight = 'bold';
        dayHeader.style.textAlign = 'center';
        dayHeader.style.padding = '5px';
        dayHeader.style.borderBottom = '1px solid #dee2e6';
        dayHeader.style.marginBottom = '10px';
        dayHeader.textContent = day;
        dayColumn.appendChild(dayHeader);

        // Add exercise slot
        const exerciseSlot = document.createElement('div');
        exerciseSlot.style.minHeight = '100px';

        // Get exercises for this day
        const dayExercises = plan.dailyExercises[day] || [];
        if (day === 'Sunday' || dayExercises.length === 0) {
            exerciseSlot.innerHTML = `
                <div style="background-color: #f5f5f5; border-left: 3px solid #ed563b; text-align: center; padding: 15px; border-radius: 4px;">
                    <i class="fa fa-bed" style="color: #ed563b;"></i>
                    <span>Rest</span>
                </div>
            `;
        } else {
            // Group exercises by type
            const exerciseGroups = {
                cardio: [],
                strength: [],
                flexibility: [],
                balance: [],
                other: []
            };

            dayExercises.forEach(exercise => {
                const type = exercise.type?.toLowerCase() || 'other';
                if (exerciseGroups[type]) {
                    exerciseGroups[type].push(exercise);
                } else {
                    exerciseGroups.other.push(exercise);
                }
            });

            // Add each exercise group
            Object.entries(exerciseGroups).forEach(([type, exercises]) => {
                if (exercises.length > 0) {
                    const groupContainer = document.createElement('div');
                    groupContainer.style.marginBottom = '10px';
                    
                    exercises.forEach(exercise => {
                        const exerciseItem = document.createElement('div');
                        let bgColor;
                        let exerciseIcon;
                        
                        // Determine icon based on exercise name
                        const exerciseName = exercise.name.toLowerCase();
                        if (exerciseName.includes('run') || exerciseName.includes('jog') || exerciseName.includes('sprint')) {
                            exerciseIcon = 'fa-running';
                        } else if (exerciseName.includes('bike') || exerciseName.includes('cycle') || exerciseName.includes('cycling')) {
                            exerciseIcon = 'fa-bicycle';
                        } else if (exerciseName.includes('swim')) {
                            exerciseIcon = 'fa-swimmer';
                        } else if (exerciseName.includes('weight') || exerciseName.includes('lift') || exerciseName.includes('press') || 
                                 exerciseName.includes('bench') || exerciseName.includes('shoulder') || exerciseName.includes('clean') ||
                                 exerciseName.includes('cable') || exerciseName.includes('fly') || exerciseName.includes('row') ||
                                 exerciseName.includes('curl') || exerciseName.includes('extension') || exerciseName.includes('raise')) {
                            exerciseIcon = 'fa-dumbbell';
                        } else if (exerciseName.includes('yoga') || exerciseName.includes('stretch') || exerciseName.includes('flex')) {
                            exerciseIcon = 'fa-child';
                        } else if (exerciseName.includes('balance') || exerciseName.includes('core') || exerciseName.includes('stability') ||
                                 exerciseName.includes('plank') || exerciseName.includes('twist') || exerciseName.includes('crunch') ||
                                 exerciseName.includes('situp') || exerciseName.includes('leg raise')) {
                            exerciseIcon = 'fa-bullseye';
                        } else if (exerciseName.includes('hiit') || exerciseName.includes('burpee') || exerciseName.includes('mountain') ||
                                 exerciseName.includes('box') || exerciseName.includes('battle') || exerciseName.includes('rope') ||
                                 exerciseName.includes('jump') || exerciseName.includes('sprint')) {
                            exerciseIcon = 'fa-bolt';
                        } else if (exerciseName.includes('push') || exerciseName.includes('pull') || exerciseName.includes('up') ||
                                 exerciseName.includes('dip')) {
                            exerciseIcon = 'fa-user';
                        } else if (exerciseName.includes('ball') || exerciseName.includes('medicine')) {
                            exerciseIcon = 'fa-basketball-ball';
                        } else if (exerciseName.includes('sled') || exerciseName.includes('carry')) {
                            exerciseIcon = 'fa-weight';
                        } else {
                            // Default icons based on type
                            switch(type) {
                                case 'cardio':
                                    exerciseIcon = 'fa-running';
                                    break;
                                case 'strength':
                                    exerciseIcon = 'fa-dumbbell';
                                    break;
                                case 'flexibility':
                                    exerciseIcon = 'fa-child';
                                    break;
                                case 'balance':
                                    exerciseIcon = 'fa-bullseye';
                                    break;
                                case 'hiit':
                                    exerciseIcon = 'fa-bolt';
                                    break;
                                case 'bodyweight':
                                    exerciseIcon = 'fa-user';
                                    break;
                                default:
                                    exerciseIcon = 'fa-running';
                            }
                        }

                        switch(type) {
                            case 'cardio':
                                bgColor = '#ffebee';
                                break;
                            case 'strength':
                                bgColor = '#e8f5e9';
                                break;
                            case 'flexibility':
                                bgColor = '#e3f2fd';
                                break;
                            case 'balance':
                                bgColor = '#fff3e0';
                                break;
                            default:
                                bgColor = '#f5f5f5';
                        }
                        
                        exerciseItem.style.padding = '8px';
                        exerciseItem.style.borderRadius = '4px';
                        exerciseItem.style.marginBottom = '5px';
                        exerciseItem.style.fontSize = '0.9em';
                        exerciseItem.style.backgroundColor = bgColor;
                        exerciseItem.style.borderLeft = '3px solid #ed563b';
                        
                        exerciseItem.innerHTML = `
                            <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 5px;">
                                <i class="fa ${exerciseIcon}" style="color: #ed563b; font-size: 1.1em;"></i>
                                <span style="font-weight: 500; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">${exercise.name}</span>
                            </div>
                            <div style="display: flex; gap: 10px; font-size: 0.85em; color: #666; flex-wrap: wrap;">
                                ${exercise.duration ? `<span style="display: flex; align-items: center; gap: 3px; white-space: nowrap;"><i class="fa fa-clock" style="color: #1e90ff;"></i>${exercise.duration}m</span>` : ''}
                                ${exercise.sets ? `<span style="display: flex; align-items: center; gap: 3px; white-space: nowrap;"><i class="fa fa-dumbbell" style="color: #ed563b;"></i>${exercise.sets}×${exercise.reps}</span>` : ''}
                                ${exercise.calories ? `<span style="display: flex; align-items: center; gap: 3px; white-space: nowrap;"><i class="fa fa-fire" style="color: #ed563b;"></i>${exercise.calories}</span>` : ''}
                            </div>
                        `;
                        groupContainer.appendChild(exerciseItem);
                    });
                    
                    exerciseSlot.appendChild(groupContainer);
                }
            });
        }

        dayColumn.appendChild(exerciseSlot);
        grid.appendChild(dayColumn);
    });

    calendarContainer.appendChild(grid);
    container.innerHTML = '';
    container.appendChild(calendarContainer);

    // Add legend
    const legend = document.createElement('div');
    legend.style.display = 'flex';
    legend.style.justifyContent = 'center';
    legend.style.gap = '20px';
    legend.style.marginTop = '20px';
    legend.style.padding = '10px';
    legend.style.background = '#f8f9fa';
    legend.style.borderRadius = '8px';
    legend.style.flexWrap = 'wrap';
    
    legend.innerHTML = `
        <div style="display: flex; align-items: center; gap: 5px;">
            <span style="width: 12px; height: 12px; border-radius: 2px; background-color: #f44336;"></span>
            <span>Cardio</span>
        </div>
        <div style="display: flex; align-items: center; gap: 5px;">
            <span style="width: 12px; height: 12px; border-radius: 2px; background-color: #4caf50;"></span>
            <span>Strength</span>
        </div>
        <div style="display: flex; align-items: center; gap: 5px;">
            <span style="width: 12px; height: 12px; border-radius: 2px; background-color: #2196f3;"></span>
            <span>Flexibility</span>
        </div>
        <div style="display: flex; align-items: center; gap: 5px;">
            <span style="width: 12px; height: 12px; border-radius: 2px; background-color: #ff9800;"></span>
            <span>Balance</span>
        </div>
        <div style="display: flex; align-items: center; gap: 5px;">
            <span style="width: 12px; height: 12px; border-radius: 2px; background-color: #9e9e9e;"></span>
            <span>Rest Day</span>
        </div>
    `;
    calendarContainer.appendChild(legend);
}

// Helper function to get exercise icon based on type
function getExerciseIcon(type) {
    switch (type?.toLowerCase()) {
        case 'cardio':
            return 'fa-heartbeat';
        case 'strength':
            return 'fa-dumbbell';
        case 'flexibility':
            return 'fa-child';
        case 'balance':
            return 'fa-balance-scale';
        default:
            return 'fa-running';
    }
}

// Function to display the meal plan
function displayMealPlan(plan) {
    console.log("Raw meal plan data:", JSON.stringify(plan, null, 2));
    
    const container = document.getElementById('meal-plan');
    if (!container) {
        console.error('Meal plan container not found');
        return;
    }

    // Clear existing content
    container.innerHTML = '';

    // Check for valid plan data
    if (!plan || !plan.dailyMeals) {
        console.error("Invalid meal plan data:", plan);
        container.innerHTML = `
            <div style="text-align: center; padding: 20px;">
                <i class="fa fa-exclamation-circle" style="color: #ed563b; font-size: 2em;"></i>
                <p>No meal plan data available. Please try generating a new plan.</p>
            </div>
        `;
        return;
    }

    // Create calendar container
    const calendarContainer = document.createElement('div');
    calendarContainer.className = 'weekly-calendar';
    calendarContainer.style.width = '100%';
    calendarContainer.style.padding = '20px';

    // Add header
    const header = document.createElement('div');
    header.className = 'calendar-header';
    header.innerHTML = `
        <h3>Weekly Meal Schedule</h3>
        <div class="week-navigation">
            <button class="nav-button" id="prevWeek">
                <i class="fas fa-chevron-left"></i>
            </button>
            <span class="current-week">Week of ${formatDate(new Date())}</span>
            <button class="nav-button" id="nextWeek">
                <i class="fas fa-chevron-right"></i>
            </button>
        </div>
    `;
    calendarContainer.appendChild(header);

    // Add weekly summary
    const weeklySummary = document.createElement('div');
    weeklySummary.className = 'weekly-summary';
    
    // Calculate weekly totals
    let totalWeeklyCalories = 0;
    let totalWeeklyPrepTime = 0;
    let totalMeals = 0;
    
    Object.values(plan.dailyMeals).forEach(dayMeals => {
        if (dayMeals && dayMeals.length > 0) {
            dayMeals.forEach(meal => {
                if (meal) {
                    totalWeeklyCalories += meal.calories || 0;
                    totalWeeklyPrepTime += meal.prepTime || 0;
                    totalMeals++;
                }
            });
        }
    });

    weeklySummary.innerHTML = `
        <div class="summary-item">
            <i class="fas fa-fire"></i>
            <span>Weekly Calories: ${totalWeeklyCalories}</span>
        </div>
        <div class="summary-item">
            <i class="fas fa-clock"></i>
            <span>Total Prep Time: ${totalWeeklyPrepTime} minutes</span>
        </div>
        <div class="summary-item">
            <i class="fas fa-utensils"></i>
            <span>Total Meals: ${totalMeals}</span>
        </div>
    `;
    calendarContainer.appendChild(weeklySummary);

    // Create calendar grid
    const grid = document.createElement('div');
    grid.style.display = 'flex';
    grid.style.flexDirection = 'row';
    grid.style.justifyContent = 'space-between';
    grid.style.gap = '10px';
    grid.style.width = '100%';
    grid.style.overflowX = 'auto';

    // Days of the week in order
    const daysOfWeek = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];

    // Create day columns
    daysOfWeek.forEach(day => {
        const dayColumn = document.createElement('div');
        dayColumn.style.flex = '1';
        dayColumn.style.minWidth = '0';
        dayColumn.style.background = '#f8f9fa';
        dayColumn.style.borderRadius = '8px';
        dayColumn.style.padding = '10px';
        dayColumn.style.minHeight = '200px';
        
        // Add day header
        const dayHeader = document.createElement('div');
        dayHeader.style.fontWeight = 'bold';
        dayHeader.style.textAlign = 'center';
        dayHeader.style.padding = '5px';
        dayHeader.style.borderBottom = '1px solid #dee2e6';
        dayHeader.style.marginBottom = '10px';
        dayHeader.textContent = day;
        dayColumn.appendChild(dayHeader);

        // Get meals for this day from the dailyMeals dictionary
        const dayMeals = plan.dailyMeals[day] || [];
        console.log(`Processing meals for ${day}:`, dayMeals);

        // Create sections for each meal type
        const mealTypes = [
            { type: 'breakfast', index: 0, icon: 'fa-coffee' },
            { type: 'lunch', index: 1, icon: 'fa-utensils' },
            { type: 'dinner', index: 2, icon: 'fa-moon' }
        ];

        mealTypes.forEach(({ type, index, icon }) => {
            const mealSection = document.createElement('div');
            mealSection.style.marginBottom = '10px';
            
            // Add meal type header
            const mealHeader = document.createElement('div');
            mealHeader.style.display = 'flex';
            mealHeader.style.alignItems = 'center';
            mealHeader.style.gap = '5px';
            mealHeader.style.marginBottom = '5px';
            mealHeader.style.color = '#232d39';
            mealHeader.innerHTML = `
                <i class="fas ${icon}" style="color: #ed563b;"></i>
                <span style="font-size: 0.9em; font-weight: 500;">${type.charAt(0).toUpperCase() + type.slice(1)}</span>
            `;
            mealSection.appendChild(mealHeader);

            // Get the meal for this type from the day's meals array
            const meal = dayMeals[index];
            if (meal && meal.name) {
                const mealItem = document.createElement('div');
                mealItem.style.padding = '8px';
                mealItem.style.borderRadius = '4px';
                mealItem.style.marginBottom = '5px';
                mealItem.style.fontSize = '0.9em';
                mealItem.style.backgroundColor = 'white';
                mealItem.style.borderLeft = '3px solid #ed563b';
                
                mealItem.innerHTML = `
                    <div style="display: flex; align-items: center; gap: 5px; margin-bottom: 5px;">
                        <span style="font-weight: 500; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">${meal.name}</span>
                    </div>
                    <div style="display: flex; gap: 10px; font-size: 0.85em; color: #666; flex-wrap: wrap;">
                        ${meal.calories ? `<span style="display: flex; align-items: center; gap: 3px; white-space: nowrap;"><i class="fa fa-fire" style="color: #ed563b;"></i>${meal.calories} cal</span>` : ''}
                        ${meal.prepTime ? `<span style="display: flex; align-items: center; gap: 3px; white-space: nowrap;"><i class="fa fa-clock" style="color: #1e90ff;"></i>${meal.prepTime} min</span>` : ''}
                    </div>
                `;
                mealSection.appendChild(mealItem);
            } else {
                const noMeal = document.createElement('div');
                noMeal.style.color = '#999';
                noMeal.style.fontStyle = 'italic';
                noMeal.style.textAlign = 'center';
                noMeal.style.padding = '10px';
                noMeal.textContent = 'No meal planned';
                mealSection.appendChild(noMeal);
            }

            dayColumn.appendChild(mealSection);
        });

        grid.appendChild(dayColumn);
    });

    calendarContainer.appendChild(grid);
    container.appendChild(calendarContainer);
}

// Helper function to format date
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
        month: 'long', 
        day: 'numeric', 
        year: 'numeric' 
    });
}

// Update the completeProfile function to handle both exercise and meal plans
async function completeProfile(username, requestBody) {
    try {
        console.log("Starting profile completion for user:", username);
        console.log("Request body:", requestBody);
        
        const response = await fetch(`http://localhost:8080/api/users/${username}/complete-profile`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(requestBody)
        });
        
        if (!response.ok) {
            const errorText = await response.text();
            console.error("Profile completion failed with status:", response.status);
            console.error("Error response:", errorText);
            throw new Error(errorText || "Profile completion failed");
        }

        const updatedUser = await response.json();
        console.log("Profile completed successfully:", updatedUser);
        
        // Generate exercise plan
        console.log("Generating exercise plan...");
        const exerciseResponse = await fetch(`http://localhost:8080/api/exerciseplans?username=${encodeURIComponent(username)}`, {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        });

        if (!exerciseResponse.ok) {
            const errorText = await exerciseResponse.text();
            throw new Error(`Exercise plan generation failed: ${errorText}`);
        }

        const exercisePlan = await exerciseResponse.json();
        console.log("Exercise plan generated:", exercisePlan);
        
        // Store the exercise plan in localStorage
        localStorage.setItem('exercisePlan', JSON.stringify(exercisePlan));
        
        // Display the exercise plan
        displayExercisePlan(exercisePlan);

        // Generate meal plan
        console.log("Generating meal plan...");
        const mealPlanResponse = await fetch(`http://localhost:8080/api/mealplans?username=${encodeURIComponent(username)}`, {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        });

        if (!mealPlanResponse.ok) {
            const errorText = await mealPlanResponse.text();
            console.error("Meal plan generation failed:", errorText);
            throw new Error(`Failed to generate meal plan: ${errorText}`);
        }

        const mealPlan = await mealPlanResponse.json();
        console.log("Meal plan generated:", mealPlan);
        
        // Store the meal plan in localStorage
        localStorage.setItem('mealPlan', JSON.stringify(mealPlan));
        
        // Display the meal plan
        displayMealPlan(mealPlan);
        // mealPlan.then(result => displayMealPlan(result));
        
        // Navigate to the plans section
        const plansSection = document.getElementById('my-plans');
        if (plansSection) {
            plansSection.scrollIntoView({ behavior: 'smooth' });
        }
    } catch (error) {
        console.error("Error in completeProfile:", error);
        throw error; // Re-throw the error to be handled by the caller
    }
}
  
document.addEventListener('DOMContentLoaded', function() {
    // Initialize Charts
    initializeCharts();
    
    // Exercise Filtering
    const categoryItems = document.querySelectorAll('.category-item');
    const exerciseCards = document.querySelectorAll('.exercise-card');
    const searchInput = document.getElementById('exercise-search');
    const readMoreBtn = document.getElementById('readMoreExercises');
    const readMoreContainer = document.querySelector('.read-more-container');

    // Function to handle exercise visibility
    function handleExerciseVisibility() {
        const visibleCards = Array.from(exerciseCards).filter(card => !card.classList.contains('hidden') && card.style.display !== 'none');
        
        if (visibleCards.length > 6) {
            readMoreContainer.classList.add('visible');
            visibleCards.forEach((card, index) => {
                if (index >= 6) {
                    card.classList.add('hidden');
                }
            });
        } else {
            readMoreContainer.classList.remove('visible');
        }
    }

    // Read More Button Click Handler
    if (readMoreBtn) {
        readMoreBtn.addEventListener('click', function(e) {
            e.preventDefault(); // Prevent default behavior
            
            // Store current scroll position
            const currentScroll = window.scrollY;
            
            const isShowingMore = this.classList.contains('active');
            const visibleCards = Array.from(exerciseCards).filter(card => card.style.display !== 'none');
            
            if (!isShowingMore) {
                // Show all cards
                visibleCards.forEach(card => {
                    card.classList.remove('hidden');
                });
                this.classList.add('active');
                this.innerHTML = 'Show Less <i class="fas fa-chevron-up"></i>';
            } else {
                // Show only first 6 cards
                visibleCards.forEach((card, index) => {
                    if (index >= 6) {
                        card.classList.add('hidden');
                    }
                });
                this.classList.remove('active');
                this.innerHTML = 'Show More Exercises <i class="fas fa-chevron-down"></i>';
                
                // Scroll to the last visible card if we're below it
                const lastVisibleCard = visibleCards[5];
                if (currentScroll > lastVisibleCard.getBoundingClientRect().bottom + window.scrollY) {
                    window.scrollTo({
                        top: lastVisibleCard.getBoundingClientRect().bottom + window.scrollY - 100,
                        behavior: 'smooth'
                    });
                }
            }
            
            // Restore scroll position for "Show More"
            if (!isShowingMore) {
                window.scrollTo({
                    top: currentScroll,
                    behavior: 'instant'
                });
            }
        });
    }

    // Filter exercises by category
    categoryItems.forEach(item => {
        item.addEventListener('click', () => {
            // Remove active class from all items
            categoryItems.forEach(cat => cat.classList.remove('active'));
            // Add active class to clicked item
            item.classList.add('active');

            const filter = item.getAttribute('data-filter');
            
            exerciseCards.forEach(card => {
                // Remove hidden class from all cards first
                card.classList.remove('hidden');
                
                if (filter === 'all' || card.getAttribute('data-category') === filter) {
                    card.style.display = 'block';
                } else {
                    card.style.display = 'none';
                }
            });

            // Reset read more button state
            if (readMoreBtn) {
                readMoreBtn.classList.remove('active');
                readMoreBtn.innerHTML = 'Show More Exercises <i class="fas fa-chevron-down"></i>';
            }

            // Re-apply search filter if there's a search term
            if (searchInput.value.trim()) {
                filterBySearch(searchInput.value.trim().toLowerCase());
            }

            // Check visibility after filter
            handleExerciseVisibility();
        });
    });

    // Initial visibility check
    handleExerciseVisibility();

    // Search functionality
    function filterBySearch(searchTerm) {
        exerciseCards.forEach(card => {
            // If search term is empty, show all cards that match the current category filter
            if (!searchTerm) {
                const currentCategory = document.querySelector('.category-item.active').getAttribute('data-filter');
                if (currentCategory === 'all' || card.getAttribute('data-category') === currentCategory) {
                    card.style.display = 'block';
                } else {
                    card.style.display = 'none';
                }
            } else {
                // If there's a search term, only search within visible cards (respecting category filter)
                if (card.style.display !== 'none') {
                    const exerciseTitle = card.querySelector('.exercise-title').textContent.toLowerCase();
                    const exerciseDetails = card.querySelector('.exercise-details').textContent.toLowerCase();
                    
                    if (exerciseTitle.includes(searchTerm) || exerciseDetails.includes(searchTerm)) {
                        card.style.display = 'block';
                    } else {
                        card.style.display = 'none';
                    }
                }
            }
        });
        
        // Check visibility after search
        handleExerciseVisibility();
    }

    // Search input handler
    if (searchInput) {
        searchInput.addEventListener('input', (e) => {
            filterBySearch(e.target.value.trim().toLowerCase());
        });
    }

    // Set first category as active by default
    if (categoryItems.length > 0) {
        categoryItems[0].classList.add('active');
    }

    // Tab Switching
    const tabButtons = document.querySelectorAll('.tab-button');
    const tabContents = document.querySelectorAll('.tab-content');
    
    tabButtons.forEach(button => {
        button.addEventListener('click', () => {
            // Remove active class from all buttons and contents
            tabButtons.forEach(btn => btn.classList.remove('active'));
            tabContents.forEach(content => content.classList.remove('active'));
            
            // Add active class to clicked button and corresponding content
            button.classList.add('active');
            const tabId = button.getAttribute('data-tab');
            document.getElementById(tabId).classList.add('active');
        });
    });

    // Plan Tab Switching
    const planTabs = document.querySelectorAll('.plan-tab');
    const planContents = document.querySelectorAll('.plan-content');
    
    planTabs.forEach(tab => {
        tab.addEventListener('click', () => {
            // Remove active class from all tabs and contents
            planTabs.forEach(t => t.classList.remove('active'));
            planContents.forEach(c => c.classList.remove('active'));
            
            // Add active class to clicked tab and corresponding content
            tab.classList.add('active');
            const planType = tab.getAttribute('data-plan');
            const contentId = `${planType}-plan`;
            const content = document.getElementById(contentId);
            if (content) {
                content.classList.add('active');
                
                // If switching to meal plan, ensure it's displayed
                if (planType === 'meal') {
                    const savedMealPlan = localStorage.getItem('mealPlan');
                    if (savedMealPlan) {
                        displayMealPlan(JSON.parse(savedMealPlan));
                    }
                }
            }
        });
    });

    // Energy Level Range Input
    const energyLevel = document.getElementById('energy-level');
    const rangeValue = energyLevel.nextElementSibling;
    
    energyLevel.addEventListener('input', () => {
        rangeValue.textContent = energyLevel.value;
    });

    // Form Submissions
    const workoutLog = document.getElementById('workout-log');
    const metricsLog = document.getElementById('metrics-log');

    if (workoutLog) {
        workoutLog.addEventListener('submit', (e) => {
            e.preventDefault();
            const formData = new FormData(workoutLog);
            const exercises = formData.getAll('completed_exercises[]');
            const notes = document.getElementById('workout-notes').value;
            
            // TODO: Send to backend
            console.log('Logged exercises:', exercises);
            console.log('Workout notes:', notes);
            
            // Show success message
            alert('Workout logged successfully!');
        });
    }

    if (metricsLog) {
        metricsLog.addEventListener('submit', (e) => {
            e.preventDefault();
            const weight = document.getElementById('current-weight').value;
            const energy = document.getElementById('energy-level').value;
            const notes = document.getElementById('health-notes').value;
            
            // TODO: Send to backend
            console.log('Logged metrics:', { weight, energy, notes });
            
            // Show success message
            alert('Health metrics logged successfully!');
        });
    }

    // Create Workout Plan Button
    const createPlanButton = document.getElementById('create-plan');
    if (createPlanButton) {
        createPlanButton.addEventListener('click', () => {
            const selectedExercises = Array.from(document.querySelectorAll('input[name="exercises[]"]:checked'))
                .map(checkbox => checkbox.value);

            if (selectedExercises.length === 0) {
                alert('Please select at least one exercise to create your plan.');
                return;
            }

            // TODO: Send to backend
            console.log('Creating plan with exercises:', selectedExercises);
            
            // Show loading state
            createPlanButton.disabled = true;
            createPlanButton.innerHTML = '<i class="fa fa-spinner fa-spin"></i> Creating Your Plan...';

            // Simulate API call
            setTimeout(() => {
                createPlanButton.disabled = false;
                createPlanButton.innerHTML = '<i class="fa fa-check-circle"></i> Create My Workout Plan';
                alert('Your personalized workout plan has been created!');
            }, 2000);
        });
    }

    // Random Motivational Quotes
    const quotes = [
        "Small steps lead to big results!",
        "The only bad workout is the one that didn't happen.",
        "Your body can stand almost anything. It's your mind you have to convince.",
        "Fitness is not about being better than someone else. It's about being better than you used to be.",
        "The hard days are the best because that's when champions are made.",
        "Don't wish for it, work for it.",
        "Your health is an investment, not an expense."
    ];

    function updateQuote() {
        const quoteElement = document.getElementById('motivational-quote');
        const randomQuote = quotes[Math.floor(Math.random() * quotes.length)];
        quoteElement.textContent = randomQuote;
    }

    // Update quote every day
    updateQuote();
    setInterval(updateQuote, 24 * 60 * 60 * 1000);

    // Print plan button
    const printPlanBtn = document.getElementById('print-plan');
    if (printPlanBtn) {
        printPlanBtn.addEventListener('click', () => {
            window.print();
        });
    }

    // Regenerate plan button
    const regeneratePlanBtn = document.getElementById('regenerate-plan');
    if (regeneratePlanBtn) {
        regeneratePlanBtn.addEventListener('click', () => {
            const username = readCookie();
            if (username) {
                // Get the current profile data
                const profileData = {
                    goal: [document.getElementById('goal').value],
                    activityLevel: document.getElementById('activity').value,
                    gender: document.getElementById('gender').value,
                    weight: Number(document.getElementById('weight').value),
                    height: Number(document.getElementById('height').value),
                    age: Number(document.getElementById('age').value),
                };
                completeProfile(username, profileData);
            }
        });
    }

    // Load existing plans if available
    const savedExercisePlan = localStorage.getItem('exercisePlan');
    if (savedExercisePlan) {
        displayExercisePlan(JSON.parse(savedExercisePlan));
    }

    const savedMealPlan = localStorage.getItem('mealPlan');
    if (savedMealPlan) {
        displayMealPlan(JSON.parse(savedMealPlan));
    }
});
function readCookie() {
    var i, c, ca, nameEQ = "username=";
    ca = document.cookie.split(';');
    for(i=0;i < ca.length;i++) {
        c = ca[i];
        while (c.charAt(0)==' ') {
            c = c.substring(1,c.length);
        }
        if (c.indexOf(nameEQ) == 0) {
            return c.substring(nameEQ.length,c.length);
        }
    }
    return '';
}
// Initialize Charts function (if needed)
function initializeCharts() {
    // Weight Progress Chart
    const weightCtx = document.getElementById('weightChart');
    if (weightCtx) {
        new Chart(weightCtx, {
            type: 'line',
            data: {
                labels: ['Week 1', 'Week 2', 'Week 3', 'Week 4'],
                datasets: [{
                    label: 'Weight (kg)',
                    data: [75, 74.5, 73.8, 73.2],
                    borderColor: '#ed563b',
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                scales: {
                    y: {
                        beginAtZero: false
                    }
                }
            }
        });
    }

    // Workout Consistency Chart
    const workoutCtx = document.getElementById('workoutChart');
    if (workoutCtx) {
        new Chart(workoutCtx, {
            type: 'bar',
            data: {
                labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
                datasets: [{
                    label: 'Workouts Completed',
                    data: [1, 1, 0, 1, 1, 1, 0],
                    backgroundColor: '#ed563b'
                }]
            },
            options: {
                responsive: true,
                scales: {
                    y: {
                        beginAtZero: true,
                        max: 1,
                        ticks: {
                            stepSize: 1
                        }
                    }
                }
            }
        });
    }
} 