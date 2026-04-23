cmake_minimum_required(VERSION 3.22.1)
project("ubuntucli")

add_library(
        ubuntucli
        SHARED
        pty.cpp
        terminal.cpp
        process.cpp
        jni_bridge.cpp)

find_library(log-lib log)

target_link_libraries(
        ubuntucli
        ${log-lib})
