package client;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;

class MyMenuHBox extends HBox {
    MyMenuHBox(Main app){
        super();
        Button btCompress = new Button("压缩");
        btCompress.setGraphic(new ImageView(new Image("file:src/resource/compress.png")));
        btCompress.setOnMouseClicked(event -> {
            app.showChooseStage();
        });
        Button btUncompress = new Button("解压");
        btUncompress.setGraphic(new ImageView(new Image("file:src/resource/uncompress.png")));
        btUncompress.setOnMouseClicked(event -> {
            try {
                app.unzip();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        Button btDelete = new Button("删除");
        btDelete.setGraphic(new ImageView(new Image("file:src/resource/delete.png")));
        Button btPwd = new Button("密码");
        btPwd.setGraphic(new ImageView(new Image("file:src/resource/pwd.png")));
        Button btAutoCom = new Button("自解压");
        btAutoCom.setGraphic(new ImageView(new Image("file:src/resource/autocompress.png")));
        Button btKit = new Button("工具箱");
        btKit.setGraphic(new ImageView(new Image("file:src/resource/kit.png")));
        //add file menu
        this.getChildren().addAll(btCompress, btUncompress, btDelete, btPwd, btAutoCom, btKit);
    }
}
