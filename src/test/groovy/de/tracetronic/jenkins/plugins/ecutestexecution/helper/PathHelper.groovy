package de.tracetronic.jenkins.plugins.ecutestexecution.helper


import java.nio.file.Path
import java.nio.file.Paths

class PathHelper {

    static String getPlatformSpecificPath(String path) {
        if (hudson.Platform.current() == hudson.Platform.UNIX) {
            return path.replace('\\', '/')
        } else {
            return path.replace('/', '\\')
        }
    }

    static String getEscapedPath(String path) {
        return path.replace('\\', '\\\\')
    }
}
