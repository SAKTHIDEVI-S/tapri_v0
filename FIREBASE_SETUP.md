# Firebase Phone Authentication Setup

## Prerequisites

1. **Firebase Project**: Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. **Android App**: Your Android app should be configured with the correct package name (`com.tapri`)

## Setup Steps

### 1. Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Create a project" or "Add project"
3. Enter project name: `tapri-app` (or your preferred name)
4. Follow the setup wizard

### 2. Add Android App to Firebase

1. In your Firebase project, click the Android icon to add an Android app
2. Enter package name: `com.tapri`
3. Enter app nickname: `Tapri`
4. Click "Register app"

### 3. Download Configuration File

1. Download the `google-services.json` file
2. Place it in the `app/` directory of your Android project
3. **Replace** the placeholder `google-services.json` file with the downloaded one

### 4. Enable Phone Authentication (CRITICAL STEP)

1. In Firebase Console, go to **Authentication**
2. Click **Sign-in method** tab
3. Find **Phone** in the list of providers
4. Click on **Phone** provider
5. **Toggle the switch to ENABLE** Phone Authentication
6. Click **Save**

### 5. Configure Test Phone Numbers (for Development)

1. In the Phone Authentication settings, scroll down to **Phone numbers for testing**
2. Click **Add phone number**
3. Add your test phone number (e.g., `+91 9999999999`)
4. Add a test code (e.g., `123456`)
5. Click **Save**

### 6. Configure SHA-1 Certificate (for Release)

For production, you'll need to add your app's SHA-1 certificate fingerprint:

1. Get your debug SHA-1:
   ```bash
   keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
   ```

2. Add the SHA-1 to your Firebase project:
   - Go to Project Settings
   - Add fingerprint under "Your apps" section

### 7. Test Phone Authentication

1. **Build and run** your Android app
2. **Sign up** with a mobile number
3. **Login** with the same mobile number
4. **Enter the OTP** received via SMS

## Troubleshooting

### Common Issues

1. **"Verification failed this process is not allowed"**
   - **SOLUTION**: Phone Authentication is not enabled in Firebase Console
   - Go to Firebase Console → Authentication → Sign-in method → Enable Phone provider
   - Make sure you've saved the settings after enabling

2. **"Invalid phone number format"**
   - Ensure phone number includes country code (+91 for India)
   - Format: `+91XXXXXXXXXX`

3. **"Quota exceeded"**
   - Firebase has daily limits for phone authentication
   - Use test phone numbers during development

4. **"App not authorized"**
   - Check if `google-services.json` is in the correct location
   - Verify package name matches Firebase configuration
   - Add SHA-1 certificate fingerprint to Firebase project

5. **"Network error"**
   - Check internet connectivity
   - Verify Firebase project is properly configured

### Test Phone Numbers

For testing, you can use these test phone numbers:
- `+91 9999999999` (any 10-digit number)
- Firebase will send a test OTP without actual SMS

### Debug Mode

Enable debug logging:
```kotlin
FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099)
```

## Security Considerations

1. **Rate Limiting**: Implement rate limiting for OTP requests
2. **User Verification**: Always verify user identity before sensitive operations
3. **Token Management**: Handle Firebase tokens securely
4. **Backend Verification**: Verify Firebase tokens on your backend

## Production Deployment

1. **Add Release SHA-1**: Add your release certificate fingerprint
2. **Configure App Check**: Enable App Check for additional security
3. **Monitor Usage**: Set up Firebase Analytics and monitoring
4. **Error Handling**: Implement proper error handling for all scenarios

## Support

- [Firebase Phone Auth Documentation](https://firebase.google.com/docs/auth/android/phone-auth)
- [Firebase Console](https://console.firebase.google.com/)
- [Firebase Support](https://firebase.google.com/support) 