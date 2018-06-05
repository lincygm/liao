package com.king.liaoba.util;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.content.Context;
import com.liaoba.R;
import com.wang.avi.AVLoadingIndicatorView;

public class CustomDialog extends ProgressDialog {

    AVLoadingIndicatorView avLoadingIndicatorView;

    public CustomDialog(Context context)
    {
        super(context);
    }

    public CustomDialog(Context context, int theme)
    {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        init(getContext());
    }

    private void init(Context context)
    {
        //设置不可取消，点击其他区域不能取消，实际中可以抽出去封装供外包设置
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.progress_layout);
        avLoadingIndicatorView = (AVLoadingIndicatorView)this.findViewById(R.id.progress);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        //params.width=70;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
    }

    @Override
    public void show() {
        super.show();
        avLoadingIndicatorView.show();
    }

    public void hide(){
        avLoadingIndicatorView.hide();
        super.cancel();
    }
}
