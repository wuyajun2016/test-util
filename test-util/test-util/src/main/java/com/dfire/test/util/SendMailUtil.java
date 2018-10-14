package com.dfire.test.util;

import com.dfire.soa.mail.service.IDfireSendCloudMailService;
import com.twodfire.share.result.Result;
import com.twodfire.util.PropertiesUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.Map;

/**
 * Created by majianfeng on 16/3/1.
 */
public class SendMailUtil {

    public Result sendMail(String mailTitle, String mailContent) throws IOException {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        IDfireSendCloudMailService dfireSendCloudMailService = (IDfireSendCloudMailService) applicationContext.getBean("dfireSendCloudMailService");
        PropertiesUtil propertiesUtil = new PropertiesUtil();
        Map<String, String> propertiesMap = propertiesUtil.readProperties("config.properties");
        String mailTo = propertiesMap.get("mailTo");
        Result result = dfireSendCloudMailService.sendCustomMailWithFileStream(mailTo, mailTitle, mailContent, null);
        return result;
    }
}
