package robot.yongyida.com.cmakeopencv;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class RegisterFaceActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener {

    JavaCameraView mJavaCameraView;
    CascadeClassifier mCascadeClassifier;
    Mat mGrayScaleImage;
    int mFaceSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognizer_face);
        mJavaCameraView = (JavaCameraView)findViewById(R.id.cameraView);
        mJavaCameraView.setCameraIndex(-1);
        mJavaCameraView.setCvCameraViewListener(this);
    }
    void initCascadeClassifier(){
        try {
            // Copy the resource into a temp file so OpenCV can load it
            InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_alt);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
            // Load the cascade classifier
            mCascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
        } catch (Exception e) {
            Log.e("OpenCVActivity", "Error loading cascade", e);
        }
        // And we are ready to go
        mJavaCameraView.enableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mGrayScaleImage = new Mat(height, width, CvType.CV_8UC4);

        mFaceSize = (int) (height * 0.2);
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(Mat inputFrame) {
        // Create a grayscale image
        Imgproc.cvtColor(inputFrame, mGrayScaleImage, Imgproc.COLOR_RGBA2RGB);
        MatOfRect faces = new MatOfRect();

        // Use the classifier to detect faces
        if (mCascadeClassifier != null) {
            mCascadeClassifier.detectMultiScale(mGrayScaleImage, faces, 1.1, 2, 2,
                    new Size(mFaceSize, mFaceSize), new Size());
        }

        // If there are any faces found, draw a rectangle around it
        Rect[] facesArray = faces.toArray();
        int faceCount = facesArray.length;

        for (int i = 0; i < facesArray.length; i++) {
            Imgproc.rectangle(inputFrame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0, 255), 3);
        }
        return inputFrame;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.e("log_wons", "OpenCV init error");
        }
        initCascadeClassifier();
    }
}
