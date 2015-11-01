package com.metapatrol.gitlab.ci.runner.fs;

import com.metapatrol.gitlab.ci.runner.Launcher;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class FileSystem {

    private File $HOME;
    private File configurationDirectory;
    private File configurationFile;
    private File buildDirectory;
    private File sourceDirectory;
    private File logDirectory;

    private static FileSystem INSTANCE;

    private FileSystem(){}

    public static FileSystem getInstance(){
        if(INSTANCE == null){
            INSTANCE = new FileSystem();

            try {
                INSTANCE.setUp();
            }catch(IOException e){
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        return INSTANCE;
    }

    protected void setUp() throws IOException {
        $HOME = new File(new File(Launcher.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile().getCanonicalPath());
        logDirectory = new File($HOME, "/logs");
        logDirectory.mkdirs();
        configurationDirectory = new File($HOME, "/config");
        configurationDirectory.mkdirs();
        configurationFile = new File(configurationDirectory, "/runner.json");
        if(!configurationFile.exists()){
            configurationFile.createNewFile();
        }
        buildDirectory = new File($HOME, "/builds");
        buildDirectory.mkdirs();
        sourceDirectory = new File($HOME, "/source");
        sourceDirectory.mkdirs();
    }

    public File get$HOME() {
        return $HOME;
    }

    public File getConfigurationFile() {
        return configurationFile;
    }

    public File getBuildDirectory() {
        return buildDirectory;
    }

    public File getConfigurationDirectory() {
        return configurationDirectory;
    }

    public File getProjectBuildDirectory(String projectName){
        File base = new File(getBuildDirectory(), projectName);
        base.mkdirs();
        return base;
    }

    public File getProjectBuildShaDirectory(File projectBuildDirectory, String sha) {
        File shaDirectory = new File(projectBuildDirectory, sha);
        shaDirectory.mkdirs();
        return shaDirectory;
    }

    public File getProjectBuildShaDateDirectory(File projectBuildShaDirectory, Date date) {
        File projectBuildShaDateDirectory = new File(projectBuildShaDirectory, ""+(date.getTime()));
        projectBuildShaDateDirectory.mkdirs();
        return projectBuildShaDateDirectory;
    }

    public File getProjectBuildShaDateAppDirectory(File projectBuildShaDateDirectory) {
        File projectBuildShaDateAppDirectory = new File(projectBuildShaDateDirectory, "/app");
        projectBuildShaDateAppDirectory.mkdirs();
        return projectBuildShaDateAppDirectory;
    }

    public File getProjectGitDirectory(String projectName){
        return new File(sourceDirectory, projectName);
    }

    public File getLogDirectory() {
        return logDirectory;
    }

    public File getSourceDirectory() {
        return sourceDirectory;
    }

    public File getProjectGitShaDirectory(String projectName, String sha) {
        File base = new File(getBuildDirectory(), projectName);
        File shaDirectory = new File(base, sha);
        shaDirectory.mkdirs();
        return shaDirectory;
    }
}
