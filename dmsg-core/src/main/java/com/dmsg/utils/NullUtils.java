package com.dmsg.utils;

/**
 * Created by jlcao on 2016/7/18.
 */
public class NullUtils {
    public static boolean isEmpty(Object port) {
        if (port != null && port.toString().length() > 0) {
            return false;
        }

        return true;
    }
}
