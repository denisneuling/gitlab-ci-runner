package com.metapatrol.gitlab.ci.runner.etc.format;

import java.io.File;
import java.io.IOException;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public interface FormatWriter extends SuffixAware  {

    public <T> T write(File file, T confObject) throws IOException;
}
