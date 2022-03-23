package de.tracetronic.jenkins.plugins.ecutestexecution.scan

import de.tracetronic.jenkins.plugins.ecutestexecution.IntegrationTestBase
import hudson.Launcher
import hudson.model.TaskListener
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.steps.StepContext

import java.nio.file.Path
import java.nio.file.Paths

class TestProjectScannerIT extends IntegrationTestBase {

    static String testFolderPath
    static String testFileName
    static String projectFile
    static String projectSubFile
    Launcher launcher
    StepContext context

    def setupSpec() {
        testFileName = 'test.prj'
        testFolderPath = Paths.get('src', 'test', 'resources', 'workspace', 'TestFolder').toFile().getAbsolutePath()
        projectFile = Paths.get(testFolderPath, testFileName).toFile().getAbsolutePath()
        projectSubFile = Paths.get(testFolderPath, 'SubTestFolder', testFileName).toFile().getAbsolutePath()
    }

    def setup() {
        launcher = jenkins.createOnlineSlave().createLauncher(jenkins.createTaskListener())
        context = Mock()
        context.get(Launcher.class) >> launcher
    }

    def 'Test Get File Extension'() {
        given:
            TestProjectScanner testProjectScanner = new TestProjectScanner(testFolderPath, true, context)

        expect:
            testProjectScanner.getFileExtension() == '.prj'
    }

    def 'Test No Projects'() {
        given:
            TestProjectScanner testProjectScanner = new TestProjectScanner(
                    Paths.get('src', 'test', 'resources').toFile().getAbsolutePath(), false, context)

        when:
            List<String> testFiles = testProjectScanner.scanTestFiles()

        then:
            testFiles.isEmpty()
    }

    def 'Test Scan Projects'() {
        given:
            TestProjectScanner testProjectScanner = new TestProjectScanner(testFolderPath, false, context)

        when:
            List<String> testFiles = testProjectScanner.scanTestFiles()

        then:
            testFiles.size() == 1
            testFiles.contains(projectFile)
    }

    def 'Test Recursive Scan'() {
        given:
            TestProjectScanner testProjectScanner = new TestProjectScanner(testFolderPath, true, context)

        when:
            List<String> testFiles = testProjectScanner.scanTestFiles()

        then:
            testFiles.size() == 2
            testFiles.contains(projectFile)
            testFiles.contains(projectSubFile)
    }

    def 'Test File Pattern'(boolean recursive, String expectedPattern) {
        given:
            TestProjectScanner testProjectScanner = new TestProjectScanner(testFolderPath, recursive, context)

        expect:
            expectedPattern == testProjectScanner.getFilePattern()

        where:
            recursive   | expectedPattern
            true        | '**/*.prj'
            false       | '*.prj'
    }

    def 'Test Input Dir Does Not Exists'() {
        given:
            TestProjectScanner testProjectScanner = new TestProjectScanner(
                    Paths.get('no', 'valid', 'path').toFile().getAbsolutePath(), false, context)

        when:
            testProjectScanner.scanTestFiles()

        then:
            thrown IllegalStateException
    }
}
