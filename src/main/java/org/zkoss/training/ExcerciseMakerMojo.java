package org.zkoss.training;

import org.apache.maven.plugin.*;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.*;

import java.io.File;

/**
 * make (default goal): make exercise files
 */
@Mojo(name = ExcerciseMakerMojo.DEFAULT_GOAL, defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class ExcerciseMakerMojo extends AbstractMojo {
    public static final String EXERCISE_OUTPUT_PATH = "/src/exercise";
    public static final String DEFAULT_GOAL = "make";

    /**
     * source folder to be processed
     */
    @Parameter(defaultValue = "${project.basedir}" + "/src/main", property = "sourceDirectory", required = true)
    private File sourceDirectory;
    
    /**
     * the folder to store resulting files for exercise
     */
    @Parameter(defaultValue = "${project.basedir}" + EXERCISE_OUTPUT_PATH, property = "outputDirectory", required = true)
    private File outputDirectory;

    private String version = "1.0.0";

    public void execute() throws MojoExecutionException {
        getLog().info("process " + sourceDirectory.toString());
        getLog().info("output to " + outputDirectory.toString());
        new ExerciseMaker(sourceDirectory, outputDirectory).make();
    }

    public File getSourceDirectory() {
        return sourceDirectory;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    //if no <configuration> exists, maven sets "version" property
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
