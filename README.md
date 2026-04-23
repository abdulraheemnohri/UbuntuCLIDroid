# UbuntuCLI Droid

🚀 **UbuntuCLI Droid** is a high-performance terminal emulator and Ubuntu Linux environment for Android. It provides a production-grade experience for developers, running a real Ubuntu rootfs via `proot`.

## ✨ Key Features

- **🐧 Ubuntu Environment**: Full ARM64 Ubuntu support without root access.
- **🖥️ Advanced Terminal**: PTY-based interactive shell with multi-tab support.
- **📦 Package Management**: Integrated UI for `apt` operations and package discovery.
- **📂 File Manager**: Browse and manage `/root`, `/sdcard`, and system paths.
- **🔌 Plugin System**: Auto-loading shell-based plugins for extensibility.
- **📊 System Monitor**: Real-time CPU and Memory tracking within the app.
- **🎨 Modern UI**: Built with Jetpack Compose, supporting hacker-themed aesthetics.

## 🏗️ Tech Stack
- **Kotlin & Jetpack Compose**: Modern Android frontend.
- **C++ (NDK)**: Native Pseudo-terminal (PTY) and process management.
- **proot**: User-space implementation of `chroot`, `mount`, and `id`.

## 🚀 Getting Started

### Prerequisites
- Android Studio Jellyfish or later.
- Android NDK and CMake installed via SDK Manager.

### Building
1. Clone the repository.
2. Open in Android Studio.
3. Sync Gradle and build the `:app` module.
4. Run on an ARM64 Android device.

### Initial Setup
1. Open the Terminal tab.
2. Run `bash scripts/install.sh` to download and extract the Ubuntu rootfs.
3. Use `bash scripts/start.sh` to enter the environment.

## 📁 Project Layout
- `app/src/main/cpp`: Native PTY and JNI logic.
- `app/src/main/java/com/ubuntucli`: Core application logic and UI.
- `scripts/`: Essential environment management scripts.
- `core/`: Architectural foundations for terminal and sessions.

## 🔐 Security
- Fully sandboxed execution.
- No root permissions required for core operations.
- Optional PIN and biometric lock for app access.

---
*Built for the portable developer workstation of the future.*
