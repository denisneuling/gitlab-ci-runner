package com.metapatrol.gitlab.ci.runner.etc.manager;

import java.io.File;
import java.io.IOException;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public interface EtcManager {

    public <T> T write(String path, T confObject) throws IOException;

    public <T> T read(String path, T confObject) throws IOException;

    public <T> T write(File file, T confObject) throws IOException;

    public <T> T read(File file, T confObject) throws IOException;
}
