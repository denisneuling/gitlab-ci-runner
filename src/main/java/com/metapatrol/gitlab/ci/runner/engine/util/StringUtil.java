package com.metapatrol.gitlab.ci.runner.engine.util;

import java.util.Iterator;
import java.util.List;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class StringUtil {

    public static String join(List<String> args, String join){
        StringBuffer buffer = new StringBuffer();
        Iterator<String> iterator = args.iterator();
        while(iterator.hasNext()){
            buffer.append(iterator.next());
            if(iterator.hasNext() && buffer.length()>0){
                buffer.append(join);
            }
        }
        return buffer.toString();
    }
}
