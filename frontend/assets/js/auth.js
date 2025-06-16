// User Authentication and Storage Management

// Function to handle user registration
async function handleSignUp(event) {
    event.preventDefault();
    
    const username = document.getElementById('fullname').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirm-password').value;

    // Basic validation
    if (password !== confirmPassword) {
        alert('Passwords do not match!');
        return;
    }

    try {
        const response = await fetch('http://localhost:8080/api/users/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                username: username,
                password: password,
                email: email
            })
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error);
        }

        const user = await response.json();
        
        // Save current user session
        localStorage.setItem('currentUser', JSON.stringify({
            username: user.username,
            email: user.email
        }));

        // Redirect to dashboard
        window.location.href = 'dashboard.html';
    } catch (error) {
        alert(error.message || 'Registration failed. Please try again.');
    }
}

// Function to handle user sign in
async function handleSignIn(event) {
    event.preventDefault();
    
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    try {
        const response = await fetch('http://localhost:8080/api/users/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                username: username,
                password: password
            })
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error);
        }

        const data = await response.json();
        
        // Save current user session
        localStorage.setItem('currentUser', JSON.stringify({
            username: data.username,
            status: data.status
        }));

        // Redirect to dashboard
        window.location.href = 'dashboard.html';
    } catch (error) {
        alert(error.message || 'Invalid username or password!');
    }
}

// Function to handle sign out
function handleSignOut() {
    localStorage.removeItem('currentUser');
    window.location.href = '../index.html';
}

// Function to check if user is authenticated
function checkAuth() {
    const currentUser = localStorage.getItem('currentUser');
    if (!currentUser && !window.location.pathname.includes('signin.html') && !window.location.pathname.includes('signup.html')) {
        window.location.href = 'signin.html';
    }
}

// Function to update UI with user info
function updateUserInfo() {
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    const userNameElement = document.getElementById('user-name');
    
    if (currentUser && userNameElement) {
        userNameElement.textContent = currentUser.username;
    }
}

// Initialize auth checks when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    // Check authentication status
    checkAuth();
    
    // Update UI with user info
    updateUserInfo();
    
    // Add event listeners to forms if they exist
    const signupForm = document.getElementById('signup-form');
    const signinForm = document.getElementById('signin-form');
    const signoutBtn = document.querySelector('a[href="../index.html"]');

    if (signupForm) {
        signupForm.addEventListener('submit', handleSignUp);
    }

    if (signinForm) {
        signinForm.addEventListener('submit', handleSignIn);
    }

    if (signoutBtn) {
        signoutBtn.addEventListener('click', function(e) {
            e.preventDefault();
            handleSignOut();
        });
    }
}); 
