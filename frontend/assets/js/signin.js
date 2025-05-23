$(document).ready(function(){
    
	$("#sign-in-btn").click(() => {
        console.log("test sign in");
        const requestBody = {
            username: document.getElementById('username').value,
            password: document.getElementById('password').value,
          };
        console.log(requestBody)
		signIn(requestBody);
	})
})
function writeCookie(userId) {
    // var date, expires;
    document.cookie = 'userId' + "=" + userId;
}
function readCookie() {
    var i, c, ca, nameEQ = "userId=";
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
      
      if (response.status == 200) {
        const data = await response.text();
        writeCookie(data);
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
  