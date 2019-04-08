package com.what2e.controller;

import com.what2e.entity.LinkInfo;
import com.what2e.service.CreatShortLinkService;
import com.what2e.util.LinkUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.awt.image.ImageWatched;

import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping(value = "/conversion")
public class ConversionController {

    @Autowired
    private CreatShortLinkService csls;
    LinkInfo linkInfo = new LinkInfo();
    @RequestMapping(value = "/getShortLink")
    @ResponseBody
    public String getShortLink(HttpServletRequest request, String URl, String custom, int validity, int length) {
        if(!custom.equals("NULLCUSTOM") && length !=8) {
            return "Custom short links and custom lengths are mutually exclusive options";
        }
        if(length > 60) {
            return "Custom long links must be less than 60 bits long";
        }
        StringBuilder prefix = LinkUtil.getPrefix(request);
        prefix.append(request.getContextPath());
        prefix.append("/");
        linkInfo.setLongLink(URl);
        linkInfo.setCustom(custom);
        linkInfo.setValidity(validity);
        linkInfo.setLength(length);
        return prefix.append(csls.creatShortLink(linkInfo)).toString();
    }


}
