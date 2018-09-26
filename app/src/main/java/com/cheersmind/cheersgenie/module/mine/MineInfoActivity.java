package com.cheersmind.cheersgenie.module.mine;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.main.activity.BaseActivity;
import com.cheersmind.cheersgenie.main.dao.ChildInfoDao;
import com.cheersmind.cheersgenie.main.entity.ChildInfoEntity;
import com.cheersmind.cheersgenie.main.util.AgeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/26.
 */

public class MineInfoActivity extends BaseActivity {

    private ListView lvMineInfo;

    private List<String> listTitle = new ArrayList<>();

    ChildInfoEntity defaultChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_info);
        getSupportActionBar().show();
        setTitle("我的资料");
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView(){
        lvMineInfo = (ListView)findViewById(R.id.lv_mine_info);
    }

    private void initData(){

        defaultChild = ChildInfoDao.getDefaultChild();

        listTitle.add("姓名");
        listTitle.add("性别");
        listTitle.add("年龄");
        listTitle.add("学校");
        listTitle.add("学段");
        listTitle.add("年级");
        listTitle.add("班级");

        lvMineInfo.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return listTitle.size();
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
            ViewHolder viewHolder ;

            if(convertView==null){
                convertView = View.inflate(MineInfoActivity.this,R.layout.item_mine_info,null);
                viewHolder = new ViewHolder();
                viewHolder.initView(convertView);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }

            viewHolder.tvTitle.setText(listTitle.get(position));

            if(position == 0){
                if(!TextUtils.isEmpty(defaultChild.getChildName())){
                    viewHolder.tvContent.setText(defaultChild.getChildName());
                }else{
                    viewHolder.tvContent.setText("未知");
                }
            }else if(position == 1){
                String sex;
                if(defaultChild.getSex()==1){
                    sex = "男";
                }else if(defaultChild.getSex()==2){
                    sex = "女";
                }else{
                    sex = "未知";
                }
                viewHolder.tvContent.setText(sex);
            }else if(position == 2){
                if(!TextUtils.isEmpty(defaultChild.getBirthDay())){
                    String str = defaultChild.getBirthDay().substring(0,10);
                    int age = AgeUtils.getAgeFromBirthTime(str);
                    viewHolder.tvContent.setText(String.valueOf(age));
                }else{
                    viewHolder.tvContent.setText("未知");
                }
            }else if(position == 3){
                if(!TextUtils.isEmpty(defaultChild.getSchoolName())){
                    viewHolder.tvContent.setText(defaultChild.getSchoolName());
                }else{
                    viewHolder.tvContent.setText("未知");
                }
            }else if(position == 4){
                if(!TextUtils.isEmpty(defaultChild.getPeriod())){
                    viewHolder.tvContent.setText(defaultChild.getPeriod());
                }else{
                    viewHolder.tvContent.setText("未知");
                }
            }else if(position == 5){
                if(!TextUtils.isEmpty(defaultChild.getGrade())){
                    viewHolder.tvContent.setText(defaultChild.getGrade());
                }else{
                    viewHolder.tvContent.setText("未知");
                }
            }else if(position == 6){
                if(!TextUtils.isEmpty(defaultChild.getClass_name())){
                    viewHolder.tvContent.setText(defaultChild.getClass_name());
                }else{
                    viewHolder.tvContent.setText("未知");
                }
            }else{
                viewHolder.tvContent.setText("未知");
            }

            return convertView;
        }
    };

    class ViewHolder{

        TextView tvTitle;
        TextView tvContent;

        void initView(View view){
            tvTitle = (TextView)view.findViewById(R.id.tv_title);
            tvContent = (TextView)view.findViewById(R.id.tv_content);
        }
    }
}
