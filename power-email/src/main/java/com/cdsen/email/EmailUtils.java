package com.cdsen.email;

import lombok.extern.slf4j.Slf4j;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;
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
    public static void getMessages(String host, String username, String password, Consumer<List<MimeMessage>> consumer) {
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
                throw new IllegalStateException();
            }
            folder.open(Folder.READ_WRITE);
            Message[] messages = folder.getMessages();
            List<MimeMessage> collect = Arrays.stream(messages).map(m -> (MimeMessage) m).collect(Collectors.toList());
            consumer.accept(collect);
        } catch (Exception e) {
            log.error("获取所有邮件失败:", e);
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
     * 获取标题
     *
     * @param message MimeMessage
     * @return 标题
     */
    public static String getSubject(MimeMessage message) throws Exception {
        return MimeUtility.decodeText(message.getSubject());
    }

    /**
     * 获取发件人的地址和姓名
     *
     * @param message MimeMessage
     * @return 发件人的地址和姓名
     */
    public static String getFrom(MimeMessage message) throws MessagingException {
        InternetAddress[] addresses = (InternetAddress[]) message.getFrom();
        String from = addresses[0].getAddress();
        from = from == null ? "" : from;
        String personal = addresses[0].getPersonal();
        personal = personal == null ? "" : personal;
        return personal.concat("<").concat(from).concat(">");
    }

    /**
     * 获得邮件的收件人、抄送、和密送的地址和姓名，根据所传递的参数的不同 "to"收件人 "cc"抄送人 "bcc"密送人
     *
     * @param message MimeMessage
     * @param type    收件人类型
     * @return 地址和姓名
     */
    public static String getMailAddress(MimeMessage message, String type) throws Exception {
        StringBuilder mailAddr = new StringBuilder();
        String addType = type.toUpperCase();
        InternetAddress[] addresses;
        switch (addType) {
            case "TO": {
                addresses = (InternetAddress[]) message.getRecipients(Message.RecipientType.TO);
                break;
            }
            case "CC": {
                addresses = (InternetAddress[]) message.getRecipients(Message.RecipientType.CC);
                break;
            }
            case "BCC": {
                addresses = (InternetAddress[]) message.getRecipients(Message.RecipientType.BCC);
                break;
            }
            default:
                throw new Exception();
        }
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

    /**
     * 获得邮件发送日期
     *
     * @param message MimeMessage
     * @return 邮件发送日期
     * @throws Exception Exception
     */
    public static Date getSentDate(MimeMessage message) throws Exception {
        return message.getSentDate();
    }

    /**
     * 是否有附件
     *
     * @param part Part
     * @return 是否
     */
    public static boolean isContainAttach(Part part) throws Exception {
        boolean attachFlag = false;
        if (part.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) part.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                BodyPart bodyPart = mp.getBodyPart(i);
                String disposition = bodyPart.getDisposition();
                if (disposition != null && (disposition.equals(Part.ATTACHMENT) || disposition.equals(Part.INLINE))) {
                    attachFlag = true;
                } else if (bodyPart.isMimeType("multipart/*")) {
                    attachFlag = isContainAttach(bodyPart);
                } else {
                    String type = bodyPart.getContentType();
                    if (type.toLowerCase().contains("application") || type.toLowerCase().contains("name")) {
                        attachFlag = true;
                    }
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            attachFlag = isContainAttach((Part) part.getContent());
        }
        return attachFlag;
    }
}
