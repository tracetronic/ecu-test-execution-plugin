package de.tracetronic.jenkins.plugins.ecutestexecution.util

import de.tracetronic.jenkins.plugins.ecutestexecution.configs.AnalysisConfig
import de.tracetronic.jenkins.plugins.ecutestexecution.configs.PackageConfig
import de.tracetronic.jenkins.plugins.ecutestexecution.configs.TestConfig
import hudson.model.TaskListener

class LogConfigUtil {
    private final TaskListener listener
    private final TestConfig testConfig
    private final PackageConfig packageConfig
    private final AnalysisConfig analysisConfig

    LogConfigUtil(TaskListener listener, TestConfig testConfig) {
        this(listener, testConfig, null, null)
    }

    LogConfigUtil(TaskListener listener, TestConfig testConfig, PackageConfig packageConfig, AnalysisConfig analysisConfig) {
        this.listener = listener
        this.testConfig = testConfig
        this.packageConfig = packageConfig
        this.analysisConfig = analysisConfig
    }

    void log() {
        if (testConfig.tbcPath) {
            listener.logger.println("-> With TBC=${testConfig.tbcPath}")
        }
        if (testConfig.tcfPath) {
            listener.logger.println("-> With TCF=${testConfig.tcfPath}")
        }
        if (testConfig.constants) {
            listener.logger.println("-> With global constants=[${testConfig.constants.each { it.toString() }}]")
        }
        if (packageConfig && packageConfig.packageParameters) {
            listener.logger.println("-> With package parameters=[${packageConfig.packageParameters.each { it.toString() }}]")
        }
        if (analysisConfig && analysisConfig.analysisName) {
            listener.logger.println("-> With analysis=${analysisConfig.analysisName}")
        }
        if (analysisConfig && analysisConfig.mapping) {
            listener.logger.println("-> With mapping=${analysisConfig.mapping}")
        }
        if (analysisConfig && analysisConfig.recordings) {
            listener.logger.println("-> With analysis recordings=[${analysisConfig.recordings.each { it.toString() }}]")
        }
    }
}
