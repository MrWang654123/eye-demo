package com.cheersmind.cheersgenie.features.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.dto.ArticleDto;
import com.cheersmind.cheersgenie.features.modules.article.activity.ArticleListActivity;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.WarpLinearLayout;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.CategoryEntity;
import com.cheersmind.cheersgenie.main.entity.CategoryRootEntity;
import com.cheersmind.cheersgenie.main.entity.TopicRootEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.DensityUtil;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.main.util.RepetitionClickUtil;
import com.cheersmind.cheersgenie.main.util.ToastUtil;

import java.util.List;
import java.util.Map;

import butterknife.BindView;


/**
 * 分类的对话框
 */
public class CategoryDialog extends Dialog implements View.OnClickListener {

    //可换行布局
    private WarpLinearLayout warpLinearLayout;
    //空布局
    private XEmptyLayout emptyLayout;
    //关闭按钮
    private ImageView ivClose;

    //分类集合数据
    private List<CategoryEntity> categories;

    //操作监听
    private OnOperationListener listener;

    public CategoryDialog(@NonNull Context context, OnOperationListener listener) {
        super(context, R.style.loading_dialog);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_category);

        setCanceledOnTouchOutside(false);
        setCancelable(false);

        //初始化视图
        initView();
        //加载分类
        loadCategory();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        ivClose = findViewById(R.id.iv_close);
        ivClose.setOnClickListener(this);

        warpLinearLayout = findViewById(R.id.warpLinearLayout);
        emptyLayout = findViewById(R.id.emptyLayout);
        //点击重载监听
        emptyLayout.setOnLayoutClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                //加载分类
                loadCategory();
            }
        });
    }

    /**
     * 加载分类
     */
    private void loadCategory() {
        //通信等待提示
        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        DataRequestService.getInstance().getCategories(new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                //网络异常
                emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(Object obj) {
                emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    CategoryRootEntity categoryRoot = InjectionWrapperUtil.injectMap(dataMap, CategoryRootEntity.class);
                    categories = categoryRoot.getItems();
                    //空的情况
                    if (ArrayListUtil.isEmpty(categories)) {
                        //无数据
                        emptyLayout.setErrorType(XEmptyLayout.NODATA);
                        return;
                    }

                    //有数据情况处理
                    for (int i=0; i<categories.size(); i++) {
                        CategoryEntity categoryEntity = categories.get(i);
                        TextView tv = new TextView(CategoryDialog.this.getContext());
                        tv.setText(categoryEntity.getName());
                        tv.setBackgroundResource(R.drawable.shape_corner);
                        int hPaddingVal = DensityUtil.dip2px(getContext(), 15);
                        int vPaddingVal = DensityUtil.dip2px(getContext(), 10);
                        tv.setPadding(hPaddingVal,vPaddingVal,hPaddingVal,vPaddingVal);
                        tv.setTag(i);
                        tv.setOnClickListener(new OnMultiClickListener() {
                            @Override
                            public void onMultiClick(View view) {
                                ArticleDto articleDto = new ArticleDto();
                                CategoryEntity tempCategory = categories.get((int) view.getTag());
                                articleDto.setCategoryId(tempCategory.getId());
                                ArticleListActivity.startArticleListActivity(getContext(), articleDto);
                            }
                        });
                        warpLinearLayout.addView(tv);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    //加载失败
                    emptyLayout.setErrorType(XEmptyLayout.NODATA_ENABLE_CLICK);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (!RepetitionClickUtil.isFastClick()) {
            return;
        }
        if (v == ivClose) {
            if (listener != null) {
                listener.onExit();
            }
            dismiss();
        }
    }


    public interface QuestionQuitDialogCallback {

        void onQuesExit();

        void onQuesContinue();
    }

    @Override
    public void show() {
        //动画
        getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        super.show();
        //设置宽度全屏，要设置在show的后面
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        //位于底部
        layoutParams.gravity = Gravity.BOTTOM;
        //宽度
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        //高度
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        //DecorView的内间距（目前测试的机型还没发现有影响）
//        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        //背景灰度
        layoutParams.dimAmount = 0.4f;
        //设置属性
        getWindow().setAttributes(layoutParams);

    }

    //操作监听
    public interface OnOperationListener {

        //退出操作
        void onExit();
    }

}
