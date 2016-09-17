package com.app.hinh.smart3g.util;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.app.hinh.smart3g.R;
import com.app.hinh.smart3g.ui.MainActivity;


public class WarringActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_warring);
    }
    public void onBackPressed() {

        Intent MyIntent = new Intent(Intent.ACTION_MAIN);
        MyIntent.addCategory(Intent.CATEGORY_HOME);
        startActivity(MyIntent);

        finish();
        return;
    }
    public void openListBlock(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
