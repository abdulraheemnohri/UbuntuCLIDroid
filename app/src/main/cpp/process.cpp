#include <jni.h>
#include <sys/wait.h>
#include <signal.h>
#include <unistd.h>

extern "C" JNIEXPORT jint JNICALL
Java_com_ubuntucli_terminal_TerminalSession_waitFor(JNIEnv* env, jobject thiz, jint pid) {
    int status;
    waitpid(pid, &status, 0);
    return WIFEXITED(status) ? WEXITSTATUS(status) : -1;
}

extern "C" JNIEXPORT void JNICALL
Java_com_ubuntucli_terminal_TerminalSession_terminateProcess(JNIEnv* env, jobject thiz, jint pid) {
    kill(pid, SIGKILL);
}
