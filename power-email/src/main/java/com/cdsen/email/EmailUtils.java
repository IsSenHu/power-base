package com.cdsen.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.function.BiConsumer;
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
    public static void readMessages(EmailAuthToken token, Consumer<List<MimeMessage>> consumer) {
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
            List<MimeMessage> collect = Arrays.stream(messages).map(m -> (MimeMessage) m).collect(Collectors.toList());
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
    public static String getMailAddress(MimeMessage message, Message.RecipientType type) throws Exception {
        StringBuilder mailAddr = new StringBuilder();
        InternetAddress[] addresses = (InternetAddress[]) message.getRecipients(type);
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
     * 读取邮件内容
     *
     * @param part Part
     * @throws Exception Exception
     */
    public static void readMailContent(Part part, StringBuilder builder) throws Exception {
        String contentType = part.getContentType();
        boolean containsName = contentType.contains(NAME);
        if (part.isMimeType(TEXT) && !containsName) {
            builder.append(part.getContent());
        } else if (part.isMimeType(HTML) && !containsName) {
            builder.append(part.getContent());
        } else if (part.isMimeType(MULTIPART)) {
            Multipart multipart = (Multipart) part.getContent();
            int count = multipart.getCount();
            for (int i = 0; i < count; i++) {
                readMailContent(multipart.getBodyPart(i), builder);
            }
        } else if (part.isMimeType(RFC822)) {
            readMailContent((Part) part.getContent(), builder);
        }
    }

    /**
     * 判断邮件是否需要回执
     *
     * @param message MimeMessage
     * @return 是否
     * @throws Exception Exception
     */
    public static boolean getReplySign(MimeMessage message) throws Exception {
        boolean replySign = false;
        String[] needReply = message.getHeader(DISPOSITION_NOTIFICATION_TO);
        if (needReply != null) {
            replySign = true;
        }
        return replySign;
    }

    /**
     * 获得此邮件的Message-ID
     *
     * @param message MimeMessage
     * @return Message-ID
     * @throws Exception Exception
     */
    public static String getMessageId(MimeMessage message) throws Exception {
        return message.getMessageID();
    }

    /**
     * 判断此邮件是否未读
     *
     * @param message MimeMessage
     * @return 是否
     * @throws Exception Exception
     */
    public static boolean isNew(MimeMessage message) throws Exception {
        boolean isNew = false;
        Flags flags = message.getFlags();
        Flags.Flag[] flag = flags.getSystemFlags();
        for (Flags.Flag value : flag) {
            if (value == Flags.Flag.SEEN) {
                isNew = true;
                break;
            }
        }
        return isNew;
    }

    /**
     * 保存附件
     *
     * @param part Part
     * @throws Exception Exception
     */
    public static void saveAttachment(Part part, BiConsumer<String, InputStream> saver) throws Exception {
        String fileName;
        if (part.isMimeType(MULTIPART)) {
            Multipart multipart = (Multipart) part.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                String disposition = bodyPart.getDisposition();
                if (disposition != null && (disposition.equals(Part.ATTACHMENT) || disposition.equals(Part.INLINE))) {
                    fileName = MimeUtility.decodeText(bodyPart.getFileName());
                    saver.accept(fileName, bodyPart.getInputStream());
                } else if (bodyPart.isMimeType(MULTIPART)) {
                    saveAttachment(bodyPart, saver);
                } else {
                    fileName = bodyPart.getFileName();
                    if (fileName != null) {
                        fileName = MimeUtility.decodeText(fileName);
                        saver.accept(fileName, bodyPart.getInputStream());
                    }
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
    public static boolean isContainAttach(Part part) throws Exception {
        boolean attachFlag = false;
        if (part.isMimeType(MULTIPART)) {
            Multipart mp = (Multipart) part.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                BodyPart bodyPart = mp.getBodyPart(i);
                String disposition = bodyPart.getDisposition();
                if (StringUtils.hasText(disposition) && (disposition.equals(Part.ATTACHMENT) || disposition.equals(Part.INLINE))) {
                    attachFlag = true;
                } else if (bodyPart.isMimeType(MULTIPART)) {
                    attachFlag = isContainAttach(bodyPart);
                } else {
                    String type = bodyPart.getContentType();
                    if (type.toLowerCase().contains(APPLICATION) || type.toLowerCase().contains(NAME)) {
                        attachFlag = true;
                    }
                }
            }
        } else if (part.isMimeType(RFC822)) {
            attachFlag = isContainAttach((Part) part.getContent());
        }
        return attachFlag;
    }
}
