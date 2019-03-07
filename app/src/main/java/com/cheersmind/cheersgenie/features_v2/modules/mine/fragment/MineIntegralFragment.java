package com.cheersmind.cheersgenie.features_v2.modules.mine.fragment;

import android.view.View;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.entity.TotalIntegralEntity;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 我的积分
 */
public class MineIntegralFragment extends LazyLoadFragment {

    Unbinder unbinder;

    @BindView(R.id.tv_usable_integral_val)
    TextView tvUsableIntegralVal;

    @Override
    protected int setContentView() {
        return R.layout.fragment_mine_integral;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
    }

    @Override
    protected void lazyLoad() {
        loadIntegralTotalScore();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    /**
     * 加载总积分
     */
    private void loadIntegralTotalScore() {
        DataRequestService.getInstance().getIntegralTotalScore(new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                onFailureDefault(e);
                //刷新总积分视图
                refreshIntegralTotalScoreView(new TotalIntegralEntity());
            }

            @Override
            public void onResponse(Object obj) {
                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    TotalIntegralEntity totalIntegralEntity = InjectionWrapperUtil.injectMap(dataMap, TotalIntegralEntity.class);
                    //刷新总积分视图
                    refreshIntegralTotalScoreView(totalIntegralEntity);

                } catch (Exception e) {
                    e.printStackTrace();
                    //刷新总积分视图
                    refreshIntegralTotalScoreView(new TotalIntegralEntity());
                }
            }
        }, httpTag, getContext());
    }

    /**
     * 刷新总积分视图
     * @param totalIntegralEntity 总积分对象
     */
    private void refreshIntegralTotalScoreView(TotalIntegralEntity totalIntegralEntity) {
        //可用积分
        tvUsableIntegralVal.setText(String.valueOf(totalIntegralEntity.getConsumable()));
    }

}

