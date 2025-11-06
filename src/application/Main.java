package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) {
        try {
            //Carrega o arquivo FXML
            // procura na pasta 'gui'
            Parent root = FXMLLoader.load(getClass().getResource("/gui/MainView.fxml"));

            // conteúdo do FXML
            Scene scene = new Scene(root, 800, 600);

            //Configura a Stage(Janela)
            primaryStage.setTitle("Department Sales Manager");
            primaryStage.setScene(scene);
            primaryStage.show(); // Mostra a janela!

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // main agora só tem a função de "lançar" a aplicação JavaFX
    public static void main(String[] args) {
        launch(args);
    }
}