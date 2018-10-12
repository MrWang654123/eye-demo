package com.cheersmind.cheersgenie.features.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据格式验证工具
 */
public class DataCheckUtil {

    /**
     * 判断字符串是否符合手机号码格式
     * 移动号段: 134,135,136,137,138,139,147,150,151,152,157,158,159,170,178,182,183,184,187,188
     * 联通号段: 130,131,132,145,155,156,170,171,175,176,185,186
     * 电信号段: 133,149,153,170,173,177,180,181,189
     * @param mobileNums
     * @return 待检测的字符串
     */
//    public static boolean isMobileNO(String mobileNums) {
//        /**
//         * 判断字符串是否符合手机号码格式
//         * 移动号段: 134,135,136,137,138,139,147,150,151,152,157,158,159,170,178,182,183,184,187,188
//         * 联通号段: 130,131,132,145,155,156,170,171,175,176,185,186
//         * 电信号段: 133,149,153,170,173,177,180,181,189
//         * @param str
//         * @return 待检测的字符串
//         */
//        String telRegex = "^((13[0-9])|(14[5,7,9])|(15[^4])|(18[0-9])|(17[0,1,3,5,6,7,8]))\\d{8}$";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
//        if (TextUtils.isEmpty(mobileNums))
//            return false;
//        else
//            return mobileNums.matches(telRegex);
//    }

    /**
     * 是否是手机号
     * @param mobileNums
     * @return
     */
    public static boolean isMobileNO(String mobileNums) {
        //11位数字，首位是1
        String telRegex = "^1\\d{10}$";
        if (TextUtils.isEmpty(mobileNums))
            return false;
        else
            return mobileNums.matches(telRegex);
    }

    /**
     * 数字和字母
     * @param str
     * @return
     */
    public static boolean isNumberOrLetter(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        } else {
            String reg = "[a-zA-Z0-9]*";
            return str.matches(reg);
        }
    }

    /**
     * 字母
     * @param str
     * @return
     */
    public static boolean isLetter(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        } else {
            String reg = "[a-zA-Z]*";
            return str.matches(reg);
        }
    }

    /**
     * 包含特殊字符
     * @param str
     * @return
     */
    public static boolean containSpecialCharacter(String str){
        String limitEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";

        Pattern pattern = Pattern.compile(limitEx);

        Matcher m = pattern.matcher(str);

        return m.find();
    }

    /**
     * 汉字
     * @param str
     * @return
     */
    public static boolean isCnCharacter(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        } else {
            String reg = "[\\u4e00-\\u9fa5]*";
            return str.matches(reg);
        }
    }

    /**
     * 昵称（3-8位，数字、英文、中文）
     * @param str
     * @return
     */
    public static boolean isNickname(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        } else {
            String reg = "([a-zA-Z0-9]|[\\u4e00-\\u9fa5]){3,8}";
            return str.matches(reg);
        }
    }

}

