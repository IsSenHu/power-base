package com.cdsen.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author HuSen
 * create on 2019/10/30 14:26
 */
@Slf4j
@SuppressWarnings("ALL")
public class EmailUtils {

    private static final String TEXT = "text/plain";
    private static final String HTML = "text/html";
    private static final String MULTIPART = "multipart/*";
    private static final String RFC822 = "message/rfc822";

    public static final String CONTENT_RES_SIGN = "cid:";
    public static final String CONTENT_FOXMAIL_SIGN = "cid:_Foxmail";

    private static final String NAME = "name";
    private static final String APPLICATION = "application";

    private static final String DISPOSITION_NOTIFICATION_TO = "Disposition-Notification-To";

    /**
     * 读取并操作所有的邮件
     *
     * @param token    安全认证
     * @param consumer 后续邮件处理函数
     */
    public static void readMessages(EmailAuthToken token, Consumer<List<EmailParser>> consumer) {
        Properties properties = System.getProperties();
        properties.setProperty("mail.store.protocol", "imap");
        properties.setProperty("mail.imap.partialfetch", "false");
        Session session = Session.getDefaultInstance(properties);
        session.setDebug(false);

        Store store = null;
        Folder folder = null;
        try {
            store = session.getStore(token.getProtocol());
            store.connect(token.getHost(), token.getUsername(), token.getPassword());
            folder = store.getFolder(token.getFolder());
            Assert.notNull(folder, "folder is null");

            folder.open(Folder.READ_ONLY);
            Message[] messages = folder.getMessages();
            List<EmailParser> collect = Arrays.stream(messages).map(m -> new EmailParser(UUID.randomUUID().toString(), (MimeMessage) m)).collect(Collectors.toList());
            consumer.accept(collect);
        } catch (Exception e) {
            log.error("read email has occur error:", e);
        } finally {
            try {
                if (folder != null) {
                    folder.close();
                }
                if (store != null) {
                    store.close();
                }
            } catch (MessagingException e) {
                log.error("close resource error:", e);
            }
        }
    }

    /**
     * 获取地址和姓名
     *
     * @param address InternetAddress
     * @return 发件人的地址和姓名
     */
    public static String getFrom(InternetAddress address) throws MessagingException {
        if (null != address) {
            String from = address.getAddress();
            String personal = address.getPersonal();
            personal = personal == null ? "" : personal;
            return personal.concat("<").concat(from).concat(">");
        }
        return null;
    }

    /**
     * 批量获取地址和姓名
     *
     * @param addresses InternetAddress[]
     * @param type    收件人类型
     * @return 地址和姓名
     */
    public static String getMailAddress(InternetAddress[] addresses) throws Exception {
        StringBuilder mailAddr = new StringBuilder();
        if (addresses != null) {
            for (InternetAddress address : addresses) {
                String email = address.getAddress();
                email = email == null ? "" : MimeUtility.decodeText(email);

                String personal = address.getPersonal();
                personal = personal == null ? "" : MimeUtility.decodeText(personal);

                mailAddr.append(",").append(personal.concat("<").concat(email).concat(">"));
            }
        }
        return mailAddr.length() > 1 ? mailAddr.substring(1) : "";
    }
}
