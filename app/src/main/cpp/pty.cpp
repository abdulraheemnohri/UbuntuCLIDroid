#include <jni.h>
#include <string>
#include <termios.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/ioctl.h>
#include <util.h>
#include <android/log.h>

#define LOG_TAG "UbuntuCLI_PTY"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jint JNICALL
Java_com_ubuntucli_terminal_TerminalSession_createPty(JNIEnv* env, jobject thiz, jstring shellPath, jobjectArray args, jobjectArray envp, jintArray pProcessId) {
    int master;
    pid_t pid = forkpty(&master, NULL, NULL, NULL);

    if (pid < 0) return -1;

    if (pid == 0) {
        // Child
        const char *c_shell_path = env->GetStringUTFChars(shellPath, NULL);

        // Setup environment
        if (envp != NULL) {
            jsize env_len = env->GetArrayLength(envp);
            for (int i = 0; i < env_len; i++) {
                jstring env_str = (jstring)env->GetObjectArrayElement(envp, i);
                const char *c_env = env->GetStringUTFChars(env_str, NULL);
                putenv(strdup(c_env));
                env->ReleaseStringUTFChars(env_str, c_env);
            }
        }

        execl(c_shell_path, c_shell_path, "-", (char *)NULL);
        exit(1);
    } else {
        // Parent
        jint* pids = env->GetIntArrayElements(pProcessId, NULL);
        pids[0] = pid;
        env->ReleaseIntArrayElements(pProcessId, pids, 0);

        // Set non-blocking
        int flags = fcntl(master, F_GETFL);
        fcntl(master, F_SETFL, flags | O_NONBLOCK);

        return master;
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_ubuntucli_terminal_TerminalSession_setPtyWindowSize(JNIEnv* env, jobject thiz, jint fd, jint rows, jint cols) {
    struct winsize sz;
    sz.ws_row = (unsigned short)rows;
    sz.ws_col = (unsigned short)cols;
    sz.ws_xpixel = 0;
    sz.ws_ypixel = 0;
    ioctl(fd, TIOCSWINSZ, &sz);
}
