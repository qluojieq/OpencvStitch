package robot.yongyida.com.cmakeopencv;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    private final static String STITCHING_SOURCE_IMAGES_DIRECTORY = "/sdcard/stitch/";
    private final String STITCHING_RESULT_IMAGES_DIRECTORY = Environment.getExternalStorageDirectory().getPath()+"/PanoDemo/result/";
    private String path = "/sdcard/stitch/";
    static {
        System.loadLibrary("native-lib");
    }
    Bitmap bitmapLeft;
    Bitmap bitmapRight;
    Bitmap bitmapResult;
    ImageView imageLeft;
    ImageView imageRight;
    ImageView imageResult;
    TextView tv;
    public Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case 11:

                   break;
                case 01:
                    if(message.arg1==1){
                        Toast.makeText(MainActivity.this,"成功啦"+message.obj,Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(MainActivity.this,"差一点点，加油！"+message.obj,Toast.LENGTH_LONG).show();
                    }
                    break;
            }



//            imageResult.setImageBitmap(bitmapResult);
            return false;
        }
    });
    ArrayList <Mat> readyToStitch;
    String[] source= getDirectoryFilelist(STITCHING_SOURCE_IMAGES_DIRECTORY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageLeft = (ImageView) findViewById(R.id.image_left);
        imageRight = (ImageView) findViewById(R.id.image_right);
        imageResult = (ImageView)findViewById(R.id.image_result);

        initStitchingImageDirectory();
        String pathStringLeft = path+"left.jpg";
     try{
        File file = new File(pathStringLeft);
        if(file.exists())
        {
            bitmapLeft = BitmapFactory.decodeFile(pathStringLeft);
        }
         String pathStringRight = path+"right.jpg";
         File fileRight = new File(pathStringLeft);

         if(fileRight.exists())
         {
             bitmapRight = BitmapFactory.decodeFile(pathStringRight);
         }
    } catch (Exception e)
    {
        // TODO: handle exception
    }

        readyToStitch = new ArrayList();
                // Example of a call to a native method
        tv = (TextView) findViewById(R.id.sample_text);
//        tv.setText("图片拼接");
        tv.setText(stringFromJNI());
        imageLeft.setImageBitmap(bitmapLeft);
        imageRight.setImageBitmap(bitmapRight);
        //图片一
        Mat src = new Mat(bitmapLeft.getHeight(),bitmapLeft.getWidth(), CvType.CV_8UC4);
        Imgproc.resize(src,src,new Size(src.rows(),src.cols()));
        Utils.bitmapToMat(bitmapLeft,src);
        Imgproc.cvtColor(src,src,Imgproc.COLOR_BGR2RGB);
        readyToStitch.add(src);

        //图片二
        Mat srcR = new Mat(bitmapRight.getHeight(),bitmapRight.getWidth(), CvType.CV_8UC4);
        Imgproc.resize(srcR,srcR,new Size(srcR.rows(),srcR.cols()));
        Utils.bitmapToMat(bitmapRight,srcR);
        Imgproc.cvtColor(srcR,srcR,Imgproc.COLOR_BGR2RGB);
        readyToStitch.add(srcR);
//
        handler.post(new Runnable() {
            @Override
            public void run() {
//                Mat matSrc = new Mat();
                long timeCost = new Date().getTime();
//                int result = StitchPanorama( readyToStitch.toArray(),readyToStitch.size(),matSrc.getNativeObjAddr());
//                timeCost = timeCost - new Date().getTime();
                 String pathStringResult = STITCHING_RESULT_IMAGES_DIRECTORY+getCurrentDateTime()+"result.jpg";

                Message msg = new Message();

//                Imgproc.cvtColor(matSrc,matSrc,Imgproc.COLOR_BGR2RGBA);
//                Log.i("result","result "+ result+"++++++time++++++"+timeCost);
//                if(result == 1){
//                bitmapResult = Bitmap.createBitmap(matSrc.cols(),matSrc.rows(),Bitmap.Config.ARGB_8888);
//                Utils.matToBitmap(matSrc,bitmapResult);
//                }
//                handler.sendMessage(msg);
                int result = jnistitching(source,pathStringResult,1.0);
                timeCost = timeCost - new Date().getTime();
                msg.what = 01;
                msg.arg1 = result;
                msg.obj = timeCost;

                handler.sendMessage(msg);

            }
        });
    }

    //get directory filelist
    private String[] getDirectoryFilelist(String directory)
    {
        String[] filelist;
        File sourceDirectory = new File(STITCHING_SOURCE_IMAGES_DIRECTORY);
        int index=0;
        int folderCount=0;
        //except folders
        for(File file : sourceDirectory.listFiles())
        {
            if(file.isDirectory())
            {
                folderCount++;
            }
        }
        filelist=new String[sourceDirectory.listFiles().length-folderCount];
        for(File file : sourceDirectory.listFiles())
        {
            if(!file.isDirectory())
            {
                //showLOG("getFilelist file:"+file.getPath());
                filelist[index]=file.getPath();
                index++;
            }
        }
        return filelist;
    }

    //get current datetime
    private String getCurrentDateTime()
    {
        Calendar c = Calendar .getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        return df.format(c.getTime());
    }

    //java show jniStitching cost time
    private void javaShowJniStitchingCostTime(double costTime)
    {
        handler.sendMessage(handler.obtainMessage(10,""+costTime));  //change double to string
    }
    private void initStitchingImageDirectory()
    {
        //check exists,if not,create
//        File sourceDirectory = new File(STITCHING_SOURCE_IMAGES_DIRECTORY);
//        if(!sourceDirectory.exists())
//        {
//            sourceDirectory.mkdirs();
//        }
        File resultDirectory = new File(STITCHING_RESULT_IMAGES_DIRECTORY);
        if(!resultDirectory.exists())
        {
            resultDirectory.mkdirs();
        }
    }
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public native int StitchPanorama(Object images[], int size, long addrSrcRes);
    public native String testjni();
//    public native int jnistitching(Object images[], String  reultAddr, double scale);
    public native int jnistitching(String[] source,String result,double scale);
}
