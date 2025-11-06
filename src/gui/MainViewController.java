package gui;

import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.dao.SellerDao; // 1. Importe o SellerDao

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer; // 2. Importe o 'Consumer'

public class MainViewController implements Initializable {

    // --- Componentes do FXML ---
    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private MenuItem menuItemSeller;

    @FXML
    private MenuItem menuItemDepartment;

    @FXML
    private MenuItem menuItemAbout;

    // --- DAOs ---
    private DepartmentDao departmentDao;
    private SellerDao sellerDao; // 3. Adicione o SellerDao

    // --- Métodos de Ação do Menu ---

    @FXML
    public void onMenuItemSellerAction() {
        System.out.println("Clicou em Seller!");

        // 4. Lógica para carregar a tela de Vendedor
        if (sellerDao == null) {
            sellerDao = DaoFactory.createSellerDao();
        }

        // 5. Chama o novo loadView genérico
        loadView("/gui/SellerList.fxml", (SellerListController controller) -> {
            controller.setSellerDao(sellerDao);
            controller.loadSellers();
        });
    }

    @FXML
    public void onMenuItemDepartmentAction() {
        System.out.println("Clicou em Department!");

        // 6. Lógica para carregar a tela de Departamento
        if (departmentDao == null) {
            departmentDao = DaoFactory.createDepartmentDao();
        }

        // 7. Chama o novo loadView genérico
        loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) -> {
            controller.setDepartmentDao(departmentDao);
            controller.loadDepartments();
        });
    }

    @FXML
    public void onMenuItemAboutAction() {
        System.out.println("Clicou em About!");
        // Agora, em vez de um print, chamamos o nosso novo método simples
        loadView("/gui/About.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Nada por enquanto
    }

    /**
     * 8. ESTE É O NOVO MÉTODO GENÉRICO (Substitua o antigo)
     *
     * @param absoluteName O caminho completo para o FXML
     * @param initializingAction Uma "função" (lambda) que será executada
     * para configurar o novo controller.
     */
    private <T> void loadView(String absoluteName, Consumer<T> initializingAction) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            Node newView = loader.load(); // Carrega a nova tela (VBox, etc)

            // Coloca a nova tela no centro do BorderPane principal
            mainBorderPane.setCenter(newView);

            // Pega o controller da tela que acabamos de carregar
            T controller = loader.getController();

            // Executa a ação de inicialização (injetar o DAO, carregar os dados)
            initializingAction.accept(controller);

        } catch (IOException e) {
            e.printStackTrace();
            // (No futuro, mostraríamos um Alert de erro para o usuário)
        }
    }

    private void loadView(String absoluteName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));

            // --- A CORREÇÃO ESTÁ AQUI ---
            // 'Node' é a classe "pai" de todos os componentes (VBox, Pane, etc.)
            // e já está a ser usada no seu outro método loadView.
            Node newView = loader.load();

            mainBorderPane.setCenter(newView);

        } catch (IOException e) {
            e.printStackTrace();
            Alerts.showAlert("IO Error", "Error loading view", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}