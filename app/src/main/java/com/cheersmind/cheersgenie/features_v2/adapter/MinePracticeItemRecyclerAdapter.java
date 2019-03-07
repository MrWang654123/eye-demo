package com.cheersmind.cheersgenie.features_v2.adapter;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features_v2.entity.ExamTaskItemEntity;
import com.cheersmind.cheersgenie.features_v2.entity.PracticeItemEntity;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * 我的实践子项recycler适配器
 */
public class MinePracticeItemRecyclerAdapter extends BaseMultiItemQuickAdapter<PracticeItemEntity, BaseViewHolder> {

    public MinePracticeItemRecyclerAdapter(List<PracticeItemEntity> data) {
        super(data);
        addItemType(PracticeItemEntity.TYPE_HEADER, R.layout.recycleritem_mine_practice_item_header);
        addItemType(ExamTaskItemEntity.TYPE_ARTICLE, R.layout.recycleritem_mine_practice_item_article);
    }

    @Override
    protected void convert(BaseViewHolder helper, PracticeItemEntity item) {

        switch (helper.getItemViewType()) {
            //Header
            case PracticeItemEntity.TYPE_HEADER: {
                //标题
                helper.setText(R.id.tv_title, item.getArticleTitle());
                break;
            }
            //文章
            case PracticeItemEntity.TYPE_ARTICLE: {
                //标题
//                helper.setText(R.id.tv_title, item.getArticleTitle());
                //提示图标
                SimpleDraweeView imageView = helper.getView(R.id.iv_main);
                imageView.setImageURI(item.getArticleImg());
                break;
            }
        }

    }

}
