package com.metapatrol.gitlab.ci.runner.engine.components;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class MessageHolder {
    private int length = 0;
    private StringBuffer stringBuffer = new StringBuffer();

    public synchronized void append(String message){
        stringBuffer.append(message);
        stringBuffer.append("\n");
        length++;
    }

    public synchronized int getLength(){
        return length;
    }
    public synchronized String getMessages(){
        return stringBuffer.toString();
    }
}
