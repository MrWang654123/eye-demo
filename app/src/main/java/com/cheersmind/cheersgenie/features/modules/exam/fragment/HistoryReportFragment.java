package com.cheersmind.cheersgenie.features.modules.exam.fragment;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.HistoryReportRecyclerAdapter;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.entity.HistoryReportItemEntity;
import com.cheersmind.cheersgenie.features.entity.HistoryReportRootEntity;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features_v2.dto.ExamReportDto;
import com.cheersmind.cheersgenie.features_v2.view.dialog.ExamReportDialog;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.module.login.UCManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 历史报告
 */
public class HistoryReportFragment extends LazyLoadFragment {

    //报告dto
    private ExamReportDto reportDto;

    @BindView(R.id.recycleView)
    RecyclerView recycleView;
    //空布局
    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;

    Unbinder unbinder;

    //适配器的数据列表
    List<HistoryReportItemEntity> recyclerItem = new ArrayList<>();
    //适配器
    private HistoryReportRecyclerAdapter recyclerAdapter;

    //recycler子项的点击监听
    BaseQuickAdapter.OnItemClickListener recyclerItemClickListener = new BaseQuickAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            showExamReportDialog(position);
        }
    };

    //recycler子项的孩子的点击监听
    BaseQuickAdapter.OnItemChildClickListener recyclerItemChildClickListener = new BaseQuickAdapter.OnItemChildClickListener() {

        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
            switch (view.getId()) {
                //查看
                case R.id.tv_goto_detail: {
//                    HistoryReportItemEntity historyReportItemEntity = recyclerItem.get(position);
//                    String childExamId = historyReportItemEntity.getChildExamId();
//                    topicInfo.getChildTopic().setChildExamId(childExamId);
////                    ToastUtil.showShort(getContext(), "点击查看 " + (position + 1));
//                    try {
//                        new TopicReportDialog().setTopicInfo(topicInfo).show(getChildFragmentManager(), "报告");
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        if (getActivity() != null) {
//                            ToastUtil.showShort(getActivity().getApplication(), e.getMessage());
//                        }
//                    }

                    showExamReportDialog(position);
                    break;
                }
            }
        }
    };

    ExamReportDialog examReportDialog;

//    /**
//     * 显示测评报告弹窗
//     * @param dto 报告dto
//     */
//    private void showExamReportDialog(ExamReportDto dto) throws QSCustomException {
//        if (examReportDialog == null) {
//            examReportDialog = new ExamReportDialog().setReportDto(dto);
//        }
//        if (examReportDialog != null) {
//            examReportDialog.setReportDto(dto);
//            examReportDialog.show(getChildFragmentManager(), "报告");
//        }
//    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_history_report;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        try {
            Bundle bundle = getArguments();
            if (bundle != null) {
                reportDto = (ExamReportDto) bundle.getSerializable(DtoKey.EXAM_REPORT_DTO);
                if (reportDto == null) {
                    throw new Exception();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //空布局：无数据可重载
            emptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
            return;
        }

        //重载监听
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                //加载报告
                loadHistoryReport();
            }
        });

        //适配器
        recyclerAdapter = new HistoryReportRecyclerAdapter(recyclerItem);
        recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        recycleView.setAdapter(recyclerAdapter);
        //设置子项点击监听
        recyclerAdapter.setOnItemClickListener(recyclerItemClickListener);
        //子项孩子的点击监听
        recyclerAdapter.setOnItemChildClickListener(recyclerItemChildClickListener);
        //添加自定义分割线
//        DividerItemDecoration divider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
//        divider.setDrawable(ContextCompat.getDrawable(getContext(),R.drawable.recycler_divider_line));
//        recycleView.addItemDecoration(divider);

    }

    @Override
    protected void lazyLoad() {
        loadHistoryReport();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 加载历史报告
     */
    private void loadHistoryReport() {
        //通信等待提示
        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        String defaultChildId = UCManager.getInstance().getDefaultChild().getChildId();
        DataRequestService.getInstance().getHistoryReport(
                reportDto.getRelationId(),
                defaultChildId,
                new BaseService.ServiceCallback() {
                    @Override
                    public void onFailure(QSCustomException e) {
                        //空布局：网络连接有误，或者请求失败
                        emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
                    }

                    @Override
                    public void onResponse(Object obj) {
                        //空布局：隐藏
                        emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

                        try {
                            Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                            HistoryReportRootEntity data = InjectionWrapperUtil.injectMap(dataMap, HistoryReportRootEntity.class);

                            if (data == null || data.getItems().size() == 0) {
                                //空布局：无数据
                                emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                                return;
                            }

                            //历史报告项集合
                            recyclerItem = data.getItems();
                            recyclerItem.add(0, new HistoryReportItemEntity(HistoryReportItemEntity.HEAD));
//                            for (int i=0; i< 15; i++) {
//                                recyclerItem.add(recyclerItem.get(1));
//                            }
                            recyclerAdapter.setNewData(recyclerItem);

//                            recyclerAdapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                            //空布局：加载失败
                            emptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
                        }
                    }
                }, httpTag, getActivity());
    }

    /**
     * 弹出话题报告对话框
     * @param position 在列表中的索引
     */
    private void showExamReportDialog(int position) {
        try {
            HistoryReportItemEntity historyReportItemEntity = recyclerItem.get(position);
            String childExamId = historyReportItemEntity.getChildExamId();
            //重置孩子测评ID
            reportDto.setChildExamId(childExamId);
            examReportDialog = new ExamReportDialog().setReportDto(reportDto).setListener(new ExamReportDialog.OnOperationListener() {
                @Override
                public void onExit() {
                    examReportDialog.clearListener();
                    examReportDialog = null;
                }
            });
            examReportDialog.show(getChildFragmentManager(), "报告");
//                        showExamReportDialog(reportDto);
        } catch (Exception e) {
            if (getActivity() != null) {
                ToastUtil.showShort(getActivity().getApplication(), getString(R.string.operate_fail));
            }
        }
    }

}
