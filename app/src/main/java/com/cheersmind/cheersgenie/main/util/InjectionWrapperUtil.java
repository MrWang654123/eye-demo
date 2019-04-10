package com.cheersmind.cheersgenie.main.util;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;
import com.cheersmind.cheersgenie.main.ioc.InjectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 注入包装类
 * @author zhouw
 *
 */
public class InjectionWrapperUtil extends InjectionUtils {

    /**
     * 注入 map转并生成bean 对应list
     * @param list
     * @param classOfT
     * @return
     */
    public static <T> List<T> injectMaps(List<Map<String,Object>> list, Class<T> classOfT){

        if(list!=null){
            List<T> lt = new ArrayList<T>();
            for (int i = 0; i < list.size(); i++) {
                lt.add(injectMap(list.get(i),classOfT));
            }
            return lt;
        }

        return null;
    }

    /**
     * 注入 map转并生成bean
     * @param map
     * @param classOfT
     * @return
     */
    public static <T> T injectMap(Map<String,Object> map, Class<T> classOfT){
        if(map==null){
            return null;
        }
        Class<?> cla = classOfT;
        Field[] fields = cla.getDeclaredFields();

        T tobj = null;
        try {
            tobj = classOfT.newInstance();
        } catch (InstantiationException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        }

        for (Field field : fields) {
            try {
                if(field.isAnnotationPresent(InjectMap.class)){
                    InjectMap ano = field.getAnnotation(InjectMap.class);
                    field.setAccessible(true);
                    if(!map.containsKey(ano.name())){
                        continue;
                    }
                    if(field.getType()==Integer.class || "int".equals(field.getType().getName())){
                        Object value = map.get(ano.name());
                        try {
                            if(value instanceof Double || value instanceof Float){
                                field.set(tobj, value==null?null:((int)Double.parseDouble(value.toString())));
                            }else {
                                field.set(tobj, value==null?null:Integer.parseInt(value.toString()));
                            }
                        }catch (Exception e) {
                            field.set(tobj, 0);
                        }
                    }else if(field.getType()==Long.class || "long".equals(field.getType().getName())){
                        Object value = map.get(ano.name());
                        try {
                            if(value instanceof Double || value instanceof Float){
                                field.set(tobj, value==null?null:((long)Double.parseDouble(value.toString())));
                            }else {
                                field.set(tobj, value==null?null:Long.parseLong(value.toString()));
                            }
                        }catch (Exception e) {
                            field.set(tobj, 0);
                        }
                    }else if(field.getType()==String.class){
                        Object value = map.get(ano.name());
                        field.set(tobj, value==null?null:String.valueOf(value));
                    }else if(field.getType()==Boolean.class || "boolean".equals(field.getType().getName())){
                        Object value = map.get(ano.name());
                        if(value==null){
                            field.set(tobj, false);
                        }else {
                            try {
                                if(value instanceof Double || value instanceof Float){
                                    field.set(tobj, (int)Double.parseDouble(value.toString())==1);
                                }else if(value.toString().equals("true")){
                                    field.set(tobj, true);
                                }else {
                                    field.set(tobj, Integer.parseInt(value.toString())==1);
                                }
                            }catch (Exception e) {
                                field.set(tobj, false);
                            }
                        }
                    }else {
                        Object value = map.get(ano.name());
                        if(value!=null && (value instanceof List && "java.util.List<java.lang.Integer>".equals(field.getGenericType().toString()))){
                            List list = (List) value;
                            List vl = new ArrayList();

                            for (int i = 0; i < list.size(); i++) {
                                if(list.get(i)!=null){
                                    Object tmp = list.get(i);
                                    try {
                                        if(tmp instanceof Double || tmp instanceof Float){
                                            vl.add(((int)Double.parseDouble(tmp.toString())));
                                        }else {
                                            vl.add(Integer.parseInt(tmp.toString()));
                                        }
                                    }catch (Exception e) {
                                        vl.add(null);
                                    }
                                }else {
                                    vl.add(null);
                                }
                            }

                            field.set(tobj,vl);
                        }else if(value!=null && (value instanceof List &&  !"java.util.List<java.lang.String>".equals(field.getGenericType().toString()))){
                            Type type = field.getGenericType();
                            ParameterizedType pType = (ParameterizedType)type;
                            Class tmp = (Class) pType.getActualTypeArguments()[0];
                            field.set(tobj, injectMaps((List)value,tmp));

                        } else if (value != null && value instanceof Map) {
                            //属性是Map类型，直接赋值
                            if (field.getType() == Map.class) {
                                field.set(tobj,value);

                            } else {
                                field.set(tobj, injectMap((Map) value, field.getType()));
                            }

                        }else {
                            field.set(tobj,value);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return tobj;
    }





    /**
     * 向target 元注释属性  调用 setText注入  src 属性值
     *
     * @param target
     * @param src
     */
    public static void injectViewHolderText(Object target,Object src) {
        Class<?> cla = target.getClass();
        Field[] fields = cla.getDeclaredFields();

        for (Field field : fields) {

            try {
                Field srcField = src.getClass().getDeclaredField(field.getName());
                srcField.setAccessible(true);
                Object value = srcField.get(src);
                if(value!=null){
                    field.setAccessible(true);
                    try {
                        Object view = field.get(target);
                        Method method = view.getClass().getMethod("setText", CharSequence.class);
                        method.invoke(view, String.valueOf(value));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }

        }
    }
}
