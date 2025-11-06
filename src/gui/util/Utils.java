package gui.util;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.control.DatePicker;

import java.io.IOException;

public class Utils {

    /**
     * Obtém o 'Stage' (Janela) principal a partir de um evento de clique.
     */
    public static Stage currentStage(Event event) {
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }

    /**
     * Método genérico para criar e exibir uma janela de formulário (modal).
     *
     * @param fxmlPath O caminho para o arquivo .fxml (ex: "/gui/DepartmentForm.fxml")
     * @param parentStage A janela "pai" que será bloqueada
     * @param title O título da nova janela
     * @return O FXMLLoader, para que possamos pegar o controller
     */
    public static FXMLLoader createDialogLoader(String fxmlPath, Stage parentStage, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(Utils.class.getResource(fxmlPath));
            Pane pane = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.setScene(new Scene(pane));

            // Define o "pai" da janela
            dialogStage.initOwner(parentStage);

            // Define a janela como "modal" (bloqueia a janela pai)
            dialogStage.initModality(Modality.WINDOW_MODAL);

            // Mostra a janela e espera ela ser fechada
            dialogStage.showAndWait();

            // Retorna o loader para que quem chamou possa pegar o controller
            return loader;

        } catch (IOException e) {
            e.printStackTrace();
            // Em um app real, mostraríamos um Alert de erro aqui
            return null;
        }
    }

    public static void formatDatePicker(DatePicker datePicker, String format) {
        datePicker.setConverter(new javafx.util.StringConverter<>() {
            private final java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern(format);

            @Override
            public String toString(java.time.LocalDate date) {
                if (date != null) {
                    return dtf.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public java.time.LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    try {
                        return java.time.LocalDate.parse(string, dtf);
                    } catch (java.time.format.DateTimeParseException e) {
                        return null; // Retorna null se a data for inválida
                    }
                } else {
                    return null;
                }
            }
        });
    }
}