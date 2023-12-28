package com.cgvsu;

import com.cgvsu.Math.Vectors.ThreeDimensionalVector;
import com.cgvsu.deleter.PolygonsDeleter;
import com.cgvsu.deleter.VerticesDeleter;
import com.cgvsu.exceptions.NullModelException;
import com.cgvsu.objreader.IncorrectFileException;
import com.cgvsu.objreader.ObjReaderException;
import com.cgvsu.objwriter.ObjWriter;
import com.cgvsu.objwriter.ObjWriterException;
import com.cgvsu.render_engine.RenderEngine;
import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.render_engine.Camera;

public class GuiController {

    final private float TRANSLATION = 5F;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TextField polygonsFrom, polygonsCount, verticesFrom, verticesCount;

    @FXML
    private CheckBox freeVertices;

    @FXML
    private Canvas canvas;

    private Map<Model, Path> models = new HashMap<>(); // путь потом заменить на какой-нибудь id
    private Model selectedModel = null;

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

        KeyFrame frame = new KeyFrame(Duration.millis(30), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            camera.setAspectRatio((float) (width / height));

            if (selectedModel != null) {
                RenderEngine.render(canvas.getGraphicsContext2D(), camera, selectedModel, (int) width, (int) height);
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();
    }

    @FXML
    private void deleteVertices() {
        int from, count;
        try {
            from = Integer.parseInt(verticesFrom.getText());
            count = Integer.parseInt(verticesCount.getText());
            //todo: catch IncorrectInputException
        } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException exception) {
            showErrorDialog("Incorrect input: " + exception.getMessage());
            return;
        }
        int len = from + count;
        ArrayList<Integer> list = new ArrayList<>();
        try {
            for (int i = from; i < len; i++) {
                list.add(i);
            }
            selectedModel = VerticesDeleter.removeVerticesFromModel(selectedModel, list);
        } catch (IndexOutOfBoundsException | NullModelException exception) {
            showErrorDialog(exception.getMessage());
        }
    }

    @FXML
    private void deletePolygons() {
        int from, count;
        boolean freeVert;
        try {
            from = Integer.parseInt(polygonsFrom.getText());
            count = Integer.parseInt(polygonsCount.getText());
            freeVert = freeVertices.isSelected();
            //todo: catch IncorrectInputException
        } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException exception) {
            showErrorDialog("Incorrect input: " + exception.getMessage());
            return;
        }
        int len = from + count;
        ArrayList<Integer> list = new ArrayList<>();
        try {
            for (int i = from; i < len; i++) {
                list.add(i);
            }
            selectedModel = PolygonsDeleter.deletePolygons(selectedModel, list, freeVert);
        } catch (IndexOutOfBoundsException | NullModelException exception) {
            showErrorDialog(exception.getMessage());
        }
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
            selectedModel = ObjReader.read(fileContent);
            selectedModel.setPath(fileName);
        } catch (ObjReaderException | IOException | IncorrectFileException exception) {
            //todo: catch MalformedInputException СРОЧНО
            showErrorDialog(exception.getMessage());
        }
    }

    @FXML
    private void onSaveModelMenuItemClick() {
        //todo: При сохранении модели следует выбирать, учитывать
        // трансформации модели или нет. То есть нужна возможность сохранить как
        // исходную модель, так и модель после преобразований.

        String fileName;

        try {
            fileName = selectedModel.getPath().toString();
            //todo: catch NullModelException
        } catch (NullModelException exception) {
            showErrorDialog(exception.getMessage());
            return;
        }

        try {
            ObjWriter.write(fileName, selectedModel);
        } catch (ObjWriterException exception) {
            showErrorDialog(exception.getMessage());
        }
    }

    //todo: сцена, несколько моделей СРОЧНО
    //todo: окно для аффинных преобразований, масштабирование, вращение, перенос xyz СРОЧНО

    //todo: управление камерой, добавить кнопки
    // Сейчас взаимодействие с камерой не очень удобное, используется только клавиатура. Но можно переделать его, добавив в систему
    // мышь. За основу можете взять управление из компьютерной игры или приложения для работы с трехмерной графикой. Здесь хорошо бы продумать
    // горячие клавиши и заодно упростить управление моделями. СТРЕЛОЧКИ СДЕЛАТЬ

    //todo: оформление интерфейса
    // светлая темная темка, цвета, анимации, alert закастомить

    //todo: 3 пункт хз

    @FXML
    private void onSaveModelAsMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Save Model");

        File file = fileChooser.showSaveDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        String fileName = file.getAbsolutePath();

        try {
            ObjWriter.write(fileName, selectedModel);
        } catch (ObjWriterException exception) {
            showErrorDialog(exception.getMessage());
        }
    }

    private void showErrorDialog(String e) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An error has occurred!");
        alert.setContentText(e);
        alert.showAndWait();
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