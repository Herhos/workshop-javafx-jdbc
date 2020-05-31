package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentService;

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
		loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) ->
			{
				controller.setDepartmentService(new DepartmentService());
				controller.updateTableView();
			});
	}
	
	@FXML
	public void onActionMenuItemAbout()
	{
		loadView("/gui/About.fxml", x -> {});
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		
	}
	
	// Método para carregar a tela about dentro da tela principal.
	private synchronized <T> void loadView(String absoluteName, Consumer<T> intitializingAction)
	{
		try
		{
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVbox = loader.load();
			
			Scene mainScene = Main.getMainScene();
			// getRoot pega o primeiro elemento da view
			VBox mainVbox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
			
			// Código para incluir os filhos da tela about na tela principal
			Node mainMenu = mainVbox.getChildren().get(0);
			mainVbox.getChildren().clear();
			mainVbox.getChildren().add(mainMenu);
			mainVbox.getChildren().addAll(newVbox.getChildren());
			
			T controller = loader.getController();
			intitializingAction.accept(controller);
		}
		catch (IOException e)
		{
			Alerts.showAlert("IO Exception", "Erro ao carregar a tela!",
				e.getMessage(), AlertType.ERROR);
		}
	}	
}
