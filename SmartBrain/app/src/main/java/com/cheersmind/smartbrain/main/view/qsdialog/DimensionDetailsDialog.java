package com.cheersmind.smartbrain.main.view.qsdialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.cheersmind.smartbrain.R;
import com.cheersmind.smartbrain.main.entity.DimensionInfoEntity;
import com.cheersmind.smartbrain.main.util.imagetool.ImageCacheTool;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2018/5/30 0030.
 */

public class DimensionDetailsDialog extends Dialog {

    private Context context;
    private DimensionInfoEntity entity;

    private ImageView ivClose;
    private ImageView ivIcon;
    private TextView tvTitle;
    private TextView tvContent;

    public DimensionDetailsDialog(@NonNull Context context, DimensionInfoEntity entity) {
        super(context);
        this.context = context;
        this.entity = entity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.qs_dialog_dimension_detail);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        initView();
    }

    private void initView(){
        ivClose = (ImageView)findViewById(R.id.iv_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDismiss();
            }
        });
        ivIcon = (ImageView)findViewById(R.id.iv_icon);
        tvTitle = (TextView)findViewById(R.id.tv_title);
        tvContent = (TextView)findViewById(R.id.tv_content);
        if(TextUtils.isDigitsOnly(entity.getDimensionName())){
            tvTitle.setText("");
        }else{
            tvTitle.setText(entity.getDimensionName());
        }
        if(TextUtils.isEmpty(entity.getDefinition())){
            if(!TextUtils.isEmpty(entity.getDescription())){
                tvContent.setText(entity.getDescription());
            }else{
                tvContent.setText("");
            }
        }else{
            tvContent.setText(entity.getDefinition());
        }

        if(TextUtils.isEmpty(entity.getIcon())){
            ivIcon.setImageResource(R.mipmap.dimension_icon_default);
        }else{
            ImageCacheTool imageCacheTool = ImageCacheTool.getInstance();
            try {
                imageCacheTool.asyncLoadImage(new URL(entity.getIcon()),ivIcon);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    public void onDismiss(){
        this.dismiss();
    }
}
