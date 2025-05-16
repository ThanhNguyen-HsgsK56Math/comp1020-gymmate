Đảm bảo là terminal là môi trường Linux. Mở 2 cửa sổ terminal và chạy "cd backend" trên cả hai cửa sổ 

# Cửa sổ 1: Khởi động hệ thống 
- Chạy "mvn spring-boot:run" để khởi tạo hệ thống 
- Sau khi chờ tầm 1 phút - 1 phút 30s cho đến khi thấy dòng này "Started GymmateApplication" thì qua cửa sổ thứ 2

# Cửa sổ 2: Testing 

**1. REGISTER**
Test 1: Tạo 1 user với data hợp lệ 
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser2",
    "password": "password123",
    "email": "new@example.com",
    "goal": "burn_500_calories"
  }'
=> Nó sẽ hiện các thông tin người dùng và nó sẽ add thẳng vào trong bộ data ở MongoDB

Test 2: Tạo 1 user nhưng trùng tên user VÀ password với user trước: 
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "password123",
    "email": "new3@example.com",
    "goal": "burn_5000_calories"
  }'
=> "User with this username and password already exists"
=> Nếu chỉ 1 chữ trong username hoặc password thay đổi thì nó sẽ tạo 1 sample mới 

**2. LOGIN**
Test 1: login hợp lệ 
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser2",
    "password": "password123"
  }'
=> "login successful" 

Test 2: Login nhưng sai mật khẩu hoặc username 
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser2",
    "password": "password1234"
  }'
=> "Cannot login. Please check your username or password!"


  # Test 1: Login with correct credentials
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser1",
    "password": "password123"
  }'

# Test 2: Login with wrong password
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser1",
    "password": "wrongpassword"
  }'

# Test 3: Login with non-existent user
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "nonexistentuser",
    "password": "password123"
  }'

**3. Tạo Exercise Plan**
curl -X GET "http://localhost:8080/api/dailyplans?userId=USER_ID" \
  -H "Content-Type: application/json"

- Chỗ USER_ID thì thay bằng 1 user_id thật, có thể xem ở trong file user.json 
- Nếu nhập ko đúng user_id thì nó sẽ hiện "User not found" 

**4. Tạo Meal Plan**
Test case 1: Balanced preferences
curl -X GET "http://localhost:8080/api/mealplans?userId=USER_ID&targetCalories=2000&weightLoss=5&muscleBuilding=5&generalHealth=5" \
  -H "Content-Type: application/json"

Test case 2: Weight loss focused
curl -X GET "http://localhost:8080/api/mealplans?userId=USER_ID&targetCalories=1800&weightLoss=8&muscleBuilding=3&generalHealth=4" \
  -H "Content-Type: application/json"

Test case 3: Muscle building focused
curl -X GET "http://localhost:8080/api/mealplans?userId=USER_ID&targetCalories=2500&weightLoss=2&muscleBuilding=9&generalHealth=4" \
  -H "Content-Type: application/json"

Test case 4: Health focused
curl -X GET "http://localhost:8080/api/mealplans?userId=USER_ID&targetCalories=2200&weightLoss=4&muscleBuilding=4&generalHealth=8" \
  -H "Content-Type: application/json"

=> Nó sẽ ra 1 cái json chứa các exercise 