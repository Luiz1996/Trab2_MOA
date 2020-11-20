package br.uem.din.moa.Model;

import java.util.Objects;

public class Route {
    private int initialVertex;
    private int vertexDistances;
    private int finalVertex;

    public Route(){}

    public int getInitialVertex() {
        return initialVertex;
    }

    public void setInitialVertex(int initialVertex) {
        this.initialVertex = initialVertex;
    }

    public int getVertexDistances() {
        return vertexDistances;
    }

    public void setVertexDistances(int vertexDistances) {
        this.vertexDistances = vertexDistances;
    }

    public int getFinalVertex() {
        return finalVertex;
    }

    public void setFinalVertex(int finalVertec) {
        this.finalVertex = finalVertec;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return initialVertex == route.initialVertex &&
                vertexDistances == route.vertexDistances &&
                finalVertex == route.finalVertex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(initialVertex, vertexDistances, finalVertex);
    }
}
