/*
 * Copyright 2016 Micro Focus or one of its affiliates.
 */
package com.fortify.sca.plugins.maven.handler;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;
import com.fortify.sca.plugins.maven.util.*;

public abstract class AbstractJavaPackagingHandler extends AbstractPackagingHandler {


    AbstractJavaPackagingHandler(MavenSession session, Log log) {
        super(session, log);
    }

    public Commandline buildMainCommand(String sourceanalyzer, List<String> options) throws CommandLineException {
        applyPluginMainConfig(options);
        Commandline commandline = buildBaseCommand(sourceanalyzer, options, false);

        boolean added = addMainFiles(commandline);

        if (added) {
            return commandline;
        } else {
            return null;
        }
    }

    public Commandline buildTestCommand(String sourceanalyzer, List<String> options) throws CommandLineException {
        applyPluginTestConfig(options);
        Commandline commandline = buildBaseCommand(sourceanalyzer, options, true);

        boolean added = addTestFiles(commandline);

        if (added) {
            return commandline;
        } else {
            return null;
        }
    }

    protected void applyPluginMainConfig(List<String> options) {

    }

    protected void applyPluginTestConfig(List<String> options) {

    }

    protected abstract boolean addMainFiles(Commandline commandline);

    protected abstract boolean addTestFiles(Commandline commandline);

    protected Commandline buildBaseCommand(String sourceanalyzer, List<String> options, boolean isTest) throws CommandLineException {
        MavenProject currentProject = session.getCurrentProject();
        Commandline commandline = new Commandline();
        commandline.setExecutable(sourceanalyzer);
        commandline.setWorkingDirectory(currentProject.getBasedir());
        try {
            commandline.addSystemEnvironment();
            // Set SCA Translation options
            for (String option : options) {
                commandline.createArg().setLine(option);
            }

            Set<String> classpathElements = new LinkedHashSet<String>();

            for (String classpath : currentProject.getCompileClasspathElements()) {
                if (FileUtil.containsClass(classpath)) {
                    classpathElements.add(classpath);
                }
            }

            ArtifactRepository localRepository = session.getLocalRepository();
            for (Artifact artifact : currentProject.getArtifacts()){
                if (isTest) {
                    if (artifact.getScope().equals("test")) {
                        File jarFile = new File(localRepository.getBasedir(), localRepository.pathOf(artifact));
                        if (FileUtil.containsClass(jarFile)) {
                            classpathElements.add(jarFile.getAbsolutePath());
                        }
                    }
                } else {
                    if (!artifact.getScope().equals("test")) {
                        File jarFile = new File(localRepository.getBasedir(), localRepository.pathOf(artifact));
                        if (FileUtil.containsClass(jarFile)) {
                            classpathElements.add(jarFile.getAbsolutePath());
                        }
                    }
                }
            }

            for (String cp : classpathElements) {
                commandline.createArg().setLine("-cp");
                commandline.createArg().setLine(FileUtil.normalizeFilePath(cp));
            }
        } catch (DependencyResolutionRequiredException e) {
            throw new CommandLineException(e.getMessage(), e);
        } catch (Exception e) {
            throw new CommandLineException(e.getMessage());
        }
        return commandline;
    }
}
