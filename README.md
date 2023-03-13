# TV Launcher

TV Launcher (name to be determined) is an Android launcher meant for big screens like televisions. It can be used as a
replacement for the default Leanback or Google TV launcher on various Android TV devices like the Nvidia Shield or
Chromecast with Google TV.

The app is currently in an early development stage.

<img src="https://user-images.githubusercontent.com/2305178/186512479-e94bf85d-ac09-4f9d-b54e-24bcf43c82da.png" height="400" />

## Planned features

- Configurable toolbar

  The toolbar is shown on the top of the screen and contains various items that can be hidden and reordered by the user.
  The planned components that can be used are:

    - Clock
    - Settings button
    - Tv input sources switcher

- Favorite app list

  A list of bookmarked apps by the user. They can be shown or hidden from the main screen and reordered.

- Channels

  App provided channels and watch next information.

- All apps

  A grid with all installed apps. Ability to also show mobile app entries.

### Unplanned features

Some features are not implemented because of platform limitations. The following features can not be implemented:

- Wallpapers

  Android TV does not allow apps to set a wallpaper since there is no implementation for the
  [WallpaperManager actions](https://developer.android.com/reference/android/app/WallpaperManager.html).

- Search
## Installation

### Build

prerequisites: Android SDK
- `./gradlew --build-cache --no-daemon --info assembleDebug`

### Install

Install with adb to your android tv.
- Enable adb on android tv
- `adb connect <android_tv_IP>:5555`
- `adb install app/build/outputs/apk/debug/app-debug.apk`

### Define as default launcher

Disable default launcher
- `adb shell pm disable-user --user 0 com.google.android.tvlauncher`


### Revert
Enable default launcher
- `adb shell pm enable-user --user 0 com.google.android.tvlauncher`

