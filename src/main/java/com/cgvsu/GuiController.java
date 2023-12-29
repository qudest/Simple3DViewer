package com.cgvsu;

import com.cgvsu.Math.Vectors.ThreeDimensionalVector;
import com.cgvsu.Math.Vectors.TwoDimensionalVector;
import com.cgvsu.deleter.PolygonsDeleter;
import com.cgvsu.deleter.VerticesDeleter;
import com.cgvsu.exceptions.NullModelException;
import com.cgvsu.objreader.IncorrectFileException;
import com.cgvsu.objreader.ObjReaderException;
import com.cgvsu.objwriter.ObjWriter;
import com.cgvsu.objwriter.ObjWriterException;
import com.cgvsu.render_engine.CameraController;
import com.cgvsu.render_engine.RenderEngine;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.File;
import java.util.ArrayList;


import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.render_engine.Camera;

public class GuiController {

    final private float TRANSLATION = 1F;
    @FXML
    private ListView<String> viewModels = new ListView<>();

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TextField polygonsFrom, polygonsCount, verticesFrom, verticesCount, scaleX, scaleY, scaleZ, rotateX, rotateY, rotateZ, translateX, translateY, translateZ;

    @FXML
    private CheckBox freeVertices;

    @FXML
    private Canvas canvas;

    private RenderEngine renderEngine;
    private int countId = 1;

    private boolean isRotationActive;

    private final TwoDimensionalVector currentMouseCoordinates = new TwoDimensionalVector(0, 0);
    private final TwoDimensionalVector centerCoordinates = new TwoDimensionalVector(0 , 0);

    private Model selectedModel = null;

    private ObservableList<String> items = FXCollections.observableArrayList();

    private Camera camera = new Camera(
            new ThreeDimensionalVector(0, 0, 100),
            new ThreeDimensionalVector(0, 0, 0),
            1.0F, 1, 0.01F, 100);

    private Timeline timeline;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        viewModels.setItems(items);
        viewModels.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            handleModelSelection(newValue);
        });

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        renderEngine = new RenderEngine(canvas.getGraphicsContext2D(), camera, (int) canvas.getWidth(), (int) canvas.getHeight());

        renderEngine.setCameraController(new CameraController(camera, TRANSLATION));

        KeyFrame frame = new KeyFrame(Duration.millis(30), event -> {
            renderEngine.setWidth((int) canvas.getWidth());
            renderEngine.setHeight((int) canvas.getHeight());

            if (isRotationActive) {
                rotateCamera();
            }

            canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            camera.setAspectRatio((float) (canvas.getWidth() / canvas.getHeight()));

            renderEngine.render();
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();
    }

    @FXML
    private void handleScale() {
        try {
            selectedModel.scaleX = Double.parseDouble(scaleX.getText());
            selectedModel.scaleY = Double.parseDouble(scaleY.getText());
            selectedModel.scaleZ = Double.parseDouble(scaleZ.getText());
            //todo: catch IncorrectInputException
        } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException exception) {
            showErrorDialog("Incorrect input: " + exception.getMessage());
        }
    }

    @FXML
    private void handleRotate() {
        try {
            selectedModel.rotateX = Double.parseDouble(rotateX.getText());
            selectedModel.rotateY = Double.parseDouble(rotateY.getText());
            selectedModel.rotateZ = Double.parseDouble(rotateZ.getText());
            //todo: catch IncorrectInputException
        } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException exception) {
            showErrorDialog("Incorrect input: " + exception.getMessage());
        }
    }

    @FXML
    private void handleTranslate() {
        try {
            selectedModel.translateX = Double.parseDouble(translateX.getText());
            selectedModel.translateY = Double.parseDouble(translateY.getText());
            selectedModel.translateZ = Double.parseDouble(translateZ.getText());
            //todo: catch IncorrectInputException
        } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException exception) {
            showErrorDialog("Incorrect input: " + exception.getMessage());
        }
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

        Path filePath = Path.of(file.getAbsolutePath());
        String fileName = filePath.toString();

        try {
            String fileContent = Files.readString(filePath);
            selectedModel = ObjReader.read(fileContent);
            if (renderEngine.getModels().containsKey(fileName)) {
                int extensionIndex = fileName.lastIndexOf(".");
                String baseName = fileName.substring(0, extensionIndex);
                String extension = fileName.substring(extensionIndex);
                fileName = baseName + countId + extension;
                countId++;
            }
            selectedModel.setPath(fileName);
            renderEngine.addModel(selectedModel);
            items.add(fileName);
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
            fileName = selectedModel.getPath();
            //todo: catch NullModelException
        } catch (NullPointerException exception) {
            showErrorDialog(exception.getMessage());
            return;
        }

        try {
            ObjWriter.write(fileName, selectedModel);
        } catch (ObjWriterException exception) {
            showErrorDialog(exception.getMessage());
        }
    }

    //todo: управление камерой, добавить кнопки
    // Сейчас взаимодействие с камерой не очень удобное, используется только клавиатура. Но можно переделать его, добавив в систему
    // мышь. За основу можете взять управление из компьютерной игры или приложения для работы с трехмерной графикой. Здесь хорошо бы продумать
    // горячие клавиши и заодно упростить управление моделями. СТРЕЛОЧКИ СДЕЛАТЬ

    //todo: оформление интерфейса
    // светлая темная темка, цвета, анимации, alert закастомить

    //todo: 3 пункт хз

    @FXML
    private void onDeleteMenuItemClick() {
        try {
            renderEngine.getModels().remove(selectedModel.getPath());
            items.remove(selectedModel.getPath());
            selectedModel = renderEngine.getModels().values().stream().reduce((first, second) -> second).orElse(null);
        } catch (NullPointerException exception) {
            showErrorDialog(exception.getMessage());
        }
    }

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
            //todo: catch NotSelectedModelException
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

    private void handleModelSelection(String modelName) {
        for (Model model: renderEngine.getModels().values()) {
            if (model.getPath().equals(modelName)) {
                selectedModel = model;
                break;
            }
        }
    }

    @FXML
    public void handleCameraForward() {
        try {
            renderEngine.getCameraController().handleCameraForward();
        } catch (Exception e) {
            showErrorDialog(e.getMessage());
        }
    }

    @FXML
    public void handleCameraBackward() {
        try {
            renderEngine.getCameraController().handleCameraBackward();
        } catch (Exception e) {
            showErrorDialog(e.getMessage());
        }
    }

    @FXML
    public void handleCameraLeft() {
        try {
            renderEngine.getCameraController().handleCameraLeft();
        } catch (Exception e) {
            showErrorDialog(e.getMessage());
        }
    }

    @FXML
    public void handleCameraRight() {
        try {
            renderEngine.getCameraController().handleCameraRight();
        } catch (Exception e) {
            showErrorDialog(e.getMessage());
        }
    }

    @FXML
    public void handleCameraUp() {
        try {
            renderEngine.getCameraController().handleCameraUp();
        } catch (Exception e) {
            showErrorDialog(e.getMessage());
        }
    }

    @FXML
    public void handleCameraDown() {
        try {
            renderEngine.getCameraController().handleCameraDown();
        } catch (Exception e) {
            showErrorDialog(e.getMessage());
        }
    }

    @FXML
    public void takeFocusCanvas() {
        canvas.requestFocus();
    }

    @FXML
    public void canvasDragDroppedGetValue() {
        isRotationActive = false;
    }

    @FXML
    public void canvasDragEnterGetValue() {
        isRotationActive = true;
    }

    public void rotateCamera() {
        centerCoordinates.setA(canvas.getWidth() / 2);
        centerCoordinates.setB(canvas.getHeight() / 2);

        double diffX = currentMouseCoordinates.getA() - centerCoordinates.getA();
        double diffY = currentMouseCoordinates.getB() - centerCoordinates.getB();

        double xAngle = (diffX / canvas.getWidth()) * 1;
        double yAngle = (diffY / canvas.getHeight()) * -1;

        try {
            renderEngine.getCameraController().rotateCamera(new TwoDimensionalVector(xAngle, yAngle));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @FXML
    public void currentMouseCoordinates(MouseEvent mouseDragEvent) {
        currentMouseCoordinates.setA( (float) mouseDragEvent.getX());
        currentMouseCoordinates.setB( (float) mouseDragEvent.getY());
    }
}