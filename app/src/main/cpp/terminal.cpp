#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_ubuntucli_TerminalViewKt_processTerminalOutput(JNIEnv* env, jclass clazz, jstring input) {
    // Simple pass-through for now
    return input;
}
