# 📍 GeoFence Tracker

GeoFence Tracker is an Android application that allows users to create location-based geofences and automatically track entry, exit, and time spent within those locations. The app stores visit history locally and works reliably on Android 10 and above.

---

## 🚀 Features

- 📌 Add geofences on a map using long-press
- 📏 Configurable geofence radius (10m – 50m)
- 📍 Detect user **entry and exit** from geofenced locations
- ⏱️ Calculate **duration spent** inside a geofence
- 💾 Store geofence and visit data using Room Database
- 🔔 Notifications on geofence entry and exit
- 🗂️ View:
  - List of all geofenced locations
  - Visit history with entry time, exit time, and duration
- 🔄 Geofences persist even after app restart

---

## 🛠️ Tech Stack

- **Language:** Kotlin  
- **UI:** XML Layouts  
- **Architecture:** MVVM  
- **Database:** Room DB  
- **Maps & Location:** Google Maps SDK, Geofencing API  
- **Minimum Android Version:** Android 10 (API 29)

> ℹ️ Note: Jetpack Compose is not used in the current version. The UI is implemented using XML for stability. Migration to Jetpack Compose is planned in a future iteration.

---

## 📱 Screens

### 1. Map Screen
- Displays Google Map
- Add geofence via long-press
- Shows geofence markers and radius circles

### 2. Geofence List Screen
- Location name
- Latitude & Longitude
- Radius
- Created date & time

### 3. Visit History Screen
- Geofence name
- Date
- Entry time
- Exit time
- Duration spent

---

## 🔐 Permissions Used

- `ACCESS_FINE_LOCATION`
- `ACCESS_BACKGROUND_LOCATION`
- `POST_NOTIFICATIONS` (Android 13+)

These permissions are required for accurate geofence detection and user notifications.

---

## ▶️ Demo Video

📹 Demo video of the application:  
**[Google Drive link coming soon]**

---

## 🧪 Tested On

- Android 10 and above
- Real device testing
- App restart and background execution scenarios

---

## 📌 Future Improvements

- Migrate UI to Jetpack Compose
- Edit existing geofence radius
- Export visit history
- UI/UX improvements

---

## 📄 License

This project is licensed under the **MIT License**.

---

## 👤 Author :- Shivam Kumar
## 🤝 Get in Touch
If you’re interested in a similar project or collaboration, feel free to reach out:  
🌐 https://shivamkumarptu.github.io/Business-Site/


