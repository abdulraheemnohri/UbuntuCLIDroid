#!/bin/bash
# Ubuntu Droid Installation Script

INSTALL_DIR="$HOME/ubuntu"
ROOTFS_URL="https://cdimage.ubuntu.com/ubuntu-base/releases/22.04/release/ubuntu-base-22.04.2-base-arm64.tar.gz"

echo "[*] Creating installation directory..."
mkdir -p "$INSTALL_DIR"

echo "[*] Downloading Ubuntu RootFS..."
# curl -L "$ROOTFS_URL" -o ubuntu-rootfs.tar.gz

echo "[*] Extracting RootFS..."
# proot --link2symlink tar -xzf ubuntu-rootfs.tar.gz -C "$INSTALL_DIR"

echo "[*] Configuring DNS..."
echo "nameserver 8.8.8.8" > "$INSTALL_DIR/etc/resolv.conf"

echo "[*] Setting up profile..."
echo "export HOME=/root" > "$INSTALL_DIR/root/.bashrc"
echo "export PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin" >> "$INSTALL_DIR/root/.bashrc"
echo "alias ll='ls -la'" >> "$INSTALL_DIR/root/.bashrc"

echo "[+] Installation finished."
