# Firebase Setup Verification Checklist

## Quick Check - Run this to verify your setup:

### 1. Firebase Console Setup
- [ ] Go to [Firebase Console](https://console.firebase.google.com/)
- [ ] Select your project: `tapri-9e996`
- [ ] Go to **Authentication** → **Sign-in method**
- [ ] **Phone** provider should be **ENABLED** (toggle switch should be ON)
- [ ] Click **Save** if you just enabled it

### 2. Test Phone Numbers (IMPORTANT FOR DEVELOPMENT)
- [ ] In Phone Authentication settings, scroll down to **Phone numbers for testing**
- [ ] Click **Add phone number**
- [ ] Add your test phone number: `+91 9999999999`
- [ ] Add a test code: `123456`
- [ ] Click **Save**
- [ ] **REPEAT** for any other phone numbers you want to test with

### 3. App Configuration
- [ ] `google-services.json` is in `app/` directory ✅ (Already done)
- [ ] Package name in Firebase matches: `com.tapri` ✅ (Already done)
- [ ] SHA-1 certificate added (for production)

### 4. Common Issues & Solutions

#### Issue: "OTP sent but no SMS received"
**Solution**: Add test phone numbers in Firebase Console
1. Go to Firebase Console → Authentication → Sign-in method → Phone
2. Scroll down to **Phone numbers for testing**
3. Add your phone number with a test code
4. Use the test code instead of waiting for SMS

#### Issue: "Verification failed this process is not allowed"
**Solution**: Phone Authentication is not enabled in Firebase Console
1. Go to Firebase Console → Authentication → Sign-in method
2. Find **Phone** provider
3. Click on it and **ENABLE** it
4. Click **Save**

#### Issue: "App not authorized"
**Solution**: Add SHA-1 certificate fingerprint
1. Get your debug SHA-1:
   ```bash
   keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
   ```
2. Go to Firebase Console → Project Settings → Your apps
3. Add the SHA-1 fingerprint

#### Issue: "Invalid phone number format"
**Solution**: Ensure phone number includes country code
- Format: `+91XXXXXXXXXX` (for India)
- The app automatically adds `+91` if not present

### 5. Testing Steps
1. **Build and run** your Android app
2. **Enter mobile number**: `9999999999` (or any 10-digit number)
3. **Check logs** for detailed error messages
4. **Use test phone number** if you added one in Firebase Console
5. **Enter the test code** instead of waiting for SMS

### 6. Debug Information
The app now logs detailed information. Check Android Studio Logcat for:
- `LoginActivity` - App-level logs
- `FirebaseAuthService` - Firebase-specific logs
- Error codes and messages

### 7. If Still Having Issues
1. **Check Firebase Console** - Make sure Phone Authentication is enabled
2. **Add test phone numbers** - This is crucial for development
3. **Check Logcat** - Look for specific error codes
4. **Verify internet connection** - Firebase needs internet access
5. **Check Firebase project** - Make sure you're using the correct project

## Next Steps
Once Phone Authentication is working:
1. Test the complete flow: Login → OTP → Home
2. Test with real phone numbers
3. Configure production settings