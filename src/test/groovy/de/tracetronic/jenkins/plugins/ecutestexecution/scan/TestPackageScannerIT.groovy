package de.tracetronic.jenkins.plugins.ecutestexecution.scan

import de.tracetronic.jenkins.plugins.ecutestexecution.IntegrationTestBase
import hudson.Launcher
import org.jenkinsci.plugins.workflow.steps.StepContext

import java.nio.file.Paths

class TestPackageScannerIT extends IntegrationTestBase {

    static String testFolderPath
    static String testFileName
    static String packageFile
    static String packageSubFile
    Launcher launcher
    StepContext context

    def setupSpec() {
        testFileName = 'test.pkg'
        testFolderPath = Paths.get('src', 'test', 'resources', 'workspace', 'TestFolder').toFile().getAbsolutePath()
        packageFile = Paths.get(testFolderPath, testFileName).toFile().getAbsolutePath()
        packageSubFile = Paths.get(testFolderPath, 'SubTestFolder', testFileName).toFile().getAbsolutePath()
    }

    def setup() {
        launcher = jenkins.createOnlineSlave().createLauncher(jenkins.createTaskListener())
        context = Mock()
        context.get(Launcher.class) >> launcher
    }

    def 'Test Get File Extension'() {
        given:
            TestPackageScanner testPackageScanner = new TestPackageScanner(testFolderPath, true, context)

        expect:
            testPackageScanner.getFileExtension() == '.pkg'
    }

    def 'Test No Packages'() {
        given:
        TestPackageScanner testPackageScanner = new TestPackageScanner(
                    Paths.get('src', 'test', 'resources').toFile().getAbsolutePath(), false, context)

        when:
            List<String> testFiles = testPackageScanner.scanTestFiles()

        then:
            testFiles.isEmpty()
    }

    def 'Test Scan Packages'() {
        given:
            TestPackageScanner testPackageScanner = new TestPackageScanner(testFolderPath, false, context)

        when:
            List<String> testFiles = testPackageScanner.scanTestFiles()

        then:
            testFiles.size() == 1
            testFiles.contains(packageFile)
    }

    def 'Test Recursive Scan'() {
        given:
            TestPackageScanner testPackageScanner = new TestPackageScanner(testFolderPath, true, context)

        when:
            List<String> testFiles = testPackageScanner.scanTestFiles()

        then:
            testFiles.size() == 2
            testFiles.contains(packageFile)
            testFiles.contains(packageSubFile)
    }

    def 'Test File Pattern'(boolean recursive, String expectedPattern) {
        given:
            TestPackageScanner testPackageScanner = new TestPackageScanner(testFolderPath, recursive, context)

        expect:
            expectedPattern == testPackageScanner.getFilePattern()

        where:
            recursive   | expectedPattern
            true        | '**/*.pkg'
            false       | '*.pkg'
    }

    def 'Test Input Dir Does Not Exists'() {
        given:
            TestPackageScanner testPackageScanner = new TestPackageScanner(
                    Paths.get('no', 'valid', 'path').toFile().getAbsolutePath(), false, context)

        when:
            testPackageScanner.scanTestFiles()

        then:
            thrown IllegalStateException
    }
}
