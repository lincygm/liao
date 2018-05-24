package com.king.liaoba.mvp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.SpaceDecoration;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import com.king.liaoba.App;
import com.king.liaoba.Constants;
import com.king.liaoba.bean.PictureBean;
import com.king.liaoba.bean.PictureList;
import com.king.liaoba.bean.PictureRoot;
import com.king.liaoba.bean.Root;
import com.king.liaoba.bean.VoiceListInfo;
import com.king.liaoba.http.APIRetrofit;
import com.king.liaoba.http.APIService;
import com.king.liaoba.mvp.adapter.BannerAdapter;
import com.king.liaoba.mvp.adapter.ImageAdapter;
import com.king.liaoba.util.DensityUtil;
import com.king.liaoba.util.MessageEvent;
import com.king.liaoba.util.RecycleViewUtils;
import com.king.liaoba.util.uploadimg.ClipImageActivity;
import com.liaoba.BuildConfig;
import com.liaoba.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by gaomou on 2018/5/17.
 */

public class PhotoWallActivity extends Activity implements View.OnClickListener{

    @BindView(R.id.add_pic)
    Button btn_add;

    private File tempFile;
    //请求相机
    private static final int REQUEST_CAPTURE = 100;
    //请求相册
    private static final int REQUEST_PICK = 101;
    //请求截图
    private static final int REQUEST_CROP_PHOTO = 102;
    //请求访问外部存储
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 103;
    //请求写入外部存储
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 104;
    List<PictureList> list  = new ArrayList<>();
    private EasyRecyclerView recyclerView;
    private ImageAdapter adapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photowall);
        ButterKnife.bind(this);
        getPicture();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @OnClick({R.id.add_pic})
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.add_pic){
            uploadHeadImage(btn_add);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPicture();
    }

    private void getPicture(){
        Retrofit retrofit = APIRetrofit.getInstance();
        APIService service =retrofit.create(APIService.class);
        service.getImageList(Constants.getSharedPreference("chatid",this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PictureRoot>() {
                    @Override
                    public void onCompleted() {
                        pictureWall();
                        Log.d("pic","fetch");
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext( PictureRoot jsonBean) {
                        if(jsonBean!=null){
                            list = jsonBean.getData().getGetdata();
                            Log.d("====>>",jsonBean.getData().getGetdata().get(0).getPicurl());
                        }
                    }
                });
    }
    private void pictureWall() {

        if(list==null)return;
        if(recyclerView==null) {
            recyclerView = (EasyRecyclerView) findViewById(R.id.photowall_recyclerView);
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
            recyclerView.setAdapter(adapter = new ImageAdapter(this,list,myItemOnClickListener));
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
            gridLayoutManager.setSpanSizeLookup(adapter.obtainGridSpanSizeLookUp(2));
            recyclerView.setLayoutManager(new GridLayoutManager(this,2));
            SpaceDecoration itemDecoration = new SpaceDecoration((int) RecycleViewUtils.convertDpToPixel(8,this));
            itemDecoration.setPaddingEdgeSide(true);
            itemDecoration.setPaddingStart(true);
            itemDecoration.setPaddingHeaderFooter(false);
            recyclerView.addItemDecoration(itemDecoration);

        }

        adapter.notifyDataSetChanged();

    }


    /**
     * 上传头像
     */
    private void uploadHeadImage(View parent) {
        View view = LayoutInflater.from(this.getApplication()).inflate(R.layout.layout_popupwindow, null);
        TextView btnCarema = (TextView) view.findViewById(R.id.btn_camera);
        TextView btnPhoto = (TextView) view.findViewById(R.id.btn_photo);
        TextView btnCancel = (TextView) view.findViewById(R.id.btn_cancel);
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        //popupWindow在弹窗的时候背景半透明
        final WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.alpha = 0.5f;
        this.getWindow().setAttributes(params);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                params.alpha = 1.0f;
                PhotoWallActivity.this.getWindow().setAttributes(params);
            }
        });

        btnCarema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //权限判断
                if (ContextCompat.checkSelfPermission(PhotoWallActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(PhotoWallActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                    Log.d("xxxx","a");

                } else {
                    //跳转到调用系统相机
                    gotoCamera();
                }
                popupWindow.dismiss();
            }
        });
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //权限判断
                if (ContextCompat.checkSelfPermission(PhotoWallActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请READ_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(PhotoWallActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            READ_EXTERNAL_STORAGE_REQUEST_CODE);
                } else {
                    //跳转到相册
                    gotoPhoto();
                }
                popupWindow.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    /**
     * 跳转到相册
     */
    private void gotoPhoto() {
        Log.d("evan", "*****************打开图库********************");
        //跳转到调用系统图库
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "请选择图片"), REQUEST_PICK);
    }
    int type;
    /**
     * 打开截图界面
     */
    public void gotoClipActivity(Uri uri) {
        if (uri == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), ClipImageActivity.class);
        intent.putExtra("type", type);
        intent.setData(uri);
        startActivityForResult(intent, REQUEST_CROP_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case REQUEST_CAPTURE: //调用系统相机返回
                if (resultCode == android.app.Activity.RESULT_OK) {
                    Log.d("camera",tempFile+"");
                    gotoClipActivity(Uri.fromFile(tempFile));
                }
                break;
            case REQUEST_PICK:  //调用系统相册返回
                if (resultCode == android.app.Activity.RESULT_OK) {
                    Uri uri = intent.getData();
                    gotoClipActivity(uri);
                }
                break;
            case REQUEST_CROP_PHOTO:  //剪切图片返回
                if (resultCode == android.app.Activity.RESULT_OK) {
                    final Uri uri = intent.getData();
                    if (uri == null) {
                        return;
                    }
                    String cropImagePath = com.king.liaoba.util.FileUtil.getRealFilePathFromUri(getApplicationContext(), uri);
                    Log.d("path",cropImagePath);
                    //Bitmap bitMap = BitmapFactory.decodeFile(cropImagePath);
                    //headImage1.setImageBitmap(bitMap);
                    File file = new File(cropImagePath);
                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
                    MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
                    Retrofit retrofit = APIRetrofit.getInstance();
                    APIService service = retrofit.create(APIService.class);
                    Log.d("pic",""+Constants.getSharedPreference("chatid",this));
                    service.uploadPictures(Constants.getSharedPreference("chatid",this),part)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<Root>() {
                                @Override
                                public void onNext(Root root) {
                                    if(root!=null){
                                        Constants.EditSharedPreference("headimage", Constants.BASE_URL+root.getData().getGetdata().get(0).getHeadimg_url().toString());
                                       // adapter.insert();

                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.d("pic","error"+e.getMessage());
                                }

                                @Override
                                public void onCompleted() {
                                    Log.d("pic","up suc");
                                }
                            });

                }
                break;
        }
    }

    /**
     * 跳转到照相机
     */
    private void gotoCamera() {
        Log.d("evan", "*****************打开相机********************");
        //创建拍照存储的图片文件
        tempFile = new File(PhotoWallActivity.this.getExternalCacheDir(),System.currentTimeMillis() + ".jpg");
        Log.d("url",PhotoWallActivity.this.getExternalCacheDir()+"");
        //Log.d("url",getActivity().getExternalMediaDirs()+"");
        // tempFile = new File(FileUtil.checkDirPath(getActivity().getFilesDir() + "/files/"), System.currentTimeMillis() + ".jpg");
        //跳转到调用系统相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(PhotoWallActivity.this, Manifest.permission.CAMERA);
            if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(PhotoWallActivity.this,new String[]{Manifest.permission.CAMERA},REQUEST_CAPTURE);
            }
            //设置7.0中共享文件，分享路径定义在xml/file_paths.xml
            Uri contentUri = FileProvider.getUriForFile(PhotoWallActivity.this, BuildConfig.APPLICATION_ID + ".fileProvider", tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        }
        startActivityForResult(intent, REQUEST_CAPTURE);
    }

    public interface MyItemOnClickListener {
        public void onItemOnClick(View view,int postion);
    }
    MyItemOnClickListener myItemOnClickListener = new MyItemOnClickListener() {
        @Override
        public void onItemOnClick(View view, int postion) {
        Log.d("DDS","delte");
        adapter.remove(postion);
        }
    };

}
