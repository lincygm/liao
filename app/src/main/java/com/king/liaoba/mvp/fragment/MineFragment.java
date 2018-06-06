package com.king.liaoba.mvp.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.king.liaoba.App;
import com.king.liaoba.Constants;
import com.king.liaoba.bean.FriendsRoot;
import com.king.liaoba.bean.Root;
import com.king.liaoba.http.APIRetrofit;
import com.king.liaoba.http.APIService;
import com.king.liaoba.mvp.activity.RecordActivity;
import com.king.liaoba.mvp.activity.SelfEditActivity;
import com.king.liaoba.util.uploadimg.CircleImageView;
import com.king.liaoba.util.uploadimg.ClipImageActivity;
import com.liaoba.BuildConfig;
import com.liaoba.R;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2017/3/9
 */

public class MineFragment extends SimpleFragment {


    @BindView(R.id.ivLeft)
    ImageView ivLeft;
    @BindView(R.id.ivRight)
    ImageView ivRight;
    @BindView(R.id.ivAvatar)
    ImageView ivAvatar;
    @BindView(R.id.btnLogin)
    TextView btnLogin;
    @BindView(R.id.tvFollow)
    TextView tvFollow;
    @BindView(R.id.tvFans)
    TextView tvFans;
    @BindView(R.id.tvSeed)
    TextView tvSeed;
    @BindView(R.id.mine_charge)
    TextView tvRecharge;
    @BindView(R.id.tvStarLight)
    TextView tvStarLight;
    @BindView(R.id.tvContribution)
    TextView tvContribution;
    @BindView(R.id.tvSetting)
    TextView tvSetting;
    @BindView(R.id.srl)
    SwipeRefreshLayout srl;
    @BindView(R.id.fab)
    View fab;
    @BindView(R.id.parent)
    View parent;
    @BindView(R.id.mine_logout)
    TextView tv_logout;
    @BindView(R.id.mine_record)
    TextView tv_record;
    @BindView(R.id.mine_sign)
    TextView tv_sign;


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
    //头像1
    private CircleImageView headImage1;
    //头像2
    private ImageView headImage2;
    //调用照相机返回图片文件
    private File tempFile;
    private int type;

    public static MineFragment newInstance() {
        
        Bundle args = new Bundle();
        MineFragment fragment = new MineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getRootViewId() {
        return R.layout.fragment_mine;
    }

    @Override
    public void initUI() {

        headImage1 =(CircleImageView)this.findView(R.id.ivAvatar);
        srl.setColorSchemeColors(getResources().getColor(R.color.progress_color));
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateRefreshStatus();
            }
        });
        btnLogin.setClickable(true);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startLogin();
                Intent intent = new Intent(MineFragment.this.getContext(),LoginFragment.class);
                startActivity(intent);
            }
        });

    }

    public void updateRefreshStatus(){
        Observable.create(new Observable.OnSubscribe<String>(){

            @Override
            public void call(Subscriber<? super String> subscriber) {
                SystemClock.sleep(1000);
                subscriber.onNext("refresh");
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        srl.setRefreshing(false);
                    }
                });
    }

    @Override
    public void initData() {

    }

    @Override
    public void onResume() {
        super.onResume();
            if(super.startLogin()){
            getFocus();
            getFans();
            btnLogin.setText(Constants.getSharedPreference("username",getActivity()));
            btnLogin.setClickable(false);
            Glide.with(getActivity()).load(Constants.BASE_URL+Constants.getSharedPreference("headimg_url",getActivity()))
            .dontAnimate().into(headImage1);
            if(Constants.getSharedPreference("signin",getActivity()).equals("1")){
                tv_sign.setText("已签到");
                tv_sign.setClickable(false);
            }else {
                tv_sign.setText("签到");
                tv_sign.setClickable(true);
            }

        }else{
            Log.d("q","b");
            btnLogin.setText("登录");
            btnLogin.setClickable(true);
        }

    }

    @OnClick({R.id.ivLeft, R.id.ivRight, R.id.ivAvatar,
            R.id.tvFollow, R.id.tvFans, R.id.mine_charge,
            R.id.tvStarLight, R.id.tvContribution, R.id.tvLevel,
            R.id.tvTask, R.id.tvSetting, R.id.fab,R.id.mine_logout,R.id.mine_record,R.id.mine_sign})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ivLeft:
                //startLogin();
                gotoSelfEditInfo();
                break;
            case R.id.ivRight:
                startLogin();
                break;
            case R.id.ivAvatar://上传头像
                uploadHeadImage(parent);
                break;
            case R.id.tvFollow:

                break;
            case R.id.tvFans:
                startLogin();
                break;
            case R.id.mine_charge:
                startLogin();
                break;
            case R.id.tvStarLight:
                break;
            case R.id.tvContribution:
                break;
            case R.id.tvSetting:
                break;
            case R.id.fab:
                startAbout();
                break;
            case R.id.mine_logout:
                Constants.clearSharedPreference();
                System.exit(0);
                break;
            case R.id.mine_record:
                Intent  intent = new Intent(getActivity(), RecordActivity.class);
                startActivity(intent);
                break;
            case R.id.mine_sign:
                sign();
                break;
                default:
                    break;
        }
    }

    private void sign(){
        Retrofit retrofit = APIRetrofit.getInstance();
        APIService service = retrofit.create(APIService.class);
        service.signIn(Constants.getSharedPreference("chatid", getActivity()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FriendsRoot>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(FriendsRoot jsonBean) {
                        if(jsonBean!=null){
                            if(jsonBean.getStatus()==1){
                                Constants.EditSharedPreference("signin","1");
                                tv_sign.setText("已签到");
                                tv_sign.setClickable(false);
                            }
                        }
                    }
                });
    }

    private void getFans(){

        Retrofit retrofit = APIRetrofit.getInstance();
        APIService service = retrofit.create(APIService.class);
        service.getFans(Constants.getSharedPreference("chatid", getActivity()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Root>() {
                    @Override
                    public void onCompleted() {
                        Log.d("getFans", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Root jsonBean) {
                        Log.d("getFans", "next");
                        if(jsonBean!=null){
                            Constants.EditSharedPreference("fans",jsonBean.getData().getInfo());
                            tvFans.setText(""+jsonBean.getData().getInfo());
                        }
                    }
                });
    }



    private void getFocus() {
        Retrofit retrofit = APIRetrofit.getInstance();
        APIService service = retrofit.create(APIService.class);
        service.getFocus(Constants.getSharedPreference("chatid", getActivity()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Root>() {
                    @Override
                    public void onCompleted() {
                        Log.d("getFocus", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(final Root jsonBean) {
                        Log.d("getFocus", "next");
                        if(jsonBean!=null){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Constants.EditSharedPreference("focus",jsonBean.getData().getInfo());
                                    tvFollow.setText(""+jsonBean.getData().getInfo());
                                }
                            });
                        }
                    }
                });
    }


    /**
     * 上传头像
     */
    private void uploadHeadImage(View parent) {

        View view = LayoutInflater.from(getActivity().getApplication()).inflate(R.layout.layout_popupwindow, null);
        TextView btnCarema = (TextView) view.findViewById(R.id.btn_camera);
        TextView btnPhoto = (TextView) view.findViewById(R.id.btn_photo);
        TextView btnCancel = (TextView) view.findViewById(R.id.btn_cancel);
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        //popupWindow在弹窗的时候背景半透明
        final WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
        params.alpha = 0.5f;
        getActivity().getWindow().setAttributes(params);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                params.alpha = 1.0f;
                getActivity().getWindow().setAttributes(params);
            }
        });

        btnCarema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //权限判断
                if (ContextCompat.checkSelfPermission(MineFragment.this.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(MineFragment.this.getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                    Log.d("xxxx","a");

                } else {
                    //跳转到调用系统相机
                    gotoCamera();
                    Log.d("xxxx","b");

                }
                popupWindow.dismiss();
            }
        });
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //权限判断
                if (ContextCompat.checkSelfPermission(MineFragment.this.getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请READ_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(MineFragment.this.getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
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
     * 外部存储权限申请返回
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                gotoCamera();
            }
        } else if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                gotoPhoto();
            }
        }
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


    /**
     * 跳转到照相机
     */
    private void gotoCamera() {
        Log.d("evan", "*****************打开相机********************");
        //创建拍照存储的图片文件
        tempFile = new File(getActivity().getExternalCacheDir(),System.currentTimeMillis() + ".jpg");
        Log.d("url",getActivity().getExternalCacheDir()+"");
        //Log.d("url",getActivity().getExternalMediaDirs()+"");
       // tempFile = new File(FileUtil.checkDirPath(getActivity().getFilesDir() + "/files/"), System.currentTimeMillis() + ".jpg");
        //跳转到调用系统相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(MineFragment.this.getActivity(), Manifest.permission.CAMERA);
            if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(MineFragment.this.getActivity(),new String[]{Manifest.permission.CAMERA},REQUEST_CAPTURE);
            }
            //设置7.0中共享文件，分享路径定义在xml/file_paths.xml
            Uri contentUri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".fileProvider", tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        }
        startActivityForResult(intent, REQUEST_CAPTURE);
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
                    String cropImagePath = com.king.liaoba.util.FileUtil.getRealFilePathFromUri(getActivity().getApplicationContext(), uri);
                    Log.d("path",cropImagePath);
                    Bitmap bitMap = BitmapFactory.decodeFile(cropImagePath);
                    headImage1.setImageBitmap(bitMap);
                    File file = new File(cropImagePath);
                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
                    MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
                    Retrofit retrofit = APIRetrofit.getInstance();
                    APIService service =retrofit.create(APIService.class);
                    service.uploadImage(Constants.getSharedPreference("chatid",getActivity()),part)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<Root>() {
                                @Override
                                public void onNext(Root root) {
                                    if(root!=null){
                                        Constants.EditSharedPreference("headimg_url", Constants.BASE_URL+root.getData().getGetdata().get(0).getHeadimg_url().toString());
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.d("pic","error");
                                }

                                @Override
                                public void onCompleted() {
                                    Glide.with(getActivity()).load(Constants.
                                            BASE_URL+Constants.getSharedPreference("headimg_url",getActivity()))
                                            .placeholder(R.mipmap.live_default).dontAnimate().error(R.mipmap.live_default).
                                            crossFade().centerCrop().into(headImage1);

                                }
                            });

                }
                break;
        }
    }


    /**
     * 打开截图界面
     */
    public void gotoClipActivity(Uri uri) {
        if (uri == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(getActivity().getApplicationContext(), ClipImageActivity.class);
        intent.putExtra("type", type);
        intent.setData(uri);
        startActivityForResult(intent, REQUEST_CROP_PHOTO);
    }

    /**
     * #跳转到个人信息界面
     * */
    private void gotoSelfEditInfo(){
        Intent intent = new Intent();
        intent.setClass(getActivity().getApplicationContext(), SelfEditActivity.class);
        startActivity(intent);
    }
}
