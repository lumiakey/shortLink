package com.what2e.util;


/**
 * 正则验证工具
 */
public class RegUtil {
    public static String CUSTOMREG = "[a-zA-Z0-9]{1,6}";
    public static String DEFUL = "(F|D|W)[a-zA-Z0-9]{6,}";
    public static String LINKPREFIX = "(http://)|(https://)";
    public static boolean isCustom(String customLink) {
        return customLink.matches(CUSTOMREG);
    }

    public static boolean isDeful(String defulLink) {
        return defulLink.matches(DEFUL);
    }

    public static String replace(String longLink) {
        return longLink.replaceFirst(LINKPREFIX,"");
    }
}
