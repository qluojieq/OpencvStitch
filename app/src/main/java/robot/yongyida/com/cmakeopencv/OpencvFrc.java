package robot.yongyida.com.cmakeopencv;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

/**
 * Created by Brandon on 2017/9/29.
 */

public class OpencvFrc {
    static {
        System.loadLibrary("native-lib");
    }
    private static OpencvFrc opencvFrc = null;
    public static OpencvFrc CreateOpencvFrcHandler()
    {

        if(opencvFrc == null){
            opencvFrc = new OpencvFrc();
        }
        return opencvFrc;
    }

    public  native String testRec();
    //    public native int jnistitching(Object images[], String  reultAddr, double scale);
    public  native int faceRec(String result,long target);

    public  String getTestjni(){
        return  testRec();
    }
    public  int jnifaceRecMethod(String source,Bitmap target){
        Mat mRgba = new Mat();
        Utils.bitmapToMat(target,mRgba);
        return  faceRec(source, mRgba.getNativeObjAddr());
    }
}
