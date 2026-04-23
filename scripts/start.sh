#!/bin/bash
# UbuntuCLI Droid - Environment Bootloader

INSTALL_DIR="$HOME/ubuntu"

if [ ! -d "$INSTALL_DIR" ]; then
    echo "[!] Error: System not installed."
    # exit 1
fi

echo "[*] Booting Ubuntu..."

# Core proot execution logic
# proot \
#    --rootfs="$INSTALL_DIR" \
#    --bind=/dev \
#    --bind=/proc \
#    --bind=/sys \
#    --bind=/sdcard \
#    /usr/bin/env -i \
#    HOME=/root \
#    TERM=xterm-256color \
#    PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin \
#    /bin/bash --login
