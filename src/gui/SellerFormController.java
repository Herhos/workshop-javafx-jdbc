package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable
{
	private Seller entity;
	
	private SellerService service;
	
	private DepartmentService departmentService;
	
	private List<DataChangeListener> dataChangeListeners = new
		ArrayList<>();
	
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;
	@FXML
	private TextField txtEmail;
	@FXML
	private DatePicker dpkBirthDate;
	@FXML
	private TextField txtBaseSalary;
	@FXML
	private ComboBox<Department> cboDepartment;
	@FXML
	private Label lblErrorName;
	@FXML
	private Label lblErrorEmail;
	@FXML
	private Label lblErrorBirthDate;
	@FXML
	private Label lblErrorBaseSalary;
	@FXML
	private Button btnSalve;
	@FXML
	private Button btnCancel;
	
	private ObservableList<Department> obsList;
	
	public void setSeller(Seller entity)
	{
		this.entity = entity;
	}
	
	public void setServices(SellerService service, DepartmentService departmentService)
	{
		this.service = service;
		this.departmentService = departmentService;
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener)
	{
		dataChangeListeners.add(listener);
	}
	
	@FXML
	public void onActionBtnSave(ActionEvent event)
	{
		if (entity == null)
		{
			throw new IllegalStateException("Entidade nula!");
		}
		
		if (service == null)
		{
			throw new IllegalStateException("Serviço nulo!");
		}
		
		try
		{
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		}
		catch (ValidationException e)
		{
			setErrorMessages(e.getErrors());
		}
		catch (DbException e)
		{
			Alerts.showAlert("Erro ao salvar objeto!", null,
				e.getMessage(), AlertType.ERROR);
		}
	}
	
	private void notifyDataChangeListeners()
	{
		for (DataChangeListener listener : dataChangeListeners)
		{
			listener.onDataChange();
		}
	}

	private Seller getFormData()
	{
		Seller obj = new Seller();
		
		ValidationException exception = new ValidationException("Erro de validação!");
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		
		if (txtName.getText() == null || txtName.getText().trim().equals(""))
		{
			exception.addError("name", " Campo vazio! Preecha-o!");
		}
		
		obj.setName(txtName.getText());
		
		if (txtEmail.getText() == null || txtEmail.getText().trim().equals(""))
		{
			exception.addError("email", " Campo vazio! Preecha-o!");
		}
		
		obj.setEmail(txtEmail.getText());
		
		if (dpkBirthDate == null)
		{
			exception.addError("birthDate", " Campo vazio! Preecha-o!");
		}
		else
		{
			Instant instant = Instant.from(dpkBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setBirthDate(Date.from(instant));
		}		
		
		if (txtBaseSalary.getText() == null || txtBaseSalary.getText().trim().equals(""))
		{
			exception.addError("baseSalary", " Campo vazio! Preecha-o!");
		}
		obj.setBaseSalary(Utils.tryParseToDouble(txtBaseSalary.getText()));
		
		obj.setDepartment(cboDepartment.getValue());
		
		if (exception.getErrors().size() > 0)
		{
			throw exception;
		}
		
		return obj;
	}

	@FXML
	public void onActionBtnCancel(ActionEvent event)
	{
		Utils.currentStage(event).close();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		initializeNodes();
	}
	
	public void initializeNodes()
	{
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLengtht(txtName, 70);
		Constraints.setTextFieldMaxLengtht(txtEmail, 70);
		Constraints.setTextFieldDouble(txtBaseSalary);
		Utils.formatDatePicker(dpkBirthDate, "dd/MM/yyyy");
		initializeComboBoxDepartment();
	}
	
	public void updateFormData()
	{
		if  (entity == null)
		{
			throw new IllegalStateException("Entidade nula!");
		}
		
		// valueOf converte string capturada pelo getId em inteiro
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		
		if (entity.getBirthDate() != null)
		{
			dpkBirthDate.setValue(LocalDateTime.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()).toLocalDate());			
		}
		Locale.setDefault(Locale.US);
		
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
		
		if (entity.getDepartment() == null)
		{
			cboDepartment.getSelectionModel().selectFirst();
		}
		else
		{
			cboDepartment.setValue(entity.getDepartment());			
		}
	}
	
	public void loadAssociatedObjects()
	{
		if (departmentService == null)
		{
			throw new IllegalStateException("DepartmentService nulo!");
		}
		List<Department> list = departmentService.findAll();
		obsList = FXCollections.observableArrayList(list);
		cboDepartment.setItems(obsList);
	}
	
	private void setErrorMessages(Map<String, String> errors)
	{
		Set<String> fields = errors.keySet();
		
		// Operadores ternários para substituir os if/else.
		lblErrorName.setText((fields.contains("name")) ? errors.get("name") : "");
		lblErrorEmail.setText((fields.contains("email")) ? errors.get("email") : "");
		lblErrorBirthDate.setText((fields.contains("birthDate")) ? errors.get("birthDate") : "");
		lblErrorBaseSalary.setText((fields.contains("baseSalary")) ? errors.get("baseSalary") : "");		
	}
	
	private void initializeComboBoxDepartment()
	{
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>()
			{
				@Override
				protected void updateItem(Department item, boolean empty)
				{
					super.updateItem(item, empty);
					setText(empty ? "" : item.getName());
				}
			};
			cboDepartment.setCellFactory(factory);
			cboDepartment.setButtonCell(factory.call(null));
	}
}
