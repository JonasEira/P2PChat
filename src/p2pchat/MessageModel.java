/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package p2pchat;

import java.util.ArrayList;

/**
 *
 * @author Jonas
 */
public class MessageModel {
    private int messageNumber;
    private String author;
    private String msgText;
    public MessageModel() {
        messageNumber = 0;
        author = "";
        msgText = "";
    }

    /**
     * @return the msgText
     */
    public String getMsgText() {
        return msgText;
    }

    /**
     * @param msgText the msgText to set
     */
    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }

    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return the messageNumber
     */
    public int getMessageNumber() {
        return messageNumber;
    }

    /**
     * @param messageNumber the messageNumber to set
     */
    public void setMessageNumber(int messageNumber) {
        this.messageNumber = messageNumber;
    }
}
