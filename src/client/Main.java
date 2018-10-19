package client;
import core.Zip;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.*;

import java.io.File;
import java.io.IOException;

public class Main extends Application{
    private Stage stage;
    private File selectedFile;
    private String zipFilePath;
    private String sourceFilePath;
    private Zip zip;
    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = new Stage();
        stage.setTitle("真好压");
        initFrame(primaryStage);
        stage.show();
    }

    private void initFrame(Stage primaryStage){
        stage.getIcons().add(new Image("file:src/resource/logo.jpg"));
        MyMenuHBox myMenuHBox = new MyMenuHBox(this);
        stage.setMinWidth(1030);//height 130
        stage.setHeight(650);

        HBox hBox = new HBox(stage.getWidth());

        hBox.getChildren().add(myMenuHBox);
        hBox.setId("myhbox");
        hBox.setPadding(new Insets(20,10,10,10));
        BorderPane pan = new BorderPane();
        pan.setTop(hBox);
        pan.setCenter(new Button("真好压"));
        Scene scene = new Scene(pan,stage.getWidth(),stage.getHeight());
        scene.getStylesheets().add("file:src/css/main.css");
        stage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }

    void showChooseStage(){
        Stage stage = new Stage();
        stage.setTitle("类型选择");
        stage.getIcons().add(new Image("file:src/resource/logo.jpg"));
        stage.setResizable(false);
        stage.setHeight(400);
        stage.setWidth(400);
        Button btChoose = new Button("选择文件");
        Button btChooseFolder = new Button("选择文件夹");
        btChoose.setOnMouseClicked(event -> {
            stage.close();
            Stage fileStage = null;
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("真好压-选择文件");
            String cwd = System.getProperty("user.dir");
            File file = new File(cwd);
            fileChooser.setInitialDirectory(file);
            selectedFile = fileChooser.showOpenDialog(null);
            if(selectedFile != null){
                showSureZipStage(selectedFile.getAbsolutePath());
            }
        });
        btChooseFolder.setOnMouseClicked(event -> {
            stage.close();
            Stage fileStage = null;
            DirectoryChooser folderChooser = new DirectoryChooser();
            folderChooser.setTitle("真好压-选择文件夹");
            String cwd = System.getProperty("user.dir");
            File file = new File(cwd);
            folderChooser.setInitialDirectory(file);
            selectedFile = folderChooser.showDialog(null);
            if(selectedFile != null){
                showSureZipStage(selectedFile.getAbsolutePath());
            }
        });
        Pane pan = new Pane();
        pan.getChildren().addAll(btChoose,btChooseFolder);
        Scene scene = new Scene(pan);
        btChoose.setLayoutX(150);
        btChoose.setLayoutY(50);
        btChooseFolder.setLayoutX(140);
        btChooseFolder.setLayoutY(150);
        scene.getStylesheets().add("file:src/css/chooseStage.css");
        stage.setScene(scene);
        stage.show();
    }

    private void showSureZipStage(String path){
        sourceFilePath = path;
        int len = 0;
        int start = 0;
        for(int i = 0; i < path.length();i++){
            if(path.charAt(i) == '.'){
                len = i;
            } else if(path.charAt(i) == '\\'){
                start = i;
            }
        }
        zipFilePath = path.substring(0,len) + ".lzip";
        if(len == 0){
            path = path + ".lzip";//文件夹
        }else{
            path = path.substring(start + 1, len) + ".lzip";
        }
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setHeight(340);
        stage.setWidth(680);
        stage.setTitle("真好压");
        stage.getIcons().add(new Image("file:src/resource/logo.jpg"));
        HBox hBox1 = new HBox();
        HBox hBox2 = new HBox();
        HBox hBox3 = new HBox();
        hBox1.setId("myhbox");
        hBox1.setPrefWidth(680);
        hBox1.setPrefHeight(110);
        GridPane gridPane = new GridPane();
        gridPane.setVgap(30);
        gridPane.add(hBox1,0,0);
        gridPane.add(hBox2,0,1);
        gridPane.add(hBox3,0,2);
        Scene scene = new Scene(gridPane);
        Label label = new Label("压缩到:");
        label.setPadding(new Insets(5,0,5,0));
        TextField tfpath = new TextField(path);
        Button btChooseFolder = new Button("选择目录");
        btChooseFolder.setOnMouseClicked(event -> {
            Stage fileStage = null;
            DirectoryChooser folderChooser = new DirectoryChooser();
            folderChooser.setTitle("真好压-选择目录");
            selectedFile = folderChooser.showDialog(null);
            if(selectedFile != null){
                zipFilePath = selectedFile.getAbsolutePath() + "\\" + tfpath.getText();//确定压缩后的路径
            }
        });
        hBox2.getChildren().addAll(label,tfpath,btChooseFolder);
        Button btOk = new Button("确认压缩");
        btOk.setOnMouseClicked(event -> {
            zip = new Zip(zipFilePath,sourceFilePath);
            try {
                zip.zip();
            } catch (Exception e) {
                e.printStackTrace();
            }
            stage.close();
        });
        hBox2.setPadding(new Insets(0,20,0,120));
        hBox3.setPadding(new Insets(0,20,0,300));
        hBox3.getChildren().add(btOk);
        scene.getStylesheets().add("file:src/css/main.css");
        stage.setScene(scene);
        stage.show();
    }

    void unzip() throws IOException, ClassNotFoundException {
            Stage fileStage = null;
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("真好压-选择解压文件");
            String cwd = System.getProperty("user.dir");
            File file = new File(cwd);
            fileChooser.setInitialDirectory(file);
            fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("ZIP FILES","*.lzip"));
            selectedFile = fileChooser.showOpenDialog(null);
            if(selectedFile != null){
                zip = new Zip(selectedFile.getAbsolutePath());
                zip.unzip();
            }
    }

}
