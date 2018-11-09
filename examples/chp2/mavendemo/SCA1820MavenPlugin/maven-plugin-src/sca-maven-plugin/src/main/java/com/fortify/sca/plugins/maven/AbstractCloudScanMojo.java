/*
 * Copyright 2016 Micro Focus or one of its affiliates.
 */
package com.fortify.sca.plugins.maven;

import org.apache.maven.plugins.annotations.Parameter;

public abstract class AbstractCloudScanMojo extends AbstractFortifyClientMojo {
    //////////////////////////////////////////////////////////////////////
    // CLOUDSCAN COMPONENT
    //////////////////////////////////////////////////////////////////////
    /**
     * Location of the CloudScan client executable. Defaults to cloudscan
     * which runs the version on the PATH or fails if none exists.
     */
    @Parameter(property = "fortify.cloudScan.executable", defaultValue = "cloudscan")
    protected String cloudscan;

    //////////////////////////////////////////////////////////////////////
    // CLOUDSCAN GLOBAL OPTIONS
    //////////////////////////////////////////////////////////////////////
    /**
     * Specifies the URL of the CloudScan controller.
     */
    @Parameter(property = "fortify.cloudScan.cloudCtrl.url")
    protected String cloudCtrlUrl;

    /**
     * Specifies the SSC URL to interact with CloudScan controller. To enable SSC Integrated Mode,
     * uploadToSSC must be true. If sscUrl is specified, sscCloudCtrlToken must also be specified.
     */
    @Parameter(property = "fortify.cloudScan.ssc.url")
    protected String sscUrl;

    /**
     * Specifies the SSC CloudCtrlToken to use when attempting to interact with the CloudScan controller.
     * To enable SSC Integrated Mode, uploadToSSC must be true.
     */
    @Parameter(property = "fortify.cloudScan.ssc.cloudCtrlToken")
    protected String sscCloudCtrlToken;

    //////////////////////////////////////////////////////////////////////
    // MAVEN PLUGIN OPTIONS
    //////////////////////////////////////////////////////////////////////
    /**
     * If set to true, the build fails if errors occur during any CloudScan processing.
     */
    @Parameter(property = "fortify.cloudScan.failOnError", defaultValue = "false")
    protected boolean cloudscanFailOnError;
}
