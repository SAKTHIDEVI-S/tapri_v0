# Login Debugging Guide

## Current Issue
The app is closing/crashing after OTP verification during login.

## Debugging Steps Implemented

### 1. Enhanced Logging
- Added comprehensive logging to `OtpActivity.kt`
- Added logging to `HomeActivity.kt` 
- Added logging to `SimpleHomeActivity.kt`

### 2. Simplified Components Created
- `SimpleLoginActivity.kt` - Minimal login activity for testing
- `SimpleOtpActivity.kt` - Minimal OTP activity that bypasses verification
- `SimpleHomeActivity.kt` - Minimal home activity with basic initialization

### 3. Response Handling Fixes
- Updated `AuthApi.kt` to handle raw Map responses from backend
- Added `convertMapToUser()` method to handle user data conversion
- Added fallback mechanisms for missing user data

### 4. Error Handling Improvements
- Added try-catch blocks around all critical operations
- Added fallback user creation when user data is missing
- Added detailed error messages and logging

## How to Debug

### Step 1: Check Logs
1. Connect device/emulator
2. Open Android Studio Logcat
3. Filter by tags: `OtpActivity`, `SimpleHomeActivity`, `HomeActivity`
4. Try login and check for error messages

### Step 2: Test with Simple Components
The app is now configured to use `SimpleHomeActivity` which has minimal initialization.
This will help identify if the crash is in:
- OTP verification process
- User session saving
- HomeActivity initialization
- Layout loading

### Step 3: Check Backend Response
The Hibernate logs show the backend is working correctly:
```
Hibernate: select oc1_0.id,oc1_0.attempts,oc1_0.code_hash,oc1_0.created_at,oc1_0.expires_at,oc1_0.phone from otp_codes oc1_0 where oc1_0.phone=? and oc1_0.expires_at>?
Hibernate: select u1_0.id,u1_0.bio,u1_0.city,u1_0.created_at,u1_0.earnings,u1_0.is_active,u1_0.last_login,u1_0.last_seen,u1_0.last_seen_visible,u1_0.name,u1_0.phone,u1_0.profile_photo_url,u1_0.profile_picture,u1_0.rating,u1_0.state,u1_0.updated_at from users u1_0 where u1_0.phone=?
```

This means:
- OTP code is found and valid
- User is found in database
- Backend should return: `{"jwt": "...", "user": {...}}`

### Step 4: Expected Log Flow
When login works correctly, you should see:
```
D/OtpActivity: Verify response: {jwt=..., user={...}}
D/OtpActivity: User login successful
D/OtpActivity: Saving JWT token: ...
D/OtpActivity: JWT token saved successfully
D/OtpActivity: Saving user session: {...}
D/OtpActivity: User session saved successfully
D/OtpActivity: Navigating to SimpleHomeActivity for debugging
D/OtpActivity: Successfully started SimpleHomeActivity
D/SimpleHomeActivity: Activity started
D/SimpleHomeActivity: SessionManager initialized
D/SimpleHomeActivity: Login status: true
D/SimpleHomeActivity: User is logged in, setting content view
D/SimpleHomeActivity: Content view set successfully
D/SimpleHomeActivity: SimpleHomeActivity initialized successfully
```

### Step 5: Common Issues and Solutions

#### Issue 1: Response Format Mismatch
**Symptoms**: "Unexpected response format" error
**Solution**: Backend returns Map<String, Object> but app expects structured response
**Fix**: Use `verifyOtpRaw()` method instead of `verifyOtp()`

#### Issue 2: User Data Serialization Error
**Symptoms**: "Error saving user session" error
**Solution**: User object from backend doesn't match Android User model
**Fix**: Added `convertMapToUser()` method and fallback user creation

#### Issue 3: HomeActivity Initialization Error
**Symptoms**: App crashes when starting HomeActivity
**Solution**: Complex HomeActivity initialization fails
**Fix**: Use SimpleHomeActivity for debugging

#### Issue 4: Session Manager Error
**Symptoms**: "Error saving JWT token" or session-related errors
**Solution**: SharedPreferences or data serialization issues
**Fix**: Added comprehensive error handling and fallbacks

## Testing Instructions

### Test 1: Basic Login Flow
1. Enter phone number
2. Enter OTP
3. Check logs for each step
4. Verify SimpleHomeActivity loads

### Test 2: Network Issues
1. Disconnect internet
2. Try login
3. Check error handling

### Test 3: Invalid OTP
1. Enter wrong OTP
2. Check error messages

### Test 4: Missing User Data
1. Login with user that has incomplete profile
2. Check fallback user creation

## Current Status

✅ **Reverted to Original HomeActivity** - The app now uses the full v0 version with backend integration
✅ **Enhanced Logging Retained** - All debugging improvements are still in place
✅ **Error Handling Improved** - Better error handling and fallback mechanisms

## Next Steps

1. **Test the original HomeActivity** - The app now uses the full v0 version with all features
2. **Check logs for any remaining issues** - Enhanced logging will show exactly what's happening
3. **Verify all features work** - Posts, groups, profile, etc. should all be functional

## Files Modified

- `app/src/main/java/com/tapri/ui/OtpActivity.kt` - Enhanced logging and error handling
- `app/src/main/java/com/tapri/network/AuthApi.kt` - Added raw response handling
- `app/src/main/java/com/tapri/ui/HomeActivity.kt` - Added logging
- `app/src/main/java/com/tapri/ui/SimpleHomeActivity.kt` - New minimal home activity
- `app/src/main/AndroidManifest.xml` - Updated to use SimpleHomeActivity

## Rollback Instructions

To revert to original HomeActivity:
1. Change `AndroidManifest.xml` back to `.ui.HomeActivity`
2. Change `OtpActivity.kt` navigateToHome() back to `HomeActivity::class.java`
3. Delete SimpleHomeActivity.kt, SimpleLoginActivity.kt, SimpleOtpActivity.kt
