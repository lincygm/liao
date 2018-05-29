package com.king.liaoba.mvp.activity;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaPlayer.OnCompletionListener;

import com.king.liaoba.Constants;
import com.king.liaoba.bean.Root;
import com.king.liaoba.http.APIRetrofit;
import com.king.liaoba.http.APIService;
import com.king.liaoba.mvp.view.RecordView;
import com.king.liaoba.util.EnvironmentShare;
import com.liaoba.BuildConfig;
import com.liaoba.R;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import android.os.Handler;
/**
 * Created by gaomou on 2018/5/17.
 */

public class RecordActivity extends Activity implements View.OnClickListener,View.OnTouchListener{

    @BindView(R.id.record_start)
    Button btn_start;
    @BindView(R.id.record_save)
    Button btn_save;
    @BindView(R.id.btn_delete)
    Button btn_delete;
    @BindView(R.id.btn_play)
    Button btn_play;

    @BindView(R.id.recordView)
    RecordView mRecorfView;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int db = (int) (Math.random()*100);
            mRecorfView.setVolume(db);
            if (mediaRecorder != null) {
                // mediaRecorder.stop();
                mediaRecorder.reset();
            }
        }
    };
    private int nowModel = RecordView.MODEL_RECORD;

    // 多媒体播放器
    private MediaPlayer mediaPlayer;
    // 多媒体录制器
    private MediaRecorder mediaRecorder = new MediaRecorder();
    // 音频文件
    private File audioFile;

    // 传给Socket服务器端的上传和下载标志
    private final int UP_LOAD = 1;
    private final int DOWN_LOAD = 2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        ButterKnife.bind(this);

    }

    private TimerTask timeTask;
    private Timer timeTimer = new Timer(true);

    @OnTouch({R.id.btn_play,R.id.record_start})
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.getId()==R.id.record_start){
            nowModel=RecordView.MODEL_RECORD;
                    mRecorfView.setModel(RecordView.MODEL_RECORD);
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                mRecorfView.start();
                timeTimer.schedule(timeTask = new TimerTask() {
                    public void run() {
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
                }, 20, 20);
                mRecorfView.setOnCountDownListener(new RecordView.OnCountDownListener() {
                    @Override
                    public void onCountDown() {
                        Toast.makeText(RecordActivity.this,"计时结束啦~~",Toast.LENGTH_SHORT).show();
                        mediaRecorder.stop();
                        mediaRecorder.release();//释放资源
                        mediaRecorder = null;
                    }
                });
                startRecord();
            }else if(event.getAction() == MotionEvent.ACTION_UP){
                mRecorfView.cancel();
                recordstop();
                mRecorfView.setModel(RecordView.MODEL_PLAY);
                nowModel = RecordView.MODEL_PLAY;
            }
        }

        return false;
    }
    public void recordstop() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
            } catch (IllegalStateException e) {
                // TODO 如果当前java状态和jni里面的状态不一致，
                //e.printStackTrace();
                mediaRecorder = null;
                mediaRecorder = new MediaRecorder();
            }
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    @OnClick({R.id.record_save,R.id.record_play,R.id.btn_play,R.id.btn_delete})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_play:

                break;
            case R.id.record_save:
                upload(audioFile);
                break;
            case R.id.record_start:
               // Toast.makeText(getApplicationContext(),"ff",Toast.LENGTH_LONG).show();
               // startRecord();
                break;
            case R.id.record_play:

                mRecorfView.setModel(RecordView.MODEL_PLAY);
                nowModel = RecordView.MODEL_PLAY;
                mRecorfView.start();
                timeTimer.schedule(timeTask = new TimerTask() {
                    public void run() {
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
                }, 20, 20);
                mRecorfView.setOnCountDownListener(new RecordView.OnCountDownListener() {
                    @Override
                    public void onCountDown() {
                        Toast.makeText(RecordActivity.this,"计时结束啦~~",Toast.LENGTH_SHORT).show();

                    }
                });

                    break;
            case R.id.btn_delete:

                break;
                default:
                    break;
        }
    }
    public boolean checkSelfPermission(String permission, int requestCode) {
        Log.i("", "checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    requestCode);
            return false;
        }
        return true;
    }


    private void startRecord(){
        if(checkSelfPermission(Manifest.permission.RECORD_AUDIO, 22)){
        try {
            if (!EnvironmentShare.haveSdCard()) {
                Toast.makeText(this, "SD不存在，不正常录音！！", Toast.LENGTH_LONG).show();
            } else {
                // 设置音频来源(一般为麦克风)
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                // 设置音频输出格式（默认的输出格式）
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                // 设置音频编码方式（默认的编码方式）
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                // 创建一个临时的音频输出文件.record_是文件的前缀名 .amr是后缀名
                audioFile = File.createTempFile("record_", ".mp3", EnvironmentShare.getAudioRecordDir());
                Log.d("record",""+EnvironmentShare.getAudioRecordDir());
                // audioFile =new
                // File(Environment.getExternalStorageDirectory().getCanonicalPath()+"/sound.amr");
                // 设置录制器的文件保留路径
                mediaRecorder.setOutputFile(audioFile.getAbsolutePath());

                // 准备并且开始启动录制器
                mediaRecorder.prepare();
                mediaRecorder.start();
               // msg = "正在录音...";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    }



    private void upload(File file){
        if(file==null){
            Toast.makeText(getApplicationContext(),"录音文件不存在!",Toast.LENGTH_LONG).show();
            return;}
        RequestBody requestBody = RequestBody.create(MediaType.parse("audio/mpeg"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        Retrofit retrofit = APIRetrofit.getInstance();
        APIService service = retrofit.create(APIService.class);
        Log.d("pic",""+ Constants.getSharedPreference("chatid",this));
        service.uploadRecord(Constants.getSharedPreference("chatid",this),part)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Root>() {
                    @Override
                    public void onNext(Root root) {
                        if(root!=null){
                            Constants.EditSharedPreference("voicelibrary",
                                    Constants.BASE_URL+root.getData().getInfo());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("pic","error"+e.getMessage());
                    }

                    @Override
                    public void onCompleted() {
                        Log.d("record","up suc");
                    }
                });


    }
}
