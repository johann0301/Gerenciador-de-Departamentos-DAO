package gui;

import db.DbIntegrityException;
import gui.util.Alerts;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import java.util.Optional;
import gui.util.DataChangeListener;
import gui.util.Utils; // Mantenha o Utils
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene; // 1. Importe a Scene
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane; // 2. Importe o Pane
import javafx.stage.Modality; // 3. Importe o Modality
import javafx.stage.Stage;
import model.dao.DepartmentDao;
import model.entities.Department;

import java.io.IOException; // 4. Importe o IOException
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DepartmentListController implements Initializable, DataChangeListener {

    private DepartmentDao departmentDao;

    @FXML
    private TableView<Department> tableViewDepartment;
    @FXML
    private TableColumn<Department, Integer> tableColumnId;
    @FXML
    private TableColumn<Department, String> tableColumnName;
    @FXML
    private Button btNew;
    @FXML
    private Button btEdit;
    @FXML
    private Button btDelete;
    @FXML
    private Button btRefresh;

    public void setDepartmentDao(DepartmentDao departmentDao) {
        this.departmentDao = departmentDao;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeNodes();
    }

    private void initializeNodes() {
        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
    }

    public void loadDepartments() {
        if (departmentDao == null) {
            throw new IllegalStateException("DepartmentDao was null");
        }
        List<Department> list = departmentDao.findAll();
        ObservableList<Department> obsList = FXCollections.observableArrayList(list);
        tableViewDepartment.setItems(obsList);
    }

    // --- ESTE MÉTODO FOI COMPLETAMENTE REESCRITO ---
    @FXML
    public void onBtNewAction(ActionEvent event) {
        System.out.println("onBtNewAction");

        try {
            // 1. Crie uma nova entidade Department (vazia)
            Department newDepartment = new Department();

            // 2. Carregue o FXML do formulário
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/DepartmentForm.fxml"));
            Pane pane = loader.load(); // Carrega o 'Pane' (GridPane)

            // 3. "Injete" as dependências ANTES de mostrar a janela
            DepartmentFormController controller = loader.getController();
            controller.setDepartment(newDepartment);
            controller.setDepartmentDao(this.departmentDao);
            controller.subscribeDataChangeListener(this);

            // 4. Atualize os dados do formulário (para mostrar o ID em branco, etc)
            controller.updateFormData();

            // 5. Configure a nova Janela (Stage) para o formulário
            Stage dialogStage = new Stage();
            dialogStage.setTitle("New Department");
            dialogStage.setScene(new Scene(pane));

            // Pega a janela "pai" (a janela principal)
            Stage parentStage = Utils.currentStage(event);
            dialogStage.initOwner(parentStage);

            // Define como "modal" (bloqueia a janela pai até ser fechada)
            dialogStage.initModality(Modality.WINDOW_MODAL);

            // 6. Mostre a janela e espere
            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            // (No futuro, mostraríamos um Alert de erro)
        }
    }

    @FXML
    public void onBtEditAction(ActionEvent event) {
        System.out.println("onBtEditAction");

        // 1. Pega o item selecionado na tabela
        Department selectedDepartment = tableViewDepartment.getSelectionModel().getSelectedItem();

        // 2. Se nada estiver selecionado, não faz nada
        if (selectedDepartment == null) {
            // (No futuro, mostraríamos um Alert "Nenhum item selecionado")
            System.out.println("Nenhum departamento selecionado!");
            return;
        }

        // 3. Se um item foi selecionado, abrimos o formulário (igual ao 'New')
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/DepartmentForm.fxml"));
            Pane pane = loader.load();

            DepartmentFormController controller = loader.getController();

            // 4. AQUI ESTÁ A MUDANÇA: Passamos o 'selectedDepartment'
            controller.setDepartment(selectedDepartment);

            controller.setDepartmentDao(this.departmentDao);
            controller.subscribeDataChangeListener(this);

            // 5. Preenche o formulário com os dados do item
            controller.updateFormData();

            // 6. O resto é igual: criar e mostrar a janela
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Department"); // Mude o título
            dialogStage.setScene(new Scene(pane));
            Stage parentStage = Utils.currentStage(event);
            dialogStage.initOwner(parentStage);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onBtDeleteAction(ActionEvent event) {
        System.out.println("onBtDeleteAction");

        // 1. Pega o item selecionado na tabela
        Department selected = tableViewDepartment.getSelectionModel().getSelectedItem();

        // 2. Se nada estiver selecionado, mostra um alerta
        if (selected == null) {
            Alerts.showAlert("No Selection", null,
                    "Please select a department to delete.", AlertType.INFORMATION);
            return;
        }

        // 3. Se um item foi selecionado, pede confirmação
        Optional<ButtonType> result = Alerts.showConfirmation("Confirmation",
                "Are you sure you want to delete '" + selected.getName() + "'?");

        // 4. Verifica se o utilizador clicou em "OK"
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (departmentDao == null) {
                throw new IllegalStateException("Dao was null");
            }
            try {
                // 5. Tenta apagar do banco
                departmentDao.deleteById(selected.getId());

                // 6. Se conseguiu, atualiza a tabela
                loadDepartments();

            }
            // 7. AQUI ESTÁ O TRATAMENTO DE ERRO!
            // Se o utilizador tentar apagar um departamento que tem vendedores
            catch (DbIntegrityException e) {
                Alerts.showAlert("Delete Error", null,
                        "Cannot delete department. It is still associated with sellers.", AlertType.ERROR);
            }
            // 8. Outros erros de banco
            catch (db.DbException e) {
                Alerts.showAlert("Delete Error", null, e.getMessage(), AlertType.ERROR);
            }
        }
    }

    @FXML
    public void onBtRefreshAction() {
        System.out.println("onBtRefreshAction (Manual)");
        // O "Refresh" é simplesmente chamar o loadDepartments!
        loadDepartments();
    }

    @Override
    public void onDataChanged() {
        // Simplesmente recarrega a tabela!
        loadDepartments();
    }
}