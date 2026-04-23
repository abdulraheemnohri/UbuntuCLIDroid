#!/bin/bash
# UbuntuCLI Droid System Installer

INSTALL_DIR="$HOME/ubuntu"

echo "Installing UbuntuCLI Droid..."
mkdir -p "$INSTALL_DIR"

echo "nameserver 8.8.8.8" > "$INSTALL_DIR/etc/resolv.conf"

cat <<OUT > "$INSTALL_DIR/root/.bashrc"
export TERM=xterm-256color
export PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
alias ll='ls -la'
echo "Welcome to UbuntuCLI Droid"
OUT
