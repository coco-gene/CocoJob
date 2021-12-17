package com.yunqiic.cocojob.worker.common;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

/**
 * 获取 Worker 版本，便于开发者排查问题
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public final class CocoJobWorkerVersion {

    private static String CACHE = null;

    /**
     * Return the full version string of the present CocoJob-Worker codebase, or {@code null}
     * if it cannot be determined.
     * @return the version of CocoJob-Worker or {@code null}
     * @see Package#getImplementationVersion()
     */
    public static String getVersion() {
        if (StringUtils.isEmpty(CACHE)) {
            CACHE = determineCocoJobVersion();
        }
        return CACHE;
    }

    private static String determineCocoJobVersion() {
        String implementationVersion = CocoJobWorkerVersion.class.getPackage().getImplementationVersion();
        if (implementationVersion != null) {
            return implementationVersion;
        }
        CodeSource codeSource = CocoJobWorkerVersion.class.getProtectionDomain().getCodeSource();
        if (codeSource == null) {
            return null;
        }
        URL codeSourceLocation = codeSource.getLocation();
        try {
            URLConnection connection = codeSourceLocation.openConnection();
            if (connection instanceof JarURLConnection) {
                return getImplementationVersion(((JarURLConnection) connection).getJarFile());
            }
            try (JarFile jarFile = new JarFile(new File(codeSourceLocation.toURI()))) {
                return getImplementationVersion(jarFile);
            }
        }
        catch (Exception ex) {
            return null;
        }
    }

    private static String getImplementationVersion(JarFile jarFile) throws IOException {
        return jarFile.getManifest().getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
    }
}
