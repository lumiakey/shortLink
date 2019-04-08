package com.what2e.controller;

import com.what2e.service.JumpService;
import com.what2e.service.impl.CreatShortLinkServiceImpl;
import com.what2e.util.LinkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = "/JumpNum")
public class JumpNumController {

    @Autowired
    JumpService jumpService;

    private static final Logger logger = LoggerFactory.getLogger(CreatShortLinkServiceImpl.class);

    @RequestMapping(value = "/getNum")
    @ResponseBody
    public int jumpNum(HttpServletRequest request, HttpServletResponse response, String shortLinkHttp) {
        logger.info("getVisits :" + shortLinkHttp);
        String shortLink = shortLinkHttp.replace(LinkUtil.getPrefix(request)+"/","");
        return jumpService.getVisitsNum(request, response, shortLink);
    }
}
