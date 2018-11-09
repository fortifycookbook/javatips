/*
 * Copyright 2016 Micro Focus or one of its affiliates.
 */
package com.fortify.sca.plugins.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.CommandLineException;
import com.fortify.sca.plugins.maven.util.*;

/**
 * Performs the Cloudscan for ther specified build ID or mobile build session.
 */
@Mojo(name = "startCloudscan", defaultPhase = LifecyclePhase.INTEGRATION_TEST)
public class StartCloudscanMojo extends AbstractCloudScanMojo {
    //////////////////////////////////////////////////////////////////////
    // CLOUDSCAN START OPTIONS
    //////////////////////////////////////////////////////////////////////
    /**
     * Specifies the SCA build ID. The default is project artifact ID and version. In aggregate mode, the top level project
     * artifact ID and version is used for all modules.
     */
    @Parameter(property = "fortify.cloudScan.buildId", defaultValue = "${project.artifactId}-${project.version}")
    private String buildId;

    /**
     * Location and name of the existing mobile build session to be uploaded to the CloudScan controller.
     */
    @Parameter(property = "fortify.cloudScan.buildSessionFile")
    private String buildSessionFile;

    /**
     * Specifies project directory for the mobile build session export.
     */
    @Parameter(property = "fortify.cloudScan.buildSessionProjectRoot")
    private String buildSessionProjectRoot;

    /**
     * Specifies the location and name of the FPR file to retrieve.
     */
    @Parameter(property = "fortify.cloudScan.resultsFile")
    private String resultsFile;

    /**
     * If set to true, CloudScan client waits for job to complete and download the results.
     */
    @Parameter(property = "fortify.cloudScan.enableBlock", defaultValue = "false")
    private boolean enableBlock;

    /**
     * If set to true, the existing FPR or log file might be overwritten with new data.
     */
    @Parameter(property = "fortify.cloudScan.overwrite", defaultValue = "false")
    private boolean overwrite;

    /**
     * Specifies an email address for job status notification.
     */
    @Parameter(property = "fortify.cloudScan.email")
    private String emailAddress;

    /**
     * Specifies the location and name of the filter file to use during the scan (repeatable).
     */
    @Parameter(property = "fortify.cloudScan.filter")
    private String filter;

    /**
     * Specifies the custom rule file/directory to use.
     */
    @Parameter(property = "fortify.cloudScan.rules")
    private String rules;

    /**
     * Specifies the location and name of the log file produced by the CloudScan client.
     */
    @Parameter(property = "fortify.cloudScan.startLogfile", defaultValue = "cloudscan-start.log")
    private String logFile;

    //////////////////////////////////////////////////////////////////////
    // CLOUDSCAN SSC OPTIONS
    //////////////////////////////////////////////////////////////////////
    /**
     * If set to true, the CloudScan controller uploads the analysis results to SSC.
     * Note that this must be supplied in conjunction with sscApplicationVersionId,
     * or sscApplicationName and sscApplicationVersion combinations.
     */
    @Parameter(property = "fortify.cloudScan.uploadToSSC", defaultValue = "false")
    private boolean uploadToSSC;

    /**
     * AnalysisUploadToken to use when attempting to upload fpr files to SSC.
     */
    @Parameter(property = "fortify.cloudScan.ssc.uploadToken")
    private String sscUploadToken;

    /**
     * Specifies Issue Template to be applied to the CloudScan.
     */
    @Parameter(property = "fortify.cloudScan.issueTemplate", alias = "fortify.cloudScan.projectTemplate")
    private String issueTemplate;

    //////////////////////////////////////////////////////////////////////
    // SCA JVM OPTIONS
    //////////////////////////////////////////////////////////////////////
    /**
     * Specifies the maximum heap size of the JVM that runs SCA.
     */
    @Parameter(property = "fortify.cloudScan.sca.Xmx", alias = "fortify.cloudScan.sca.jvm.Xmx")
    private String maxHeap;

    /**
     * Specifies the thread stack size of JVM that runs SCA.
     */
    @Parameter(property = "fortify.cloudScan.sca.Xss", alias = "fortify.cloudScan.sca.jvm.Xss")
    private String stackSize;

    //////////////////////////////////////////////////////////////////////
    // SCA OPTIONS
    //////////////////////////////////////////////////////////////////////
    /**
     * If set to true, SCA runs in debug mode. This can be useful for troubleshooting.
     */
    @Parameter(property = "fortify.cloudScan.sca.debug", defaultValue = "false")
    private boolean debug;

    /**
     * IIf set to true, SCA sends verbose status messages to the console.
     */
    @Parameter(property = "fortify.cloudScan.sca.verbose", defaultValue = "false")
    private boolean verbose;

    /**
     * If set true, SCA writes minimal message to the console.
     */
    @Parameter(property = "fortify.cloudScan.sca.quiet", defaultValue = "false")
    private boolean quiet;

    /**
     * If set to true, SCA prints the version to the console.
     */
    @Parameter(property = "fortify.cloudScan.sca.version", defaultValue = "true")
    private boolean version;

    /**
     * Build Label inserted in to generated FPR.
     */
    @Parameter(property = "fortify.cloudScan.sca.buildLabel", defaultValue = "${project.artifactId}-${project.version}")
    private String buildLabel;

    /**
     * Build Project Name inserted in to generated FPR.
     */
    @Parameter(property = "fortify.cloudScan.sca.buildProject", defaultValue = "${project.artifactId}")
    private String buildProject;

    /**
     * Build Version inserted in to generated FPR.
     */
    @Parameter(property = "fortify.cloudScan.sca.buildVersion", defaultValue = "${project.version}")
    private String buildVersion;

    /**
     * If set to true, source files are included in the FPR file.
     */
    @Parameter(property = "fortify.cloudScan.sca.renderSources", defaultValue = "true")
    private boolean renderSources;

    /**
     * If set to true, scans the project in Quick Scan Mode (uses the fortifysca-quickscan.properties file).
     * By default, this scan only searches for high-confidence, high-severity issues.
     */
    @Parameter(property = "fortify.cloudScan.sca.quickScan", defaultValue = "false")
    private boolean quickScan;

    /**
     * Specify the number of worker threads for parallel analysis.
     */
    @Parameter(property = "fortify.cloudScan.sca.numOfWorkerThreads")
    private Integer numOfWorkerThreads;

    /**
     * If set to true, the build fails if errors occur during the CloudScan start.
     */
    @Parameter(property = "fortify.cloudScan.start.failOnError", defaultValue = "false")
    private boolean cloudscanStartFailOnError;

    private String mavenProjectName;

    private String mavenProjectVersion;

    public void doExecute() throws MojoExecutionException {
        if (aggregate) {
            MavenProject topLevelProject = session.getTopLevelProject();

            workingDirectory = topLevelProject.getBasedir();
            outputDirectory = new File(topLevelProject.getBuild().getDirectory(), FORTIFY_DIR);

            mavenProjectName = topLevelProject.getArtifactId();
            mavenProjectVersion = topLevelProject.getVersion();

            if (StringUtil.isEmpty(resultsFile)) {
                resultsFile = topLevelProject.getArtifactId() + "-" + topLevelProject.getVersion() + ".fpr";
            }

            if (isFirstProject(project) && isLastProject(project)) {
                startCloudscan();
            } else if (isFirstProject(project) && !isLastProject(project)) {
                session.getUserProperties().setProperty(BUILD_ID_KEY, buildId);
                log.info("Skipping to cloud scan in aggregate mode");
            } else if (!isFirstProject(project) && isLastProject(project)) {
                buildId = session.getUserProperties().getProperty(BUILD_ID_KEY);
                startCloudscan();
            } else {
                log.info("Skipping to cloud scan in aggregate mode");
            }
        } else {
            workingDirectory = project.getBasedir();
            outputDirectory = new File(project.getBuild().getDirectory(), FORTIFY_DIR);

            mavenProjectName = project.getArtifactId();
            mavenProjectVersion = project.getVersion();

            String defaultBuildId = project.getArtifactId() + "-" + project.getVersion();
            if (!defaultBuildId.equals(buildId)) {
                log.warn("Specified build Id :" + buildId + " cannot by applied in individual mode");
                buildId = defaultBuildId;
            }

            if (StringUtil.isEmpty(resultsFile)) {
                resultsFile = project.getArtifactId() + "-" + project.getVersion() + ".fpr";
            }
            startCloudscan();
        }
    }

    public void startCloudscan() throws MojoExecutionException {
        Commandline commandline = new Commandline();

        commandline.setExecutable(cloudscan);
        commandline.setWorkingDirectory(workingDirectory);
        try {
            validateParameters();
            commandline.addSystemEnvironment();

            for (String option : buildOptions()) {
                commandline.createArg().setLine(option);
            }

            log.info("Starting Cloudscan...");
            log.info("Build ID: " + buildId);
            log.info("CloudScan URL: " + cloudCtrlUrl);
            log.info("Upload to SSC: " + uploadToSSC);
            if (uploadToSSC) {
                log.info("SSC URL: " + sscUrl);
                log.info("Application Version Id: " + sscApplicationVersionId != null ? sscApplicationVersionId : "N/A");
                log.info("Application Name: " + sscApplicationName != null ? sscApplicationName : "N/A");
                log.info("Application Version: " + sscApplicationVersion != null ? sscApplicationVersion : "N/A");
            }

            executeCommand(commandline);
        } catch (IllegalArgumentException e) {
            handleException(e);
        } catch (CommandLineException e) {
            handleException(e);
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void handleException(Exception e) throws MojoExecutionException {
        if (cloudscanFailOnError || cloudscanStartFailOnError) {
            throw new MojoExecutionException(e.getMessage(), e);
        } else {
            log.error(e.getMessage());
        }
    }

    private void validateParameters() throws CommandLineException, IllegalArgumentException {
        if (uploadToSSC) {
            if (StringUtil.isEmpty(sscUrl)) {
                throw new IllegalArgumentException("'sscUrl' is not specified.");
            }
            if (StringUtil.isEmpty(sscCloudCtrlToken)) {
                throw new IllegalArgumentException("'sscCloudCtrlToken' is not specified.");
            }
            if (StringUtil.isEmpty(sscUploadToken)) {
                throw new IllegalArgumentException("'sscUploadToken' is not specified.");
            }

            if (StringUtil.isEmpty(sscApplicationVersionId) && !StringUtil.isEmpty(sscProjectVersionId)) {
                log.warn("Use 'sscApplicationVersionId' instead of 'sscProjectVersionId'");
                sscApplicationVersionId = sscProjectVersionId;
            }

            if (StringUtil.isEmpty(sscApplicationName) && !StringUtil.isEmpty(sscProjectName)) {
                log.warn("Use 'sscApplicationName' instead of 'sscProjectName'");
                sscApplicationName = sscProjectName;
            }

            if (StringUtil.isEmpty(sscApplicationVersion) && !StringUtil.isEmpty(sscProjectVersion)) {
                log.warn("Use 'sscApplicationName' instead of 'sscProjectName'");
                sscApplicationVersion = sscProjectVersion;
            }

            if (StringUtil.isEmpty(sscApplicationVersionId) && (StringUtil.isEmpty(sscApplicationName) || StringUtil.isEmpty(sscApplicationVersion))) {
                log.warn("'sscProjectName', 'sscProjectVersion' and 'sscProjectVersionId' are not specified.");
                String message = "Using " + mavenProjectName + " - " + mavenProjectVersion + " to try to get SSC Application Version Id";
                log.warn(message);
                sscApplicationVersionId = getApplicationVersionId(sscUrl, sscUploadToken, mavenProjectName, mavenProjectVersion);
            }
        } else {
            if (StringUtil.isEmpty(this.cloudCtrlUrl)) {
                StringBuffer errMsg = new StringBuffer();
                errMsg.append("CloudScan controller URL is not specified.");
                if (!StringUtil.isEmpty(sscUrl) && !StringUtil.isEmpty(sscCloudCtrlToken)) {
                    errMsg.append(" When you want to upload analysis result to SSC, you need to set fortify.cloudScan.uploadToSSC to true.");
                }
                throw new IllegalArgumentException(errMsg.toString());
            }
        }

        if (StringUtil.isEmpty(buildId) && StringUtil.isEmpty(buildSessionFile)) {
            throw new IllegalArgumentException("Both 'buildId' and 'buildSessionFile' are not specified.");
        }

        if (!StringUtil.isEmpty(buildSessionFile) && StringUtil.isEmpty(buildSessionProjectRoot)) {
            throw new IllegalArgumentException("'buildSessionFile' must be supplied in conjunction with 'buildSessionProjectRoot'.");
        }

        if (StringUtil.isEmpty(buildSessionFile) && !StringUtil.isEmpty(buildSessionProjectRoot)) {
            throw new IllegalArgumentException("'buildSessionProjectRoot' must be supplied in conjunction with 'buildSessionFile'.");
        }
    }

    protected List<String> buildOptions() {
        List<String> options = new ArrayList<String>();

        addCloudscanStartOptions(options);
        addSCAScanOptions(options);

        return options;
    }

    private void addCloudscanStartOptions(List<String> options) {
        if (uploadToSSC) {
            OptionUtil.setOption(options, "-sscurl", sscUrl);
            OptionUtil.setOption(options, "-ssctoken", sscCloudCtrlToken);
            OptionUtil.setSwitchOption(options, "start", true);
            OptionUtil.setSwitchOption(options, "-upload", true);
            if (!StringUtil.isEmpty(sscApplicationVersionId)) {
                OptionUtil.setOption(options, "-versionid", sscApplicationVersionId);
            } else {
                OptionUtil.setOption(options, "-project", StringUtil.encloseQuotes(sscApplicationName));
                OptionUtil.setOption(options, "-versionname", sscApplicationVersion);
            }
            OptionUtil.setOption(options, "-uptoken", sscUploadToken);
        } else {
            OptionUtil.setOption(options, "-url", cloudCtrlUrl);
            OptionUtil.setSwitchOption(options, "start", true);
        }

        if (!StringUtil.isEmpty(buildSessionFile)) {
            OptionUtil.setOption(options, "-mbs", FileUtil.normalizeFilePath(buildSessionFile));
            OptionUtil.setOption(options, "-projroot", FileUtil.normalizeFilePath(buildSessionProjectRoot));
        } else {
            OptionUtil.setOption(options, "-b", StringUtil.encloseQuotes(buildId));
        }
        OptionUtil.setSwitchOption(options, "-block", enableBlock);
        OptionUtil.setOption(options, "-email", StringUtil.encloseQuotes(emailAddress));
        OptionUtil.setOption(options, "-f", FileUtil.normalizeFilePath(resultsFile));
        OptionUtil.setOption(options, "-filter", FileUtil.normalizeFilePath(filter));
        OptionUtil.setOption(options, "-log", FileUtil.normalizeFilePath(logFile));
        OptionUtil.setSwitchOption(options, "-o", overwrite);
        OptionUtil.setOption(options, "-projtl", FileUtil.normalizeFilePath(issueTemplate));
        OptionUtil.setOption(options, "-rules", FileUtil.normalizeFilePath(rules));

        // Set -scan : as delimiter
        OptionUtil.setSwitchOption(options, "-scan", true);
    }

    private void addSCAScanOptions(List<String> options) {

        OptionUtil.setJvmOption(options, "-Xmx", maxHeap);
        OptionUtil.setJvmOption(options, "-Xss", stackSize);

        OptionUtil.setSwitchOption(options, "-debug", debug);
        OptionUtil.setSwitchOption(options, "-verbose", verbose);
        OptionUtil.setSwitchOption(options, "-quiet", quiet);
        OptionUtil.setSwitchOption(options, "-version", version);

        OptionUtil.setOption(options, "-build-label", buildLabel);
        OptionUtil.setOption(options, "-build-project", buildProject);
        OptionUtil.setOption(options, "-build-version", buildVersion);

        OptionUtil.setOption(options, "-j", numOfWorkerThreads);

        OptionUtil.setSwitchOption(options, "-disable-source-bundling", !renderSources);
        OptionUtil.setSwitchOption(options, "-quick", quickScan);
    }
}
