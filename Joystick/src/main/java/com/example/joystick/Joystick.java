package com.example.joystick;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class Joystick extends ConstraintLayout {

    public static final int
            OUTER_STYLE_00 = R.drawable.outer_circle_model_00,
            OUTER_STYLE_01 = R.drawable.outer_circle_model_01,
            OUTER_STYLE_02 = R.drawable.outer_circle_model_02;
    public static final int
            INDICATOR_INVISIBLE = 0,
            INDICATOR_VERTICAL = 1,
            INDICATOR_HORIZONTAL = 2;

    private final ImageView innerCircle,outerCircle, indicator;
    private final Coordinate joystickPos = new Coordinate(0,0);
    private final Coordinate startPos = new Coordinate(0,0);
    private final Coordinate center = new Coordinate(0,0);
    private final Coordinate outerPos = new Coordinate(0,0);
    private final Coordinate innerPos = new Coordinate(0,0);
    private float outerRadius,innerRadius;
    private boolean fixedCenter;
    private int indicatorMode;
    private OnMoveListener onMoveListener;

    public Joystick(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.joystick_layout, this, true);
        this.innerCircle = this.findViewById(R.id.iv_inner);
        this.outerCircle = this.findViewById(R.id.iv_outer);
        this.indicator = this.findViewById(R.id.iv_indicator);

        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        startPos.x = left;
        startPos.y = top;
        center.x = getWidth() / 2f;
        center.y = getHeight() / 2f;
        outerRadius = outerCircle.getHeight() / 2f;
        innerRadius = innerCircle.getHeight() / 2f;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(!fixedCenter) {
                    outerPos.x = x;
                    outerPos.y = y;
                }
                updateOuterPos();
                break;
            case MotionEvent.ACTION_MOVE:
                float disX = x - outerPos.x;
                float disY = y - outerPos.y;
                float dis = (float) Math.sqrt(Math.pow(disX,2) + Math.pow(disY,2));
                if(dis < outerRadius) {
                    innerPos.x = x;
                    innerPos.y = y;
                }else {
                    float a = outerRadius/dis;
                    innerPos.x = disX * a + outerPos.x;
                    innerPos.y = disY * a + outerPos.y;
                }
                updateInnerPos();
                break;
            case MotionEvent.ACTION_UP:
                innerPos.x = outerPos.x;
                innerPos.y = outerPos.y;
                updateInnerPos();
                break;
        }
        updateJoystickPos();
        updateIndicator();
        return true;
    }

    private void init() {
        setOuterStyle(OUTER_STYLE_00);
        setSize(500);
        setFixedCenter(false);
        setIndicatorMode(INDICATOR_INVISIBLE);
    }

    private void updateOuterPos() {
        outerCircle.setX(startPos.x - outerRadius + outerPos.x);
        outerCircle.setY(startPos.y - outerRadius + outerPos.y);
    }

    private void updateInnerPos() {
        innerCircle.setX(startPos.x - innerRadius + innerPos.x);
        innerCircle.setY(startPos.y - innerRadius + innerPos.y);
    }

    private void updateIndicator() {
        if(indicatorMode == INDICATOR_INVISIBLE) {
            indicator.setVisibility(INVISIBLE);
            return;
        }
        if(indicator.getVisibility() != VISIBLE) indicator.setVisibility(VISIBLE);

        int theta = indicatorMode == INDICATOR_VERTICAL ? 0 : 90;
        float axis = indicatorMode == INDICATOR_VERTICAL ? joystickPos.y : joystickPos.x;
        if(axis == 0) indicator.setVisibility(INVISIBLE);
        else if(axis < 0) theta += 180;
        indicator.setRotation(theta);
        outerCircle.setRotation(theta);

        indicator.setX(outerCircle.getX());
        indicator.setY(outerCircle.getY());
    }

    private void updateJoystickPos() {
        this.joystickPos.x = (innerPos.x - outerPos.x) / outerRadius;
        this.joystickPos.y = -(innerPos.y - outerPos.y) / outerRadius;
        notifyMove();
    }

    private void notifyMove() {
        if(onMoveListener == null) return;
        onMoveListener.onMove(joystickPos.x,joystickPos.y);
    }

//    -------------------------------------
    public void setOuterStyle(int style) {
        outerCircle.setImageResource(style);
    }

    public void setSize(int size) {
        LayoutParams outerParams = new LayoutParams(size,size);
        LayoutParams innerParams = new LayoutParams((int)(size*0.382),(int)(size*0.382));
        outerParams.topToTop = LayoutParams.PARENT_ID;
        outerParams.leftToLeft = LayoutParams.PARENT_ID;
        outerParams.rightToRight = LayoutParams.PARENT_ID;
        outerParams.bottomToBottom = LayoutParams.PARENT_ID;
        innerParams.topToTop = LayoutParams.PARENT_ID;
        innerParams.leftToLeft = LayoutParams.PARENT_ID;
        innerParams.rightToRight = LayoutParams.PARENT_ID;
        innerParams.bottomToBottom = LayoutParams.PARENT_ID;
        outerCircle.setLayoutParams(outerParams);
        innerCircle.setLayoutParams(innerParams);
        indicator.setLayoutParams(outerParams);
    }

    public void setFixedCenter(boolean fixedCenter) {
        this.fixedCenter = fixedCenter;
        outerPos.x = center.x;
        outerPos.y = center.y;
        updateOuterPos();
    }

    public void setIndicatorMode(int mode) {
        this.indicatorMode = mode;
    }

    public void setOnMoveListener(OnMoveListener onMoveListener) {
        this.onMoveListener = onMoveListener;
    }

    public float getJoystickX() {
        return joystickPos.x;
    }

    public float getJoystickY() {
        return joystickPos.y;
    }

    public static float getJoystickDistance(float x, float y) {
        return (float) Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
    }

    public static float getJoystickAngle(float x, float y) {
        x = x == -0.0 ? 0 : x;
        y = y == -0.0 ? 0 : y;
        return (float) Math.toDegrees(Math.atan2(x,y)); // y,x (+90)> -x,y (flip)> x,y
    }

//    -------------------------------------
    public interface OnMoveListener {
        void onMove(float x, float y);
    }

}
