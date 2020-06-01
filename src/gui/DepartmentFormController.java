package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;

public class DepartmentFormController implements Initializable
{
	private Department entity;
	
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;
	@FXML
	private Label lblErrorName;
	@FXML
	private Button btnSalve;
	@FXML
	private Button btnCancel;
	
	public void setDepartment(Department entity)
	{
		this.entity = entity;
	}
	
	@FXML
	public void onActionBtnSave()
	{
		System.out.println("onActionBtnSave");
	}
	
	@FXML
	public void onActionBtnCancel()
	{
		System.out.println("onActionBtnCancel");
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		initializeNodes();
	}
	
	public void initializeNodes()
	{
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLengtht(txtName, 35);
	}
	
	public void updateFormData()
	{
		if  (entity == null)
		{
			throw new IllegalStateException("Entidade nula!");
		}
		
		// valueOf converte a string capturada pelo getId em inteiro
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
	}
}
