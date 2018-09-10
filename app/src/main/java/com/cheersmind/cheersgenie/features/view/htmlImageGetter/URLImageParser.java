package com.cheersmind.cheersgenie.features.view.htmlImageGetter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

/**
 * Html图片加载器（用于TextView显示html文本带图片）
 */
public class URLImageParser implements Html.ImageGetter {

    private Context context;

    //html文本将要显示在哪个TextView中
    private TextView tvContent;

//        private int actX;//实际的宽  放大缩小基于textview的宽度
//        private int actY;

    /**
     * html图片加载器构造
     * @param context 上下文
     * @param tvContent 将要显示在哪个TextView
     */
    public URLImageParser(Context context, TextView tvContent) {
        this.context = context;
        this.tvContent = tvContent;
        //获取全屏大小
//            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        //我的textview有左右留边  margin
//            actX = metrics.widthPixels - DensityUtil.dip2px(TextViewForHtmlImageActivity.this, 40);
//            actY = metrics.heightPixels;
    }

    @Override
    public Drawable getDrawable(String source) {
        final URLImageParser.URLDrawable urlDrawable = new URLImageParser.URLDrawable();
        Glide.with(context)
                .load(source)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        //目前TextView加左右padding的时候左边会留白边，未找到原因，所以用margin替代
                        //控件宽度 - 左右padding = 图片宽度
                        float width = tvContent.getWidth() - tvContent.getPaddingLeft() - tvContent.getPaddingRight();
                        float scale = width / resource.getWidth();
                        int afterWidth = (int) (resource.getWidth() * scale);
                        int afterHeight = (int) (resource.getHeight() * scale);

                        //进行等比例缩放设置
                        Matrix matrix = new Matrix();
                        //高的比例减去0.05是因为底部总是会多出一行空白行
//                        matrix.postScale(scale,  (scale - 0.05f));
                        matrix.postScale(scale,  scale);
                        //缩放
                        resource = Bitmap.createBitmap(resource, 0, 0, resource.getWidth(), resource.getHeight(), matrix, true);
                        //设置到urlDrawable中
                        urlDrawable.setBounds(0, 0, afterWidth, afterHeight);
                        urlDrawable.setBitmap(resource);

                        tvContent.invalidate();
                        tvContent.setText(tvContent.getText());// 解决图文重叠
                    }
                });
        return urlDrawable;
    }

    public class URLDrawable extends BitmapDrawable {
        protected Bitmap bitmap;

        @Override
        public void draw(Canvas canvas) {
            if (bitmap != null) {
                //居中处理
                int i = canvas.getWidth() - bitmap.getWidth();
                canvas.drawBitmap(bitmap, i/2, 0, getPaint());
            }
        }

        void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }
    }
}
