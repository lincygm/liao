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
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaPlayer.OnCompletionListener;

import com.carlos.voiceline.mylibrary.VoiceLineView;
import com.king.liaoba.Constants;
import com.king.liaoba.bean.Root;
import com.king.liaoba.http.APIRetrofit;
import com.king.liaoba.http.APIService;
import com.king.liaoba.mvp.view.RecordView;
import com.king.liaoba.push.Logger;
import com.king.liaoba.util.CustomDialog;
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

public class RecordActivity extends Activity implements View.OnClickListener{

    @BindView(R.id.record_start)
    ImageView btn_start;
    @BindView(R.id.record_save)
    ImageView btn_save;
    @BindView(R.id.record_delete)
    ImageView btn_delete;
    @BindView(R.id.record_play)
    ImageView btn_play;
    @BindView(R.id.voicLine)
    VoiceLineView voiceLineView;
    // 多媒体播放器
    private MediaPlayer mediaPlayer;
    // 多媒体录制器
    private MediaRecorder mediaRecorder = null;
    // 音频文件
    private File audioFile;
    private int time=0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        ButterKnife.bind(this);

        btn_start.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    startRecord();
                    voiceLineView.setVisibility(View.VISIBLE);
                    btn_delete.setVisibility(View.GONE);
                    btn_play.setVisibility(View.GONE);
                    btn_save.setVisibility(View.GONE);
                }else if(event.getAction()==MotionEvent.ACTION_UP){
                    recordstop();
                    voiceLineView.setVisibility(View.GONE);
                    btn_delete.setVisibility(View.VISIBLE);
                    btn_play.setVisibility(View.VISIBLE);
                    btn_save.setVisibility(View.VISIBLE);
                    btn_start.setVisibility(View.GONE);
                }
                return true;
            }
        });
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

    private void playrecord(){
        if (mediaRecorder != null) {
            mediaRecorder.reset();
        }

        if (audioFile != null && audioFile.exists()) {
            try {
                Log.i("com.kingtone.www.record", ">>>>>>>>>" + audioFile);
                mediaPlayer = new MediaPlayer();
                // 为播放器设置数据文件
                mediaPlayer.setDataSource(audioFile.getAbsolutePath());
                // 准备并且启动播放器
                mediaPlayer.prepare();
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        setTitle("录音播放完毕.");

                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @OnClick({R.id.record_save,R.id.record_play,R.id.record_delete})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.record_save:
                upload(audioFile);
                break;
            case R.id.record_start:
                startRecord();
                break;
            case R.id.record_play:
                playrecord();
                    break;
            case R.id.record_delete:
                btn_save.setVisibility(View.GONE);
                btn_play.setVisibility(View.GONE);
                btn_delete.setVisibility(View.GONE);
                deleteFile(EnvironmentShare.getAudioRecordDir());
                break;
                default:
                    break;
        }
    }


    public void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if
                    (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    this.deleteFile(files[i]);
                }
            }
            file.delete();
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

                if(mediaRecorder == null){
                    mediaRecorder = new MediaRecorder();
                }
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

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                Logger.v( "rx_map" , "handler"+Thread.currentThread().getName()  );
                new CustomDialog(RecordActivity.this,R.style.CustomDialog).hide();
            }
        }
    };



    private void upload(File file){
        Logger.v( "rx_map" , "upload"+Thread.currentThread().getName()  );

        if(file==null){
            Toast.makeText(getApplicationContext(),"录音文件不存在!",Toast.LENGTH_LONG).show();
            return;
        }
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
                        Logger.v( "rx_map" , "onNext"+Thread.currentThread().getName()  );

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
                        Logger.v( "rx_map" , "onCompleted"+Thread.currentThread().getName()  );
                        Message message = new Message();
                        message.what=1;
                        handler.sendMessage(message);
                        Log.d("record","up suc");

                    }
                });


    }
}
