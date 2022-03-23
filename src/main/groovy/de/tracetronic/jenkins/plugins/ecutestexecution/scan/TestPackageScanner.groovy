package de.tracetronic.jenkins.plugins.ecutestexecution.scan

import org.jenkinsci.plugins.workflow.steps.StepContext

class TestPackageScanner extends AbstractTestScanner {
    private static final FILE_EXTENSION = '.pkg'

    TestPackageScanner(String inputDir, boolean recursive, StepContext context) {
        super(inputDir, recursive, context)
    }

    @Override
    protected String getFileExtension() {
        return FILE_EXTENSION
    }
}
