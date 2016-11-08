/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Connection;

/**
 *
 * @author Jonas
 */
public class ChatException extends Exception {

    public ChatException(String send_thread_down){
        super(send_thread_down);
    }
    
}
