package de.tracetronic.jenkins.plugins.ecutestexecution.steps

import com.google.common.collect.ImmutableSet
import de.tracetronic.jenkins.plugins.ecutestexecution.configs.AnalysisConfig
import de.tracetronic.jenkins.plugins.ecutestexecution.configs.PackageConfig
import de.tracetronic.jenkins.plugins.ecutestexecution.util.ValidationUtil
import hudson.EnvVars
import hudson.Extension
import hudson.Launcher
import hudson.model.Run
import hudson.model.TaskListener
import hudson.util.FormValidation
import hudson.util.ListBoxModel
import org.jenkinsci.plugins.workflow.steps.StepContext
import org.jenkinsci.plugins.workflow.steps.StepDescriptor
import org.jenkinsci.plugins.workflow.steps.StepExecution
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.DataBoundSetter
import org.kohsuke.stapler.QueryParameter

import javax.annotation.Nonnull

class RunFolderStep extends RunTestStep{
    /**
    * Defines the default {@link ScanMode}.
    */
    protected static final ScanMode DEFAULT_SCANMODE = ScanMode.PACKAGES_AND_PROJECTS;
    // Scan settings
    @Nonnull
    private ScanMode scanMode = DEFAULT_SCANMODE;
    private boolean recursiveScan;
    private boolean failFast = true;
    // Test settings
    @Nonnull
    private PackageConfig packageConfig
    @Nonnull
    private AnalysisConfig analysisConfig

    @DataBoundConstructor
    RunFolderStep(String testCasePath) {
        super(testCasePath)
        this.packageConfig = new PackageConfig([])
        this.analysisConfig = new AnalysisConfig()
    }

    @Nonnull
    ScanMode getScanMode() {
        return scanMode;
    }

    @DataBoundSetter
    void setScanMode(@Nonnull final ScanMode scanMode) {
        this.scanMode = scanMode;
    }

    boolean isRecursiveScan() {
        return recursiveScan;
    }

    @DataBoundSetter
    void setRecursiveScan(final boolean recursiveScan) {
        this.recursiveScan = recursiveScan;
    }

    boolean isFailFast() {
        return failFast;
    }

    @DataBoundSetter
    void setFailFast(final boolean failFast) {
        this.failFast = failFast;
    }

    @Nonnull
    PackageConfig getPackageConfig() {
        return packageConfig
    }

    @DataBoundSetter
    void setPackageConfig(PackageConfig packageConfig) {
        this.packageConfig = packageConfig ?: new PackageConfig([])
    }

    @Nonnull
    AnalysisConfig getAnalysisConfig() {
        return analysisConfig
    }

    @DataBoundSetter
    void setAnalysisConfig(AnalysisConfig analysisConfig) {
        this.analysisConfig = analysisConfig ?: new AnalysisConfig()
    }

    /**
     * Defines the modes to scan the test folder.
     */
    @Override
    StepExecution start(StepContext context) throws Exception {
        return null
    }

    enum ScanMode {
        /**
         * Scan packages only.
         */
        PACKAGES_ONLY,

        /**
         * Scan projects only.
         */
        PROJECTS_ONLY,

        /**
         * Scan both packages and projects.
         */
        PACKAGES_AND_PROJECTS
    }

    @Extension
    static final class DescriptorImpl extends StepDescriptor {

        @Override
        String getFunctionName() {
            'ttRunFolder'
        }

        @Override
        String getDisplayName() {
            '[TT] Run an ECU-TEST test folder'
        }

        static ScanMode getDefaultScanMode() {
            return ScanMode.PACKAGES_AND_PROJECTS;
        }

        ListBoxModel doFillScanModeItems() {
            final ListBoxModel items = new ListBoxModel();
            items.add('Scan for package files only', ScanMode.PACKAGES_ONLY.toString());
            items.add('Scan for project files only', ScanMode.PROJECTS_ONLY.toString());
            items.add('Scan both for package and project files', ScanMode.PACKAGES_AND_PROJECTS.toString());
            return items;
        }

        @Override
        Set<? extends Class<?>> getRequiredContext() {
            return ImmutableSet.of(Launcher.class, Run.class, EnvVars.class, TaskListener.class)
        }

        FormValidation doCheckTestCasePath(@QueryParameter String value) {
            return ValidationUtil.validateParameterizedValue(value, true)
        }
    }
}
