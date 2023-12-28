package com.cgvsu.render_engine;

import java.util.ArrayList;
import java.util.Arrays;

import com.cgvsu.Math.Matrix.FourDimensionalMatrix;
import com.cgvsu.Math.Matrix.NDimensionalMatrix;
import com.cgvsu.Math.Point.Point2f;
import com.cgvsu.Math.Vectors.FourDimensionalVector;
import com.cgvsu.Math.Vectors.ThreeDimensionalVector;
import com.cgvsu.Math.Vectors.Vector;
import javafx.scene.canvas.GraphicsContext;
import com.cgvsu.model.Model;
import static com.cgvsu.render_engine.GraphicConveyor.*;

public class RenderEngine {

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height)
    {
        FourDimensionalMatrix modelMatrix = rotateScaleTranslate();
        FourDimensionalMatrix viewMatrix =  camera.getViewMatrix();
        FourDimensionalMatrix projectionMatrix = camera.getProjectionMatrix();

        NDimensionalMatrix modelViewProjectionMatrix = modelMatrix;
        modelViewProjectionMatrix = (NDimensionalMatrix)  modelViewProjectionMatrix.multiplyMatrix(viewMatrix);
        modelViewProjectionMatrix = (NDimensionalMatrix)  modelViewProjectionMatrix.multiplyMatrix(projectionMatrix);

        final int nPolygons = mesh.getPolygons().size();
        for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
            final int nVerticesInPolygon = mesh.getPolygons().get(polygonInd).getVertexIndices().size();

            ArrayList<Point2f> resultPoints = new ArrayList<>();
            for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {


                ThreeDimensionalVector vertex = mesh.getVertices().get(mesh.getPolygons().get(polygonInd).getVertexIndices().get(vertexInPolygonInd));

                ThreeDimensionalVector vertexVecmath = new ThreeDimensionalVector(vertex.getA(), vertex.getB(), vertex.getC());
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