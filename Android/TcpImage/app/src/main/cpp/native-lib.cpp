#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_yubao_tcpimage_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Server";
    return env->NewStringUTF(hello.c_str());
}