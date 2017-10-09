package robot.yongyida.com.cmakeopencv;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Main2Activitytestfr extends AppCompatActivity {

    TextView testJni;
    OpencvFrc opencvFrc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2_activitytestfr);
        testJni = (TextView)findViewById(R.id.testText);
        opencvFrc = OpencvFrc.CreateOpencvFrcHandler();
        testJni.setText(opencvFrc.getTestjni());
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.test2);
        opencvFrc.jnifaceRecMethod("/sdcard/etc1/at.txt",bitmap);
    }
}
