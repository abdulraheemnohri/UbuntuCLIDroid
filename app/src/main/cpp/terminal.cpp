#include <jni.h>
#include <string>

// Optimized for future ANSI processing logic
extern "C" JNIEXPORT jstring JNICALL
Java_com_ubuntucli_terminal_AnsiParser_processBuffer(JNIEnv* env, jobject thiz, jstring input) {
    return input;
}
