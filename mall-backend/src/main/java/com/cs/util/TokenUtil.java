package com.cs.util;

public class TokenUtil {
    public static String getNewtoken(String token, Long adminUserId) {
        String newtoken = token + adminUserId + NumberUtil.genRandomNum(6);
        return SystemUtil.genToken(newtoken);
    }
}
