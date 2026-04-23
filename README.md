# UbuntuCLI Droid

🚀 **UbuntuCLI Droid** is a high-performance terminal emulator and Linux environment for Android. It provides a real Ubuntu rootfs (ARM64) running via `proot`, enabling a production-grade developer workstation on your mobile device.

## ✨ Key Features

- **🐧 Minimal Ubuntu System**: Pre-configured Ubuntu 20.04 LTS environment with optimized network and environment layers.
- **🖥️ Pro Terminal Engine**: PTY-based shell with multi-tab, **split-screen** support, and virtual keys (Arrows, ESC, TAB).
- **📦 Package Management**: Integrated UI and CLI for `apt` operations and package discovery.
- **📂 File Manager**: Advanced file explorer for `/root`, `/sdcard`, and system paths with navigation and management.
- **📊 System Monitor**: Real-time CPU, Memory, and Process tracking.
- **🔌 Plugin System**: Auto-loading shell-based plugins for custom extensibility.
- **🎨 Modern UI**: Built with 100% Jetpack Compose and Material 3 design principles.

## 🏗️ Technical Architecture

- **Native Layer**: C++ NDK implementation of `forkpty` and process management for maximum efficiency.
- **Frontend**: Clean, modular Kotlin logic with ViewModel persistence and gesture support.
- **Runtime**: `proot` for user-space root emulation (no root required), ensuring safety and compatibility.
- **DNA Layer**: Automatic initialization, integrity verification, and self-healing system setup.

## 🚀 Getting Started

### Prerequisites
- Android device (ARM64).
- Android SDK 24+ support.

### Build Instructions
```bash
./gradlew assembleDebug
```

### First Launch
Launch the application and wait for the **Booting DNA Layer** initialization to complete. Once the "System Ready" message appears, you'll be dropped into a real Ubuntu bash shell. Use `apt install <package>` to add your favorite tools.

---
*Built for the portable developer workstation of the future.*
