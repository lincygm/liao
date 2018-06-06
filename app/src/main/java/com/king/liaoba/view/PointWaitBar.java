package com.king.liaoba.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.liaoba.R;

import java.lang.ref.WeakReference;

public class PointWaitBar extends LinearLayout {
    private static final int NUM = 5;
    private Context context;
    private String TAG = "PointWaitBar";
    private ImageView mOldIM;
    private UpdateHandler handler;

    public PointWaitBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    public PointWaitBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public PointWaitBar(Context context) {
        super(context);
        this.context = context;
        init();
    }

    private void init() {
        //初始化数据
        this.setOrientation(LinearLayout.HORIZONTAL);
        this.setGravity(Gravity.CENTER);
        handler = new UpdateHandler(context);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_dot_normal);
        LayoutParams tLayoutParams = new LayoutParams(bitmap.getWidth(), bitmap.getHeight());
        tLayoutParams.leftMargin = 10;
        tLayoutParams.rightMargin = 10;
        //添加5个小点省略号
        for (int i = 0; i < NUM; i++) {
            ImageView vDot = new ImageView(context);
            vDot.setLayoutParams(tLayoutParams);
            if (i == 0) {
                vDot.setBackgroundResource(R.drawable.ic_dot_pressed);
            } else {
                vDot.setBackgroundResource(R.drawable.ic_dot_normal);
            }
            this.addView(vDot);
        }
        mOldIM = (ImageView) this.getChildAt(0);
        handler.sendEmptyMessage(0);
    }

    //提供给外部消除message
    public void setDestroyCallBack() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            //LogUtil.i(TAG, "已经清除消息");
        }
    }

    class UpdateHandler extends Handler {
        WeakReference<Context> reference;

        public UpdateHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int cPosition = msg.what;
            if (mOldIM != null)
                mOldIM.setBackgroundResource(R.drawable.ic_dot_pressed);
            ImageView currentIM = (ImageView) PointWaitBar.this.getChildAt(cPosition);
            currentIM.setBackgroundResource(R.drawable.ic_dot_normal);
            mOldIM = currentIM;
            if (++cPosition == NUM)
                cPosition = 0;
            this.sendEmptyMessageDelayed(cPosition, 200);
        }
    }
}