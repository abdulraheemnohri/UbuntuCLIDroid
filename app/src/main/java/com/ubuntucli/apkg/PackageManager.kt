package com.ubuntucli.apkg

class PackageManager {
    fun getAptInstallCmd(pkg: String) = "apt install $pkg -y"
    fun getAptRemoveCmd(pkg: String) = "apt remove $pkg -y"
    fun getAptUpdateCmd() = "apt update"
    fun getPopularPackages(): List<String> = listOf("vim", "git", "curl", "python3", "nodejs", "gcc", "make", "htop", "wget")
}
