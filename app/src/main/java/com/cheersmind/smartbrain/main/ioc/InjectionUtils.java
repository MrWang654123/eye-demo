package com.cheersmind.smartbrain.main.ioc;

import android.app.Activity;
import android.view.View;

import java.lang.reflect.Field;

/**
 * 注入帮助类
 * <p>Created by john wu on 2014-9-26</p>
 */
public class InjectionUtils {



    public InjectionUtils() {

    }


    /**
     * 注入
     *
     * @param activity
     */
    /*
    public static void injectAllViews(Activity activity)
    {
        injectViews(activity, activity);
    }*/


    /**
     * 注入视图
     *
     * @param activity 视图所在的activity
     * @param target 视图绑定的目标
     */
    private static void injectViews(Activity activity, Object target)
    {
        Class<?> cla=target.getClass();
        Field[] fields=cla.getDeclaredFields();

        for(Field field:fields)
        {
            if(field.isAnnotationPresent(InjectView.class))
            {
                InjectView ano=field.getAnnotation(InjectView.class);

                field.setAccessible(true);
                try {
                    field.set(target, activity.findViewById(ano.id()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void injectView(View v, Object target){
        Class<?> cla=target.getClass();
        Field[] fields=cla.getDeclaredFields();

        for(Field field:fields)
        {
            if(field.isAnnotationPresent(InjectView.class))
            {
                InjectView ano=field.getAnnotation(InjectView.class);

                field.setAccessible(true);
                try {
                    field.set(target, v.findViewById(ano.id()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 注入视图
     *
     * @param controller 视图所在的activity
     */
//    public static void injectView(TController controller)
//    {
//        injectViews(controller,controller.getView());
//    }


    /**
     * 注入视图到目标
     *
     * @param activity 视图所属的activity
     * @param target 目标
     */
    public static void injectView(Activity activity,Object target){
        injectViews(activity,target);
    }

}

