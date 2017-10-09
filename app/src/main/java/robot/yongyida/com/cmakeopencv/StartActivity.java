package robot.yongyida.com.cmakeopencv;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class StartActivity extends AppCompatActivity {

    Intent mIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    public  void startRecognizer(View view){
        mIntent = new Intent(this,RecognizerFaceActivity.class);
        startActivity(mIntent);
    }

    public  void startStitch(View view){
        mIntent = new Intent(this,StitchActivity.class);
        startActivity(mIntent);
    }

    public  void startRegister(View view){
        mIntent = new Intent(this,RegisterFaceActivity.class);
        startActivity(mIntent);
    }
}
