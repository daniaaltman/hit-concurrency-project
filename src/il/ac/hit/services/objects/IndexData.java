package il.ac.hit.services.objects;

import il.ac.hit.objects.Index;

import java.io.Serializable;

public class IndexData implements Serializable {
    protected Index parent;
    protected Integer distance;
    protected transient boolean isSource;
    protected transient volatile boolean visited = false;

    public IndexData(Index parent, Integer distance) {
        this.parent = parent;
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "(parent=" + parent + ", distance=" + distance + ")";
    }

    public Index getParent() {
        return parent;
    }

    public Integer getDistance() {
        return distance;
    }

    protected void setDistance(int distance) {
        this.distance = distance;
    }

    protected void setParent(Index parent) {
        this.parent = parent;
    }

    public boolean isVisited() {
        return visited;
    }

    public void resetVisit() {
        visited = false;
    }

    public void visit() {
        visited = true;
    }

    public boolean isSource() {
        return isSource;
    }

    public void setSource(boolean source) {
        isSource = source;
    }

    public void setParentAndDistance(Index parent, int distance) {
        setDistance(distance);
        setParent(parent);
    }
}