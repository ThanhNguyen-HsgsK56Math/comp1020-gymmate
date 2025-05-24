// Dashboard JavaScript

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
                if (lastVisibleCard) {
                    const lastCardBottom = lastVisibleCard.getBoundingClientRect().bottom + window.scrollY;
                    if (currentScroll > lastCardBottom) {
                        window.scrollTo({
                            top: lastCardBottom - 100,
                            behavior: 'smooth'
                        });
                    }
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
            if (card.style.display !== 'none') { // Only search visible cards (respecting category filter)
                const exerciseTitle = card.querySelector('.exercise-title').textContent.toLowerCase();
                const exerciseDetails = card.querySelector('.exercise-details').textContent.toLowerCase();
                
                if (exerciseTitle.includes(searchTerm) || exerciseDetails.includes(searchTerm)) {
                    card.style.display = 'block';
                } else {
                    card.style.display = 'none';
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
            planContents.forEach(content => content.classList.remove('active'));
            
            // Add active class to clicked tab and corresponding content
            tab.classList.add('active');
            const planType = tab.getAttribute('data-plan');
            document.getElementById(`${planType}-plan`).classList.add('active');
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
});

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