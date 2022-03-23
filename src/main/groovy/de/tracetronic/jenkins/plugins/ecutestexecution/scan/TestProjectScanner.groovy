package de.tracetronic.jenkins.plugins.ecutestexecution.scan

import org.jenkinsci.plugins.workflow.steps.StepContext

class TestProjectScanner extends AbstractTestScanner {
    private static final FILE_EXTENSION = '.prj'

    TestProjectScanner(String inputDir, boolean recursive, StepContext context) {
        super(inputDir, recursive, context)
    }

    @Override
    protected String getFileExtension() {
        return FILE_EXTENSION
    }
}
