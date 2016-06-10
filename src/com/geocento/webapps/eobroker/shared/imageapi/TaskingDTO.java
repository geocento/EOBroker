package com.geocento.webapps.eobroker.shared.imageapi;

/**
 * Created by thomas on 08/03/2016.
 */
public class TaskingDTO {

    Long modeId;
    Long start;
    Long stop;
    Integer orbit;
    String orbitDirection;
    String ascendingNodeDate;
    String startTimeFromAscendingNode;
    String completionTimeFromAscendingNodeDate;
    private String coordinatesWKT;
    private int frameNumbers;

    public TaskingDTO() {
    }

    public Long getModeId() {
        return modeId;
    }

    public void setModeId(Long modeId) {
        this.modeId = modeId;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getStop() {
        return stop;
    }

    public void setStop(Long stop) {
        this.stop = stop;
    }

    public Integer getOrbit() {
        return orbit;
    }

    public void setOrbit(Integer orbit) {
        this.orbit = orbit;
    }

    public String getOrbitDirection() {
        return orbitDirection;
    }

    public void setOrbitDirection(String orbitDirection) {
        this.orbitDirection = orbitDirection;
    }

    public String getAscendingNodeDate() {
        return ascendingNodeDate;
    }

    public void setAscendingNodeDate(String ascendingNodeDate) {
        this.ascendingNodeDate = ascendingNodeDate;
    }

    public String getStartTimeFromAscendingNode() {
        return startTimeFromAscendingNode;
    }

    public void setStartTimeFromAscendingNode(String startTimeFromAscendingNode) {
        this.startTimeFromAscendingNode = startTimeFromAscendingNode;
    }

    public String getCompletionTimeFromAscendingNodeDate() {
        return completionTimeFromAscendingNodeDate;
    }

    public void setCompletionTimeFromAscendingNodeDate(String completionTimeFromAscendingNodeDate) {
        this.completionTimeFromAscendingNodeDate = completionTimeFromAscendingNodeDate;
    }

    public String getCoordinatesWKT() {
        return coordinatesWKT;
    }

    public void setCoordinatesWKT(String coordinatesWKT) {
        this.coordinatesWKT = coordinatesWKT;
    }

    public int getFrameNumbers() {
        return frameNumbers;
    }

    public void setFrameNumbers(int frameNumbers) {
        this.frameNumbers = frameNumbers;
    }
}
