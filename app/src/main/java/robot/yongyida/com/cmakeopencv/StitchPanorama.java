package robot.yongyida.com.cmakeopencv;

/**
 * Created by Brandon on 2017/9/29.
 */

public class StitchPanorama {
    static {
        System.loadLibrary("native-lib");
    }
    private static StitchPanorama mStitchPanorama = null;
    public static StitchPanorama CreateStitchHandler()
    {

        if(mStitchPanorama == null){
            mStitchPanorama = new StitchPanorama();
        }
        return mStitchPanorama;
    }

    //java show jniStitching cost time
    private void javaShowJniStitchingCostTime(double costTime)
    {
//        handler.sendMessage(handler.obtainMessage(10,""+costTime));  //change double to string
    }

    public  native String testjni();
    //    public native int jnistitching(Object images[], String  reultAddr, double scale);
    public  native int jnistitching(String[] source,String result,double scale);

    public  String getTestjni(){
        return  testjni();
    }
    public  int jniStitchMethod(String[] source,String result,double scale){
        return  jnistitching(source, result, scale);
    }
}
