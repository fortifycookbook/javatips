/*
 * Copyright 2016 Micro Focus or one of its affiliates.
 */
package com.fortify.sca.plugins.maven.handler;

import java.util.ArrayList;
import java.util.List;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.cli.Commandline;
import com.fortify.sca.plugins.maven.util.*;

public class JarPackagingHandler extends AbstractJavaPackagingHandler {

    JarPackagingHandler(MavenSession session, Log log) {
        super(session, log);
    }

    protected void applyPluginMainConfig(List<String> options) {
        if (options.indexOf("-source") == -1) {
            MavenProject currentProject = session.getCurrentProject();
            // Respect maven-compiler-plugin configuration
            Plugin plugin = currentProject.getPlugin("org.apache.maven.plugins:maven-compiler-plugin");
            String source = PluginUtil.getExecutionConfig(plugin, "default-compile", "source");
            if (source != null) {
                options.add("-source");
                options.add(source);
            }
        }

        if (options.indexOf("-encoding") == -1) {
            MavenProject currentProject = session.getCurrentProject();
            // Respect maven-compiler-plugin configuration
            Plugin plugin = currentProject.getPlugin("org.apache.maven.plugins:maven-compiler-plugin");
            String encoding = PluginUtil.getExecutionConfig(plugin, "default-compile", "encoding");
            if (encoding != null) {
                options.add("-encoding");
                options.add(encoding);
            }
        }
    }

    protected boolean addMainFiles(Commandline commandline) {
        boolean added = false;
        MavenProject currentProject = session.getCurrentProject();
        // main java source files
        List<String> sourceDirs = new ArrayList<>();
        for (String sourceDirPath : currentProject.getCompileSourceRoots()) {
            if (FileUtil.containsFile(sourceDirPath)) {
                commandline.createArg().setLine(FileUtil.normalizeFilePath(sourceDirPath));
                added = true;
                sourceDirs.add(sourceDirPath);
            }
        }

        for (String sourceDir : sourceDirs) {
            log.info("Source File Path: " + sourceDir);
        }

        // main script files
        List<String> scriptSourceDirs = new ArrayList<>();
        String scriptDirPath = currentProject.getBuild().getScriptSourceDirectory();
        if (FileUtil.containsFile(scriptDirPath)) {
            commandline.createArg().setLine(FileUtil.normalizeFilePath(scriptDirPath));
            added = true;
            scriptSourceDirs.add(scriptDirPath);
        }

        for (String scriptDir : scriptSourceDirs) {
            log.info("Script File Path: " + scriptDir);
        }

        // main resource files
        List<String> resourceDirs = new ArrayList<>();
        for (String resourceFilePath : getResourcesAsStringList(currentProject.getResources())) {
            if (FileUtil.containsFile(resourceFilePath)) {
                commandline.createArg().setLine(FileUtil.normalizeFilePath(resourceFilePath));
                added = true;
                resourceDirs.add(resourceFilePath);
            }
        }

        for (String resourceDir : resourceDirs) {
            log.info("Resources: " + resourceDir);
        }

        return added;
    }

    protected void applyPluginTestConfig(List<String> options) {
        if (options.indexOf("-source") == -1) {
            MavenProject currentProject = session.getCurrentProject();
            // Respect maven-compiler-plugin configuration
            Plugin plugin = currentProject.getPlugin("org.apache.maven.plugins:maven-compiler-plugin");
            String source = PluginUtil.getExecutionConfig(plugin, "default-test-compile", "source");
            if (source != null) {
                options.add("-source");
                options.add(source);
            }
        }

        if (options.indexOf("-encoding") == -1) {
            MavenProject currentProject = session.getCurrentProject();
            // Respect maven-compiler-plugin configuration
            Plugin plugin = currentProject.getPlugin("org.apache.maven.plugins:maven-compiler-plugin");
            String encoding = PluginUtil.getExecutionConfig(plugin, "default-test-compile", "encoding");
            if (encoding != null) {
                options.add("-encoding");
                options.add(encoding);
            }
        }
    }

    protected boolean addTestFiles(Commandline commandline) {
        boolean added = false;
        MavenProject currentProject = session.getCurrentProject();
        // test java source files

        List<String> sourceDirs = new ArrayList<>();
        for (String sourceDirPath : currentProject.getTestCompileSourceRoots()) {
            if (FileUtil.containsFile(sourceDirPath)) {
                commandline.createArg().setLine(FileUtil.normalizeFilePath(sourceDirPath));
                added = true;
                sourceDirs.add(sourceDirPath);
            }
        }

        for (String sourceDir : sourceDirs) {
            log.info("Test Source File Path: " + sourceDir);
        }

        // test resource files
        List<String> resourceDirs = new ArrayList<>();
        for (String resourceFilePath : getResourcesAsStringList(currentProject.getTestResources())){
            if (FileUtil.containsFile(resourceFilePath)) {
                commandline.createArg().setLine(FileUtil.normalizeFilePath(resourceFilePath));
                added = true;
            }
        }

        for (String resourceDir : resourceDirs) {
            log.info("Test Resources: " + resourceDir);
        }

        return added;
    }
}
