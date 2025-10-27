# API Keys Setup Guide

This project uses `local.properties` to store sensitive API keys and credentials securely. These keys are **NOT** committed to version control.

## Quick Setup

1. **Copy the template file:**
   ```bash
   cp local.properties.example local.properties
   ```

2. **Edit `local.properties` and add your actual API keys**

3. **Never commit `local.properties` to git** (it's already in `.gitignore`)

## Required API Keys

### 1. Firebase Web Client ID

**Where to get it:**
- Go to [Firebase Console](https://console.firebase.google.com/)
- Select your project
- Go to **Project Settings** → **General**
- Scroll down to **Your apps** section
- Copy the **Web API Key**

OR from `google-services.json`:
- Look for `client[].oauth_client[]` where `client_type: 3`
- Copy the `client_id` value

**Add to `local.properties`:**
```properties
FIREBASE_WEB_CLIENT_ID=your-actual-firebase-web-client-id-here
```

### 2. Google AdMob IDs

**Where to get them:**
- Go to [AdMob Console](https://apps.admob.com/)
- Select your app
- Go to **Ad units**
- Copy each Ad Unit ID

**Add to `local.properties`:**
```properties
# AdMob Application ID (from AdMob Console -> App settings)
ADMOB_APP_ID=ca-app-pub-XXXXXXXXXXXXXXXX~XXXXXXXXXX

# Ad Unit IDs
ADMOB_APP_OPEN_AD_UNIT_ID=ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX
ADMOB_INTERSTITIAL_AD_UNIT_ID=ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX
ADMOB_REWARDED_AD_UNIT_ID=ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX
ADMOB_BANNER_AD_UNIT_ID=ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX
```

## Testing with Google's Test IDs

If you don't have production Ad Unit IDs yet, the app will automatically use Google's official test IDs (already configured as fallback values).

**Google's Test IDs:**
- App ID: `ca-app-pub-3940256099942544~3347511713`
- App Open: `ca-app-pub-3940256099942544/9257395921`
- Interstitial: `ca-app-pub-3940256099942544/1033173712`
- Rewarded: `ca-app-pub-3940256099942544/5224354917`
- Banner: `ca-app-pub-3940256099942544/9214589741`

## Security Best Practices

1. **Never commit sensitive keys to git**
2. **Always use `local.properties.example` as a template for team members**
3. **Rotate keys immediately if accidentally exposed**
4. **Use Firebase App Check for additional security**
5. **Set up proper Firebase security rules**

## Troubleshooting

### Build fails with "FIREBASE_WEB_CLIENT_ID not found"
- Make sure `local.properties` exists in the root directory
- Verify the key is properly formatted (no extra spaces)
- Try cleaning and rebuilding: `./gradlew clean build`

### Ads not showing
- Check LogCat for AdMob error messages
- Verify Ad Unit IDs are correct in `local.properties`
- Ensure test device is registered in AdMob Console
- Wait a few hours after creating new ad units

## How It Works

1. `build.gradle.kts` reads API keys from `local.properties`
2. Keys are injected into `BuildConfig` at compile time
3. Code references `BuildConfig.ADMOB_APP_OPEN_AD_UNIT_ID` etc.
4. If a key is missing, fallback test IDs are used (for AdMob only)

## Files Modified

- `app/build.gradle.kts` - Reads keys and creates BuildConfig fields
- `app/src/main/AndroidManifest.xml` - Uses `${ADMOB_APP_ID}` placeholder
- `presentation/ad/AppOpenAdManager.kt` - Uses `BuildConfig.ADMOB_APP_OPEN_AD_UNIT_ID`
- `presentation/ad/InterstitialAdManager.kt` - Uses `BuildConfig.ADMOB_INTERSTITIAL_AD_UNIT_ID`
- `presentation/ad/RewardedAdManager.kt` - Uses `BuildConfig.ADMOB_REWARDED_AD_UNIT_ID`
- `presentation/ad/BannerAdView.kt` - Uses `BuildConfig.ADMOB_BANNER_AD_UNIT_ID`

## Important Notes

- **DO NOT** commit `local.properties` to git
- **DO** commit `local.properties.example` as a template
- **DO** share this setup guide with your team
- If you accidentally commit API keys, **immediately rotate them** in Firebase/AdMob Console
