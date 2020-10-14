package com.group8.odin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-09-30
 * Description:
 */
public class MainActivity extends AppCompatActivity {
    @BindView(R.id.tvHeaderTitle)
    TextView mHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}