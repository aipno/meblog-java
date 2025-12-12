package cn.iswxl.meblog.common.utils;

/**
 * @description: 邮件生成验证码
 */
public class EmailRandomUtil {

    public static String randomNumBuilder(){

        StringBuilder result = new StringBuilder();
        for(int i=0;i<6;i++){
            result.append(Math.round(Math.random() * 9));
        }

        return result.toString();

    }
}

