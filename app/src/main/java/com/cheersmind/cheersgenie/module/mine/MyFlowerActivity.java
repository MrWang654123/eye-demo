package com.cheersmind.cheersgenie.module.mine;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.activity.BaseActivity;
import com.cheersmind.cheersgenie.main.dao.ChildInfoDao;
import com.cheersmind.cheersgenie.main.entity.FlowerRecordInfoEntity;
import com.cheersmind.cheersgenie.main.entity.FlowerRecordRootEntity;
import com.cheersmind.cheersgenie.main.entity.UserDetailsEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/15.
 */

public class MyFlowerActivity extends BaseActivity {

    private TextView tvFlowerCount;
    private ListView lvFlower;

    List<FlowerRecordInfoEntity> fRecordData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_flower);
        getSupportActionBar().show();
        setTitle("我的鲜花");

        initView();
        initData();
    }

    private void initView(){
        View headView = View.inflate(this,R.layout.activity_my_flower_head,null);
        tvFlowerCount = (TextView)headView.findViewById(R.id.tv_folwer_count);
        lvFlower = (ListView)findViewById(R.id.lv_my_flower);
        lvFlower.addHeaderView(headView);
        lvFlower.setAdapter(adapter);
    }

    private void initData(){
        getUserDetails();
        getFlowersRecord();
    }

    BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return fRecordData.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHoler;
            if(convertView == null){
                convertView = View.inflate(MyFlowerActivity.this,R.layout.item_my_flower,null);
                viewHoler = new ViewHolder();
                viewHoler.initView(convertView);
                convertView.setTag(viewHoler);
            }else{
                viewHoler = (ViewHolder)convertView.getTag();
            }

            FlowerRecordInfoEntity entity = fRecordData.get(position);
            if(entity.getType()==2){
                if(TextUtils.isEmpty(entity.getFactorName())){
                    viewHoler.tvFlowerTitle.setText("答题");
                }else{
                    viewHoler.tvFlowerTitle.setText(getResources().getString(R.string.myflower_record_title,entity.getFactorName()));
                }

            }else{
                if(TextUtils.isEmpty(entity.getDimensionName())){
                    viewHoler.tvFlowerTitle.setText("解锁");
                }else{
                    viewHoler.tvFlowerTitle.setText(getResources().getString(R.string.myflower_record_title2,entity.getDimensionName()));

                }
            }

            viewHoler.tvFlowerNum.setText(String.valueOf(entity.getFlowers()));
            return convertView;
        }
    };

    class ViewHolder{

        TextView tvFlowerTitle;
        TextView tvFlowerNum;

        void initView(View view){
            tvFlowerTitle = (TextView)view.findViewById(R.id.tv_flower_title);
            tvFlowerNum = (TextView)view.findViewById(R.id.tv_flower_num);
        }
    }

    private void getFlowersRecord(){
        DataRequestService.getInstance().getChildFlowerRecord(0, 1000, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                Toast.makeText(MyFlowerActivity.this,"获取鲜花记录失败！",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Object obj) {
                if(obj!=null){
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    FlowerRecordRootEntity frRoot = InjectionWrapperUtil.injectMap(dataMap, FlowerRecordRootEntity.class);
                    if(frRoot!=null && frRoot.getItems()!=null){
                        fRecordData = frRoot.getItems();
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void getUserDetails(){
        if(ChildInfoDao.getDefaultChild().getFlowerCount()>0){
            tvFlowerCount.setText(String.valueOf(ChildInfoDao.getDefaultChild().getFlowerCount()));
            return;
        }
        DataRequestService.getInstance().getUserDetails(new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {

            }

            @Override
            public void onResponse(Object obj) {
                if(obj!=null){
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    UserDetailsEntity userDetails = InjectionWrapperUtil.injectMap(dataMap, UserDetailsEntity.class);
                    if(userDetails!=null){
                        tvFlowerCount.setText(String.valueOf(userDetails.getFlowers()));
                    }
                }
            }
        });
    }
}
