package de.tracetronic.jenkins.plugins.ecutestexecution.scan

import de.tracetronic.jenkins.plugins.ecutestexecution.ETInstallation
import hudson.Launcher
import jenkins.security.MasterToSlaveCallable
import org.apache.tools.ant.DirectoryScanner
import org.jenkinsci.plugins.workflow.steps.StepContext

abstract class AbstractTestScanner {
    private final String inputDir
    private final boolean recursive
    private final transient StepContext context


    AbstractTestScanner(final String inputDir, final boolean recursive, final StepContext context) {
        super()
        this.inputDir = inputDir
        this.recursive = recursive
        this.context = context
    }

    String getInputDir() {
        return inputDir
    }

    boolean isRecursive() {
        return recursive
    }

    List<String> scanTestFiles() throws IOException, InterruptedException {
        return context.get(Launcher.class).getChannel().call(new ScanTestCallable(inputDir, getFilePattern()))
    }

    protected String getFilePattern() {
        final String filePattern
        if (isRecursive()) {
            filePattern = '**/*' + getFileExtension()
        } else {
            filePattern = '*' + getFileExtension()
        }
        return filePattern
    }

    protected abstract String getFileExtension()

    private static final class ScanTestCallable extends MasterToSlaveCallable<List<String>, IOException> {
        private final String inputDir
        private final filePattern

        ScanTestCallable(final String inputDir, final String filePattern) {
            this.inputDir = inputDir
            this.filePattern = filePattern
        }


        @Override
        List<String> call() throws IOException {
            final String[] includes = [filePattern]
            final List<String> includeFiles = new ArrayList<>()
            final DirectoryScanner scanner = new DirectoryScanner()
            scanner.setBasedir(inputDir)
            scanner.setIncludes(includes)
            scanner.scan()

            final String[] fileNames = scanner.getIncludedFiles()
            fileNames.each {fileName ->
                includeFiles.add(new File(inputDir, fileName).getAbsolutePath())
            }

            return includeFiles
        }
    }
}
