package gui.util;

import javafx.scene.control.TextField;

public class Constraints {

    /**
     * Adiciona um listener que impede que o TextField aceite
     * qualquer coisa que não seja um número inteiro.
     */
    public static void setTextFieldInteger(TextField txt) {
        txt.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && !newValue.matches("\\d*")) {
                txt.setText(oldValue);
            }
        });
    }

    /**
     * Adiciona um listener que limita o número máximo
     * de caracteres que podem ser digitados.
     */
    public static void setTextFieldMaxLength(TextField txt, int max) {
        txt.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > max) {
                txt.setText(oldValue);
            }
        });
    }

    /**
     * Adiciona um listener que impede que o TextField aceite
     * qualquer coisa que não seja um número de ponto flutuante (double).
     */
    public static void setTextFieldDouble(TextField txt) {
        txt.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && !newValue.matches("\\d*([\\.]\\d*)?")) {
                txt.setText(oldValue);
            }
        });
    }
}