package com.king.liaoba.http;

import android.service.media.MediaBrowserService.Result;

import com.king.liaoba.bean.AppStart;
import com.king.liaoba.bean.LiveCategory;
import com.king.liaoba.bean.LiveListResult;
import com.king.liaoba.bean.Recommend;
import com.king.liaoba.bean.Room;
import com.king.liaoba.bean.Root;
import com.king.liaoba.bean.SearchRequestBody;
import com.king.liaoba.bean.SearchResult;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import rx.Observable;

;
/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2017/2/13
 */

public interface APIService {

    /**
     * 获取App启动页信息
     * @return
     */
    @GET("json/page/app-data/info.json?v=3.0.1&os=1&ver=4")
    Observable<AppStart> getAppStartInfo();

    /**
     * 获取分类列表
     * @return
     *
     * categories/list.json
     */
    @GET("json/app/index/category/info-android.json?v=3.0.1&os=1&ver=4")
    Observable<List<LiveCategory>> getAllCategories();

    /**
     * 获取推荐列表
     * @return
     */
    @GET("json/app/index/recommend/list-android.json?v=3.0.1&os=1&ver=4")
    Observable<Recommend> getRecommend();

    /**
     * 获取直播列表
     * @return
     */
    @GET("json/play/list.json?v=3.0.1&os=1&ver=4")
    Observable<LiveListResult> getLiveListResult();


    @GET("json/categories/{slug}/list.json?v=3.0.1&os=1&ver=4")
    Observable<LiveListResult> getLiveListResultByCategories(@Path("slug") String slug);

    /**
     * 进入房间
     * @param uid
     * @return
     */
    @GET("json/rooms/{uid}/info.json?v=3.0.1&os=1&ver=4")
    Observable<Room> enterRoom(@Path("uid")String uid);

    /**
     * 搜索
     * @param searchRequestBody
     * @return
     */
    @POST("site/search")
    Observable<SearchResult> search(@Body SearchRequestBody searchRequestBody);

    /**
     * 登录
     * */
    @POST("index.php/Home/User/login/username/{username}/password/{password}")
    Observable<Root> login(@Path("username") String username, @Path("password")String password);

    /**
     * 更新用户数据
     * */
    @POST("index.php/Home/User/updateUser/sex/{sex}/age/{age}/sign/{sign}/username/{username}")
    Observable<Root> saveuserinfo(@Path("sex") String sex, @Path("age") String age,@Path("sign") String sign,@Path("username") String username);

    /**
     * 上传头像
     */
    @Multipart
    @POST("/index.php/Home/User/uploadMemberIcon")
    Observable<Result<String>> uploadMemberIcon(@Part List<MultipartBody.Part> partList);

    /**
     * @心跳链接
     * */
    @POST("index.php/Home/FangJian/recvHeat/chatid/{chatid}")
    Observable<Root> sendHeart(@Path("chatid") String chatid);

    /**
     * @存储jpush id
     * */
    @POST("/index.php/Home/User/savePushRegisterid/chatid/{chatid}/registerid/{registerid}")
    Observable<Root> saveJPushRegisterId(@Path("chatid") String chatid,@Path("registerid") String registerid);

    /**
     * @获取房间列表
     * */
    @POST("/index.php/Home/FangJian/getFangJianList")
    Observable<Root> getFangList();

    /**
     * 获取用户信息
     * */

    @POST("index.php/Home/FangJian/getUserInfoByChatid/chatid/{chatid}")
    Observable<Root> getUserInfoByChatid(@Path("chatid") String chatid);


    /**
     * 保存极光推送id
     * */
    @POST("index.php/Home/User/savePushRegisterid/chatid/chatid/{chatid}/registerid/{registerid}")
    Observable<Root> savePushRegisterid(@Path("chatid")String chatid,@Path("registerid") String registerid);

    /** 上传图片 */
    @Multipart
    @POST("index.php/Home/Upload/uppic/chatid/{chatid}")
    Observable<Root> uploadImage( @Path("chatid")String chatid,@Part MultipartBody.Part part);

    /** 上传生活照 */
    @Multipart
    @POST("index.php/Home/Upload/upblo/chatid/{chatid}")
    Observable<Root> uploadPictures( @Path("chatid")String chatid,@Part MultipartBody.Part part);

    /** 关注 */
    @POST("index.php/Home/User/searchFollowsAndFocus/chatid/{chatid}/foucschatid/{focus_chatid}")
    Observable<Root> focus( @Path("chatid")String chatid,@Path("focus_chatid")String chatid2);

    /** 获取关注列表 */
    @POST("index.php/Home/User/getFocusList/chatid/{chatid}")
    Observable<Root> focusList( @Path("chatid")String chatid);

}
