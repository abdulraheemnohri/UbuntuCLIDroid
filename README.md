# UbuntuCLI Droid

🚀 **UbuntuCLI Droid** is a professional, modern terminal emulator and Linux environment for Android. It provides a production-grade developer workstation on your mobile device, running a real Ubuntu rootfs (ARM64) via `proot`.

## ✨ Key Features

- **🐧 Minimal Ubuntu Stack**: Pre-configured Ubuntu 22.04 LTS (ARM64) environment.
- **🖥️ Pro Terminal Engine**: PTY-based shell with multi-tab and split-screen support.
- **📦 Package Manager**: Integrated UI for `apt` operations and package discovery.
- **📂 File Bridge**: Advanced file explorer for `/root`, `/sdcard`, and system paths.
- **📊 System Monitoring**: Real-time CPU, RAM, and process tracking.
- **🔌 Plugin System**: Auto-loading shell-based plugins for extensibility.
- **🎨 Modern UI**: Built with 100% Jetpack Compose and Material 3.

## 🏗️ Technical Architecture

- **Native**: C++ NDK implementation of `forkpty` and process management.
- **UI**: Clean, modular Kotlin logic with ViewModel persistence.
- **Runtime**: `proot` for user-space root emulation (no root required).
- **CI/CD**: Automated GitHub Actions workflow for Debug APK generation.

## 🚀 Getting Started

### Prerequisites
- Android Studio Jellyfish or later.
- Android NDK & CMake.

### Building
```bash
./gradlew assembleDebug
```

### Setup
1. Launch the app and wait for the **DNA Layer** initialization.
2. The app will extract the optimized rootfs and apply default configurations.
3. Once ready, use the terminal to install tools via `apt`.

---
*Built for the next generation of mobile Linux power users.*
