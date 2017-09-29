#include <jni.h>
#include <string>
#include<vector>
#include <opencv2/core/core.hpp>

using namespace cv;
using namespace std;
extern "C"
JNIEXPORT jstring JNICALL
//Java_robot_yongyida_com_cmakeopencv_MainActivity_stringFromJNI(
Java_robot_yongyida_com_cmakeopencv_StitchPanorama_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++ good choice";
    return env->NewStringUTF(hello.c_str());
}
