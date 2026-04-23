#!/bin/bash
# UbuntuCLI Droid - Environment Runner

INSTALL_DIR="$HOME/ubuntu"

if [ ! -d "$INSTALL_DIR" ]; then
    echo "[!] Error: RootFS not found."
    # exit 1
fi

echo "[*] Booting Ubuntu Environment..."

# Execution via proot
proot \
    --rootfs="$INSTALL_DIR" \
    --bind=/dev \
    --bind=/proc \
    --bind=/sys \
    --bind=/sdcard \
    --bind=/storage \
    /usr/bin/env -i \
    HOME=/root \
    TERM=xterm-256color \
    PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin \
    /bin/bash --login
