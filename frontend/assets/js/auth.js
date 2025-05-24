// User Authentication and Storage Management

// Function to handle user registration
function handleSignUp(event) {
    event.preventDefault();
    
    const fullname = document.getElementById('fullname').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirm-password').value;

    // Basic validation
    if (password !== confirmPassword) {
        alert('Passwords do not match!');
        return;
    }

    // Get existing users or initialize empty array
    const users = JSON.parse(localStorage.getItem('users') || '[]');

    // Check if user already exists
    if (users.some(user => user.email === email)) {
        alert('A user with this email already exists!');
        return;
    }

    // Create new user object
    const newUser = {
        id: Date.now().toString(),
        fullname,
        email,
        password: btoa(password), // Basic encoding (not secure for production)
        createdAt: new Date().toISOString()
    };

    // Add new user to array
    users.push(newUser);

    // Save updated users array
    localStorage.setItem('users', JSON.stringify(users));

    // Save current user session
    localStorage.setItem('currentUser', JSON.stringify({
        id: newUser.id,
        fullname: newUser.fullname,
        email: newUser.email
    }));

    // Redirect to dashboard
    // window.location.href = 'dashboard.html';
}

// Function to handle user sign in
function handleSignIn(event) {
    event.preventDefault();
    
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    // Get users from localStorage
    const users = JSON.parse(localStorage.getItem('users') || '[]');

    // Find user by username (which can be either username or email)
    const user = users.find(u => u.email === username || u.fullname === username);

    if (!user || btoa(password) !== user.password) {
        alert('Invalid username or password!');
        return;
    }

    // Save current user session
    localStorage.setItem('currentUser', JSON.stringify({
        id: user.id,
        fullname: user.fullname,
        email: user.email
    }));

    // Redirect to dashboard
    // window.location.href = 'dashboard.html';
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
        userNameElement.textContent = currentUser.fullname;
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