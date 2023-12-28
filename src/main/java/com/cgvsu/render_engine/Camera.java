package com.cgvsu.render_engine;
import com.cgvsu.Math.Matrix.FourDimensionalMatrix;
import com.cgvsu.Math.Matrix.NDimensionalMatrix;
import com.cgvsu.Math.Vectors.*;



public class Camera {
    private Vector position;
    private Vector target;
    private final float fov;
    private float aspectRatio;
    private final float nearPlane;
    private final float farPlane;

    public Camera(
            final Vector position,
            final Vector target,
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        this.position = position;
        this.target = target;
        this.fov = fov;
        this.aspectRatio = aspectRatio;
        this.nearPlane = nearPlane;
        this.farPlane = farPlane;
    }

    public void setPosition(final Vector position) {
        this.position = position;
    }

    public void setTarget(final ThreeDimensionalVector target) {
        this.target = target;
    }

    public void setAspectRatio(final float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public Vector getPosition() {
        return position;
    }

    public Vector getTarget() {
        return target;
    }

    public void movePosition(final Vector translation) {
        this.position = this.position.addition(translation);
    }

    public void moveTarget(final ThreeDimensionalVector translation) {
        this.target = target.addition(translation);
    }
    public void moveCamera(final ThreeDimensionalVector translation) {
        movePosition(translation);
        moveTarget(translation);
    }
    public void rotateCamera(final NDimensionalMatrix mR) throws Exception {
        ThreeDimensionalVector vZ = ThreeDimensionalVector.subtraction((ThreeDimensionalVector) target, (ThreeDimensionalVector) position);
        vZ = GraphicConveyor.multiplyMatrix4ByVector3(mR, vZ);
        target = ThreeDimensionalVector.addition((ThreeDimensionalVector) position, vZ);

    }
    NDimensionalMatrix getViewMatrix() {
        return GraphicConveyor.lookAt(position,target);
    }

    NDimensionalMatrix getProjectionMatrix() {
        return GraphicConveyor.perspective(fov, aspectRatio, nearPlane, farPlane);
    }
}