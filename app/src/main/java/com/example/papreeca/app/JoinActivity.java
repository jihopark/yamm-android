package com.example.papreeca.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class JoinActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        hideActionBar();
        configButton();

    }

    ////////////////////////////////Private Methods/////////////////////////////////////////////////

    private void configButton(){
        Button sendV = (Button) findViewById(R.id.send_verification_code);

        sendV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"버튼눌림",Toast.LENGTH_LONG).show();
            }
        });
    }

}
