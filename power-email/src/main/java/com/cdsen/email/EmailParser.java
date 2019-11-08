package com.cdsen.email;

import lombok.Data;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
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
        String messageID = mimeMessage.getMessageID();
        return mimeMessage != null ? messageID : "";
    }

    /**
     * @return 是否有附件
     * @throws IOException        IO异常
     * @throws MessagingException Message异常
     */
    public boolean hasAttach() throws IOException, MessagingException {
        return isContainAttachment(mimeMessage);
    }

    private static boolean isContainAttachment(Part part) throws MessagingException, IOException {
        boolean haveAttach = false;
        if (part.isMimeType(Constant.MULTIPART)) {
            MimeMultipart multipart = (MimeMultipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                String disposition = bodyPart.getDisposition();
                if (disposition != null && (disposition.equalsIgnoreCase(Part.ATTACHMENT) || disposition.equalsIgnoreCase(Part.INLINE))) {
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
}
