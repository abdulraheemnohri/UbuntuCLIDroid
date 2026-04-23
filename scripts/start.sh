#!/bin/bash
# Ubuntu Environment Startup Script

INSTALL_DIR="$HOME/ubuntu"

echo "Starting Ubuntu CLI Droid environment..."

proot     --rootfs=$INSTALL_DIR     --bind=/dev     --bind=/proc     --bind=/sys     --bind=/sdcard     /usr/bin/env -i     HOME=/root     TERM=xterm-256color     PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin     /bin/bash --login
