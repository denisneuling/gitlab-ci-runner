package com.metapatrol.gitlab.ci.runner.etc.format;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class Formats {

    private static Formats INSTANCE;

    private Formats(){};

    public Formats getInstance(){
        if(INSTANCE==null){
            INSTANCE = new Formats();
        }
        return INSTANCE;
    }


}
