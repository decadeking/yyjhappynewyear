package com.campus.lostfound.util;

import cn.hutool.core.util.IdUtil;

public class TokenUtil {

    public static String generateToken() {
        return IdUtil.fastSimpleUUID();
    }
}
