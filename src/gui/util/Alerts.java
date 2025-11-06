package gui.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class Alerts {

    /**
     * Mostra um pop-up de alerta simples (Erro, Informação, Aviso).
     *
     * @param title O título da janela do alerta
     * @param header O cabeçalho (pode ser null)
     * @param content O texto principal da mensagem
     * @param type O tipo de alerta (ERROR, INFORMATION, etc.)
     */
    public static void showAlert(String title, String header, String content, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header); // Opcional, pode ser null
        alert.setContentText(content);
        alert.showAndWait(); // Mostra o alerta e espera o utilizador o fechar
    }

    /**
     * Mostra um pop-up de confirmação (OK/Cancel) e
     * retorna a escolha do utilizador.
     *
     * @param title O título da janela
     * @param content A pergunta (ex: "Tem a certeza?")
     * @return Um 'Optional' com o ButtonType (OK ou CANCEL) que foi clicado
     */
    public static Optional<ButtonType> showConfirmation(String title, String content) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null); // Sem cabeçalho para confirmação simples
        alert.setContentText(content);
        return alert.showAndWait(); // Mostra e retorna a resposta
    }
}