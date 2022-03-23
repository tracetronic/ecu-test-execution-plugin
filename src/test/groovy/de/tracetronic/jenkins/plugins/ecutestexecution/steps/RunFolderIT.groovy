package de.tracetronic.jenkins.plugins.ecutestexecution.steps

import de.tracetronic.jenkins.plugins.ecutestexecution.ETInstallation
import de.tracetronic.jenkins.plugins.ecutestexecution.IntegrationTestBase
import de.tracetronic.jenkins.plugins.ecutestexecution.configs.AnalysisConfig
import de.tracetronic.jenkins.plugins.ecutestexecution.configs.ExecutionConfig
import de.tracetronic.jenkins.plugins.ecutestexecution.configs.PackageConfig
import de.tracetronic.jenkins.plugins.ecutestexecution.configs.TestConfig
import de.tracetronic.jenkins.plugins.ecutestexecution.model.Constant
import de.tracetronic.jenkins.plugins.ecutestexecution.model.PackageParameter
import de.tracetronic.jenkins.plugins.ecutestexecution.model.Recording
import hudson.model.Result
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition
import org.jenkinsci.plugins.workflow.cps.SnippetizerTester
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.job.WorkflowRun
import org.jenkinsci.plugins.workflow.steps.StepConfigTester
import org.jvnet.hudson.test.JenkinsRule

import java.nio.file.Path
import java.nio.file.Paths

class RunFolderIT extends IntegrationTestBase {

    String testFolderPath

    def setup() {
        ETInstallation.DescriptorImpl etDescriptor = jenkins.jenkins.
                getDescriptorByType(ETInstallation.DescriptorImpl.class)
        etDescriptor.setInstallations(new ETInstallation('ECU-TEST', 'C:\\ECU-TEST',
                JenkinsRule.NO_PROPERTIES))

        Path resourcePath = Paths.get('src', 'test', 'resources', 'workspace', 'TestFolder')
        testFolderPath = resourcePath.toFile().getAbsolutePath()
    }

    def 'Default config round trip'() {
        given:
            RunFolderStep before = new RunFolderStep('../TestFolder')
        when:
            RunFolderStep after = new StepConfigTester(jenkins).configRoundTrip(before)
        then:
            jenkins.assertEqualDataBoundBeans(before, after)
    }

    def 'Config round trip'() {
        given:
            RunFolderStep before = new RunFolderStep('../TestFolder')
            before.setScanMode(RunFolderStep.ScanMode.PACKAGES_ONLY)
            before.setRecursiveScan(true)
            before.setFailFast(false)

            TestConfig testConfig = new TestConfig()
            testConfig.setTbcPath('test.tbc')
            testConfig.setTcfPath('test.tcf')
            testConfig.setForceConfigurationReload(true)
            testConfig.setConstants(Arrays.asList(new Constant('constLabel', 'constValue')))
            before.setTestConfig(testConfig)

            PackageConfig packageConfig = new PackageConfig(Arrays.asList(
                    new PackageParameter('paramLabel', 'paramValue')))
            before.setPackageConfig(packageConfig)

            AnalysisConfig analysisConfig = new AnalysisConfig()
            analysisConfig.setMapping('mappingName')
            analysisConfig.setAnalysisName('analysisName')
            Recording recording = new Recording('recording.csv')
            recording.setDeviceName('deviceName')
            recording.setFormatDetails('formatDetails')
            recording.setRecordingGroup('recordingGroup')
            // recording.setMappingNames(['mapping1', 'mapping2'])
            analysisConfig.setRecordings(Arrays.asList(recording))
            before.setAnalysisConfig(analysisConfig)

            ExecutionConfig executionConfig = new ExecutionConfig()
            executionConfig.setStopOnError(false)
            executionConfig.setTimeout(60)
            before.setExecutionConfig(executionConfig)
        when:
            RunFolderStep after = new StepConfigTester(jenkins).configRoundTrip(before)
        then:
            jenkins.assertEqualDataBoundBeans(before, after)
    }

    def 'Snippet generator'() {
        given:
            SnippetizerTester st = new SnippetizerTester(jenkins)
        when:
            RunFolderStep step = new RunFolderStep('../TestFolder')
        then:
            st.assertRoundTrip(step, "ttRunFolder '../TestFolder'")
        when:
            step.setRecursiveScan(true)
            step.setFailFast(false)
            step.setScanMode(RunFolderStep.ScanMode.PROJECTS_ONLY)
        then:
            st.assertRoundTrip(step, "ttRunFolder failFast: false, recursiveScan: true, " +
                    "scanMode: 'PROJECTS_ONLY', testCasePath: '../TestFolder'")
        when:
            TestConfig testConfig = new TestConfig()
            testConfig.setTbcPath('test.tbc')
            testConfig.setTcfPath('test.tcf')
            testConfig.setForceConfigurationReload(true)
            testConfig.setConstants(Arrays.asList(new Constant('constLabel', 'constValue')))
            step.setTestConfig(testConfig)
        then:
            st.assertRoundTrip(step, "ttRunFolder failFast: false, recursiveScan: true, " +
                    "scanMode: 'PROJECTS_ONLY', testCasePath: '../TestFolder', " +
                    "testConfig: [constants: [[label: 'constLabel', value: 'constValue']], " +
                    "forceConfigurationReload: true, tbcPath: 'test.tbc', tcfPath: 'test.tcf']")
        when:
            PackageConfig packageConfig = new PackageConfig(Arrays.asList(
                    new PackageParameter('paramLabel', 'paramValue')))
            step.setPackageConfig(packageConfig)
        then:
            st.assertRoundTrip(step, "ttRunFolder failFast: false, " +
                    "packageConfig: [packageParameters: [[label: 'paramLabel', value: 'paramValue']]], " +
                    "recursiveScan: true, scanMode: 'PROJECTS_ONLY', testCasePath: '../TestFolder', " +
                    "testConfig: [constants: [[label: 'constLabel', value: 'constValue']], " +
                    "forceConfigurationReload: true, tbcPath: 'test.tbc', tcfPath: 'test.tcf']")
        when:
            AnalysisConfig analysisConfig = new AnalysisConfig()
            analysisConfig.setMapping('mappingName')
            analysisConfig.setAnalysisName('analysisName')
            Recording recording = new Recording('recording.csv')
            recording.setDeviceName('deviceName')
            recording.setFormatDetails('formatDetails')
            recording.setRecordingGroup('recordingGroup')
            //recording.setMappingNames(['mapping1', 'mapping2'])
            analysisConfig.setRecordings(Arrays.asList(recording))
            step.setAnalysisConfig(analysisConfig)
        then:
            st.assertRoundTrip(step, "ttRunFolder " +
                    "analysisConfig: [analysisName: 'analysisName', mapping: 'mappingName', " +
                    "recordings: [[deviceName: 'deviceName', formatDetails: 'formatDetails', " +
                    "path: 'recording.csv', recordingGroup: 'recordingGroup']]], failFast: false, " +
                    "packageConfig: [packageParameters: [[label: 'paramLabel', value: 'paramValue']]], " +
                    "recursiveScan: true, scanMode: 'PROJECTS_ONLY', testCasePath: '../TestFolder', " +
                    "testConfig: [constants: [[label: 'constLabel', value: 'constValue']], " +
                    "forceConfigurationReload: true, tbcPath: 'test.tbc', tcfPath: 'test.tcf']")
        when:
            ExecutionConfig executionConfig = new ExecutionConfig()
            executionConfig.setStopOnError(false)
            executionConfig.setTimeout(0)
            step.setExecutionConfig(executionConfig)
        then:
            st.assertRoundTrip(step, "ttRunFolder " +
                    "analysisConfig: [analysisName: 'analysisName', mapping: 'mappingName', " +
                    "recordings: [[deviceName: 'deviceName', formatDetails: 'formatDetails', " +
                    "path: 'recording.csv', recordingGroup: 'recordingGroup']]], " +
                    "executionConfig: [stopOnError: false, timeout: 0], failFast: false, " +
                    "packageConfig: [packageParameters: [[label: 'paramLabel', value: 'paramValue']]], " +
                    "recursiveScan: true, scanMode: 'PROJECTS_ONLY', testCasePath: '../TestFolder', " +
                    "testConfig: [constants: [[label: 'constLabel', value: 'constValue']], " +
                    "forceConfigurationReload: true, tbcPath: 'test.tbc', tcfPath: 'test.tcf']")
    }

    def 'Run default pipeline'() {
        given:
            WorkflowJob job = jenkins.createProject(WorkflowJob.class, 'pipeline')
            job.setDefinition(
                    new CpsFlowDefinition(
                            "node { ttRunFolder '${testFolderPath.replace('\\', '\\\\')}' }",
                            true))
        expect:
            WorkflowRun run = jenkins.assertBuildStatus(Result.FAILURE, job.scheduleBuild2(0).get())
            jenkins.assertLogContains('Found 1 package(s)', run)
            jenkins.assertLogContains('Found 1 project(s)', run)
            // packages will be execute first
            jenkins.assertLogContains("Executing package ${testFolderPath}\\test.pkg...", run)
    }

    def 'Run pipeline recursive'() {
        given:
            WorkflowJob job = jenkins.createProject(WorkflowJob.class, 'pipeline')
            job.setDefinition(new CpsFlowDefinition(
                    "node { ttRunFolder  recursiveScan: true, " +
                            "testCasePath: '${testFolderPath.replace('\\', '\\\\')}' }",
                    true))
        expect:
            WorkflowRun run = jenkins.assertBuildStatus(Result.FAILURE, job.scheduleBuild2(0).get())
            jenkins.assertLogContains('Found 2 package(s)', run)
            jenkins.assertLogContains('Found 2 project(s)', run)
            // packages in subfolder will be execute first
            jenkins.assertLogContains("Executing package ${testFolderPath}\\SubTestFolder\\test.pkg...", run)
    }

    def 'Run pipeline scan mode'() {
        given:
            WorkflowJob job = jenkins.createProject(WorkflowJob.class, 'pipeline')
        job.setDefinition(new CpsFlowDefinition(
                "node { ttRunFolder  scanMode: 'PROJECTS_ONLY', " +
                        "testCasePath: '${testFolderPath.replace('\\', '\\\\')}' }",
                true))
        expect:
            WorkflowRun run = jenkins.assertBuildStatus(Result.FAILURE, job.scheduleBuild2(0).get())
        jenkins.assertLogNotContains('No packages found!', run)
        jenkins.assertLogNotContains('Found 1 packages(s)', run)
        jenkins.assertLogContains('Found 1 project(s)', run)
        // packages will be execute first
        jenkins.assertLogContains("Executing project ${testFolderPath}\\test.prj...", run)
    }
}
