package gui;

import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.DataChangeListener;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import model.dao.DepartmentDao;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class SellerFormController implements Initializable {

    // --- Dependências ---
    private Seller entity;
    private SellerDao sellerDao;
    private DepartmentDao departmentDao;

    // A lista de listeners que serão notificados
    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

    // --- Componentes FXML ---
    @FXML
    private TextField txtId;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtEmail;
    @FXML
    private DatePicker dpBirthDate;
    @FXML
    private TextField txtBaseSalary;
    @FXML
    private ComboBox<Department> comboBoxDepartment;

    // Labels de Erro
    @FXML
    private Label labelErrorName;
    @FXML
    private Label labelErrorEmail;
    @FXML
    private Label labelErrorBirthDate;
    @FXML
    private Label labelErrorBaseSalary;
    @FXML
    private Label labelErrorDepartment;

    @FXML
    private Button btSave;
    @FXML
    private Button btCancel;

    // --- Injeção de Dependência (OS MÉTODOS QUE ESTAVAM A FALTAR) ---

    public void setSeller(Seller entity) {
        this.entity = entity;
    }

    public void setDaos(SellerDao sellerDao, DepartmentDao departmentDao) {
        this.sellerDao = sellerDao;
        this.departmentDao = departmentDao;
    }

    public void subscribeDataChangeListener(DataChangeListener listener) {
        dataChangeListeners.add(listener);
    }

    // --- Métodos de Ação dos Botões ---

    @FXML
    public void onBtSaveAction(ActionEvent event) {
        if (entity == null || sellerDao == null || departmentDao == null) {
            throw new IllegalStateException("Dependencies were null");
        }

        try {
            // Pega os dados do formulário e valida
            getEntityFromFormData();

            // Salva no banco (insert ou update)
            if (entity.getId() == null) {
                sellerDao.insert(entity);
            } else {
                sellerDao.update(entity);
            }

            // Notifica o listener (a lista)
            notifyDataChangeListeners();

            // Fecha a janela
            Utils.currentStage(event).close();

        } catch (DbException e) {
            // Mostra o alerta de erro de validação
            Alerts.showAlert("Validation Error", null, e.getMessage(), AlertType.ERROR);
        }
    }

    @FXML
    public void onBtCancelAction(ActionEvent event) {
        Utils.currentStage(event).close();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Aplica as restrições (constraints) aos campos
        Constraints.setTextFieldInteger(txtId);
        Constraints.setTextFieldMaxLength(txtName, 70);
        Constraints.setTextFieldDouble(txtBaseSalary);
        Constraints.setTextFieldMaxLength(txtEmail, 60);

        // Formata o DatePicker
        Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
    }

    /**
     * Pega os dados da entidade e coloca nos campos de texto.
     */
    public void updateFormData() {
        if (entity == null) {
            throw new IllegalStateException("Entity was null");
        }
        if (departmentDao == null) {
            throw new IllegalStateException("DepartmentDao was null. (Did you call setDaos?)");
        }

        // Carrega o ComboBox (AGORA o departmentDao existe)
        List<Department> depList = departmentDao.findAll();
        ObservableList<Department> obsList = FXCollections.observableArrayList(depList);
        comboBoxDepartment.setItems(obsList);

        // Preenche os campos
        txtId.setText((entity.getId() == null) ? "" : String.valueOf(entity.getId()));
        txtName.setText(entity.getName());
        txtEmail.setText(entity.getEmail());

        if (entity.getBaseSalary() != null) {
            txtBaseSalary.setText(String.format(Locale.US, "%.2f", entity.getBaseSalary()));
        }

        // Preenche a data (Date -> LocalDate)
        if (entity.getBirthDate() != null) {
            // A CORREÇÃO DA DATA (java.sql.Date) ESTÁ AQUI:
            dpBirthDate.setValue(new java.sql.Date(entity.getBirthDate().getTime()).toLocalDate());
        }

        if (entity.getDepartment() == null) {
            comboBoxDepartment.getSelectionModel().selectFirst();
        } else {
            comboBoxDepartment.setValue(entity.getDepartment());
        }
    }

    /**
     * Pega os dados dos campos de texto (txtName) e
     * atualiza a entidade 'this.entity' na memória.
     */
    private void getEntityFromFormData() {
        Map<String, String> errors = new HashMap<>();

        String name = txtName.getText();
        if (name == null || name.trim().isEmpty()) {
            errors.put("Name", "Name can't be empty");
        }
        entity.setName(name);

        String email = txtEmail.getText();
        if (email == null || email.trim().isEmpty()) {
            errors.put("Email", "Email can't be empty");
        }
        entity.setEmail(email);

        String salaryStr = txtBaseSalary.getText();
        if (salaryStr == null || salaryStr.trim().isEmpty()) {
            errors.put("BaseSalary", "Base Salary can't be empty");
        } else {
            try {
                entity.setBaseSalary(Double.parseDouble(salaryStr.replace(",", ".")));
            } catch (NumberFormatException e) {
                errors.put("BaseSalary", "Must be a valid number");
            }
        }

        LocalDate date = dpBirthDate.getValue();
        if (date == null) {
            errors.put("BirthDate", "Birth Date can't be empty");
        } else {
            entity.setBirthDate(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }

        Department dep = comboBoxDepartment.getValue();
        if (dep == null) {
            errors.put("Department", "Department must be selected");
        }
        entity.setDepartment(dep);

        setValidationErrors(errors);

        if (!errors.isEmpty()) {
            throw new DbException(errors.values().iterator().next());
        }
    }

    /**
     * Pega o Map de erros e os exibe nos Labels de erro.
     */
    private void setValidationErrors(Map<String, String> errors) {
        labelErrorName.setText(errors.getOrDefault("Name", ""));
        labelErrorEmail.setText(errors.getOrDefault("Email", ""));
        labelErrorBaseSalary.setText(errors.getOrDefault("BaseSalary", ""));
        labelErrorBirthDate.setText(errors.getOrDefault("BirthDate", ""));
        labelErrorDepartment.setText(errors.getOrDefault("Department", ""));
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