package com.what2e.service.impl;


import com.what2e.dao.LinkMappingDao;
import com.what2e.entity.LinkInfo;
import com.what2e.entity.LinkMapping;
import com.what2e.service.CreatShortLinkService;
import com.what2e.util.RandomUtil;
import com.what2e.util.RegUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;

@Service
public class CreatShortLinkServiceImpl implements CreatShortLinkService {

    private static final Logger logger = LoggerFactory.getLogger(CreatShortLinkServiceImpl.class);

    @Autowired
    RedisServiceImpl redisWriteService;

    @Autowired
    LinkMappingDao linkMappingDao;


    final static int DAT = 1; //一天
    final static int WEEK = 7; //一周
    final static int FOREVER = 0; //永久有效
    final static String CUSTOM = "NULLCUSTOM"; //自定义字符串
    final static int LENGTH = 8; //默认短链接长度
    static ArrayBlockingQueue<String> fABQ = new ArrayBlockingQueue<>(20);  //存储永久有效短链接的队列
    static ArrayBlockingQueue<String> dABQ = new ArrayBlockingQueue<>(20);  //存储一天有效短链接的队列
    static ArrayBlockingQueue<String> wABQ = new ArrayBlockingQueue<>(20);  //存储七天有效短链接的队列


    static class fLineProducer implements Runnable {
        private ArrayBlockingQueue<String> fABQ;

        public fLineProducer(ArrayBlockingQueue<String> fABQ) {
            this.fABQ = fABQ;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    logger.info("fABQ === ");
                    fABQ.put(RandomUtil.getRandomString("F", LENGTH));
                }
            } catch (InterruptedException ex) {
                System.out.println("Producer INTERRUPTED");
            }

        }
    }

    static class wLineProducer implements Runnable {
        private ArrayBlockingQueue<String> wABQ;

        public wLineProducer(ArrayBlockingQueue<String> wABQ) {
            this.wABQ = wABQ;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    wABQ.put(RandomUtil.getRandomString("W", LENGTH));
                }
            } catch (InterruptedException ex) {
                System.out.println("Producer INTERRUPTED");
            }
        }
    }

    static class dLineProducer implements Runnable {
        private ArrayBlockingQueue<String> dABQ;

        public dLineProducer(ArrayBlockingQueue<String> dABQ) {
            this.dABQ = dABQ;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    dABQ.put(RandomUtil.getRandomString("D", LENGTH));
                }
            } catch (InterruptedException ex) {
                System.out.println("Producer INTERRUPTED");
            }
        }
    }


    static {
        new Thread(new fLineProducer(fABQ)).start();
        new Thread(new dLineProducer(dABQ)).start();
        new Thread(new wLineProducer(wABQ)).start();
    }

    @Override
    public String creatShortLink(LinkInfo linkInfo) {
        logger.info("In to creatShortLinkImpl");
        String shortLink = "";
        switch (linkInfo.getCustom()) {
            case CUSTOM:
                logger.info("In to creatShortLinkImpl NS——CASE");
                shortLink = defaultTake(linkInfo);
                break;
            default:
                logger.info("In to creatShortLinkImpl CUSTOM——CASE");
                shortLink = customTake(linkInfo);
        }
        return shortLink;
    }

    public String defaultTake(LinkInfo linkInfo) {
        logger.info("In to defaultTake");
        String shortLink = "";
        switch (linkInfo.getLength()) {
            case 8:                                 //判断长度是否是默认
                logger.info("In to defaultTake —— CASE  8");
                shortLink = getFromQueue(linkInfo);
                break;
            default:
                logger.info("In to defaultTake —— CASE  自定义长度");
                shortLink = getFromRandomUtil(linkInfo);
        }
        return shortLink;
    }

    public String getFromRandomUtil(LinkInfo linkInfo) {
        logger.info("In to getFromRandomUtil");
        String shortLink = "";
        switch (linkInfo.getValidity()) {
            case DAT:
                shortLink = RandomUtil.getRandomString("D", linkInfo.getLength());
                break;
            case WEEK:
                shortLink = RandomUtil.getRandomString("W", linkInfo.getLength());
                break;
            case FOREVER:
                shortLink = RandomUtil.getRandomString("F", linkInfo.getLength());
                break;
        }
        new Thread(new DBOper(shortLink, linkInfo)).start();
        return shortLink;
    }

    public String getFromQueue(LinkInfo linkInfo) {
        logger.info("In to getFromQueue");
        String shortLink = "";
        switch (linkInfo.getValidity()) {
            case DAT:
                logger.info("In to getFromQueue —— CASE DAY");
                try {
                    shortLink = dABQ.take();
                }catch (InterruptedException e) {
                    new Thread(new fLineProducer(dABQ)).start();
                    shortLink = getFromRandomUtil(linkInfo);
                }
                break;
            case WEEK:
                logger.info("In to getFromQueue —— CASE WEEK");
                try {
                    shortLink = wABQ.take();
                }catch (InterruptedException e) {
                    new Thread(new fLineProducer(wABQ)).start();
                    shortLink = getFromRandomUtil(linkInfo);
                }
                break;
            case FOREVER:
                logger.info("In to getFromQueue —— CASE FOREVER");
                try {
                    shortLink = fABQ.take();
                }catch (InterruptedException e) {
                    new Thread(new fLineProducer(fABQ)).start();
                    shortLink = getFromRandomUtil(linkInfo);
                }
                break;
        }
        new Thread(new DBOper(shortLink, linkInfo)).start();
        return shortLink;
    }

    /**
     * 自定义短链接时 无论时效是多久都是插入到Custom_*表
     * @param linkInfo
     * @return
     */
    public String customTake(LinkInfo linkInfo) { //自定义key key必须是小于7（不包含7）
        logger.info("In to customTake");
        StringBuilder shortLink = new StringBuilder();
        shortLink.append("\"");
        shortLink.append(linkInfo.getCustom());
        shortLink.append("\"");
        StringBuilder tableName = new StringBuilder();
        tableName.append("Custom_");
        tableName.append(shortLink.subSequence(1, 2));
        try{
            if(linkMappingDao.select(tableName.toString(), shortLink.toString()) == null) {
                new Thread(new DBOper(linkInfo.getCustom(), linkInfo)).start();
            }else return "Already Existing";
        }catch (Exception e){    //查询异常 没有这个库
            new Thread(new DBOper(linkInfo.getCustom(), linkInfo)).start();
        }
        return linkInfo.getCustom();
    }

    class DBOper implements Runnable {    //肩负判断有效期 和 是否是自定义 并且插入到对应表里的任务
        StringBuilder shortLinkInsrt = new StringBuilder();
        StringBuilder longLinkInsrt = new StringBuilder();
        StringBuilder createDateInsert = new StringBuilder();
        StringBuilder failureDateInsert = new StringBuilder();
        String shortLink = "";
        LinkInfo linkInfo = new LinkInfo();
        public DBOper(String shortLink, LinkInfo linkInfo) {
            this.shortLink = shortLink;    //如果是自定义短链那么shortLink依然为空
            this.linkInfo = linkInfo;
        }

        @Override
        public void run() {
            logger.info("In to DBOper");

            failureDateInsert.append("\"");
            shortLinkInsrt.append("\"");
            shortLinkInsrt.append(shortLink);
            shortLinkInsrt.append("\"");
            longLinkInsrt.append("\"");
            longLinkInsrt.append(RegUtil.replace(linkInfo.getLongLink()));
            longLinkInsrt.append("\"");
            int validity = linkInfo.getValidity();
            LinkMapping linkMapping = new LinkMapping();
            String tableName = "";
            Date createDate = new Date();
            createDateInsert.append("\"");
            createDateInsert.append(createDate.toString());
            createDateInsert.append("\"");

            linkMapping.setShortLink(shortLinkInsrt.toString());
            linkMapping.setCreateTime(createDateInsert.toString());
            linkMapping.setLongLink(longLinkInsrt.toString());
            Date failureTime;
            if (!(linkInfo.getCustom()).equals("NULLCUSTOM")) {
                logger.info("In to DBOper  ======= 自定义短链");
                shortLink = linkInfo.getCustom();
                switch (validity) {
                    case DAT:
                        failureTime = calculateFailureTime(createDate, DAT);
                        break;
                    case WEEK:
                        failureTime = calculateFailureTime(createDate, WEEK);
                        break;
                    default:
                        failureTime = calculateFailureTime(createDate, 9999);
                        break;
                }
                failureDateInsert.append(failureTime.toString());
                failureDateInsert.append("\"");
                tableName = getTableName(validity, shortLink);
                linkMapping.setFailureTime(failureDateInsert.toString());
            } else {
                switch (validity) {
                    case DAT:
                        logger.info("In to DBOper  ======= 一天短链");
                        failureTime = calculateFailureTime(createDate, DAT);
                        failureDateInsert.append(failureTime.toString());
                        failureDateInsert.append("\"");
                        System.out.println(failureDateInsert.toString()+"======================");
                        linkMapping.setFailureTime(failureDateInsert.toString());
                        tableName = getTableName(validity, shortLink);
                        break;
                    case WEEK:
                        logger.info("In to DBOper  ======= 一周短链");
                        failureTime = calculateFailureTime(createDate, WEEK);
                        failureDateInsert.append(failureTime.toString());
                        failureDateInsert.append("\"");
                        System.out.println(failureDateInsert.toString()+"======================");
                        linkMapping.setFailureTime(failureDateInsert.toString());
                        tableName = getTableName(validity, shortLink);
                        break;
                    case FOREVER:
                        logger.info("In to DBOper  ======= 永久短链");
                        failureTime = calculateFailureTime(createDate, 9999);
                        failureDateInsert.append(failureTime.toString());
                        failureDateInsert.append("\"");
                        System.out.println(failureDateInsert.toString()+"======================");
                        linkMapping.setFailureTime(failureDateInsert.toString());
                        tableName = getTableName(validity, shortLink);
                        break;
                }
            }
            try {
                logger.info("In to DBOper  ======= try Insert " + tableName + " " + linkMapping.toString());
                linkMappingDao.insert(tableName,
                    linkMapping.getShortLink(),
                    linkMapping.getLongLink(),
                    linkMapping.getCreateTime(),
                    linkMapping.getFailureTime());
            } catch (Exception e) {
                logger.info("In to DBOper  ======= Creat and Insert " + tableName + " " + linkMapping.toString());
                StringBuilder creatTableName = new StringBuilder();
                creatTableName.append("`");
                creatTableName.append(tableName);
                creatTableName.append("`");
                linkMappingDao.createTable(creatTableName.toString());
                linkMappingDao.insert(tableName,
                    linkMapping.getShortLink(),
                    linkMapping.getLongLink(),
                    linkMapping.getCreateTime(),
                    linkMapping.getFailureTime());
            }
        }
    }

    public Date calculateFailureTime(Date createTime, int validity) {
        logger.info("In to calculateFailureTime");
        Date validityDate;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(createTime);
        calendar.add(Calendar.DAY_OF_MONTH, validity);
        validityDate = calendar.getTime();
        return validityDate;
    }

    public String getTableName(int validity, String shortLink) {
        logger.info("In to getTableName ");
        StringBuilder tableName = new StringBuilder();
        if (shortLink.length()<7) {
            tableName.append("Custom_");
            tableName.append(shortLink.subSequence(0, 1));
        } else {
            switch (validity) {
                case FOREVER:
                    tableName.append("Persistent_");
                    break;
                default:
                    tableName.append("Temporary_");
                    break;
            }
            tableName.append(shortLink.subSequence(1, 2));

        }
        logger.info("In to getTableName" + tableName.toString());
        return tableName.toString();
    }
}
