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

    static final String TODO = "TODO"; //a starting keyword

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
     *always overwrite existing exercise files
     * @param sourceFile a file that might contain an exercise mark
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void makeExerciseFile(File sourceFile) throws FileNotFoundException, IOException {
        getLog().debug("processing " + sourceFile.getAbsolutePath());
        //find the marker
        BufferedReader reader = new BufferedReader(new FileReader(sourceFile));
        StringBuilder exerciseContentBuffer = new StringBuilder();
        String line = null;
        boolean anyMarkExisted = false; //if false, ignore a file without any mark
        while ((line = reader.readLine()) != null) {
            try {
                exerciseContentBuffer.append(line + System.lineSeparator()); //readLine() doesn't include a line separator
                if (isValidMark(line)) {
                    anyMarkExisted = true;
                    removeLinesUponMark(reader, exerciseContentBuffer, line);
                }
            }catch (Exception e){
                //ignore any syntax error in marker
                getLog().warn("marker parsing error in "+ sourceFile.getName()+" for "+e.getMessage());
            }
        }
        exerciseContentBuffer.deleteCharAt(exerciseContentBuffer.length()-1); //delete the last line separator
        reader.close();

        if (anyMarkExisted) {
            getLog().info("found TODO marker in " + sourceFile.getAbsolutePath());
            writeExerciseFile(sourceFile, exerciseContentBuffer);
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

    private void removeLinesUponMark(BufferedReader reader, StringBuilder exerciseContentBuffer, String line) throws IOException {
        LineRange lineRange = getLineRange(line.split(",")[1]);
        int lineNumber = 1;
        String lineToDel = null;
        while ( lineNumber <= lineRange.getEnd() && (lineToDel = reader.readLine()) != null) {
            if (lineNumber >= lineRange.getStart()) { //drop this line
                getLog().debug("remove " + lineToDel);
            } else {
                exerciseContentBuffer.append(lineToDel + System.lineSeparator());
            }
            lineNumber++;
        }
    }

    private boolean isValidMark(String line){
        return line.contains(TODO) //case sensitive
                && line.split(",").length >= 3; //a hint might contain a comma
    }

    private LineRange getLineRange(String rangeString) {
        String range[] = rangeString.trim().split("-");
        if (range.length == 2){ //a range
            return new LineRange(Integer.parseInt(range[0].trim()), Integer.parseInt(range[1].trim()));
        }else{
            return new LineRange(Integer.parseInt(rangeString.trim()));
        }
    }

    class LineRange {
        private int start = 1;
        private int end = 1;

        LineRange(int end){
            this.end = end;
        }

        LineRange(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
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
