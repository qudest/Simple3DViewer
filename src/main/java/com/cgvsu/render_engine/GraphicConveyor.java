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

    public static FourDimensionalMatrix rotateScaleTranslate(){
        return new FourDimensionalMatrix(
                new FourDimensionalVector(1,0,0,0),
                new FourDimensionalVector(0,1,0,0),
                new FourDimensionalVector(0,0,1,0),
                new FourDimensionalVector(0,0,0,1)
        );
    }


    public static FourDimensionalMatrix lookAt(Vector eye, Vector target) {
        return lookAt(new ThreeDimensionalVector(eye.getArrValues()[0],eye.getArrValues()[1],eye.getArrValues()[2]), new ThreeDimensionalVector(target.getArrValues()[0],target.getArrValues()[1],target.getArrValues()[2]), new ThreeDimensionalVector(0F, 1.0F, 0F));
    }
    // методы соотвествуют главе "Из мировых координат в камеру" стр 59
    public static FourDimensionalMatrix lookAt(ThreeDimensionalVector eye, ThreeDimensionalVector target, ThreeDimensionalVector up) {
        Vector tempNd = target.subtraction(eye);
        ThreeDimensionalVector resultZ = new ThreeDimensionalVector(tempNd.getArrValues()[0], tempNd.getArrValues()[1],tempNd.getArrValues()[2]);
        ThreeDimensionalVector resultX = up.vectorProduct(resultZ);
        ThreeDimensionalVector resultY = resultZ.vectorProduct(resultX);

        resultX = resultX.normalization();
        resultY =resultY.normalization();
        resultZ = resultZ.normalization();

 /*
 внимание работа с векторамии столбцами
  */
        return new FourDimensionalMatrix(
                new NDimensionalVector(resultX.getA(), resultY.getA(), resultZ.getA(), 0),
                new NDimensionalVector(resultX.getB(), resultY.getB(), resultZ.getB(), 0),
                new NDimensionalVector(resultX.getC(), resultY.getC(), resultZ.getC(), 0),
                new NDimensionalVector(-resultX.scalarProduct(eye), -resultY.scalarProduct(eye), -resultZ.scalarProduct(eye), 1)
        );
    }

    public static FourDimensionalMatrix perspective(

      //метод соответствует главе "Из системы координат в плоскость проецирования" стр 61
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        float tangentMinusOnDegree = (float) (1.0F / (Math.tan(fov * 0.5F)));


        return new FourDimensionalMatrix(
                new NDimensionalVector(tangentMinusOnDegree / aspectRatio, 0,0,0),
                new NDimensionalVector(0, tangentMinusOnDegree,0,0),
                new NDimensionalVector(0, 0,(farPlane + nearPlane) / (farPlane - nearPlane),1),
                new NDimensionalVector(0,0, 2 * (nearPlane * farPlane) / (nearPlane - farPlane),0)
        );
    }

    public static ThreeDimensionalVector multiplyMatrix4ByVector3(final NDimensionalMatrix matrix, final ThreeDimensionalVector vertex) {
        final double x = (vertex.getA() * matrix.getMatrixInVectors()[0].getArrValues()[0]) + (vertex.getB() * matrix.getMatrixInVectors()[1].getArrValues()[0]) + (vertex.getC() * matrix.getMatrixInVectors()[2].getArrValues()[0]) + matrix.getMatrixInVectors()[3].getArrValues()[0];
        final double y = (vertex.getA() * matrix.getMatrixInVectors()[0].getArrValues()[1]) + (vertex.getB() * matrix.getMatrixInVectors()[1].getArrValues()[1]) + (vertex.getC() * matrix.getMatrixInVectors()[2].getArrValues()[1]) + matrix.getMatrixInVectors()[3].getArrValues()[1];
        final double z = (vertex.getA() * matrix.getMatrixInVectors()[0].getArrValues()[2]) + (vertex.getB() * matrix.getMatrixInVectors()[1].getArrValues()[2]) + (vertex.getC() * matrix.getMatrixInVectors()[2].getArrValues()[2]) + matrix.getMatrixInVectors()[3].getArrValues()[2];
        final double w = (vertex.getA() * matrix.getMatrixInVectors()[0].getArrValues()[3]) + (vertex.getB() * matrix.getMatrixInVectors()[1].getArrValues()[3]) + (vertex.getC() * matrix.getMatrixInVectors()[2].getArrValues()[3]) + matrix.getMatrixInVectors()[3].getArrValues()[3];
        Vector tmpVector = matrix.multiplyVector(new FourDimensionalVector(vertex.getA(), vertex.getB(), vertex.getC(), 1));
        return new ThreeDimensionalVector(tmpVector.getArrValues()[0] / w, tmpVector.getArrValues()[1] / w, tmpVector.getArrValues()[2] / w);
    }

    public static Point2f vertexToPoint(final ThreeDimensionalVector vertex, final int width, final int height) {
        return new Point2f((float) (vertex.getA() * width + width / 2.0F), (float) (-vertex.getB() * height + height / 2.0F));
    }
}
