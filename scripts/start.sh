#!/bin/bash
# UbuntuCLI Droid Bootloader

INSTALL_DIR="$HOME/ubuntu"

proot \
    --rootfs="$INSTALL_DIR" \
    --bind=/dev \
    --bind=/proc \
    --bind=/sys \
    --bind=/sdcard \
    /usr/bin/env -i \
    HOME=/root \
    TERM=xterm-256color \
    PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin \
    /bin/bash --login
