package com.example.remotecarapp_customviews;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.joystick.Joystick;


public class MainActivity extends AppCompatActivity {

    Button button;
    SeekBar seekBar;
    Joystick joystick;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.btn);
        seekBar = findViewById(R.id.seekbar);
        joystick = findViewById(R.id.joystick);
        textView = findViewById(R.id.textView);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                joystick.setLayoutParams(new ConstraintLayout.LayoutParams(progress,progress));
                joystick.setSize(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {            }
        });

        joystick.setOnMoveListener(new Joystick.OnMoveListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onMove(float x, float y) {
                textView.setText(
                        "x:" + Math.round(x*10000)/100f +
                                "\ny:" + Math.round(y*10000)/100f +
                                "\nangle:" + Math.round(Joystick.getJoystickAngle(x,y)*10000)/10000f+
                                "\ndistance:" + Math.round(Joystick.getJoystickDistance(x,y)*10000)/100f );
            }
        });

    }

    int[] style = {Joystick.OUTER_STYLE_00,Joystick.OUTER_STYLE_01,Joystick.OUTER_STYLE_02};
    int nowStyle = 0;
    public void changeStyle(View view){
        nowStyle++;
        joystick.setOuterStyle(style[nowStyle%3]);

    }
    boolean fixedCenter = true;
    public void fixedCenter(View view){
        fixedCenter = !fixedCenter;
        joystick.setFixedCenter(fixedCenter);
    }

    int[] mode = {Joystick.INDICATOR_INVISIBLE, Joystick.INDICATOR_VERTICAL,Joystick.INDICATOR_HORIZONTAL};
    int nowMode = 0;
    public void indicatorMode(View view) {
        joystick.setIndicatorMode(mode[nowMode++%3]);
    }
}