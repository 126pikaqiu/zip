package client;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class MyMenuHBox extends HBox {
    private Button btCompress = new Button("压缩");
    private Button btUncompress = new Button("解压");
    private Button btDelete = new Button("删除");
    private Button btPwd = new Button("密码");
    private Button btAutoCom = new Button("自解压");
    private Button btKit = new Button("工具箱");
    MyMenuHBox(Main app){
        super();
        btCompress.setGraphic(new ImageView(new Image("file:src/resource/compress.png")));
        btCompress.setOnMouseClicked(event -> {
            app.showChooseStage();
        });
        btUncompress.setGraphic(new ImageView(new Image("file:src/resource/uncompress.png")));
        btUncompress.setOnMouseClicked(event -> {
            try {
                app.unzip();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        btDelete.setGraphic(new ImageView(new Image("file:src/resource/delete.png")));
        btPwd.setGraphic(new ImageView(new Image("file:src/resource/pwd.png")));
        btAutoCom.setGraphic(new ImageView(new Image("file:src/resource/autocompress.png")));
        btKit.setGraphic(new ImageView(new Image("file:src/resource/kit.png")));
        //add file menu
        this.getChildren().addAll(btCompress,btUncompress,btDelete,btPwd,btAutoCom,btKit);
    }
}
