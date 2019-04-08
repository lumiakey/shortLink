package com.what2e.service.impl;

import com.what2e.dao.LinkMappingDao;
import com.what2e.entity.LinkMapping;
import com.what2e.service.JumpService;
import com.what2e.service.RedisService;
import com.what2e.util.LinkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class JumpServiceImpl implements JumpService {

    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(CreatShortLinkServiceImpl.class);

    @Autowired
    LinkMappingDao linkMappingDao;

    @Autowired
    RedisService redisService;

    final static String FOREVER = "F"; //永久有效
    final static String TEMPORARY = "T"; //临时有效

    @Override
    public void forward(HttpServletRequest request, HttpServletResponse response, String shortLink) {
        logger.info("In to forward");

        StringBuilder prefix = LinkUtil.getPrefix(request);
        String table = "";
        StringBuilder tableName = new StringBuilder();
        StringBuilder shortLinkSelect = new StringBuilder();
        shortLinkSelect.append("\"");
        shortLinkSelect.append(shortLink);
        shortLinkSelect.append("\"");
        if (shortLink.length() < 7) {
            logger.info("JumpService Into Custom");
            tableName.append("Custom_");
            table = shortLink.substring(0, 1);
        } else {
            String type = shortLink.substring(0, 1);
            logger.info("Jump Service Table Type : " + type);
            table = shortLink.substring(1, 2);
            switch (type) {
                case FOREVER:
                    tableName.append("Persistent_");
                    break;
                case TEMPORARY:
                    tableName.append("Temporary_");
                    break;
                default:
                    try {
                        response.sendRedirect(prefix.toString());
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
        if ((redisService.findLongLink(shortLink)) != null) {
            tableName.append(table);
            logger.info("getLongLink From Redis");
            try {
                String longLink = (LinkUtil.getHttp(request).append(redisService.findLongLink(shortLink))).toString();
                response.sendRedirect(longLink);
                linkMappingDao.upVisits(tableName.toString(), shortLinkSelect.toString());
                return;
            }catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            tableName.append(table);
            try {
                LinkMapping linkMapping = linkMappingDao.select(tableName.toString(), shortLinkSelect.toString());
                if (linkMapping == null) {

                    try {
                        response.sendRedirect(prefix.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        logger.info("Jump to " + linkMapping.getLongLink());
                        linkMappingDao.upVisits(tableName.toString(), shortLinkSelect.toString());
                        String longLink = (LinkUtil.getHttp(request).append(linkMapping.getLongLink())).toString();
                        response.sendRedirect(longLink);
                        redisService.redisWrite(shortLink, linkMapping.getLongLink()); //做了一个redis缓存优化
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }catch (Exception e){
                return;
            }


        }
    }

    @Override
    public int getVisitsNum(HttpServletRequest request, HttpServletResponse response, String shortLink) {
        logger.info("In to getVisitsNum");

        logger.info(shortLink);
        String table = "";
        StringBuilder tableName = new StringBuilder();
        StringBuilder shortLinkSelect = new StringBuilder();
        shortLinkSelect.append("\"");
        shortLinkSelect.append(shortLink);
        shortLinkSelect.append("\"");
        if (shortLink.length() < 7) {
            tableName.append("Custom_");
            table = shortLink.substring(0, 1);
        } else {
            String type = shortLink.substring(0, 1);
            logger.info("Jump Service Table Type : " + type);
            table = shortLink.substring(1, 2);
            switch (type) {
                case FOREVER:
                    tableName.append("Persistent_");
                    break;
                case TEMPORARY:
                    tableName.append("Temporary_");
                    break;
                default:
                    return -1;
            }
        }
        tableName.append(table);
        try {
            int visitsNum = linkMappingDao.selectVisits(tableName.toString(), shortLinkSelect.toString());
            return visitsNum;
        }catch (Exception e) {
            return -1;
        }
    }
}
