#include <jni.h>
#include <string>
#include <termios.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/ioctl.h>
#include <android/log.h>
#include <stdlib.h>

#define LOG_TAG "UbuntuCLI_PTY"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jint JNICALL
Java_com_ubuntucli_terminal_TerminalSession_createPty(JNIEnv* env, jobject thiz, jstring shellPath, jobjectArray args, jobjectArray envp, jintArray pProcessId) {
    int master = posix_openpt(O_RDWR | O_NOCTTY);
    if (master < 0) return -1;
    if (grantpt(master) < 0 || unlockpt(master) < 0) return -1;

    char *pts_name = ptsname(master);

    pid_t pid = fork();

    if (pid < 0) return -1;

    if (pid == 0) {
        // Child
        int slave = open(pts_name, O_RDWR);
        close(master);

        setsid();
        ioctl(slave, TIOCSCTTY, 0);

        dup2(slave, 0);
        dup2(slave, 1);
        dup2(slave, 2);
        close(slave);

        const char *c_shell_path = env->GetStringUTFChars(shellPath, NULL);
        execl(c_shell_path, c_shell_path, "-", (char *)NULL);
        exit(1);
    } else {
        // Parent
        jint* pids = env->GetIntArrayElements(pProcessId, NULL);
        pids[0] = pid;
        env->ReleaseIntArrayElements(pProcessId, pids, 0);

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
