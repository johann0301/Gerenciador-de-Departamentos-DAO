package gui;

import db.DbIntegrityException;
import gui.util.Alerts;
import gui.util.DataChangeListener;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.dao.SellerDao;
import model.entities.Seller;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class SellerListController implements Initializable, DataChangeListener {

    // --- Dependências ---
    private SellerDao sellerDao;
    private DepartmentDao departmentDao;

    // --- Componentes FXML ---
    @FXML
    private TableView<Seller> tableViewSeller;
    @FXML
    private TableColumn<Seller, Integer> tableColumnId;
    @FXML
    private TableColumn<Seller, String> tableColumnName;
    @FXML
    private TableColumn<Seller, String> tableColumnEmail;
    @FXML
    private TableColumn<Seller, Date> tableColumnBirthDate;
    @FXML
    private TableColumn<Seller, Double> tableColumnBaseSalary;
    @FXML
    private Button btNew;
    @FXML
    private Button btEdit;
    @FXML
    private Button btDelete;
    @FXML
    private Button btRefresh; // O seu botão de Refresh manual
    @FXML
    private TableColumn<Seller, String> tableColumnDepartment;

    // --- Métodos do Controller ---

    public void setSellerDao(SellerDao sellerDao) {
        this.sellerDao = sellerDao;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeNodes();
        // Inicializa o DepartmentDao (necessário para o formulário)
        this.departmentDao = DaoFactory.createDepartmentDao();
    }

    private void initializeNodes() {
        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));

        tableColumnDepartment.setCellValueFactory(cellData -> {
            // 'cellData.getValue()' devolve o objeto Seller da linha atual
            Seller seller = cellData.getValue();

            // Verificamos se o departamento do vendedor é nulo
            if (seller.getDepartment() == null) {
                return new SimpleStringProperty("-"); // Mostra um "-" se for nulo
            }

            // Se não for nulo, pegamos o nome do departamento
            String departmentName = seller.getDepartment().getName();

            // Devolvemos o nome "empacotado" num 'SimpleStringProperty'
            return new SimpleStringProperty(departmentName);
        });
    }

    public void loadSellers() {
        if (sellerDao == null) {
            throw new IllegalStateException("SellerDao was null");
        }
        List<Seller> list = sellerDao.findAll();
        ObservableList<Seller> obsList = FXCollections.observableArrayList(list);
        tableViewSeller.setItems(obsList);
    }

    // O seu botão de Refresh manual
    @FXML
    public void onBtRefreshAction() {
        System.out.println("onBtRefreshAction (Manual)");
        loadSellers();
    }

    @FXML
    public void onBtNewAction(ActionEvent event) {
        Seller newSeller = new Seller();
        createDialogForm(newSeller, "/gui/SellerForm.fxml", Utils.currentStage(event), "New Seller");
    }

    // --- AQUI ESTÁ A CORREÇÃO DE LÓGICA ---
    // O onBtEditAction agora é limpo e também chama o helper
    @FXML
    public void onBtEditAction(ActionEvent event) {
        Seller selectedSeller = tableViewSeller.getSelectionModel().getSelectedItem();

        if (selectedSeller == null) {
            Alerts.showAlert("No Selection", null, "Please select a seller to edit.", AlertType.INFORMATION);
            return;
        }

        // AGORA AMBOS (NEW E EDIT) USAM O MESMO CÓDIGO DO 'createDialogForm'
        createDialogForm(selectedSeller, "/gui/SellerForm.fxml", Utils.currentStage(event), "Edit Seller");
    }

    @FXML
    public void onBtDeleteAction(ActionEvent event) {
        Seller selected = tableViewSeller.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alerts.showAlert("No Selection", null, "Please select a seller to delete.", AlertType.INFORMATION);
            return;
        }
        Optional<ButtonType> result = Alerts.showConfirmation("Confirmation",
                "Are you sure you want to delete '" + selected.getName() + "'?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                sellerDao.deleteById(selected.getId());
                loadSellers(); // O 'Delete' funciona porque chama o refresh (loadSellers)
            }
            catch (DbIntegrityException e) {
                Alerts.showAlert("Delete Error", null, e.getMessage(), AlertType.ERROR);
            }
            catch (db.DbException e) {
                Alerts.showAlert("Delete Error", null, e.getMessage(), AlertType.ERROR);
            }
        }
    }

    // Este é o método que é chamado pelo formulário!
    @Override
    public void onDataChanged() {
        System.out.println("onDataChanged (Listener) foi chamado! Atualizando tabela...");
        loadSellers(); // O 'Edit' e 'New' chamam este
    }

    // Método auxiliar (helper) para abrir o formulário
    private void createDialogForm(Seller entity, String fxmlPath, Stage parentStage, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Pane pane = loader.load();

            // 1. Pegamos o Controller (como Object genérico)
            Object controllerObject = loader.getController();

            // 2. Verificamos se ele é do tipo que esperamos
            if (controllerObject instanceof SellerFormController) {
                // 3. Se for, fazemos o "cast" (conversão de tipo)
                SellerFormController controller = (SellerFormController) controllerObject;

                // 4. Agora podemos chamar os métodos dele
                controller.setSeller(entity);
                controller.setDaos(this.sellerDao, this.departmentDao);

                // AQUI ESTÁ A INSCRIÇÃO!
                controller.subscribeDataChangeListener(this);

                controller.updateFormData();

                // 5. Cria e mostra a nova janela (Stage)
                Stage dialogStage = new Stage();
                dialogStage.setTitle(title);
                dialogStage.setScene(new Scene(pane));
                dialogStage.initOwner(parentStage);
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.showAndWait();

            } else {
                System.out.println("Erro: O controller carregado não é um SellerFormController.");
            }

        } catch (IOException e) {
            e.printStackTrace();
            Alerts.showAlert("IO Error", "Error loading view", e.getMessage(), AlertType.ERROR);
        }
    }
}