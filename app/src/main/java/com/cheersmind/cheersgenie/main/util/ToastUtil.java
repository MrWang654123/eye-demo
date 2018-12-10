package com.cheersmind.cheersgenie.main.util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by gwb on 2018/5/24 0024.
 */

public class ToastUtil {

    private static boolean isShow = true;//默认显示
    private static Toast mToast = null;//全局唯一的Toast

    /*private控制不应该被实例化*/
    private ToastUtil() {
        throw new UnsupportedOperationException("不能被实例化");
    }

    /**
     * 全局控制是否显示Toast
     * @param isShowToast
     */
    public static void controlShow(boolean isShowToast){
        isShow = isShowToast;
    }

    /**
     * 取消Toast显示
     */
    public static void cancelToast() {
//        if(isShow && mToast != null){
//            mToast.cancel();
//            mToast = null;
//        }
        try {
            if (mToast != null) {
                mToast.cancel();
                mToast = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            mToast = null;
        }
    }

    /**
     * 短时间显示Toast
     *
     * @param application
     * @param message
     */
    @SuppressLint("ShowToast")
    public static void showShort(Application application, CharSequence message) {
        if (isShow){
            if (mToast == null) {
                if (application != null) {
                    mToast = Toast.makeText(application.getApplicationContext(), message, Toast.LENGTH_SHORT);
                }
            } else {
                mToast.setText(message);
            }

            if (mToast != null) {
                mToast.show();
            }
        }
    }

    /**
     * 短时间显示Toast
     *
     * @param application
     * @param resId 资源ID:getResources().getString(R.string.xxxxxx);
     */
    @SuppressLint("ShowToast")
    public static void showShort(Application application, int resId) {
        if (isShow){
            if (mToast == null) {
                if (application != null) {
                    mToast = Toast.makeText(application.getApplicationContext(), resId, Toast.LENGTH_SHORT);
                }
            } else {
                mToast.setText(resId);
            }
            if (mToast != null) {
                mToast.show();
            }
        }
    }

    /**
     * 长时间显示Toast
     *
     * @param application
     * @param message
     */
    public static void showLong(Application application, CharSequence message) {
        if (isShow){
            if (mToast == null) {
                if (application != null) {
                    mToast = Toast.makeText(application.getApplicationContext(), message, Toast.LENGTH_LONG);
                }
            } else {
                mToast.setText(message);
            }
            if (mToast != null) {
                mToast.show();
            }
        }
    }

    /**
     * 长时间显示Toast
     *
     * @param application
     * @param resId 资源ID:getResources().getString(R.string.xxxxxx);
     */
    public static void showLong(Application application, int resId) {
        if (isShow){
            if (mToast == null) {
                if (application != null) {
                    mToast = Toast.makeText(application.getApplicationContext(), resId, Toast.LENGTH_LONG);
                }
            } else {
                mToast.setText(resId);
            }
            if (mToast != null) {
                mToast.show();
            }
        }
    }

    /**
     * 自定义显示Toast时间
     *
     * @param application
     * @param message
     * @param duration 单位:毫秒
     */
    public static void show(Application application, CharSequence message, int duration) {
        if (isShow){
            if (mToast == null) {
                if (application != null) {
                    mToast = Toast.makeText(application.getApplicationContext(), message, duration);
                }
            } else {
                mToast.setText(message);
            }
            if (mToast != null) {
                mToast.show();
            }
        }
    }

    /**
     * 自定义显示Toast时间
     *
     * @param application
     * @param resId 资源ID:getResources().getString(R.string.xxxxxx);
     * @param duration 单位:毫秒
     */
    public static void show(Application application, int resId, int duration) {
        if (isShow){
            if (mToast == null) {
                if (application != null) {
                    mToast = Toast.makeText(application.getApplicationContext(), resId, duration);
                }
            } else {
                mToast.setText(resId);
            }
            if (mToast != null) {
                mToast.show();
            }
        }
    }

    /**
     * 自定义Toast的View
     * @param application
     * @param message
     * @param duration 单位:毫秒
     * @param view 显示自己的View
     */
    public static void customToastView(Application application, CharSequence message, int duration, View view) {
        if (isShow){
            if (mToast == null) {
                if (application != null) {
                    mToast = Toast.makeText(application.getApplicationContext(), message, duration);
                }
            } else {
                mToast.setText(message);
            }
            if(view != null){
                mToast.setView(view);
            }
            if (mToast != null) {
                mToast.show();
            }
        }
    }

    /**
     * 自定义Toast的位置
     * @param application
     * @param message
     * @param duration 单位:毫秒
     * @param gravity
     * @param xOffset
     * @param yOffset
     */
    public static void customToastGravity(Application application, CharSequence message, int duration, int gravity, int xOffset, int yOffset) {
        if (isShow){
            if (mToast == null) {
                if (application != null) {
                    mToast = Toast.makeText(application.getApplicationContext(), message, duration);
                }
            } else {
                mToast.setText(message);
            }
            mToast.setGravity(gravity, xOffset, yOffset);
            if (mToast != null) {
                mToast.show();
            }
        }
    }

    /**
     * 自定义带图片和文字的Toast，最终的效果就是上面是图片，下面是文字
     * @param context
     * @param message
     * @param iconResId 图片的资源id,如:R.drawable.icon
     * @param duration
     * @param gravity
     * @param xOffset
     * @param yOffset
     */
//    public static void showToastWithImageAndText(Context context, CharSequence message, int iconResId, int duration, int gravity, int xOffset, int yOffset) {
//        if (isShow){
//            if (mToast == null) {
//                mToast = Toast.makeText(context, message, duration);
//            } else {
//                mToast.setText(message);
//            }
//            mToast.setGravity(gravity, xOffset, yOffset);
//            LinearLayout toastView = (LinearLayout) mToast.getView();
//            ImageView imageView = new ImageView(context);
//            imageView.setImageResource(iconResId);
//            toastView.addView(imageView, 0);
//            mToast.show();
//        }
//    }

    /**
     * 自定义Toast,针对类型CharSequence
     * @param context
     * @param message
     * @param duration
     * @param view
     * @param isGravity true,表示后面的三个布局参数生效,false,表示不生效
     * @param gravity
     * @param xOffset
     * @param yOffset
     * @param isMargin true,表示后面的两个参数生效，false,表示不生效
     * @param horizontalMargin
     * @param verticalMargin
     */
//    public static void customToastAll(Context context, CharSequence message, int duration, View view, boolean isGravity, int gravity, int xOffset, int yOffset, boolean isMargin, float horizontalMargin, float verticalMargin) {
//        if (isShow){
//            if (mToast == null) {
//                mToast = Toast.makeText(context, message, duration);
//            } else {
//                mToast.setText(message);
//            }
//            if(view != null){
//                mToast.setView(view);
//            }
//            if(isMargin){
//                mToast.setMargin(horizontalMargin, verticalMargin);
//            }
//            if(isGravity){
//                mToast.setGravity(gravity, xOffset, yOffset);
//            }
//            mToast.show();
//        }
//    }

    /**
     * 自定义Toast,针对类型resId
     * @param context
     * @param resId
     * @param duration
     * @param view :应该是一个布局，布局中包含了自己设置好的内容
     * @param isGravity true,表示后面的三个布局参数生效,false,表示不生效
     * @param gravity
     * @param xOffset
     * @param yOffset
     * @param isMargin true,表示后面的两个参数生效，false,表示不生效
     * @param horizontalMargin
     * @param verticalMargin
     */
//    public static void customToastAll(Context context, int resId, int duration, View view, boolean isGravity, int gravity, int xOffset, int yOffset, boolean isMargin, float horizontalMargin, float verticalMargin) {
//        if (isShow){
//            if (mToast == null) {
//                mToast = Toast.makeText(context, resId, duration);
//            } else {
//                mToast.setText(resId);
//            }
//            if(view != null){
//                mToast.setView(view);
//            }
//            if(isMargin){
//                mToast.setMargin(horizontalMargin, verticalMargin);
//            }
//            if(isGravity){
//                mToast.setGravity(gravity, xOffset, yOffset);
//            }
//            mToast.show();
//        }
//    }
}
