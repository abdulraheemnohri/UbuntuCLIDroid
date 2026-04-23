# UbuntuCLI Droid

🚀 **UbuntuCLI Droid** is a modern, feature-rich Android terminal emulator and Ubuntu Linux environment runner. It provides a production-grade Linux experience on Android using `proot`, with advanced UI capabilities and AI-powered command assistance.

## ✨ Features

- **🐧 Real Ubuntu Environment**: Run a full Ubuntu 22.04 LTS (ARM64) rootfs without root access.
- **🖥️ Advanced Terminal**:
  - Multi-tab session management.
  - **Split-screen** mode for multitasking.
  - Interactive PTY-based execution.
  - Scrollback buffer and auto-scrolling.
  - Copy/Paste support via tap and long-press.
- **🧠 AI CLI Mode**: Integrated AI engine for command suggestions and error troubleshooting.
- **🔌 Plugin System**: Load and run custom shell scripts from the `plugins/` directory.
- **📦 Package Manager**: Simple UI interface for `apt` operations.
- **📂 File Bridge**: Direct access to Android `/sdcard` storage from within the Linux environment.
- **📊 System Monitor**: Real-time monitoring of CPU, Memory, Uptime, and active processes.
- **🎨 Customization**: Hacker-themed UI with multiple color schemes (Green, Amber, White).
- **🔐 Security**: Optional App PIN lock for securing your terminal sessions.

## 🏗️ Tech Stack
- **Frontend**: Kotlin & Jetpack Compose
- **Backend/Native**: C++/NDK for Pseudo-terminal (PTY) management.
- **Runtime**: `proot` for user-space root emulation.
- **CI/CD**: GitHub Actions for automated Debug APK generation.

## 🚀 Getting Started

### 1. Build & Install
1. Open the project in **Android Studio**.
2. Sync Gradle and ensure NDK/CMake are installed.
3. Run `./gradlew assembleDebug` or use the GitHub Actions artifact.
4. Install the generated APK on an ARM64 device.

### 2. Setup Ubuntu
1. Launch the app and open a Terminal tab.
2. Run the installation script:
   ```bash
   bash scripts/install.sh
   ```
3. Once finished, enter the environment:
   ```bash
   bash scripts/start.sh
   ```

## 📁 Project Structure
- `app/src/main/java`: Core Kotlin logic and Compose UI.
- `app/src/main/cpp`: Native PTY and process management.
- `scripts/`: Essential shell scripts for environment setup.
- `.github/workflows/`: CI configuration.

## 📈 Roadmap
- [ ] SSH Server integration.
- [ ] X11/VNC support for graphical Linux apps.
- [ ] Cloud-synced configurations.

---
*Developed for the Linux enthusiasts and Android power users.*
