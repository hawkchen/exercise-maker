package org.zkoss.training;

public class ExerciseMark {
    static final String TODO = "TODO"; //a starting keyword
    protected int start = 1;
    protected int end = 1;


    ExerciseMark(int end){
        this.end = end;
    }

    ExerciseMark(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public static boolean isValidMark(String line){
        return line.contains(TODO) //case sensitive
                && line.split(",").length >= 3; //a hint might contain a comma
    }

    public static ExerciseMark parse(String exerciseMarkString) {
        String rangeString = exerciseMarkString.split(",")[1];
        String range[] = rangeString.trim().split("-");
        if (range.length == 2){ //a range
            return new ExerciseMark(Integer.parseInt(range[0].trim()), Integer.parseInt(range[1].trim()));
        }else{
            return new ExerciseMark(Integer.parseInt(rangeString.trim()));
        }
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
}
