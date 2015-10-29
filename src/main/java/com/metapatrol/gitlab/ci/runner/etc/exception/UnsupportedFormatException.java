package com.metapatrol.gitlab.ci.runner.etc.exception;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class UnsupportedFormatException extends RuntimeException {

    public UnsupportedFormatException(String path){
        super(String.format("Cannot read format of '%s'", path));
    }
}
