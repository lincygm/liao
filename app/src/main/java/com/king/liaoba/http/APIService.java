package com.king.liaoba.http;

import android.service.media.MediaBrowserService.Result;

import com.king.liaoba.bean.AppStart;
import com.king.liaoba.bean.FriendsRoot;
import com.king.liaoba.bean.LiveCategory;
import com.king.liaoba.bean.LiveListResult;
import com.king.liaoba.bean.PictureRoot;
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
    @POST("/index.php/Home/FangJian/getFangJianList/page/{page}")
    Observable<Root> getFangList(@Path("page")String page);

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
    Observable<PictureRoot> uploadPictures( @Path("chatid")String chatid,@Part MultipartBody.Part part);

    /** 关注 */
    @POST("index.php/Home/User/searchFollowsAndFocus/chatid/{chatid}/foucschatid/{focus_chatid}")
    Observable<Root> focus( @Path("chatid")String chatid,@Path("focus_chatid")String chatid2);



    /**删除图片**/
    @POST("index.php/Home/User/deletePicture/chatid/{chatid}/id/{id}")
    Observable<Root> deletePic(@Path("chatid") String chatid,@Path("id") String id);

    /**获取图片列表**/
    @POST("index.php/Home/User/getImageList/chatid/{chatid}")
    Observable<PictureRoot> getImageList(@Path("chatid")String chatid);

    /**上传声音**/
    @Multipart
    @POST("index.php/Home/Upload/uploadRecord/chatid/{chatid}")
    Observable<Root> uploadRecord(@Path("chatid")String chatid,@Part MultipartBody.Part part);

    /**获取关注数**/
    @POST("index.php/Home/User/showFocus/chatid/{chatid}")
    Observable<Root> getFocus(@Path("chatid")String chatid);

    /**获取粉丝数**/
    @POST("index.php/Home/User/showFans/chatid/{chatid}")
    Observable<Root> getFans(@Path("chatid")String chatid);
    /** 获取关注列表 */
    @POST("index.php/Home/User/getFocusList/chatid/{chatid}/page/{page}")
    Observable<FriendsRoot> getFocusList( @Path("chatid")String chatid,@Path("page")int page);
    /** 获取粉丝列表 */
    @POST("index.php/Home/User/getFansList/chatid/{chatid}/page/{page}")
    Observable<FriendsRoot> getFansList(@Path("chatid")String chatid,@Path("page")int page);

    /** 更新userinfo */
    @POST("index.php/Home/User/updateUser/chatid/{chatid}/sex/{sex}/age/{age}/sign/{sign}/nickname/{nickname}")
    Observable<Root> updateUser(@Path("chatid")String chatid,@Path("sex") String sex,@Path("age")String age,
                                @Path("sign")String sign,@Path("nickname")String nickname);

    /**签到**/
    @POST("index.php/Home/User/getSignIn/chatid/{chatid}")
    Observable<FriendsRoot> signIn(@Path("chatid")String chatid);

    /**获取签到状态**/
    @POST("index.php/Home/User/getSignInStatus/chatid/{chatid}")
    Observable<FriendsRoot> signInStatus(@Path("chatid")String chatid);

    /**setprice**/
    @POST("index.php/Home/User/setPrice/chatid/{chatid}/price/{price}")
    Observable<Root> setPrice(@Path("chatid")String chatid,@Path("price")String price);

    /**randomChat**/
    @POST("index.php/Home/Heart/getRandOne/chatid{chatid}")
    Observable<Root> randomChat(@Path("chatid")String chatid);

    /**register**/
    @POST("index.php/Home/User/register/username/{username}/password/{password}/nickname/{nickname}/sex/{sex}/telephone/{telephone}")
    Observable<Root> register(@Path("username")String username,@Path("password")String password,@Path("nickname")String nickname,@Path("sex")String sex
    ,@Path("telephone")String telephone);

    /**updatePassword**/
    @POST("index.php/Home/User/updatePassword/password/{password}/telephone/{telephone}")
    Observable<Root> updatePassword(@Path("password")String password,@Path("telephone")String telephone);

    /**发送短信**/
    @POST("index.php/Home/Message/sendRegist/numbers/{number}")
    Observable<Root> sendMessage(@Path("number")String number);

    /**获取关注状态**/
    @POST("index.php/Home/User/getFocusStatus/chatid/{chatid}/focus_id/{focus_id}")
    Observable<Root> getFocusStatus(@Path("chatid")String chatid,@Path("focus_id")String focus_id);

    /**删除关注状态**/
    @POST("index.php/Home/User/deleteFocus/chatid/{chatid}/focus_id/{focus_id}")
    Observable<Root> deleteFocus(@Path("chatid")String chatid,@Path("focus_id")String focus_id);


    /**随机匹配**/
    @POST("index.php/Home/Heart/getRandOne/chatid/{chatid}")
    Observable<Root> getRandOne(@Path("chatid")String chatid);

}
