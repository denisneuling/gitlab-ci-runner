package com.metapatrol.gitlab.ci.runner.engine.util;


import java.net.URI;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class DockerNamingUtil {

    public static String nameFromURI(URI uri, String appendable, boolean dockerlike) {
        String name = uri.getPath();
        String extension = getFileExtension(name);
        if (extension != null && !extension.isEmpty()) {
            name = name.substring(0, name.length() - 4);
        }
        if (name.startsWith("/")) {
            name = name.substring(1, name.length());
        }
        if (dockerlike) {
            name = name.replaceAll("/", "_");
            name = name.replaceAll(".", "-");
        }
        if(appendable!=null&&!appendable.isEmpty()){
            name += appendable;
        }
        return name;
    }
    public static String getFileExtension(String path){
        String extension = "";

        int i = path.indexOf(".");
        if (i > 0) {
            extension = path.substring(i+1);
        }
        return extension;
    }
}
