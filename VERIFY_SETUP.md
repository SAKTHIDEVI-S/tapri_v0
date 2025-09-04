# Quick Setup Verification

## Step 1: Verify Firebase Console Setup

### Check Phone Authentication is Enabled:
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select project: `tapri-9e996`
3. Go to **Authentication** → **Sign-in method**
4. **Phone** provider should be **ENABLED** ✅

### Check Test Phone Number Format:
1. In Phone Authentication settings, scroll to **Phone numbers for testing**
2. **Verify the format**: `+91 9999999999` (with space after +91)
3. **Verify the test code**: `123456`

## Step 2: Test the App

### Test Flow:
1. **Run your Android app**
2. **Enter phone number**: `9999999999` (10 digits only)
3. **Click "Send OTP"**
4. **Check the logs** in Android Studio Logcat:
   - Look for: `"Formatted phone number: +91 9999999999"`
   - Look for: `"Test phone detected. Use test code from Firebase Console"`
5. **You should see**: "Test OTP sent! Use test code from Firebase Console"
6. **Enter test code**: `123456`
7. **Should login successfully!**

## Step 3: Debug Information

### Check Logcat for these messages:
```
LoginActivity: Original mobile: 9999999999
LoginActivity: Formatted phone number: +91 9999999999
LoginActivity: Using test phone number. Check Firebase Console for test code!
LoginActivity: Firebase OTP sent successfully
```

### If you see errors, check:
- **"ERROR_APP_NOT_AUTHORIZED"**: Add SHA-1 certificate
- **"ERROR_INVALID_PHONE_NUMBER"**: Check phone number format
- **"Verification failed"**: Phone Authentication not enabled

## Step 4: Common Issues & Fixes

### Issue: "No test phone numbers found"
**Fix**: 
- Add test phone number in Firebase Console: `+91 9999999999`
- Make sure there's a space after +91

### Issue: "Test code not working"
**Fix**:
- Check the test code in Firebase Console
- Make sure you're using the correct phone number

### Issue: "App not authorized"
**Fix**:
- Add SHA-1 certificate to Firebase project
- Check `google-services.json` is in correct location

## Step 5: Success Indicators

### ✅ Everything is working if:
1. App shows "Test OTP sent! Use test code from Firebase Console"
2. You can enter the test code and login successfully
3. No error messages in Logcat
4. Phone number format matches: `+91 9999999999`

### ❌ Something is wrong if:
1. App shows "Verification failed this process is not allowed"
2. No "Test OTP sent" message
3. Error messages in Logcat
4. Phone number format doesn't match

## Quick Fix Checklist

- [ ] Phone Authentication enabled in Firebase Console
- [ ] Test phone number added: `+91 9999999999` (with space)
- [ ] Test code set: `123456`
- [ ] App shows "Test OTP sent" message
- [ ] Can login with test code
- [ ] No error messages in logs

If all checkboxes are ✅, your setup is working correctly!