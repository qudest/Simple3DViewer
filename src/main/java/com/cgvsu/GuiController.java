package com.cgvsu;

import com.cgvsu.Math.Vectors.ThreeDimensionalVector;
import com.cgvsu.objreader.IncorrectFileException;
import com.cgvsu.objwriter.ObjWriter;
import com.cgvsu.objwriter.ObjWriterException;
import com.cgvsu.render_engine.RenderEngine;
import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.File;

import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.render_engine.Camera;

public class GuiController {

    final private float TRANSLATION = 0.5F;

    @FXML
    AnchorPane anchorPane;

    @FXML
    private Canvas canvas;

    private Model mesh = null;

    private Camera camera = new Camera(
            new ThreeDimensionalVector(0, 0, 100),
            new ThreeDimensionalVector(0, 0, 0),
            1.0F, 1, 0.01F, 100);

    private Timeline timeline;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.millis(15), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            camera.setAspectRatio((float) (width / height));

            if (mesh != null) {
                RenderEngine.render(canvas.getGraphicsContext2D(), camera, mesh, (int) width, (int) height);
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();
    }

    @FXML
    private void onOpenModelMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Load Model");

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        Path fileName = Path.of(file.getAbsolutePath());

        try {
            String fileContent = Files.readString(fileName);
            mesh = ObjReader.read(fileContent);
            // todo: обработка ошибок
        } catch (Exception exception) {
            System.out.println("test reader");
        }
    }

    @FXML
    private void onSaveModelMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Save Model");

        File file = fileChooser.showSaveDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        String fileName = file.getAbsolutePath();

        try {
            ObjWriter.write(fileName, mesh);
            // todo: обработка ошибок
        } catch (ObjWriterException exception) {
            System.out.println("test writer");
        }
    }

    @FXML
    public void handleCameraForward(ActionEvent actionEvent) {
        camera.movePosition(new ThreeDimensionalVector(0, 0, -TRANSLATION));
    }

    @FXML
    public void handleCameraBackward(ActionEvent actionEvent) {
        camera.movePosition(new ThreeDimensionalVector(0, 0, TRANSLATION));
    }

    @FXML
    public void handleCameraLeft(ActionEvent actionEvent) {
        camera.movePosition(new ThreeDimensionalVector(TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        camera.movePosition(new ThreeDimensionalVector(-TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraUp(ActionEvent actionEvent) {
        camera.movePosition(new ThreeDimensionalVector(0, TRANSLATION, 0));
    }

    @FXML
    public void handleCameraDown(ActionEvent actionEvent) {
        camera.movePosition(new ThreeDimensionalVector(0, -TRANSLATION, 0));
    }
}