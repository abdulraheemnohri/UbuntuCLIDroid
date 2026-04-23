#include <jni.h>
#include <unistd.h>
#include <sys/wait.h>

extern "C" JNIEXPORT jint JNICALL
Java_com_ubuntucli_ShellSession_waitFor(JNIEnv* env, jobject thiz, jint pid) {
    int status;
    waitpid(pid, &status, 0);
    return WIFEXITED(status) ? WEXITSTATUS(status) : -1;
}
