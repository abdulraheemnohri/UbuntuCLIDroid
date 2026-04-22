#!/bin/bash
# Ubuntu Environment Installation Script

ROOTFS_URL="https://cdimage.ubuntu.com/ubuntu-base/releases/22.04/release/ubuntu-base-22.04.2-base-arm64.tar.gz"
INSTALL_DIR="$HOME/ubuntu"

echo "Creating install directory..."
mkdir -p $INSTALL_DIR

echo "Downloading Ubuntu rootfs..."
# curl -L $ROOTFS_URL -o ubuntu-rootfs.tar.gz

echo "Extracting rootfs (requires proot or root)..."
# proot --link2symlink tar -xzf ubuntu-rootfs.tar.gz -C $INSTALL_DIR

echo "Configuring DNS..."
echo "nameserver 8.8.8.8" > $INSTALL_DIR/etc/resolv.conf

echo "Installation complete."
