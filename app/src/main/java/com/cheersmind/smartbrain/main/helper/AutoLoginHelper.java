package com.cheersmind.smartbrain.main.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.cheersmind.smartbrain.main.Exception.QSCustomException;
import com.cheersmind.smartbrain.main.activity.MainActivity;
import com.cheersmind.smartbrain.main.dao.ChildInfoDao;
import com.cheersmind.smartbrain.main.entity.ChildInfoEntity;
import com.cheersmind.smartbrain.main.entity.ChildInfoRootEntity;
import com.cheersmind.smartbrain.main.entity.TopicInfoChildEntity;
import com.cheersmind.smartbrain.main.entity.TopicInfoEntity;
import com.cheersmind.smartbrain.main.entity.TopicRootEntity;
import com.cheersmind.smartbrain.main.entity.WXUserInfoEntity;
import com.cheersmind.smartbrain.main.service.BaseService;
import com.cheersmind.smartbrain.main.service.DataRequestService;
import com.cheersmind.smartbrain.main.util.InjectionWrapperUtil;
import com.cheersmind.smartbrain.main.util.JsonUtil;
import com.cheersmind.smartbrain.main.util.ToastUtil;
import com.cheersmind.smartbrain.main.view.LoadingView;
import com.cheersmind.smartbrain.module.login.UCManager;
import com.cheersmind.smartbrain.module.login.UserService;

import org.litepal.crud.DataSupport;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/7/4.
 */

public class AutoLoginHelper {

    private Context context;

    private static AutoLoginHelper instance;
    private AutoLoginHelper(Context context){
        this.context = context;
    }
    public static synchronized AutoLoginHelper getInstance(Context context){
        if (instance == null) {
            instance = new AutoLoginHelper(context);
        }
        return instance;
    }

    public void canAutoLogin(){
        WXUserInfoEntity wxUserInfoEntity  = DataSupport.findFirst(WXUserInfoEntity.class);
        if(wxUserInfoEntity!=null){

            long curTime = System.currentTimeMillis() - wxUserInfoEntity.getSysTimeMill();
            long t = 1000 * 60 * 60 * 24 * 7;//6天
            if(curTime < t){
                //缓存token有效,自动登录
                UCManager.getInstance().setAcccessToken(wxUserInfoEntity.getAccessToken());
                UCManager.getInstance().setMacKey(wxUserInfoEntity.getMacKey());
                UCManager.getInstance().setRefreshToken(wxUserInfoEntity.getRefreshToken());
                UCManager.getInstance().setUserId(wxUserInfoEntity.getUserId());
                getChildList();
            }else{
                //重新请求token
                DataSupport.deleteAll(WXUserInfoEntity.class);
                Log.i("wxtest","重新请求token1");
            }
        }else{
            //重新请求token
            DataSupport.deleteAll(WXUserInfoEntity.class);
            Log.i("wxtest","重新请求token2");
        }
    }

    public void getChildList() {
        UserService.getChildList(new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                LoadingView.getInstance().dismiss();
                Toast.makeText(context,"获取孩子列表失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Object obj) {
                LoadingView.getInstance().dismiss();
                ChildInfoDao.deleteAllChild();
                if(obj != null){
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    ChildInfoRootEntity childData = InjectionWrapperUtil.injectMap(dataMap, ChildInfoRootEntity.class);
                    if(childData != null && childData.getItems()!=null){
                        List<ChildInfoEntity> childList = childData.getItems();
                        if(childList.size()==0){
                            Toast.makeText(context,"孩子列表为空",Toast.LENGTH_SHORT).show();
                        }
                        for(int i=0;i<childList.size();i++){
                            ChildInfoEntity entity = childList.get(i);
                            if(i==0){
                                entity.setDefaultChild(true);
                            }else{
                                entity.setDefaultChild(false);
                            }
                            UCManager.getInstance().setDefaultChild(entity);
                            entity.save();
                        }
                        goMainPage();
                    }else{
                        Toast.makeText(context,"获取孩子列表失败",Toast.LENGTH_SHORT).show();
                        return;
                    }

                }


            }
        });
    }

    private void goMainPage () {
//        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.putString("userName_cache", etUsername.getText().toString());
//        editor.putString("password_cache", etPassword.getText().toString());
//        editor.commit();

        LoadingView.getInstance().dismiss();
        //缓存基础主题列表数据
        LoadingView.getInstance().show(context);
        DataRequestService.getInstance().loadChildTopicList(ChildInfoDao.getDefaultChildId(),0, 100, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                LoadingView.getInstance().dismiss();
                ToastUtil.showShort(context,"获取主题列表数据失败");
            }

            @Override
            public void onResponse(Object obj) {
                LoadingView.getInstance().dismiss();
                DataSupport.deleteAll(TopicInfoEntity.class);
                if(obj!=null){
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    TopicRootEntity topicData = InjectionWrapperUtil.injectMap(dataMap, TopicRootEntity.class);
                    if(topicData != null && topicData.getItems()!=null){
                        DataSupport.saveAll(topicData.getItems());
                        Intent intent = new Intent(context, MainActivity.class); // 从启动动画ui跳转到主ui
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        ((Activity)context).finish();
                    }else{
                        Toast.makeText(context,"获取主题列表数据失败",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(context,"获取主题列表数据失败",Toast.LENGTH_SHORT).show();
                }
            }
        });

//        DataRequestService.getInstance().getUserProject(ChildInfoDao.getDefaultChildId(), new BaseService.ServiceCallback() {
//            @Override
//            public void onFailure(QSCustomException e) {
//                e.printStackTrace();
//                ToastUtil.showShort(context,"获取项目列表失败");
//            }
//
//            @Override
//            public void onResponse(Object obj) {
//                if(obj!=null){
//                    Map dataMap = JsonUtil.fromJson(obj.toString(),Map.class);
//                    List<Map<String,Object>> list = (List<Map<String,Object>>)dataMap.get("items");
//                    List<ProjectEntity> data = InjectionWrapperUtil.injectMaps(list, ProjectEntity.class);
//                    if(data!=null && data.size()>0){
//                        int slidingSelectIndex = (int) SharedPreferencesUtils.getParam(context, MainActivity.SLIDING_ITEM_SHARE_KEY, 0);
//                        if(slidingSelectIndex>0 && slidingSelectIndex<data.size()){
//                            Constant.CUR_EXAM_ID = data.get(slidingSelectIndex).getExamId();
//                        }else{
//                            for(int i=0;i<data.size();i++){
//                                if(i==0){
//                                    //todo 设置第一个项目为默认项目
//                                    data.get(i).setCurSelect(1);
//                                    Constant.CUR_EXAM_ID = data.get(i).getExamId();
//                                    SharedPreferencesUtils.setParam(context,MainActivity.SLIDING_ITEM_SHARE_KEY,i);
//                                }else{
//                                    data.get(i).setCurSelect(0);
//                                }
//                            }
//                        }
//
//                        DataSupport.deleteAll(ProjectEntity.class);
//                        DataSupport.saveAll(data);
//
//
//                    }else{
//                        ToastUtil.showShort(context,"项目列表为空");
//                    }
//                }
//            }
//        });

    }

    //是否已经关注过
    private boolean isFollowed(List<TopicInfoEntity> list){
        for(int i=0;i<list.size();i++){
            TopicInfoChildEntity entity= list.get(i).getChildTopic();
            if(entity!=null && entity.isFollowed()){
                return true;
            }

        }
        return false;
    }
}
