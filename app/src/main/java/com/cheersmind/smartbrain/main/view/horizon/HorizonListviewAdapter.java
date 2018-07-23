package com.cheersmind.smartbrain.main.view.horizon;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cheersmind.smartbrain.R;
import com.cheersmind.smartbrain.main.entity.DimensionInfoEntity;
import com.cheersmind.smartbrain.main.entity.TopicInfoEntity;
import com.cheersmind.smartbrain.main.util.imagetool.ImageCacheTool;
import com.cheersmind.smartbrain.main.view.VerticalProgressBar;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.cheersmind.smartbrain.R.id.ver_pregress;

public class HorizonListviewAdapter extends BaseAdapter {

	private Context context;
	List<TopicInfoEntity> topicList = new ArrayList<>();
	private List<DimensionInfoEntity> entities = new ArrayList<>();
	private int index;

	public HorizonListviewAdapter(Context context, List<DimensionInfoEntity> entities,List<TopicInfoEntity> topicList,int index) {
		this.context = context;
		this.entities = entities;
		this.topicList = topicList;
		this.index = index;
	}

	@Override
	public int getCount() {
		return entities.size();
	}

	@Override
	public Object getItem(int position) {
		return entities.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder ;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context,R.layout.item_my_evaluate_topic, null);
			holder.initView(convertView);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}

		DimensionInfoEntity entity = entities.get(position);

		if(index%2 == 0){
			holder.verPregress.setColorPaint(Color.rgb(245, 164, 0));
		} else {
			holder.verPregress.setColorPaint(Color.rgb(18, 178, 244));
		}

		holder.tvItemTitle.setText(entity.getDimensionName());
		if (entity.getChildDimension() != null) {

			holder.ivIcon.setVisibility(View.VISIBLE);
			String iconUrl = entity.getIcon();
			ImageCacheTool imageCacheTool = ImageCacheTool.getInstance();
			if (!TextUtils.isEmpty(iconUrl)) {
				try {
					imageCacheTool.asyncLoadImage(new URL(iconUrl), holder.ivIcon);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}

			holder.llLock.setVisibility(View.GONE);
			holder.tvProgress.setVisibility(View.VISIBLE);
			holder.verPregress.setVisibility(View.VISIBLE);
			if (entity.getFactorCount() == 0) {
				holder.tvProgress.setText("0%");
				holder.verPregress.setProgress(0);
			} else {
				float p = entity.getChildDimension().getCompleteFactorCount() * 1.00f / entity.getFactorCount();
				int present = (int) (p * 100);
				if (present >= 100) {
					present = 100;
				}
				holder.tvProgress.setText(String.valueOf(present) + "%");
				holder.verPregress.setProgress(present);
			}
		} else {
			holder.ivIcon.setVisibility(View.GONE);
			holder.llLock.setVisibility(View.VISIBLE);
			holder.tvFlower.setText(String.valueOf(entity.getUnlockFlowers()) + "朵鲜花");
			holder.tvProgress.setVisibility(View.GONE);
			holder.verPregress.setVisibility(View.GONE);
		}

		final DimensionInfoEntity dimensionInfoEntity = entities.get(position);
//		holder.rtItem.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(context,DimensionDetailsActivity.class);
//				intent.putExtra("dimension_count", entities.size());
//				intent.putExtra("cur_index", position);
//				intent.putExtra("topic_name", topicList.get(position).getTopicName());
//				Bundle bundle = new Bundle();
//				bundle.putSerializable("dimension_info", dimensionInfoEntity);
//				intent.putExtras(bundle);
//				context.startActivity(intent);
//			}
//		});
		
		return convertView;
	}

	class ViewHolder {
		RelativeLayout rtItem;
		VerticalProgressBar verPregress;
		LinearLayout llLock;
		TextView tvFlower;
		TextView tvProgress;
		ImageView ivIcon;
		TextView tvItemTitle;

		void initView(View view){
			rtItem = (RelativeLayout)view.findViewById(R.id.rt_item);
			verPregress = (VerticalProgressBar)view.findViewById(ver_pregress);
			llLock = (LinearLayout)view.findViewById(R.id.ll_lock);
			tvFlower = (TextView)view.findViewById(R.id.tv_flower);
			tvProgress = (TextView)view.findViewById(R.id.tv_progress);
			ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
			tvItemTitle = (TextView)view.findViewById(R.id.tv_item_title);
		}
	}
	
}
