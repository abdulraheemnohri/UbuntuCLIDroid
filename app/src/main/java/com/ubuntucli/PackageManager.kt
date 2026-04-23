package com.ubuntucli

class PackageManager {
    fun installPackage(packageName: String): String {
        return "apt install $packageName"
    }

    fun removePackage(packageName: String): String {
        return "apt remove $packageName"
    }

    fun updatePackages(): String {
        return "apt update && apt upgrade"
    }
}
