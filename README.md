# 🔋 BatteryNotifier

BatteryNotifier is a lightweight Java application that runs in the background and sends desktop notifications when your battery reaches a specific charge percentage (like 80–85%). It's designed to help users unplug their charger in time and preserve long-term battery health.

---

## ✨ Features

- 🔔 Sends notification when battery level hits your specified threshold.
- 🛡 Helps maintain battery longevity by avoiding overcharging.
- 🖥 Designed for macOS (can be modified for other platforms).
- ✅ Easy to toggle on/off with a simple Java interface.

---

## ⚙️ How It Works

- Continuously checks your battery status at fixed intervals (e.g., every 2 minutes).
- When the charge hits your chosen level and you're still plugged in, a desktop notification is triggered.
- You can customize the message and interval time.

---

## 🖥 Requirements

- Java 11 or above
- macOS (or modify the notification part for other platforms)

---

## 🚀 Usage

1. Compile the program:

```bash
javac BatteryNotifier.java
