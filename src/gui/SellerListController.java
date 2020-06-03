package gui;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener
{
	// Dependência de SellerService com SellerListController
	private SellerService service;
	
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
	private TableColumn<Seller, Seller> tableColumnEdit;
	
	@FXML
	private TableColumn<Seller, Seller> tableColumnRemove;
	
	@FXML
	private Button btnNew;
	
	private ObservableList<Seller> obsList;
	
	@FXML
	private void onActionBtnNew(ActionEvent event)
	{
		Stage parentStage = Utils.currentStage(event);
		Seller obj = new Seller();
		createDialogForm(obj, "/gui/SellerForm.fxml", parentStage);
	}
	
	// Injeção da dependência na classe
	public void setSellerService(SellerService service)
	{
		this.service = service;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		initializeNodes();
	}

	private void initializeNodes()
	{
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
		tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		Utils.formatTableColumnDouble(tableColumnBaseSalary, 2);
		
		// Código para o TableView autoajustar ao tamanho da janela
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
		tableViewSeller.prefWidthProperty().bind(stage.widthProperty());
	}
	
	public void updateTableView()
	{
		if (service == null)
		{
			throw new IllegalStateException("Serviço nulo!");
		}
		
		List<Seller> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewSeller.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}
	
	// Método para carregar o formulário departamento na forma modal
	private void createDialogForm(Seller obj, String absoluteName, Stage parentStage)
	{
//		try
//		{
//			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
//			Pane pane = loader.load();
//			
//			SellerFormController controller = loader.getController();
//			controller.setSeller(obj);
//			controller.setSellerService(new SellerService());
//			controller.subscribeDataChangeListener(this);
//			controller.updateFormData();
//			
//			Stage dialogStage = new Stage();
//			dialogStage.setTitle("Digite o nome do departamento!");
//			dialogStage.setScene(new Scene(pane));
//			dialogStage.setResizable(false);
//			dialogStage.initOwner(parentStage);
//			dialogStage.initModality(Modality.WINDOW_MODAL);
//			dialogStage.showAndWait();
//		}
//		catch (IOException e)
//		{
//			Alerts.showAlert("IO Exception", "Erro ao carregar a tela!", e.getMessage(), AlertType.ERROR);
//		}
	}

	@Override
	public void onDataChange()
	{
		updateTableView();
	}
	
	private void initEditButtons()
	{
		tableColumnEdit.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		
		tableColumnEdit.setCellFactory(param ->	new TableCell<Seller, Seller>()
			{		
				private final Button button = new Button("Edit");
		
				@Override
				protected void updateItem(Seller obj, boolean empty)
				{
					super.updateItem(obj, empty);
					if (obj == null)
					{
						setGraphic(null);
						return;
					}
					setGraphic(button);
					button.setOnAction(event ->
					createDialogForm(obj, "/gui/SellerForm.fxml",
						Utils.currentStage(event)));
				}
			});
	}
	
	private void initRemoveButtons()
	{
		tableColumnRemove.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		
		tableColumnRemove.setCellFactory(param -> new TableCell<Seller, Seller>()
			{
				private final Button button = new Button("Delete");
				
				@Override
				protected void updateItem(Seller obj, boolean empty)
				{
					super.updateItem(obj, empty);
					if (obj == null)
					{
						setGraphic(null);
						return;
					}
					setGraphic(button);
					button.setOnAction(event -> removeEntity(obj));
				}
			});
	}

	private void removeEntity(Seller obj)
	{
		Optional<ButtonType> result = Alerts.showConfirmation
			("Confirmação", "Tem certeza que deseja excluir?");
		
		if (result.get() == ButtonType.OK)
		{
			if (service == null)
			{
				throw new IllegalStateException("Serviço nulo!");
			}
			
			try
			{
				service.remove(obj);
				updateTableView();
			}
			catch (DbIntegrityException e)
			{
				Alerts.showAlert("Erro ao deletar objeto!", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
}
