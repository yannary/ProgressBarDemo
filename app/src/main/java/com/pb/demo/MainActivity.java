package com.pb.demo;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.pb.demo.widget.CircleProgressBar;

public class MainActivity extends AppCompatActivity {

    private CircleProgressBar mProgressBar;
    private CircleProgressBar mProgressBar2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        mProgressBar = (CircleProgressBar)findViewById(R.id.circleProgressBar);
        mProgressBar.setMax(100);
        mProgressBar.setProgress(80,"心动值");

        mProgressBar2 = (CircleProgressBar)findViewById(R.id.circleProgressBar2);
        mProgressBar2.setRoundWidth(6f);
        mProgressBar2.setCircleColor(Color.LTGRAY);
        mProgressBar2.setCircleProgressColor(Color.BLUE);
        mProgressBar2.setTopTextSize(dp2px(this,18));
        mProgressBar2.setTextColor(Color.GRAY);
        mProgressBar2.setMax(100);
        mProgressBar2.setProgress(30,"兴趣值");
    }

    /**
     * dp2px
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
