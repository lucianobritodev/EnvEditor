package br.com.lucianobrito.enveditor;

import br.com.lucianobrito.enveditor.utils.EnvUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;

public class MainApplication extends Application {
    public static Stage stage;
    public static Parent root;


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/views/main.fxml"));
        root = fxmlLoader.load();
        Scene scene = new Scene(root, 1280, 800);
        stage.setTitle("ENV Editor");
        stage.getIcons().add(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("/icons/enveditor.png"))));
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        MainApplication.stage = stage;
    }

    public static void main(String[] args) {

        Stream.of(args).forEach(env -> {
            if (env.contains("user.home")) {
                EnvUtils.USER_HOME = env.replaceAll("^.*=", "");
            }
        });

        launch();
    }
}