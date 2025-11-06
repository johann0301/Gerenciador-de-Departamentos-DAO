package gui;

import db.DbException; // Importe suas exceções
import gui.util.DataChangeListener; // 1. Importe o Listener
import gui.util.Utils;
import javafx.event.ActionEvent; // 2. Importe o ActionEvent
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import model.dao.DepartmentDao;
import model.entities.Department;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DepartmentFormController implements Initializable {

    // --- Dependências ---
    private DepartmentDao departmentDao;
    private Department entity;

    // 3. Lista de 'Listeners' que serão notificados
    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

    // --- Componentes FXML ---
    @FXML
    private TextField txtId;
    @FXML
    private TextField txtName;
    @FXML
    private Button btSave;
    @FXML
    private Button btCancel;

    // --- Injeção de Dependência ---
    public void setDepartmentDao(DepartmentDao departmentDao) {
        this.departmentDao = departmentDao;
    }

    public void setDepartment(Department entity) {
        this.entity = entity;
    }

    // 4. Método para "se inscrever" para receber avisos
    public void subscribeDataChangeListener(DataChangeListener listener) {
        dataChangeListeners.add(listener);
    }

    // --- Métodos de Ação dos Botões ---

    @FXML
    public void onBtSaveAction(ActionEvent event) { // 5. Precisa do ActionEvent
        // Validação (não podem ser nulos)
        if (entity == null) {
            throw new IllegalStateException("Entity was null");
        }
        if (departmentDao == null) {
            throw new IllegalStateException("Dao was null");
        }

        try {
            getEntityFromFormData();

            // --- AQUI ESTÁ A LÓGICA DO UPDATE ---
            // Se o ID for nulo, é um INSERT. Se não, é um UPDATE.
            if (entity.getId() == null) {
                departmentDao.insert(entity);
                System.out.println("Inserido!");
            }
            else {
                departmentDao.update(entity);
                System.out.println("Atualizado!");
            }

            notifyDataChangeListeners();
            Utils.currentStage(event).close();

        } catch (DbException e) {
            e.printStackTrace();
            // (Ainda precisamos de um Alert de erro aqui)
        }
    }

    @FXML
    public void onBtCancelAction(ActionEvent event) { // 10. Precisa do ActionEvent
        // Apenas fecha a janela do formulário
        Utils.currentStage(event).close();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // (Aqui podemos adicionar validações)
    }

    /**
     * Pega os dados dos campos de texto (txtName) e
     * atualiza a entidade 'this.entity' na memória.
     */
    private void getEntityFromFormData() {
        // (Validação simples)
        if (txtName.getText() == null || txtName.getText().trim().isEmpty()) {
            throw new DbException("Name field can't be empty");
        }

        entity.setName(txtName.getText());
        // (O ID é nulo, pois é um 'insert', o banco vai gerar)
    }

    /**
     * Pega os dados da entidade e coloca nos campos de texto.
     */
    public void updateFormData() {
        if (entity == null) {
            throw new IllegalStateException("Entity was null");
        }
        txtId.setText((entity.getId() == null) ? "" : String.valueOf(entity.getId()));
        txtName.setText(entity.getName());
    }

    /**
     * Método auxiliar para notificar todos os listeners da lista.
     */
    private void notifyDataChangeListeners() {
        for (DataChangeListener listener : dataChangeListeners) {
            listener.onDataChanged();
        }
    }
}