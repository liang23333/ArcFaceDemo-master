package com.arcsoft.sdk_demo;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;

public class ListActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.list);

        View v = this.findViewById(R.id.first);
        v.setOnClickListener(this);
        v = this.findViewById(R.id.second);
        v.setOnClickListener(this);
        v = this.findViewById(R.id.third);
        v.setOnClickListener(this);
        v = this.findViewById(R.id.fourth);
        v.setOnClickListener(this);
        v = this.findViewById(R.id.fifth);
        v.setOnClickListener(this);

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }


    public void onClick(View paramView) {
        // TODO Auto-generated method stub
        switch (paramView.getId()) {
            case R.id.first:
                Intent intent=new Intent(this,FRActivity.class);
                startActivity(intent);
                break;
            case R.id.second:
                intent=new Intent(this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.third:

                break;
            case R.id.fourth:

                break;
            case R.id.fifth:

                break;
            default:;
        }
    }
}
