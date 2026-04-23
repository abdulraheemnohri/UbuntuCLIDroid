# UbuntuCLI Droid

🚀 **UbuntuCLI Droid** is a high-performance terminal emulator and Ubuntu Linux environment for Android. It is designed as a professional developer workstation, providing a real Ubuntu rootfs via `proot` with a modern, feature-rich interface.

## ✨ Production Features

- **🐧 Minimal Ubuntu Stack**: Pre-configured Ubuntu 20.04 LTS (ARM64) with optimized sources and DNS.
- **🖥️ Pro Terminal**:
  - PTY-based interactive shell.
  - **Multi-tab** and **Split-screen** support.
  - Virtual keys (Ctrl, Alt, Tab, Esc).
  - Persistence of session history.
- **📦 Package Management**: Full `apt` support with a dedicated management UI.
- **🔌 Plugin Engine**: Auto-loading bash scripts from `/root/plugins`.
- **📂 File System Bridge**: Integrated file explorer for `/root`, `/sdcard`, and system paths.
- **📊 System Monitoring**: Real-time CPU, RAM, and process viewer (Htop Lite).
- **🎨 Customization**: Persisted settings for font size, themes, and more.
- **🔐 Secure Foundation**: Sandboxed execution, optional biometric/PIN lock.

## 🏗️ Technical Architecture
- **Native**: C++ NDK implementation of `forkpty` and process management.
- **UI**: 100% Jetpack Compose for a fast, modern Material3 experience.
- **Runtime**: `proot` for user-space implementation of `chroot` and bind mounts.

## 🚀 Setup & Build

### Requirements
- Android SDK 24+
- Android NDK & CMake
- ARM64 hardware

### Building
```bash
./gradlew assembleDebug
```

### Initial Launch
1. Launch the app and wait for the **Booting DNA Layer** initialization.
2. The app will extract the optimized rootfs and apply default configurations.
3. Once ready, you'll be dropped into a real Ubuntu bash shell.

## 📁 Project Structure
- `app/src/main/cpp`: High-performance PTY layer.
- `app/src/main/java/com/ubuntucli`: Clean, modular Kotlin logic.
- `app/src/main/assets`: Ubuntu "DNA" (rootfs + default configs).
- `.github/workflows`: CI for automated APK delivery.

---
*Built for the next generation of mobile Linux power users.*
