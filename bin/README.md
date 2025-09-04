# Tapri Android App – Onboarding Starter Project

This is a starter Android project for the Tapri app, focusing on the onboarding and login flow for commercial drivers.

## Features
- Splash screen with logo
- Onboarding carousel (Join Us, Earn More, Compare)
- Login (mobile number, OTP)
- Sign Up (name, location, state, terms)
- MVVM-ready structure
- Material Design UI

## Setup Instructions

### 1. Clone or Download
Clone this repository or copy the project files to your local machine.

### 2. Open in Android Studio
- Open the project folder in Android Studio.
- Let Gradle sync and build the project.

### 3. Add Images
Place the following images in `app/src/main/res/drawable/`:

| Image Purpose         | Suggested Filename         |
|----------------------|---------------------------|
| App logo (splash)    | `tapri_logo.png`          |
| Onboarding: Join Us  | `onboarding_join.png`     |
| Onboarding: Earn More| `onboarding_earn.png`     |
| Onboarding: Compare  | `onboarding_compare.png`  |

You can use the images provided in your Figma or export them as PNGs. Make sure the filenames match those above, or update the code to match your filenames.

### 4. Build & Run
- Select an emulator or connect a device.
- Click **Run** in Android Studio.
- The app will launch with the onboarding flow.

## Customization
- Update UI layouts in `app/src/main/res/layout/` as needed.
- Add real backend logic in the ViewModels and Repository layers.
- Expand with additional features as per your PRD.

## Tech Stack
- Kotlin
- MVVM
- ViewPager2
- Material Components
- AndroidX Navigation

---

For any issues or questions, please refer to the code comments or reach out to your development team. 