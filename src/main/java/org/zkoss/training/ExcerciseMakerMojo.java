package org.zkoss.training;

import org.apache.maven.plugin.*;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.*;
import java.util.LinkedList;

/**
 * make (default): make exercise files
 */
@Mojo(name = ExcerciseMakerMojo.DEFAULT_GOAL, defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class ExcerciseMakerMojo extends AbstractMojo {
    public static final String EXERCISE_OUTPUT_PATH = "/src/exercise";
    public static final String DEFAULT_GOAL = "make";

    @Parameter(defaultValue = "${project.basedir}" + "/src/main", property = "sourceDirectory", required = true)
    private File sourceDirectory;
    @Parameter(defaultValue = "${project.basedir}" + EXERCISE_OUTPUT_PATH, property = "outputDirectory", required = true)
    private File outputDirectory;
    private String version = "1.0";

    public void execute() throws MojoExecutionException {
        outputDirectory.mkdirs();
        LinkedList<File> directoryQueue = new LinkedList<File>();
        directoryQueue.add(sourceDirectory);

        while (!directoryQueue.isEmpty()) {
            File file = directoryQueue.removeFirst();
            for (File childFile : file.listFiles()) {
                if (childFile.isDirectory()) {
                    directoryQueue.add(childFile);
                } else {
                    try {
                        makeExerciseFile(childFile);
                    } catch (Exception e) {
                        getLog().error(e);
                    }
                }
            }
        }
    }

    /**
     * always overwrite existing exercise files
     * @param sourceFile a file that might contain an exercise mark
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void makeExerciseFile(File sourceFile) throws FileNotFoundException, IOException {
        getLog().debug("processing " + sourceFile.getAbsolutePath());
        //find the marker
        BufferedReader reader = new BufferedReader(new FileReader(sourceFile));
        StringBuilder exerciseFileBuffer = new StringBuilder();
        String line = null;
        boolean anyMarkExisted = false; //if false, ignore a file without any mark
        while ((line = reader.readLine()) != null) {
            try {
                exerciseFileBuffer.append(line + System.lineSeparator()); //readLine() doesn't include a line separator
                if (ExerciseMark.isValidMark(line)) {
                    anyMarkExisted = true;
                    removeLinesUponMark(reader, exerciseFileBuffer, line);
                }
            }catch (Exception e){
                //ignore any syntax error in marker
                getLog().warn("exercise mark parsing error in "+ sourceFile.getName()+" for "+e.getMessage());
            }
        }
        exerciseFileBuffer.deleteCharAt(exerciseFileBuffer.length()-1); //delete the last line separator
        reader.close();

        if (anyMarkExisted) {
            getLog().info("found exercise mark in " + sourceFile.getAbsolutePath());
            writeExerciseFile(sourceFile, exerciseFileBuffer);
        }
     }

    private void writeExerciseFile(File sourceFile, StringBuilder exerciseContentBuffer) throws IOException {
        File exercisePath = new File(outputDirectory,
                sourceFile.getPath().substring(0, sourceFile.getPath().lastIndexOf(File.separator)).replace(sourceDirectory.getPath(), ""));
        if (!exercisePath.exists()){
            exercisePath.mkdirs();
        }
        File exerciseFile = new File(outputDirectory, sourceFile.getPath().replace(sourceDirectory.getPath(), ""));
        exerciseFile.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(exerciseFile));
        writer.write(exerciseContentBuffer.toString());
        writer.close();
    }

    private void removeLinesUponMark(BufferedReader reader, StringBuilder exerciseFileBuffer, String exerciseMarkString) throws IOException {
        ExerciseMark exerciseMark = ExerciseMark.parse(exerciseMarkString);
        int lineNumber = 1;
        String lineToDel = null;
        while ( lineNumber <= exerciseMark.getEnd() && (lineToDel = reader.readLine()) != null) {
            if (lineNumber >= exerciseMark.getStart()) { //drop this line
                getLog().debug("remove " + lineToDel);
            } else {
                exerciseFileBuffer.append(lineToDel + System.lineSeparator());
            }
            lineNumber++;
        }
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
