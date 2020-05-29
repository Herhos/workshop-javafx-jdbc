package gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;

public class MainViewController implements Initializable
{
	@FXML
	private MenuItem menuItemSeller;
	@FXML
	private MenuItem menuItemDepartment;
	@FXML
	private MenuItem menuItemAbout;
	
	@FXML
	public void onActionMenuItemSeller()
	{
		System.out.println("Teste seller");
	}
	
	@FXML
	public void onActionMenuItemDepartment()
	{
		System.out.println("Teste department");
	}
	
	@FXML
	public void onActionMenuItemAbout()
	{
		System.out.println("Teste about");
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		
	}
}
