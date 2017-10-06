package com.seoulprojet.seoulpoem.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.seoulprojet.seoulpoem.R;

public class WriterApply extends AppCompatActivity {

    Button writerapply_back_btn;
    Button writerapply_ok_btn;
    EditText writerapply_message_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writer_apply);

        //////////////////////////////////
        writerapply_back_btn = (Button)findViewById(R.id.writerapply_back_btn);
        writerapply_ok_btn = (Button)findViewById(R.id.writerapply_ok_btn);
        writerapply_message_et = (EditText)findViewById(R.id.writerapply_message_et);
        /////////////////////////////////

        writerapply_back_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WriterList.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
