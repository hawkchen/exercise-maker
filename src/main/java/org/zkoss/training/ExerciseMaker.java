package org.zkoss.training;

import org.slf4j.*;

import java.io.*;
import java.util.LinkedList;

/**
 * parse source files to produce exercise files.
 */
public class ExerciseMaker {
    private Logger logger = LoggerFactory.getLogger(ExerciseMaker.class);
    private File sourceDirectory;
    private File outputDirectory;

    public ExerciseMaker(File sourceDirectory, File outputDirectory){
        this.sourceDirectory = sourceDirectory;
        this.outputDirectory = outputDirectory;
    }

    /**
     * iterate files under {@link ExerciseMaker#sourceDirectory} to produce exercise files.
     */
    public void make(){
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
                        logger.error("An error happens during making exercises: " + e);
                    }
                }
            }
        }
    }

    /**
     * Make an exercise file based on a source file. It always overwrite existing exercise files.
     * @param sourceFile a file that might contain an exercise mark
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void makeExerciseFile(File sourceFile) throws FileNotFoundException, IOException {
        logger.debug("processing " + sourceFile.getAbsolutePath());
        BufferedReader sourceReader = new BufferedReader(new FileReader(sourceFile));
        StringBuilder exerciseFileBuffer = new StringBuilder();
        String line = null;
        int markerCounter = 0; // the total count of checked exercise markers
        while ((line = sourceReader.readLine()) != null) {
            try {
                exerciseFileBuffer.append(line + System.lineSeparator()); //readLine() doesn't include a line separator
                if (ExerciseMark.isValidMark(line)) {
                    removeLinesUponMark(sourceReader, exerciseFileBuffer, line);
                    markerCounter++;
                }
            }catch (Exception e){
                //ignore any syntax error in marker
                logger.warn("exercise mark parsing error in "+ sourceFile.getName()+" for "+e.getMessage());
            }
        }
        exerciseFileBuffer.deleteCharAt(exerciseFileBuffer.length()-1); //delete the last line separator
        sourceReader.close();

        if (markerCounter > 0) {
            logger.info("found " + markerCounter +
                    " exercise marks in " + sourceFile.getAbsolutePath());
            writeExerciseFile(sourceFile, exerciseFileBuffer);
        }
    }

    private void removeLinesUponMark(BufferedReader reader, StringBuilder exerciseFileBuffer, String line) throws IOException {
        ExerciseMark exerciseMark = ExerciseMark.parse(line);
        int lineNumber = 1;
        String lineToDel = null;
        while ( lineNumber <= exerciseMark.getEnd() && (lineToDel = reader.readLine()) != null) {
            if (lineNumber >= exerciseMark.getStart()) { //drop this line
                logger.debug("remove " + lineToDel);
            } else {
                exerciseFileBuffer.append(lineToDel + System.lineSeparator());
            }
            lineNumber++;
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
}
