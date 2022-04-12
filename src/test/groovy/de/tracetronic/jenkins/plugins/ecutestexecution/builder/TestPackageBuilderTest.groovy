package de.tracetronic.jenkins.plugins.ecutestexecution.builder

import de.tracetronic.cxs.generated.et.client.model.AdditionalSettings
import de.tracetronic.cxs.generated.et.client.model.Execution
import de.tracetronic.cxs.generated.et.client.model.ExecutionOrder
import de.tracetronic.cxs.generated.et.client.model.LabeledValue
import de.tracetronic.cxs.generated.et.client.model.Recording
import de.tracetronic.jenkins.plugins.ecutestexecution.IntegrationTestBase
import de.tracetronic.jenkins.plugins.ecutestexecution.configs.AnalysisConfig
import de.tracetronic.jenkins.plugins.ecutestexecution.configs.ExecutionConfig
import de.tracetronic.jenkins.plugins.ecutestexecution.configs.PackageConfig
import de.tracetronic.jenkins.plugins.ecutestexecution.configs.TestConfig
import de.tracetronic.jenkins.plugins.ecutestexecution.model.AdditionalSetting
import de.tracetronic.jenkins.plugins.ecutestexecution.model.Constant
import de.tracetronic.jenkins.plugins.ecutestexecution.model.PackageParameter
import hudson.Launcher
import org.jenkinsci.plugins.workflow.steps.StepContext
import org.junit.Rule
import org.junit.rules.TemporaryFolder

class TestPackageBuilderTest extends IntegrationTestBase {

    @Rule
    TemporaryFolder folder = new TemporaryFolder()

    Launcher launcher
    StepContext context

    def setup() {
        launcher = jenkins.createOnlineSlave().createLauncher(jenkins.createTaskListener())
        context = Mock()
        context.get(Launcher.class) >> launcher
    }

    def 'Test Get Artifact Name'() {
        given:
            TestPackageBuilder testPackageBuilder = new TestPackageBuilder(null, null, null, null, null, null)

        expect:
            testPackageBuilder.getTestArtifactName() == 'package'
    }

    def 'Test Empty Execution Order'() {
        given:
            final File testFile = folder.newFile()
            final TestConfig testConfig = new TestConfig()
            final ExecutionConfig executionConfig = new ExecutionConfig()
            final PackageConfig packageConfig = new PackageConfig()
            final AnalysisConfig analysisConfig = new AnalysisConfig()
            final AdditionalSettings additionalSettings = new AdditionalSettings()
                    .forceConfigurationReload(testConfig.forceConfigurationReload)
                    .packageParameters(packageConfig.packageParameters as List<LabeledValue>)
                    .analysisName(analysisConfig.analysisName)
                    .mapping(analysisConfig.mapping)
                    .recordings(analysisConfig.recordings as List<Recording>)
            TestPackageBuilder testPackageBuilder = new TestPackageBuilder(testFile.getAbsolutePath(), testConfig,
                    executionConfig, context, packageConfig, analysisConfig)

        when:
            ExecutionOrder executionOrder = testPackageBuilder.getExecutionOrder()

        then:
            executionOrder.getAdditionalSettings().equals(additionalSettings)
            // executionOrder.getAdditionalSettings().getMapping().equals(HERE DEFINED EX. ORDER...)
            executionOrder.getTbcPath() == ''
            executionOrder.getTcfPath() == ''
            executionOrder.getTestCasePath() == testFile.getAbsolutePath()
            executionOrder.getConstants() == (List<LabeledValue>) testConfig.constants
            executionOrder.getExecutionId() == ""
    }

    def 'Test Nonempty Execution Order'() {
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
            ExecutionOrder executionOrder = testPackageBuilder.getExecutionOrder()

        then:
            executionOrder.getAdditionalSettings().equals(additionalSettings)
            executionOrder.getTbcPath() == testTbc.getAbsolutePath()
            executionOrder.getTcfPath() == testTcf.getAbsolutePath()
            executionOrder.getTestCasePath() == testFile.getAbsolutePath()
            executionOrder.getConstants() == testConstants
            executionOrder.getExecutionId() == ""
            executionOrder.getAdditionalSettings().getMapping() == "testMapping"
            executionOrder.getAdditionalSettings().getAnalysisName() == "testName"
            executionOrder.getAdditionalSettings().getPackageParameters().size() == 1
            executionOrder.getAdditionalSettings().getPackageParameters()[0].getLabel() == "testPackageLabel"
            executionOrder.getAdditionalSettings().getPackageParameters()[0].getValue() == "testPackageValue"
    }

}