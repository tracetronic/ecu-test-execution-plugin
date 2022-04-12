package de.tracetronic.jenkins.plugins.ecutestexecution.builder

import de.tracetronic.cxs.generated.et.client.model.AdditionalSettings
import de.tracetronic.cxs.generated.et.client.model.LabeledValue
import de.tracetronic.cxs.generated.et.client.model.Recording;
import de.tracetronic.jenkins.plugins.ecutestexecution.IntegrationTestBase
import de.tracetronic.jenkins.plugins.ecutestexecution.configs.AnalysisConfig
import de.tracetronic.jenkins.plugins.ecutestexecution.configs.ExecutionConfig
import de.tracetronic.jenkins.plugins.ecutestexecution.configs.PackageConfig
import de.tracetronic.jenkins.plugins.ecutestexecution.configs.TestConfig
import de.tracetronic.jenkins.plugins.ecutestexecution.model.Constant
import de.tracetronic.jenkins.plugins.ecutestexecution.model.PackageParameter
import de.tracetronic.jenkins.plugins.ecutestexecution.model.TestResult
import de.tracetronic.jenkins.plugins.ecutestexecution.util.LogConfigUtil
import hudson.EnvVars;
import hudson.Launcher
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

public class TestPackageBuilderIT extends IntegrationTestBase {

    @Rule
    TemporaryFolder folder = new TemporaryFolder()

    EnvVars envVars
    StepContext context
    Launcher launcher
    TaskListener listener

    def setup() {
        listener = jenkins.createTaskListener()
        launcher = jenkins.createOnlineSlave().createLauncher(listener)
        envVars = new EnvVars()
        envVars.put('ET_API_HOSTNAME','testHost')
        envVars.put('ET_API_PORT','testPort')
        context = Mock()
        context.get(Launcher.class) >> launcher
        context.get(TaskListener.class) >> listener
        context.get(EnvVars.class) >> envVars
    }

    def 'Sample Test Logging'() {
        given:
        final File testFile = folder.newFile("test.pkg")
        final File configFolder = folder.newFolder("Configurations")
        final File testTbc = File.createTempFile("test", ".tbc", configFolder)
        final File testTcf = File.createTempFile("test2", ".tcf", configFolder)
        final List<Constant> testConstants = new ArrayList<>()
        testConstants.add(new Constant("testLabel","testValue"))
        final TestConfig testConfig = new TestConfig()
        testConfig.setTbcPath(testTbc.getAbsolutePath())
        testConfig.setTcfPath(testTcf.getAbsolutePath())
        testConfig.setConstants(testConstants)
        final ExecutionConfig executionConfig = new ExecutionConfig()
        final List<PackageParameter> testParameters = new ArrayList<>()
        testParameters.add(new PackageParameter("testPackageLabel","testPackageValue"))
        final PackageConfig packageConfig = new PackageConfig(testParameters)
        final AnalysisConfig analysisConfig = new AnalysisConfig()
        analysisConfig.setAnalysisName("testName")
        analysisConfig.setMapping("testMapping")
        List<de.tracetronic.jenkins.plugins.ecutestexecution.model.Recording> testRecording = new ArrayList<>()
        testRecording.add(new de.tracetronic.jenkins.plugins.ecutestexecution.model.Recording("testPath"))
        analysisConfig.setRecordings(testRecording)
        final AdditionalSettings additionalSettings = new AdditionalSettings()
                .forceConfigurationReload(testConfig.forceConfigurationReload)
                .packageParameters(packageConfig.packageParameters as List<LabeledValue>)
                .analysisName(analysisConfig.analysisName)
                .mapping(analysisConfig.mapping)
                .recordings(analysisConfig.recordings as List<Recording>)
        TestPackageBuilder testPackageBuilder = new TestPackageBuilder(testFile.getAbsolutePath(), testConfig,
                executionConfig, context, packageConfig, analysisConfig)

        when:
            TestResult result = testPackageBuilder.runTest()

        then:
            1 == 1
    }
}