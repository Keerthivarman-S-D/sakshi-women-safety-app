# SAKSHI – Women Safety SOS Application (MVP)

SAKSHI is an Android-based women safety application designed to provide **instant SOS alerts and real-time live location sharing** during emergency situations.  
This project is developed as a **hackathon MVP (30% completion)** with a strong focus on reliability, simplicity, and real-world usability.

---

## 🚨 Problem Statement

Women often face unsafe situations where:
- Immediate help is required
- Sharing live location quickly is difficult
- Existing solutions are complex or slow during emergencies

There is a need for a **simple, fast, and reliable SOS system** that works with minimal user interaction.

---

## 💡 Proposed Solution (MVP)

SAKSHI enables users to:
- Trigger an SOS with **one tap**
- Share **live location continuously**
- Instantly notify **trusted emergency contacts**

The MVP focuses on **emergency response**, with advanced safety features planned for future versions.

---

## ✨ Features Implemented (MVP – 30%)

- Phone number authentication using OTP  
- One-tap SOS emergency trigger  
- Countdown & cancel option to avoid false alerts  
- Real-time live location capture  
- Live location visualization using **OpenStreetMap (OSM)**  
- SOS alerts sent to emergency contacts  
- Emergency contacts management  
- Emergency profile with basic personal details  

---

## 🏗 Architecture Overview (MVP)

- **Android App (Kotlin + Jetpack)** – Client application  
- **Firebase Authentication** – Secure user login  
- **OpenStreetMap + Device GPS** – Map & live location  
- **Firebase Backend** – Data storage & SOS processing  
- **Firebase Cloud Messaging (FCM)** – Alert delivery  
- **Emergency Contacts** – Alert receivers  

---

## 🛠 Tech Stack

- **Platform:** Android  
- **Language:** Kotlin  
- **Architecture:** Jetpack (MVVM-based structure)  
- **Authentication:** Firebase Authentication (OTP)  
- **Backend:** Firebase  
- **Notifications:** Firebase Cloud Messaging (FCM)  
- **Maps:** OpenStreetMap (OSM)  
- **Location:** Android Device GPS  

---

## 📱 Demo

- **App Demo (Sender Phone):**  
  👉 *https://drive.google.com/file/d/1lhbnQYCSkdNNKT_q9N1O3Rhc-PpDqlyI/view?usp=drive_link*

- **SOS Alert & Live Location (Receiver Phone):**  
  👉 *https://drive.google.com/file/d/1RoPvYE7WRYSuguurTvZkVC_VpiK-iEPG/view?usp=drive_link*

> Both videos together demonstrate the complete SOS and live location flow.

---

## 📦 MVP Link

- **APK Download:**  
  👉 *https://drive.google.com/file/d/19xmCCwlJ6ZFu7aRoiIl18_aAOmq8ld2x/view?usp=drive_link*

---

## 🚀 How to Run the Project

1. Clone the repository:
   ```bash
   git clone https://github.com/Keerthivarman-S-D/sakshi-women-safety.git
