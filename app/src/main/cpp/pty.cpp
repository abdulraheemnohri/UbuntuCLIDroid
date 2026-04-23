#include <jni.h>
#include <string>
#include <termios.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/ioctl.h>
#include <android/log.h>
#include <signal.h>

#define LOG_TAG "UbuntuCLI_PTY"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jint JNICALL
Java_com_ubuntucli_ShellSession_createPty(JNIEnv* env, jobject thiz, jstring shell_path, jobjectArray args, jobjectArray envp, jintArray pProcessId) {
    int ptm;
    ptm = posix_openpt(O_RDWR | O_CLOEXEC);
    if (ptm < 0) return -1;
    if (grantpt(ptm) != 0 || unlockpt(ptm) != 0) return -1;

    int pts = open(ptsname(ptm), O_RDWR);
    if (pts < 0) return -1;

    pid_t pid = fork();
    if (pid < 0) return -1;

    if (pid == 0) {
        close(ptm);
        setsid();
        if (ioctl(pts, TIOCSCTTY, 0) < 0) exit(1);
        dup2(pts, 0);
        dup2(pts, 1);
        dup2(pts, 2);
        if (pts > 2) close(pts);

        const char *c_shell_path = env->GetStringUTFChars(shell_path, NULL);
        execl(c_shell_path, c_shell_path, "-", (char *)NULL);
        exit(1);
    } else {
        close(pts);
        jint* pids = env->GetIntArrayElements(pProcessId, NULL);
        pids[0] = pid;
        env->ReleaseIntArrayElements(pProcessId, pids, 0);
        return ptm;
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_ubuntucli_ShellSession_setPtyWindowSize(JNIEnv* env, jobject thiz, jint fd, jint rows, jint cols) {
    struct winsize sz;
    sz.ws_row = (unsigned short)rows;
    sz.ws_col = (unsigned short)cols;
    sz.ws_xpixel = 0;
    sz.ws_ypixel = 0;
    ioctl(fd, TIOCSWINSZ, &sz);
}

extern "C" JNIEXPORT void JNICALL
Java_com_ubuntucli_ShellSession_terminateProcess(JNIEnv* env, jobject thiz, jint pid) {
    kill(pid, SIGKILL);
}
