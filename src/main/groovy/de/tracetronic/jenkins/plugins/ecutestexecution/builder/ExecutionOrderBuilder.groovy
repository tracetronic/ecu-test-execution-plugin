package de.tracetronic.jenkins.plugins.ecutestexecution.builder

import de.tracetronic.cxs.generated.et.client.model.AdditionalSettings
import de.tracetronic.cxs.generated.et.client.model.ExecutionOrder
import de.tracetronic.cxs.generated.et.client.model.LabeledValue
import de.tracetronic.cxs.generated.et.client.model.Recording
import de.tracetronic.jenkins.plugins.ecutestexecution.configs.AnalysisConfig
import de.tracetronic.jenkins.plugins.ecutestexecution.configs.PackageConfig
import de.tracetronic.jenkins.plugins.ecutestexecution.configs.TestConfig

class ExecutionOrderBuilder implements Serializable {

    private final String testCasePath
    private final TestConfig testConfig
    private final PackageConfig packageConfig
    private final AnalysisConfig analysisConfig
    private boolean isPackage

    ExecutionOrderBuilder(String testCasePath, TestConfig testConfig, PackageConfig packageConfig, AnalysisConfig analysisConfig) {
        this(testCasePath, testConfig)
        this.packageConfig = packageConfig
        this.analysisConfig = analysisConfig
        isPackage = true
    }

    ExecutionOrderBuilder(String testCasePath, TestConfig testConfig) {
        this.testCasePath = testCasePath
        this.testConfig = testConfig
        isPackage = false
    }

    ExecutionOrder build() {
        AdditionalSettings settings
        if (isPackage) {
            settings = new AdditionalSettings()
                .forceConfigurationReload(testConfig.forceConfigurationReload)
                .packageParameters(packageConfig.packageParameters as List<LabeledValue>)
                .analysisName(analysisConfig.analysisName)
                .mapping(analysisConfig.mapping)
                .recordings(analysisConfig.recordings as List<Recording>)
        }
        else {
            settings = new AdditionalSettings()
                .forceConfigurationReload(testConfig.forceConfigurationReload)
        }

        ExecutionOrder executionOrder = new ExecutionOrder()
                .testCasePath(testCasePath)
                .tbcPath(testConfig.tbcPath)
                .tcfPath(testConfig.tcfPath)
                .constants(testConfig.constants as List<LabeledValue>)
                .additionalSettings(settings)

        return executionOrder
    }
}
