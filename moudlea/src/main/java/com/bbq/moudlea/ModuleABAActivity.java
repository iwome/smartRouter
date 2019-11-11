package com.bbq.moudlea;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bbq.smart_router_annotion.Router;

/**
 * Created by bangbang.qiu on 2019/11/7.
 */
@Router(path = "ModuleABAActivity")
public class ModuleABAActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modulea_bb);
        String str = getIntent().getStringExtra("param1");
        TextView tv = findViewById(R.id.tv_content);
        tv.setText(str);
    }
}
