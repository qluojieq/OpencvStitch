package robot.yongyida.com.cmakeopencv;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
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
            if(message.arg1==1){
                Toast.makeText(MainActivity.this,"成功啦"+message.obj,Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(MainActivity.this,"差一点点，加油！"+message.obj,Toast.LENGTH_LONG).show();
            }
            imageResult.setImageBitmap(bitmapResult);
            return false;
        }
    });
    ArrayList <Mat> readyToStitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageLeft = (ImageView) findViewById(R.id.image_left);
        imageRight = (ImageView) findViewById(R.id.image_right);
        imageResult = (ImageView)findViewById(R.id.image_result);
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
        tv.setText("图片拼接");
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

        handler.post(new Runnable() {
            @Override
            public void run() {
                Mat matSrc = new Mat();
                long timeCost = new Date().getTime();
                int result = StitchPanorama( readyToStitch.toArray(),readyToStitch.size(),matSrc.getNativeObjAddr());
                timeCost = timeCost - new Date().getTime();
                Message msg = new Message();
                msg.arg1 = result;
                msg.obj = timeCost;
                Imgproc.cvtColor(matSrc,matSrc,Imgproc.COLOR_BGR2RGBA);
                Log.i("result","result "+ result+"++++++time++++++"+timeCost);
                if(result == 1){
                bitmapResult = Bitmap.createBitmap(matSrc.cols(),matSrc.rows(),Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(matSrc,bitmapResult);
                }
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public native int StitchPanorama(Object images[], int size, long addrSrcRes);
}
