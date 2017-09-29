#include <jni.h>
#include <vector>

#include "opencv2/imgproc/imgproc.hpp"
#include "opencv2/highgui/highgui.hpp"
#include <opencv2/stitching.hpp>

using namespace cv;
using namespace std;

char filepath1[100] = "/storage/emulated/0/Download/PacktBook/Chapter6/panorama_stitched.jpg";

extern "C" {
    JNIEXPORT jint JNICALL Java_robot_yongyida_com_cmakeopencv_StitchPanorama(JNIEnv*, jobject, jobjectArray, jint, jlong);
    JNIEXPORT jint JNICALL Java_robot_yongyida_com_cmakeopencv_StitchPanorama(JNIEnv* env, jobject, jobjectArray images, jint size, jlong resultMatAddr)

    {
        jint result = 0;
//        vector<Mat> clickedImages = vector<Mat>();
//        Mat& srcRes = *(Mat*)resultMatAddr, img;
//        Mat output_stitched = Mat();
//
//        jclass clazz = (env)->FindClass("org/opencv/core/Mat");
//        jmethodID getNativeObjAddr = (env)->GetMethodID(clazz, "getNativeObjAddr", "()J");
//
//        for(int i=0; i < size; i++){
//            jobject obj = (env->GetObjectArrayElement(images, i));
//            jlong result = (env)->CallLongMethod(obj, getNativeObjAddr, NULL);
//            img = *(Mat*)result;
//            resize(img, img, Size(img.rows, img.cols));
//            clickedImages.push_back(img);
//            env->DeleteLocalRef(obj);
//        }
//        env->DeleteLocalRef(images);
//
//        Stitcher stitcher = Stitcher::createDefault();
//        Stitcher::Status status = stitcher.stitch(clickedImages, output_stitched);
//
//        output_stitched.copyTo(srcRes);
//
//        imwrite(filepath1, srcRes);


        Stitcher::Mode mode;
        vector<Mat> imgs;
        Mat& srcRes = *(Mat*)resultMatAddr, img;
        bool divide_images = true;
        bool try_use_gpu = false;
        jclass clazz = (env)->FindClass("org/opencv/core/Mat");
        jmethodID getNativeObjAddr = (env)->GetMethodID(clazz, "getNativeObjAddr", "()J");
        mode = Stitcher::SCANS;
        for (int i = 0; i < size; ++i)
        {
            jobject obj = (env->GetObjectArrayElement(images, i));
            jlong result = (env)->CallLongMethod(obj, getNativeObjAddr, NULL);
            Mat img = *(Mat*)result;
            if (img.empty())
            {
                return -1;
            }
            if (divide_images)
            {
                Rect rect(0, 0, img.cols / 2, img.rows);
                imgs.push_back(img(rect).clone());
                rect.x = img.cols / 3;
                imgs.push_back(img(rect).clone());
                rect.x = img.cols / 2;
                imgs.push_back(img(rect).clone());
            }
            else
                imgs.push_back(img);
        }
        //
        Mat pano;
        Ptr<Stitcher> stitcher = Stitcher::create(mode, try_use_gpu);
        Stitcher::Status status = stitcher->stitch(imgs, pano);
        pano.copyTo(srcRes);
        if (status == Stitcher::OK)
            result = 1;
        else
            result = 0;

        return result;
    }
}
