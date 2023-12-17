package com.cgvsu.model;
import com.cgvsu.Math.Vectors.TwoDimensionalVector;
import com.cgvsu.Math.Vectors.ThreeDimensionalVector;

import java.util.*;

public class Model {

    private ArrayList<ThreeDimensionalVector> vertices = new ArrayList<ThreeDimensionalVector>();
    private ArrayList<TwoDimensionalVector> textureVertices = new ArrayList<TwoDimensionalVector>();
    private ArrayList<ThreeDimensionalVector> normals = new ArrayList<ThreeDimensionalVector>();
    private ArrayList<Polygon> polygons = new ArrayList<Polygon>();

    public Model(ArrayList<ThreeDimensionalVector> vertices, ArrayList<TwoDimensionalVector> textureVertices, ArrayList<ThreeDimensionalVector> normals, ArrayList<Polygon> polygons) {
        this.vertices = vertices;
        this.textureVertices = textureVertices;
        this.normals = normals;
        this.polygons = polygons;
    }

    public Model() {
        this.vertices = new ArrayList<>();
        this.textureVertices = new ArrayList<>();
        this.normals = new ArrayList<>();
        this.polygons = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Model model = (Model) o;
        return Objects.equals(vertices, model.vertices) && Objects.equals(textureVertices, model.textureVertices) && Objects.equals(normals, model.normals) && Objects.equals(polygons, model.polygons);
    }

    public ArrayList<ThreeDimensionalVector> getVertices() {
        return vertices;
    }

    public void setVertices(ArrayList<ThreeDimensionalVector> vertices) {
        this.vertices = vertices;
    }

    public ArrayList<TwoDimensionalVector> getTextureVertices() {
        return textureVertices;
    }

    public void setTextureVertices(ArrayList<TwoDimensionalVector> textureVertices) {
        this.textureVertices = textureVertices;
    }

    public ArrayList<ThreeDimensionalVector> getNormals() {
        return normals;
    }

    public void setNormals(ArrayList<ThreeDimensionalVector> normals) {
        this.normals = normals;
    }

    public ArrayList<Polygon> getPolygons() {
        return polygons;
    }

    public void setPolygons(ArrayList<Polygon> polygons) {
        this.polygons = polygons;
    }

    public boolean isEmpty() {
        return vertices.isEmpty();
    }
}
