$(document).ready(function(){
    
	$("#sign-in-btn").click(() => {
        console.log("test sign in");
        const requestBody = {
            username: document.getElementById('username').value,
            password: document.getElementById('password').value,
          };
        console.log(requestBody)
		signIn(requestBody);
        window.location.href="./dashboard"
	})
})

async function signIn(requestBody) {
    // Helper function to build the request body

    // console.log(requestBody);
    try {
      const response = await fetch('http://localhost:8080/api/users/login', {
        method: 'POST',
        mode: 'cors',
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json',

        },
        body: JSON.stringify(requestBody)
      });
      
      if (response.ok) {
        const data = await response.text();
        console.log("Login successful!", data);
        window.location.href = "./dashboard.html";
        // Store token or navigate user here
      } else {
        const error = await response.text();
        console.error("Login failed:", error);
      }
    } catch (err) {
      console.error("Error:", err);
    }
  }
  