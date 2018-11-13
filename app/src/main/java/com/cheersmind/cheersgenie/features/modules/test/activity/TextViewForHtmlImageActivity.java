package com.cheersmind.cheersgenie.features.modules.test.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.DisplayMetrics;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.GlideApp;
import com.cheersmind.cheersgenie.features.utils.HtmlUtil;
import com.cheersmind.cheersgenie.features.view.htmlImageGetter.URLImageParser;
import com.cheersmind.cheersgenie.main.util.DensityUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * TextView显示html包含图片
 */
public class TextViewForHtmlImageActivity extends Activity {

    @BindView(R.id.tv_content)
    TextView tvContent;

    //测试的文章内容
    String testArticleContent = "<div class=\"article\" id=\"article\">\n" +
            "\t\t\t\t<p>　　原标题：外交部今天再谈中非“全家福”，说了一句意味深长的话</p>\n" +
            "<div class=\"img_wrapper\"><img style=\"max-width: 500px;\" src=\"http://n.sinaimg.cn/translate/784/w500h284/20180821/OHhj-hhzsnea3625408.jpg\" alt=\"\"></div>\n" +
            "<p>　　[环球网综合报道]2018年8月21日外交部发言人陆慷主持例行记者会，有记者问到，问：第一，台湾方面认为中国与萨尔瓦多建交与大陆“金元外交”有关。外交部发言人对此有何回应？第二，今年年初，王毅国务委员兼外长表示希望在中非合作论坛北京峰会期间拍一张中非“全家福”。这次斯威士兰是否将与会？</p>\n" +
//            "<p>　　陆慷回应，关于第一个问题，相信你已经关注到了今天上午王毅国务委员兼外长与卡斯塔内达外长共见记者的详细情况。按说对台湾岛内某些人的表态，应由中央政府主管部门作回应，不是外交部的职责。但就这一问题而言，我可以强调一下，中国和萨尔瓦多建立外交关系是政治决断，绝不是台湾方面某些人想象的所谓“交易筹码”。萨尔瓦多政府决定同中国建交，是出于对一个中国原则的认同，是一项政治决断，没有任何经济前提。中方高度赞赏萨方的立场。</p>\n" +
//            "<p>　　我也想提醒台湾方面某些人，世界各国同中华人民共和国建立和发展正常友好的国家关系是大势所趋、民心所向，希望岛内某些人能够看清世界大势，不要把任何问题都理解为钱的问题。</p>\n" +
            "<p>　　至于你提到现在还有一个非洲国家仍然没有放弃同台湾的所谓“外交关系”，我们已经多次表明，中国政府有意愿在和平共处五项原则和一个中国原则基础上，同世界上所有国家发展友好合作关系。我们相信这样的关系符合彼此利益，希望有关国家能够认清世界大势。</p>\n" +
            "\t\t\t\t\n" +
            "\t\t\t\t<div class=\"wap_special\" data-sudaclick=\"content_relativetopics_p\">\n" +
            "        <div class=\"tlt\">点击进入专题：</div>\n" +
            "\t<a href=\"http://news.sina.cn/zt_d/zfhzlt\" target=\"_blank\">习近平将主持2018年中非合作论坛北京峰会</a></div>\n" +
            "  \n" +
            "\t\t\t\t\n" +
            "\t\t\t\t\n" +
            "<p class=\"show_author\">责任编辑：张建利 </p>\n" +
            "<h4>一个无序列表：</h4>\n" +
"<ul>\n" +
        "<li>咖啡</li>\n" +
        "<li>茶</li>\n" +
        "<li>牛奶</li>\n" +
        "</ul>\n" +
            "\t\t\t\t\n" +
            "\t\t\t\t\n" +
            "\t\t\t</div>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view_for_html_image);
        ButterKnife.bind(this);

        String htmlText = testArticleContent;
//        htmlText = HtmlUtil.getNewContentByHandleImage(htmlText);

//        tvContent.setText(Html.fromHtml(htmlText, new URLImageParser(TextViewForHtmlImageActivity.this, tvContent), null));
        tvContent.setText(Html.fromHtml(htmlText, new URLImageParser(TextViewForHtmlImageActivity.this, tvContent), null));
    }

    public class URLImageParser implements Html.ImageGetter {

        private Context context;
//        private TextView tvContent;
//        private int actX;//实际的宽  放大缩小基于textview的宽度
//        private int actY;

        public URLImageParser(Context context, TextView tvContent) {
            this.context = context;
//            this.tvContent = tvContent;
            //获取全屏大小
//            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            //我的textview有左右留边  margin
//            actX = metrics.widthPixels - DensityUtil.dip2px(TextViewForHtmlImageActivity.this, 40);
//            actY = metrics.heightPixels;
        }

        Request request;

        @Override
        public Drawable getDrawable(String source) {
            final URLDrawable urlDrawable = new URLDrawable();
            RequestOptions options = new RequestOptions();
            options.diskCacheStrategy(DiskCacheStrategy.DATA);
            options.placeholder(R.drawable.default_image_round);
            GlideApp.with(context)
                    .load(source)
                    .apply(options)
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            //目前TextView加左右padding的时候左边会留白边，未找到原因，所以用margin替代
                            //控件宽度 - 左右padding = 图片宽度
                            float width = tvContent.getWidth() - tvContent.getPaddingLeft() - tvContent.getPaddingRight();
                            Bitmap resourceBitmap = ((BitmapDrawable)resource).getBitmap();
                            int resourceWidth = resourceBitmap.getWidth();
                            int resourceHeight = resourceBitmap.getHeight();
                            float scale = width / resourceWidth;

                            int afterWidth = (int) (resourceWidth * scale);
                            int afterHeight = (int) (resourceHeight * scale);

                            //进行等比例缩放设置
                            Matrix matrix = new Matrix();
                            //高的比例减去0.05是因为底部总是会多出一行空白行
//                        matrix.postScale(scale,  (scale - 0.05f));
                            matrix.postScale(scale,  scale);

                            //缩放
                            resourceBitmap = Bitmap.createBitmap(resourceBitmap, 0, 0, resourceWidth, resourceHeight, matrix, true);
                            //设置到urlDrawable中
                            urlDrawable.setBounds(0, 0, afterWidth, afterHeight);
                            urlDrawable.setBitmap(resourceBitmap);

                            tvContent.invalidate();
                            tvContent.setText(tvContent.getText());// 解决图文重叠
                        }
                    });
            /*Glide.with(context)
                    .load(source)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            //控件宽度 - 左右padding = 图片宽度
                            float paddingLeft = tvContent.getPaddingLeft();
                            float paddingRight = tvContent.getPaddingRight();
                            float oldWidth = tvContent.getWidth();
                            float width = oldWidth - paddingLeft - paddingRight;
                            float scaleWidth = width / resource.getWidth();
                            float scaleHeight = scaleWidth;
                            //高的比例减去0.05是因为底部总是会多出一行空白行
//                            float scaleHeight = scaleWidth - 0.05f;
                            int afterWidth = (int) (resource.getWidth() * scaleWidth);
                            int afterHeight = (int) (resource.getHeight() * scaleHeight);

                            //进行等比例缩放设置
                            Matrix matrix = new Matrix();
                            matrix.postScale(scaleWidth,  scaleHeight);
                            //缩放
                            resource = Bitmap.createBitmap(resource, 0, 0, resource.getWidth(), resource.getHeight(), matrix, true);

                            //设置到urlDrawable中
                            urlDrawable.setBounds(0, 0, afterWidth, afterHeight);
                            urlDrawable.setBitmap(resource);

                            tvContent.invalidate();
                            tvContent.setText(tvContent.getText());// 解决图文重叠
                        }
                    });*/
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
}
