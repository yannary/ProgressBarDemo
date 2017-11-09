# 关于定义ProgressBar的整理

一、自定义圆形ProgressBar
        
        mProgressBar = (CircleProgressBar) findViewById(R.id.circleProgressBar);
        mProgressBar.setMax(100);
        mProgressBar.setProgress(80, "心动值");

        mProgressBar2 = (CircleProgressBar) findViewById(R.id.circleProgressBar2);
        mProgressBar2.setRoundWidth(6f);
        mProgressBar2.setCircleColor(Color.LTGRAY);
        mProgressBar2.setCircleProgressColor(Color.BLUE);
        mProgressBar2.setTopTextSize(dp2px(this, 18));
        mProgressBar2.setTextColor(Color.GRAY);
        mProgressBar2.setMax(100);
        mProgressBar2.setProgress(30, "兴趣值");
        
二、自定义弧形ProgressBar
        
        mArcProgressBar = (ArcProgressBar) findViewById(R.id.arcProgress);
        mArcProgressBar.setArcProgress(0, 80);
