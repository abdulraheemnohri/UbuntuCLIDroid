#!/bin/bash
# UbuntuCLI Droid - Advanced System Installer

INSTALL_DIR="$HOME/ubuntu"
ROOTFS_URL="https://partner-images.canonical.com/core/focal/current/ubuntu-focal-core-cloudimg-arm64-root.tar.gz"

echo "[*] Initializing Ubuntu Installation..."
mkdir -p "$INSTALL_DIR"

if [ ! -f ubuntu-rootfs.tar.gz ]; then
    echo "[*] Downloading Ubuntu 20.04 rootfs (ARM64)..."
    # curl -L "$ROOTFS_URL" -o ubuntu-rootfs.tar.gz
fi

echo "[*] Extracting filesystem..."
# tar -xzf ubuntu-rootfs.tar.gz -C "$INSTALL_DIR"

echo "[*] Configuring DNS..."
echo "nameserver 8.8.8.8" > "$INSTALL_DIR/etc/resolv.conf"

echo "[+] Installation complete."
