package com.bbq.smartrouter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bbq.moudlea.ModuleABAActivity;
import com.bbq.smart_router.core.RouterRequest;
import com.bbq.smart_router.core.laucher.SmartRouter;
import com.bbq.smart_router_annotion.Router;

@Router(path = "MainActivity")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void jump(View view) {
        SmartRouter.getInstance().start(new RouterRequest(this,"smartApp://ModuleABAActivity")
                .withString("param1","test router transform value"));
    }

    public void jump2(View view) {
        startActivity(new Intent(this, ModuleABAActivity.class));
    }
}
