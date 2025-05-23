$(document).ready(function(){
    
	$("#sign-up-btn").click(() => {
        console.log("test sign in")
        const requestBody = {
            username: document.getElementById('fullname').value,
            // email: document.getElementById('email').value,
            password: document.getElementById('password').value,
          };;
        console.log(requestBody)
		signUp(requestBody);
	})
})

async function signUp(requestBody) {
    // Helper function to build the request body

    // console.log(requestBody);
    try {
      const response = await fetch('http://localhost:8080/api/users/register', {
        method: 'POST',
        mode: 'cors',
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json',

        },
        body: JSON.stringify(requestBody)
      });
      
      if (response.status == 200) {
        const data = await response.text();
        console.log("Sign up successful!", data);
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
  