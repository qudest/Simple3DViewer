package com.cgvsu.render_engine;

import com.cgvsu.Math.Matrix.FourDimensionalMatrix;
import com.cgvsu.Math.Matrix.Matrix;
import com.cgvsu.Math.Matrix.NDimensionalMatrix;
import com.cgvsu.Math.Point.Point2f;
import com.cgvsu.Math.Vectors.FourDimensionalVector;
import com.cgvsu.Math.Vectors.NDimensionalVector;
import com.cgvsu.Math.Vectors.ThreeDimensionalVector;
import com.cgvsu.Math.Vectors.Vector;


public class GraphicConveyor {

    //    public static FourDimensionalMatrix rotateScaleTranslate(){
//        return new FourDimensionalMatrix(
//                new FourDimensionalVector(1,0,0,0),
//                new FourDimensionalVector(0,1,0,0),
//                new FourDimensionalVector(0,0,1,0),
//                new FourDimensionalVector(0,0,0,1)
//        );
//    }
    private static FourDimensionalMatrix getScaleMatrix(ThreeDimensionalVector scale) {
        NDimensionalVector  v1 = new FourDimensionalVector(1, 0, 0, 0);
        NDimensionalVector  v2 = new FourDimensionalVector(0, 1, 0, 0);
        NDimensionalVector  v3 = new FourDimensionalVector(0, 0, 1, 0);
        NDimensionalVector  v4 = new FourDimensionalVector(0, 0, 0, 1);

        return new FourDimensionalMatrix(v1, v2, v3, v4);
    }
    public static FourDimensionalMatrix getRotationMatrix(ThreeDimensionalVector angle) {
        FourDimensionalMatrix matrixRotationX = getRotationMatrixX(angle.getA());
        FourDimensionalMatrix matrixRotationY = getRotationMatrixY(angle.getB());
        FourDimensionalMatrix matrixRotationZ = getRotationMatrixZ(angle.getC());

        return (FourDimensionalMatrix) matrixRotationX.multiplyMatrix(matrixRotationY).multiplyMatrix(matrixRotationZ);
    }
    private static FourDimensionalMatrix getRotationMatrixX(double xAngle) {
        xAngle = Math.toRadians(xAngle);
        double cosX = Math.cos(xAngle);
        double sinX = Math.sin(xAngle);

        FourDimensionalVector v1 = new FourDimensionalVector(1, 0, 0, 0);
        FourDimensionalVector v2 = new FourDimensionalVector(0, cosX, -sinX, 0);
        FourDimensionalVector v3 = new FourDimensionalVector(0, sinX, cosX, 0);
        FourDimensionalVector v4 = new FourDimensionalVector(0, 0, 0, 1);

        return new FourDimensionalMatrix(v1, v2, v3, v4);
    }
    private static FourDimensionalMatrix getRotationMatrixY(double yAngle) {
        yAngle = Math.toRadians(yAngle);
        double cosY = Math.cos(yAngle);
        double sinY = Math.sin(yAngle);

        FourDimensionalVector v1 = new FourDimensionalVector(cosY, 0, sinY, 0);
        FourDimensionalVector v2 = new FourDimensionalVector(0, 1, 0, 0);
        FourDimensionalVector v3 = new FourDimensionalVector(-sinY, 0, cosY, 0);
        FourDimensionalVector v4 = new FourDimensionalVector(0, 0, 0, 1);

        return new FourDimensionalMatrix(v1, v2, v3, v4);
    }
    private static FourDimensionalMatrix getRotationMatrixZ(double zAngle) {
        zAngle = Math.toRadians(zAngle);
        double cosZ = Math.cos(zAngle);
        double sinZ = Math.sin(zAngle);

        FourDimensionalVector v1 = new FourDimensionalVector(cosZ, -sinZ, 0, 0);
        FourDimensionalVector v2 = new FourDimensionalVector(sinZ, cosZ, 0, 0);
        FourDimensionalVector v3 = new FourDimensionalVector(0, 0, 1, 0);
        FourDimensionalVector v4 = new FourDimensionalVector(0, 0, 0, 1);

        return new FourDimensionalMatrix(v1, v2, v3, v4);
    }
    private static FourDimensionalMatrix getTranslationMatrix(ThreeDimensionalVector translate) {
        NDimensionalVector  v1 = new FourDimensionalVector(1, 0, 0, translate.getA());
        NDimensionalVector  v2 = new FourDimensionalVector(0, 1, 0, translate.getB());
        NDimensionalVector  v3 = new FourDimensionalVector(0, 0, 1, translate.getC());
        NDimensionalVector  v4 = new FourDimensionalVector(0, 0, 0, 1);

        return new FourDimensionalMatrix(v1, v2, v3, v4);
    }
    public static FourDimensionalMatrix getModelMatrix(ThreeDimensionalVector translate, ThreeDimensionalVector anglesOfRotate, ThreeDimensionalVector scale) {
        FourDimensionalMatrix translationMatrix = getTranslationMatrix(translate);
        FourDimensionalMatrix rotationMatrix = getRotationMatrix(anglesOfRotate);
        FourDimensionalMatrix scaleMatrix = getScaleMatrix(scale);

        return (FourDimensionalMatrix) translationMatrix.multiplyMatrix(rotationMatrix).multiplyMatrix(scaleMatrix);
    }
    public static FourDimensionalMatrix lookAt(Vector eye, Vector target) {
        return lookAt(new ThreeDimensionalVector(eye.getArrValues()[0], eye.getArrValues()[1], eye.getArrValues()[2]), new ThreeDimensionalVector(target.getArrValues()[0], target.getArrValues()[1], target.getArrValues()[2]), new ThreeDimensionalVector(0F, 1.0F, 0F));
    }

    public static FourDimensionalMatrix lookAt(ThreeDimensionalVector eye, ThreeDimensionalVector target, ThreeDimensionalVector up) {
        Vector tempNd = target.subtraction(eye);
        ThreeDimensionalVector resultZ = new ThreeDimensionalVector(tempNd.getArrValues()[0], tempNd.getArrValues()[1], tempNd.getArrValues()[2]);
        ThreeDimensionalVector resultX = up.vectorProduct(resultZ);
        ThreeDimensionalVector resultY = resultZ.vectorProduct(resultX);

        resultX = resultX.normalization();
        resultY = resultY.normalization();
        resultZ = resultZ.normalization();

        return new FourDimensionalMatrix(
                new NDimensionalVector(resultX.getA(), resultY.getA(), resultZ.getA(), 0),
                new NDimensionalVector(resultX.getB(), resultY.getB(), resultZ.getB(), 0),
                new NDimensionalVector(resultX.getC(), resultY.getC(), resultZ.getC(), 0),
                new NDimensionalVector(-resultX.scalarProduct(eye), -resultY.scalarProduct(eye), -resultZ.scalarProduct(eye), 1)
        );
    }

    public static FourDimensionalMatrix perspective(
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        float tangentMinusOnDegree = (float) (1.0F / (Math.tan(fov * 0.5F)));

        return new FourDimensionalMatrix(
                new NDimensionalVector(tangentMinusOnDegree / aspectRatio, 0, 0, 0),
                new NDimensionalVector(0, tangentMinusOnDegree, 0, 0),
                new NDimensionalVector(0, 0, (farPlane + nearPlane) / (farPlane - nearPlane), 1),
                new NDimensionalVector(0, 0, 2 * (nearPlane * farPlane) / (nearPlane - farPlane), 0)
        );
    }

    public static ThreeDimensionalVector multiplyMatrix4ByVector3(final NDimensionalMatrix matrix, final ThreeDimensionalVector vertex) {
        final double x = (vertex.getA() * matrix.getMatrixInVectors()[0].getArrValues()[0]) + (vertex.getB() * matrix.getMatrixInVectors()[1].getArrValues()[0]) + (vertex.getC() * matrix.getMatrixInVectors()[2].getArrValues()[0]) + matrix.getMatrixInVectors()[3].getArrValues()[0];
        final double y = (vertex.getA() * matrix.getMatrixInVectors()[0].getArrValues()[1]) + (vertex.getB() * matrix.getMatrixInVectors()[1].getArrValues()[1]) + (vertex.getC() * matrix.getMatrixInVectors()[2].getArrValues()[1]) + matrix.getMatrixInVectors()[3].getArrValues()[1];
        final double z = (vertex.getA() * matrix.getMatrixInVectors()[0].getArrValues()[2]) + (vertex.getB() * matrix.getMatrixInVectors()[1].getArrValues()[2]) + (vertex.getC() * matrix.getMatrixInVectors()[2].getArrValues()[2]) + matrix.getMatrixInVectors()[3].getArrValues()[2];
        final double w = (vertex.getA() * matrix.getMatrixInVectors()[0].getArrValues()[3]) + (vertex.getB() * matrix.getMatrixInVectors()[1].getArrValues()[3]) + (vertex.getC() * matrix.getMatrixInVectors()[2].getArrValues()[3]) + matrix.getMatrixInVectors()[3].getArrValues()[3];
        return new ThreeDimensionalVector(x / w, y / w, z / w);
    }

    public static Point2f vertexToPoint(final ThreeDimensionalVector vertex, final int width, final int height) {
        return new Point2f((float) (vertex.getA() * width + width / 2.0F), (float) (-vertex.getB() * height + height / 2.0F));
    }
}
