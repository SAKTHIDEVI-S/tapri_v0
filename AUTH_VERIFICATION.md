# TAPRI BACKEND - AUTH VERIFICATION GUIDE

##  CURRENT STATUS: FULLY CONFIGURED

### Database Setup (Flyway + MySQL)
- **Flyway Dependencies**:  Present (flyway-core:10.7.1, flyway-mysql:10.7.1)
- **Migration Files**:  Present (V1__init.sql)
- **Tables Created**:
  - users (id, phone, name, city, profile_picture, rating, earnings, last_login, timestamps)
  - otp_codes (id, phone, code_hash, expires_at, attempts, created_at)

### Authentication Setup (JWT + OTP)
- **Firebase**:  Completely removed
- **JWT Dependencies**:  Present (jjwt-api, jjwt-impl, jjwt-jackson)
- **JWT Configuration**:  Present in application.properties
- **Auth Endpoints**:  All present
  - POST /api/auth/otp - Send OTP
  - POST /api/auth/verify - Verify OTP
  - POST /api/auth/signup/complete - Complete signup

### Security Configuration
- **Spring Security**:  Configured
- **JWT Filter**:  Present (JwtAuthFilter.java)
- **Security Config**:  Present (SecurityConfig.java)
- **Protected Routes**: /api/auth/** and /api/signup/** are public

##  HOW TO VERIFY AUTH WORKS

### 1. Start MySQL Database
`ash
# Make sure MySQL is running on port 3306
# Database: tapri, User: root, Password: 1234
`

### 2. Run Backend
`ash
# In Eclipse: Right-click TapriApplication.java  Run As  Java Application
# Or via Maven:
mvn spring-boot:run
`

### 3. Test OTP Flow with cURL

#### Step 1: Send OTP
`ash
curl -X POST http://localhost:8080/api/auth/otp \
  -H "Content-Type: application/json" \
  -d '{"phone": "+1234567890", "purpose": "login"}'
`

**Expected Response:**
`json
{"message": "OTP sent successfully"}
`

**Check Console:** You'll see the actual OTP code logged (for testing)

#### Step 2: Verify OTP (New User)
`ash
curl -X POST http://localhost:8080/api/auth/verify \
  -H "Content-Type: application/json" \
  -d '{"phone": "+1234567890", "code": "123456"}'
`

**Expected Response (New User):**
`json
{
  "needsSignup": true,
  "tempToken": "eyJhbGciOiJIUzI1NiJ9..."
}
`

#### Step 3: Complete Signup
`ash
curl -X POST http://localhost:8080/api/auth/signup/complete \
  -H "Content-Type: application/json" \
  -H "Temp-Token: YOUR_TEMP_TOKEN_FROM_STEP_2" \
  -d '{"name": "John Doe", "city": "New York"}'
`

**Expected Response:**
`json
{
  "jwt": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": 1,
    "phone": "+1234567890",
    "name": "John Doe",
    "city": "New York"
  }
}
`

#### Step 4: Verify OTP (Existing User)
`ash
curl -X POST http://localhost:8080/api/auth/verify \
  -H "Content-Type: application/json" \
  -d '{"phone": "+1234567890", "code": "123456"}'
`

**Expected Response (Existing User):**
`json
{
  "jwt": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": 1,
    "phone": "+1234567890",
    "name": "John Doe",
    "city": "New York"
  }
}
`

#### Step 5: Test Protected Endpoint
`ash
curl -X GET http://localhost:8080/api/users/profile/1 \
  -H "Authorization: Bearer YOUR_JWT_FROM_STEP_3_OR_4"
`

**Expected Response:**
`json
{
  "id": 1,
  "phone": "+1234567890",
  "name": "John Doe",
  "city": "New York"
}
`

##  CONFIGURATION FILES

### application.properties
`properties
spring.datasource.url=jdbc:mysql://localhost:3306/tapri?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=1234
spring.flyway.enabled=true
jwt.secret=dGFwcmlfc2VjcmV0X2tleV9mb3JfandlYl90b2tlbl9zaWduaW5nX3Zlcnlfc2VjdXJlXzEyMw==
jwt.expiration=86400000
`

### Database Schema (V1__init.sql)
- Creates users and otp_codes tables
- Proper indexes for performance
- Timestamps for audit trails

##  ANDROID INTEGRATION

The Android app is configured to:
- Use http://10.0.2.2:8080/api/auth/ for emulator
- Send OTP  Verify OTP  Complete Signup/Login
- Store JWT in SessionManager
- Add Authorization header to protected requests

##  TROUBLESHOOTING

1. **MySQL Connection Error**: Ensure MySQL is running on port 3306
2. **Flyway Error**: Check if database exists and user has permissions
3. **JWT Error**: Verify jwt.secret is set in application.properties
4. **OTP Not Working**: Check console logs for the actual OTP code

##  CURRENT TABLES

### users
- id (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- phone (VARCHAR(15), UNIQUE, NOT NULL)
- name (VARCHAR(100), NOT NULL)
- city (VARCHAR(100))
- profile_picture (VARCHAR(255))
- rating (DOUBLE)
- earnings (DOUBLE)
- last_login (TIMESTAMP)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

### otp_codes
- id (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- phone (VARCHAR(15), NOT NULL)
- code_hash (VARCHAR(255), NOT NULL)
- expires_at (TIMESTAMP, NOT NULL)
- attempts (INT, DEFAULT 0)
- created_at (TIMESTAMP)

##  VERIFICATION CHECKLIST

- [ ] MySQL running on port 3306
- [ ] Backend starts without errors
- [ ] Send OTP returns success
- [ ] Verify OTP works for new user (returns tempToken)
- [ ] Complete signup works (returns JWT + user)
- [ ] Verify OTP works for existing user (returns JWT + user)
- [ ] Protected endpoint works with JWT
- [ ] Android app can connect to backend
