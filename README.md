# UbuntuCLI Droid

🚀 **UbuntuCLI Droid** is a full-featured Android application that provides a real Ubuntu Linux CLI environment similar to Termux, but with a modern UI, extensibility, and AI-powered features.

## 🏗️ Tech Stack
- **Frontend**: Kotlin & Jetpack Compose
- **Backend/Native**: C/C++ (NDK) for PTY management
- **Linux Runtime**: proot (no root required)
- **Ubuntu**: Ubuntu 22.04 LTS (ARM64)

## 📁 Project Structure
- `app/`: Main Android application code and native sources.
- `scripts/`: Installation and startup scripts for the Ubuntu environment.
- `plugins/`: Extensibility system for shell-based plugins.
- `ai/`: AI CLI mode integration logic.
- `core/`: Core architectural definitions.

## 🚀 Build Instructions
1. Open the project in **Android Studio**.
2. Ensure you have the **NDK** and **CMake** installed via SDK Manager.
3. Sync Gradle and build the project.
4. Run on an ARM64 Android device.

## 🐧 First Launch Setup
Upon launch, the app will initialize the PTY. To install the full Ubuntu rootfs:
1. Run `scripts/install.sh` via the terminal.
2. Once complete, use `scripts/start.sh` to enter the Ubuntu environment.

## 🧩 Features
- **Interactive Terminal**: Real-time shell with hacker-style UI.
- **Package Manager**: UI wrappers for `apt`.
- **Plugin System**: Load custom shell scripts as plugins.
- **AI CLI Mode**: Get command suggestions and help via `ubuntu-ai`.

## 📈 Roadmap
- [ ] SSH Server support.
- [ ] Multi-tab and split-screen terminal.
- [ ] X11/VNC support for GUI apps.
- [ ] Cloud sync for configurations.

## 🔐 Security
- Fully sandboxed.
- No root required.
- Permission-based storage access.

---
*Built with ❤️ for the Android Linux community.*
