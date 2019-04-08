package com.what2e.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface JumpService {
    void forward(HttpServletRequest request, HttpServletResponse response, String shortLink);

    int getVisitsNum(HttpServletRequest request, HttpServletResponse response, String shortLink);
}
