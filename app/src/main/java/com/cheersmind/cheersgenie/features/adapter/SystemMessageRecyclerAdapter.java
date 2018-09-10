package com.cheersmind.cheersgenie.features.adapter;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.main.entity.MessageEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * 系统消息recycler适配器
 */
public class SystemMessageRecyclerAdapter extends BaseMultiItemQuickAdapter<MessageEntity, BaseViewHolder> {

    public SystemMessageRecyclerAdapter(List<MessageEntity> data) {
        super(data);
        addItemType(MessageEntity.UNREAD, R.layout.recycleritem_system_message_unread);
        addItemType(MessageEntity.READ, R.layout.recycleritem_system_message_read);
    }

    @Override
    protected void convert(BaseViewHolder helper, MessageEntity item) {
        switch (helper.getItemViewType()) {
            //未读
            case MessageEntity.UNREAD:
            case MessageEntity.READ: {
                //标题
                helper.setText(R.id.tv_from, item.getTitle());
                //时间
                String dateStr = item.getSendTime();//ISO8601 时间字符串
                SimpleDateFormat formatIso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                try {
                    Date date = formatIso8601.parse(dateStr);
                    SimpleDateFormat formatNormal = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String normalDateStr = formatNormal.format(date);
                    helper.setText(R.id.tv_date_time, normalDateStr);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //内容
                helper.setText(R.id.tv_content, item.getMessage());
                break;
            }
        }
    }
}
