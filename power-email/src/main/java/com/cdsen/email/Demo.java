package com.cdsen.email;

import javax.mail.internet.MimeMessage;

/**
 * @author HuSen
 * create on 2019/10/22 15:43
 */
public class Demo {

    public static void main(String[] args) {
        String host = "imap.exmail.qq.com";
        String username = "husen@archly.cc";
        String password = "521428Slyt";

        EmailUtils.getMessages(host, username, password, mimeMessages -> {
            for (MimeMessage message : mimeMessages) {
                try {
                    System.out.println(EmailUtils.getSubject(message));
                    System.out.println(EmailUtils.isContainAttach(message));
                    System.out.println(EmailUtils.getFrom(message));
                    System.out.println(EmailUtils.getMailAddress(message, "to"));
                    System.out.println(EmailUtils.getSentDate(message));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
