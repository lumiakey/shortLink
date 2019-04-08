package com.what2e.controller;

import com.what2e.service.JumpService;
import com.what2e.service.impl.CreatShortLinkServiceImpl;
import com.what2e.util.RegUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
@RequestMapping(value = "/")
public class JumpController {

    @Autowired
    JumpService jumpService;
    private static final Logger logger = LoggerFactory.getLogger(CreatShortLinkServiceImpl.class);


    @RequestMapping(value = "{message}")
    @ResponseBody
    public void jump(HttpServletRequest request, HttpServletResponse response, @PathVariable("message") String shortLink) {
        logger.info("Jump Controller Info The ShortLink:" + shortLink);
        if(RegUtil.isCustom(shortLink) || RegUtil.isDeful(shortLink)) {
            jumpService.forward(request, response, shortLink);
        }else {
            return;
        }
    }
}
