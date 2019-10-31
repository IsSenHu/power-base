package com.cdsen.email;

import lombok.extern.slf4j.Slf4j;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author HuSen
 * create on 2019/10/30 14:26
 */
@Slf4j
public class EmailUtils {

    private static final String IMAP = "imap";
    private static final String INBOX = "INBOX";

    /**
     * 获取所有的邮件
     *
     * @param host     Host
     * @param username 用户名
     * @param password 密码
     * @return 所有的邮件
     */
    public static List<MimeMessage> getMessages(String host, String username, String password) {
        Properties properties = new Properties();
        Session session = Session.getDefaultInstance(properties);
        session.setDebug(false);

        Store store = null;
        Folder folder = null;
        try {
            store = session.getStore(IMAP);
            store.connect(host, username, password);
            folder = store.getFolder(INBOX);
            if (folder == null) {
                return new ArrayList<>(0);
            }
            folder.open(Folder.READ_WRITE);
            Message[] messages = folder.getMessages();
            return Arrays.stream(messages).map(m -> (MimeMessage) m).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取所有邮件失败:", e);
            return new ArrayList<>(0);
        } finally {
            if (folder != null) {
                try {
                    folder.close();
                } catch (MessagingException e) {
                    log.error("close folder error:", e);
                }
            }
            if (store != null) {
                try {
                    store.close();
                } catch (MessagingException e) {
                    log.error("close store error:", e);
                }
            }
        }
    }

    /**
     * 是否有附件
     *
     * @param part Part
     * @return 是否
     */
    public static boolean isContainAttach(Part part) {

    }
}
