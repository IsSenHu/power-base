package com.cdsen.email;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 邮件解析类
 *
 * @author HuSen
 * create on 2019/11/7 10:25
 */
@Slf4j
@Data
public class EmailParser implements Serializable {

    private String uid;
    private boolean html;
    private MimeMessage mimeMessage;
    private StringBuilder mailTextContent = new StringBuilder();
    private StringBuilder mailHtmlContent = new StringBuilder();

    private List<String> attachments = new ArrayList<>();
    private List<InputStream> attachmentsInputStreams = new ArrayList<>();
    private List<Long> attachSizeList = new ArrayList<>();
    private List<String> cidList = new ArrayList<>();

    public EmailParser(String uid, MimeMessage mimeMessage) {
        this.uid = uid;
        this.mimeMessage = mimeMessage;
    }

    /**
     * @return 邮件标题
     * @throws MessagingException 异常
     */
    public String getSubject() throws MessagingException {
        return mimeMessage.getSubject();
    }

    /**
     * @return 送信人的姓名和邮件地址
     * @throws MessagingException 异常
     */
    public InternetAddress getFrom() throws MessagingException {
        InternetAddress[] addresses = null;
        if (null != mimeMessage) {
            addresses = (InternetAddress[]) mimeMessage.getFrom();
        }

        if (addresses == null && null != mimeMessage) {
            String[] from = mimeMessage.getHeader(Constant.HEADER_FROM);
            addresses = new InternetAddress[]{new InternetAddress(from[0])};
        }

        if (null != addresses && addresses.length > 0) {
            return addresses[0];
        }
        return null;
    }

    /**
     * 根据类型获取邮件地址
     *
     * @param type 类型
     * @return 邮件地址
     * @throws MessagingException 异常
     */
    public InternetAddress[] getMailAddress(Message.RecipientType type) throws MessagingException {
        InternetAddress[] addresses = new InternetAddress[]{};
        if (null != mimeMessage) {
            addresses = (InternetAddress[]) mimeMessage.getRecipients(type);
        }
        return addresses;
    }

    /**
     * @return 邮件日期
     * @throws MessagingException 异常
     */
    public Date getSentDate() throws MessagingException {
        return mimeMessage.getSentDate();
    }

    /**
     * @return 是否有回执
     * @throws MessagingException 异常
     */
    public boolean getReplySign() throws MessagingException {
        String[] needReply = mimeMessage.getHeader(Constant.DISPOSITION_NOTIFICATION_TO);
        return Objects.nonNull(needReply);
    }

    /**
     * @return MessageNumber
     */
    public int getMessageNumber() {
        return mimeMessage.getMessageNumber();
    }

    /**
     * @return MessageId
     * @throws MessagingException 异常
     */
    public String getMessageId() throws MessagingException {
        String id = mimeMessage.getMessageID();
        return mimeMessage != null ? id : "";
    }

    /**
     * @return 是否有附件
     * @throws IOException        IO异常
     * @throws MessagingException Message异常
     */
    public boolean hasAttach() throws IOException, MessagingException {
        return isContainAttachment(mimeMessage);
    }

    private boolean isContainAttachment(Part part) throws MessagingException, IOException {
        boolean haveAttach = false;
        if (part.isMimeType(Constant.MULTIPART)) {
            MimeMultipart multipart = (MimeMultipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                String disposition = bodyPart.getDisposition();
                boolean judge = disposition != null && (disposition.equalsIgnoreCase(Part.ATTACHMENT) || disposition.equalsIgnoreCase(Part.INLINE));
                if (judge) {
                    haveAttach = true;
                } else if (bodyPart.isMimeType(Constant.MULTIPART)) {
                    haveAttach = isContainAttachment(bodyPart);
                } else {
                    String contentType = bodyPart.getContentType();
                    if (contentType.contains(Constant.APPLICATION) || contentType.contains(Constant.NAME)) {
                        haveAttach = true;
                    }
                }
                if (haveAttach) {
                    break;
                }
            }
        } else if (part.isMimeType(Constant.RFC822)) {
            haveAttach = isContainAttachment((Part) part.getContent());
        }
        return haveAttach;
    }

    private String getCid(Part part) throws MessagingException {
        String content, cid;
        String[] headers = part.getHeader("Content-Id");
        if (headers != null && headers.length > 0) {
            content = headers[0];
        } else {
            return null;
        }
        if (content.startsWith(Constant.LEFT_TAG_SIGN) && content.endsWith(Constant.RIGHT_TAG_SIGN)) {
            cid = "cid:" + content.substring(1, content.length() - 1);
        } else {
            cid = "cid:" + content;
        }
        return cid;
    }

    private void parserMailContent(Part part) throws MessagingException, IOException {
        String contentType = part.getContentType();
        boolean containName = contentType.contains(Constant.NAME);
        if (part.isMimeType(Constant.TEXT) && !containName) {
            mailTextContent.append(part.getContent().toString());
        } else if (part.isMimeType(Constant.HTML) && !containName) {
            html = true;
            mailHtmlContent.append(part.getContent().toString());
        } else if (part.isMimeType(Constant.MULTIPART)) {
            Multipart mp = (Multipart) part.getContent();
            int count = mp.getCount();
            for (int i = 0; i < count; i++) {
                parserMailContent(mp.getBodyPart(i));
            }
        } else if (part.isMimeType(Constant.RFC822)) {
            parserMailContent((Part) part.getContent());
        }
    }

    /**
     * @return 是否未读
     * @throws MessagingException 异常
     */
    public boolean isNew() throws MessagingException {
        return mimeMessage.getFlags().contains(Flags.Flag.SEEN);
    }

    public boolean isStart() throws MessagingException {
        return mimeMessage.getFlags().contains(Flags.Flag.FLAGGED);
    }

    /**
     * @return 邮件内容
     * @throws IOException        IO异常
     * @throws MessagingException Message异常
     */
    public String getMailContent() throws IOException, MessagingException {
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);

        resetList();

        saveAttachment(mimeMessage);

        parserMailContent(mimeMessage);

        String content = mailTextContent.append(mailHtmlContent).toString();
        mailTextContent.setLength(0);
        mailTextContent.setLength(0);

        return content;
    }

    private void saveAttachment(Part part) throws IOException, MessagingException {
        String fileName;
        if (part.isMimeType(Constant.MULTIPART)) {
            Multipart mp = (Multipart) part.getContent();
            int count = mp.getCount();
            for (int i = 0; i < count; i++) {
                BodyPart bodyPart = mp.getBodyPart(i);
                String disposition = bodyPart.getDisposition();
                boolean judge = disposition != null && (disposition.equalsIgnoreCase(Part.ATTACHMENT) || disposition.equalsIgnoreCase(Part.INLINE));
                if (judge) {
                    fileName = bodyPart.getFileName();
                    if (fileName != null) {
                        fileName = MimeUtility.decodeText(fileName);
                        attachments.add(fileName);
                        attachmentsInputStreams.add(bodyPart.getInputStream());
                        attachSizeList.add((long) bodyPart.getSize());
                    }
                } else if (bodyPart.isMimeType(Constant.MULTIPART)) {
                    saveAttachment(bodyPart);
                } else {
                    fileName = bodyPart.getFileName();
                    if (fileName != null) {
                        fileName = MimeUtility.decodeText(fileName);
                        attachments.add(fileName);
                        attachmentsInputStreams.add(bodyPart.getInputStream());
                        attachSizeList.add((long) bodyPart.getSize());
                        String cid = getCid(bodyPart);
                        if (StringUtils.hasText(cid)) {
                            cidList.add(cid);
                        }
                        log.info("Cid={}", cid);
                    }
                }
            }
        } else if (part.isMimeType(Constant.RFC822)) {
            saveAttachment((Part) part.getContent());
        }
    }

    private void resetList() {
        attachments.clear();
        attachmentsInputStreams.clear();
        attachSizeList.clear();
        cidList.clear();
    }
}
