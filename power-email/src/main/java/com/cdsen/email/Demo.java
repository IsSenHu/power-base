package com.cdsen.email;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import javax.mail.search.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

/**
 * @author HuSen
 * create on 2019/10/22 15:43
 */
public class Demo {

    public static void main(String[] args) {
        String host = "imap.exmail.qq.com";
        String username = "husen@archly.cc";
        String password = "521428Slyt";

        Properties properties = new Properties();
        Session session = Session.getDefaultInstance(properties);
        session.setDebug(true);

        try {
            Store store = session.getStore("imap");
            store.connect(host, username, password);

            Folder folder = store.getFolder("INBOX");
            if (folder == null) {
                System.out.println("获取邮箱文件信息为空");
            }
            folder.open(Folder.READ_WRITE);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -10);
            Date time = calendar.getTime();
            SentDateTerm sentDateTerm = new SentDateTerm(ComparisonTerm.GT, time);
            SearchTerm address = new SubjectTerm(MimeUtility.encodeText("关于更换工资卡的通知"));
            SearchTerm comparisonAndTerm = new AndTerm(address, sentDateTerm);
            Message[] messages = folder.getMessages();
            for (Message message : messages) {
                MimeMessage msg = (MimeMessage) message;
//                System.out.println(MimeUtility.decodeText(msg.getMessageID()));
                System.out.println(msg.getMessageID());
                System.out.println("=======================");
            }
            folder.close();
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
