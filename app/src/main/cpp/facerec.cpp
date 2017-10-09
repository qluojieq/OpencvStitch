//
// Created by Administrator on 2017/10/9.
// 人脸识别
//
#include <jni.h>
#include <stdio.h>
#include <time.h>
#include <iostream>
#include <opencv2/face/facerec.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <android/log.h>
#include <fstream>
#include <opencv2/imgproc.hpp>

using namespace std;
using namespace cv;
using namespace face;

#define  LOG_TAG    "FaceRecognizer"
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
extern "C"
{
//testjni
JNIEXPORT jstring JNICALL Java_robot_yongyida_com_cmakeopencv_OpencvFrc_testRec(JNIEnv *env, jobject obj);
//OpenCV Stitching(return:0-success,other-error)
JNIEXPORT jint JNICALL Java_robot_yongyida_com_cmakeopencv_OpencvFrc_faceRec(JNIEnv *env, jobject obj, jstring source ,jlong target);
}

void read_csv(const string& filename, vector<Mat>& images, vector<int>& labels, char separator = ';') {
    std::ifstream file(filename.c_str(), ifstream::in);
    if (!file) {
        string error_message = "No valid input file was given, please check the given filename.";
        CV_Error(CV_StsBadArg, error_message);
    }
    string line, path, classlabel;
    while (getline(file, line)) {
        stringstream liness(line);
        getline(liness, path, separator);
        getline(liness, classlabel);
        if (!path.empty() && !classlabel.empty()) {
            images.push_back(imread(path,0));
            labels.push_back(atoi(classlabel.c_str()));
        }
    }
}

//testjni
JNIEXPORT jstring JNICALL Java_robot_yongyida_com_cmakeopencv_OpencvFrc_testRec(JNIEnv *env, jobject obj)
{
    return env->NewStringUTF("face recognizer");
}

JNIEXPORT jint JNICALL Java_robot_yongyida_com_cmakeopencv_OpencvFrc_faceRec(JNIEnv *env, jobject obj, jstring source ,jlong target)
{
    Mat & mRgb = *(Mat*)target;
    clock_t beginTime, endTime;
    double timeSpent;
    beginTime = clock();
    endTime = clock();
    const char* str;
    str = env->GetStringUTFChars(source, false);
    if(str == NULL) {
        return -1; /* OutOfMemoryError already thrown */
    }
    string fn_csv = str;
    vector<Mat> images;
    vector<int> labels;
    try {
        read_csv(fn_csv, images, labels);
    }
    catch (cv::Exception& e) {
        cerr << "Error opening file \"" << fn_csv << "\". Reason: " << e.msg << endl;
        // nothing more we can do
        exit(1);
    }
    if (images.size() <= 1) {
        string error_message = "This demo needs at least 2 images to work. Please add more images to your data set!";//还要弹一个出来，少于两个没法玩
        CV_Error(CV_StsError, error_message);
    }
//    int height = images[0].rows;
//    Mat testSample = images[images.size() - 1];
//    int testLabel = labels[labels.size() - 1];
//    images.pop_back();
//    labels.pop_back();
    Mat testSample ;
    cvtColor(mRgb, testSample, CV_BGR2GRAY); // 转为灰度图像

    Ptr<LBPHFaceRecognizer> model = LBPHFaceRecognizer::create(1, 8, 8, 8, DBL_MAX);

    model->train(images, labels);
    int predictedLabel = model->predict(testSample);

//    string result_message = format("Predicted class = %d / Actual class = %d.", predictedLabel, testLabel);
//    cout << result_message << endl;

    LOGE("success,total cost time %d seconds",predictedLabel);

    timeSpent = (double)(endTime - beginTime) / CLOCKS_PER_SEC;
    LOGE("success,total cost time %f seconds",timeSpent);
    return predictedLabel;
}


