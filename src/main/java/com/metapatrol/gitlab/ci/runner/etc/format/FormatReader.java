package com.metapatrol.gitlab.ci.runner.etc.format;

import java.io.File;
import java.io.IOException;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public interface FormatReader extends SuffixAware {

    public <T> T read(File file, T confObjectClass) throws IOException;

}
