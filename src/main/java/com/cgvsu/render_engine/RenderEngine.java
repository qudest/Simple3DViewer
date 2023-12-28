package com.cgvsu.render_engine;

import java.util.ArrayList;
import java.util.List;

import com.cgvsu.Math.Matrix.FourDimensionalMatrix;
import com.cgvsu.Math.Matrix.NDimensionalMatrix;
import com.cgvsu.Math.Point.Point2f;
import com.cgvsu.Math.Vectors.ThreeDimensionalVector;
import javafx.scene.canvas.GraphicsContext;
import com.cgvsu.model.Model;
import static com.cgvsu.render_engine.GraphicConveyor.*;

public class RenderEngine {
    private final GraphicsContext graphicsContext;
    private final Camera camera;
    private final List<Model> models = new ArrayList<>();
    private int width;
    private int height;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void addModel(Model model) {
        models.add(model);
    }

    public List<Model> getModels() {
        return models;
    }

    public RenderEngine(GraphicsContext graphicsContext, Camera camera, int width, int height) {
        this.graphicsContext = graphicsContext;
        this.camera = camera;
        this.width = width;
        this.height = height;
    }

    public void render() {
        FourDimensionalMatrix modelMatrix = rotateScaleTranslate();
        FourDimensionalMatrix viewMatrix =  camera.getViewMatrix();
        FourDimensionalMatrix projectionMatrix = camera.getProjectionMatrix();

        NDimensionalMatrix modelViewProjectionMatrix = modelMatrix;
        modelViewProjectionMatrix = (NDimensionalMatrix)  modelViewProjectionMatrix.multiplyMatrix(viewMatrix);
        modelViewProjectionMatrix = (NDimensionalMatrix)  modelViewProjectionMatrix.multiplyMatrix(projectionMatrix);

        for (Model mesh: models) {
            drawPolygons(mesh, modelViewProjectionMatrix);
        }
    }

    private void drawPolygons(Model mesh, NDimensionalMatrix modelViewProjectionMatrix) {
        final int nPolygons = mesh.getPolygons().size();
        for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
            final int nVerticesInPolygon = mesh.getPolygons().get(polygonInd).getVertexIndices().size();

            ArrayList<Point2f> resultPoints = new ArrayList<>();
            for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {


                ThreeDimensionalVector vertex = mesh.getVertices().get(mesh.getPolygons().get(polygonInd).getVertexIndices().get(vertexInPolygonInd));
                Point2f resultPoint = vertexToPoint(multiplyMatrix4ByVector3(modelViewProjectionMatrix, vertex), width, height);

                resultPoints.add(resultPoint);
            }

            for (int vertexInPolygonInd = 1; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                graphicsContext.strokeLine(
                        resultPoints.get(vertexInPolygonInd - 1).getX(),
                        resultPoints.get(vertexInPolygonInd - 1).getY(),
                        resultPoints.get(vertexInPolygonInd).getX(),
                        resultPoints.get(vertexInPolygonInd).getY());
            }

            if (nVerticesInPolygon > 0)
                graphicsContext.strokeLine(
                        resultPoints.get(nVerticesInPolygon - 1).getX(),
                        resultPoints.get(nVerticesInPolygon - 1).getY(),
                        resultPoints.get(0).getX(),
                        resultPoints.get(0).getY());
        }
    }
}