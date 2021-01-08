package com.example.helloioc;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

//标识出需要哪个布局文件
@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    @ViewInject(R.id.button1)
    private Button btn1;

    @ViewInject(R.id.button2)
    private Button btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        btn1 = findViewById(R.id.button1);
//        btn2 = findViewById(R.id.button2);

        btn1.setText("我是按钮1");
        btn2.setText("我是按钮2");

//        btn1.setOnClickListener(this);
//        btn2.setOnClickListener(this);

    }

    @OnClick({R.id.button1, R.id.button2})
    public void onClick(View v) {
        Toast.makeText(this, "短按下了", Toast.LENGTH_SHORT).show();
    }
}