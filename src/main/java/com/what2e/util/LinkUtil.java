package com.what2e.util;

import javax.servlet.http.HttpServletRequest;

public class LinkUtil {
    public  static StringBuilder getPrefix(HttpServletRequest request) {
        StringBuilder prefix = new StringBuilder();
        prefix.append("http://");
        prefix.append(request.getServerName());
        return prefix;
    }

    public static StringBuilder getHttp(HttpServletRequest request) {
        StringBuilder prefix = new StringBuilder();
        prefix.append("http://");
        return prefix;
    }

}
