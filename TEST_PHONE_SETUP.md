# How to Add Test Phone Numbers in Firebase Console

## Why Test Phone Numbers?
During development, Firebase doesn't send real SMS messages to avoid costs and spam. Instead, you need to add test phone numbers that will use predefined OTP codes.

## Step-by-Step Setup

### 1. Go to Firebase Console
1. Open [Firebase Console](https://console.firebase.google.com/)
2. Select your project: `tapri-9e996`

### 2. Navigate to Phone Authentication Settings
1. Click **Authentication** in the left sidebar
2. Click **Sign-in method** tab
3. Find **Phone** provider and click on it

### 3. Add Test Phone Numbers
1. Scroll down to **Phone numbers for testing** section
2. Click **Add phone number**
3. **IMPORTANT**: Enter your test phone number exactly as: `+91 63839 81590` (with space after +91 and in the middle)
4. Enter a test code: `123456`
5. Click **Save**

### 4. Add More Test Numbers (Optional)
You can add multiple test phone numbers:
- `+91 9999999999` with code `654321`
- `+91 7777777777` with code `111111`
- Your actual phone number with any code you want

## How to Use Test Phone Numbers

### In Your App:
1. **Enter the test phone number** in your app (e.g., `6383981590` - just the 10 digits)
2. **Click "Send OTP"**
3. **You'll see**: "Test OTP sent! Use test code from Firebase Console"
4. **Enter the test code** (e.g., `123456`) instead of waiting for SMS
5. **Login successful!**

### Example Test Flow:
1. App: Enter `6383981590` (10 digits only)
2. App: Click "Send OTP"
3. App: Automatically formats to `+91 63839 81590` (matches Firebase Console)
4. App: Shows "Test OTP sent! Use test code from Firebase Console"
5. App: Enter `123456` (the test code you set)
6. App: Login successful!

## Important Format Notes

### Firebase Console Format:
- **Use**: `+91 63839 81590` (with space after +91 and in the middle)
- **Don't use**: `+91 6383981590` (without space in the middle)

### App Input Format:
- **Enter**: `6383981590` (10 digits only)
- **App automatically formats to**: `+91 63839 81590` (matches Firebase Console exactly)

## Troubleshooting

### Issue: "No test phone numbers found"
**Solution**: 
1. Make sure you've added the exact phone number format: `+91 63839 81590`
2. Check that the spaces are in the correct positions
3. Verify you're in the correct Firebase project

### Issue: "Test code not working"
**Solution**: 
1. Check the test code in Firebase Console
2. Make sure you're using the correct phone number format
3. Try adding the phone number again with proper format

### Issue: "Still getting real SMS"
**Solution**: 
1. Make sure the phone number is exactly as added in Firebase Console
2. Check that you're in the correct Firebase project
3. Wait a few minutes for changes to take effect

## Production vs Development

### Development (Current Setup):
- Use test phone numbers
- No real SMS sent
- Fast and free testing

### Production:
- Remove test phone numbers
- Real SMS will be sent
- May incur costs based on usage

## Next Steps
1. Add your test phone numbers in Firebase Console with format: `+91 63839 81590`
2. Test the login flow with test codes
3. Once working, you can test with real phone numbers
4. For production, remove test phone numbers