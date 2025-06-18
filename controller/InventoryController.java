package controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import model.Medicine;
import util.MedicinesDAO;
import util.TextToSpeech;
import util.WikiFetcher;
import java.time.LocalDate;
import java.util.List;

public class InventoryController 
{
    @FXML private TableView<Medicine> tableView;
    @FXML private TableColumn<Medicine, String> colName;
    @FXML private TableColumn<Medicine, String> colCategory;
    @FXML private TableColumn<Medicine, Double> colPrice;
    @FXML private TableColumn<Medicine, Integer> colQty;
    @FXML private TableColumn<Medicine, LocalDate> colExpiry;
    
    
    @FXML private TextField nameField;
    @FXML private TextField categoryField;
    @FXML private TextField priceField;
    @FXML private TextField qtyField;
    @FXML private DatePicker expiryPicker;
    @FXML private TextField searchField;
    @FXML private TextField wikiSearchField;

    private ObservableList<Medicine> medicineList;
    private final TextToSpeech tts = new TextToSpeech();

    
    
    
    
    @FXML
    public void initialize() 
    {
        colName.setCellValueFactory(cell->new javafx.beans.property.SimpleStringProperty(cell.getValue().getName()));
        colCategory.setCellValueFactory(cell->new javafx.beans.property.SimpleStringProperty(cell.getValue().getCategory()));
        colPrice.setCellValueFactory(cell->new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getPrice()));
        colQty.setCellValueFactory(cell->new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getQuantity()));
        colExpiry.setCellValueFactory(cell->new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getExpiryDate()));
        loadMedicines();
        tableView.setOnMouseClicked(this::handleRowClick);
    }

    
    
    
    
    
    private void loadMedicines() 
    {
        List<Medicine> medicines=MedicinesDAO.getAllMedicines();
        medicineList=FXCollections.observableArrayList(medicines);
        tableView.setItems(medicineList);
    }

    
    
    
    
    private void clearForm() 
    {
        nameField.clear();
        categoryField.clear();
        priceField.clear();
        qtyField.clear();
        expiryPicker.setValue(null);
        tableView.getSelectionModel().clearSelection();
    }

     
    
    
    private Medicine getMedicineFromForm() 
    {
        try 
        {
            String name=nameField.getText();
            String category=categoryField.getText();
            String priceText=priceField.getText();
            String qtyText=qtyField.getText();
            LocalDate expiry=expiryPicker.getValue();

            if(name==null||name.isEmpty()||category==null || category.isEmpty() ||
                priceText==null|| priceText.isEmpty() ||
          qtyText==null ||qtyText.isEmpty() ||
                expiry==null) 
            {
                return null;
            }

            
            double price=Double.parseDouble(priceText);
            int qty=Integer.parseInt(qtyText);
            
            
            return new Medicine(name, category, price, qty, expiry);
        } 
        catch(Exception e) 
        {
            showAlert("Invalid input. Please check all fields.",Alert.AlertType.ERROR);
            
            return null;
        }
    }

    
    
    
    
    @FXML
    private void handleUpdate() 
    {
        Medicine med=getMedicineFromForm();
        if(med==null) 
        	return;
        
        boolean exists=MedicinesDAO.getMedicineByName(med.getName())!=null;
        if(exists) 
        {
            MedicinesDAO.updateMedicine(med);
            showAlert("Medicine updated successfully.",Alert.AlertType.INFORMATION);
        } 
        else 
        {
            MedicinesDAO.insertMedicine(med);
            showAlert("Medicine added successfully.",Alert.AlertType.INFORMATION);
        }
        loadMedicines();
        clearForm();
    }

    
     
    
    
    @FXML
    private void handleDelete() 
    {
        Medicine selected=tableView.getSelectionModel().getSelectedItem();
        if(selected!=null) 
        {
            MedicinesDAO.deleteMedicine(selected.getName());
            showAlert("Medicine deleted.",Alert.AlertType.INFORMATION);
            loadMedicines();
            clearForm();
        } 
        else 
        {
            showAlert("No medicine selected.",Alert.AlertType.WARNING);
        }
    }

    
    
    
    
    
    @FXML
    private void handleSearch() 
    {
        String keyword=searchField.getText().trim();
        if(!keyword.isEmpty()) 
        {
            List<Medicine> results=MedicinesDAO.searchMedicines(keyword);
            tableView.setItems(FXCollections.observableArrayList(results));
        }
    }

    
    
    
    
    @FXML
    private void handleClearSearch() 
    {
        searchField.clear();
        loadMedicines();
    }

    
    
    
    @FXML
    private void handleRowClick(MouseEvent event) 
    {
        Medicine selected=tableView.getSelectionModel().getSelectedItem();
        if(selected!=null) 
        {
            nameField.setText(selected.getName());
            categoryField.setText(selected.getCategory());
            priceField.setText(String.valueOf(selected.getPrice()));
            qtyField.setText(String.valueOf(selected.getQuantity()));
            expiryPicker.setValue(selected.getExpiryDate());
        }
    }

    
    
    @FXML
    private void handleExpiryAlert() 
    {
        StringBuilder alertText=new StringBuilder("Expired or soon expiring medicines:\n\n");
        boolean found=false;
        LocalDate now=LocalDate.now();

        for(Medicine m:medicineList) 
        {
            if(m.getExpiryDate().isBefore(now.plusDays(30))) 
            {
                alertText.append(m.getName()).append("(").append(m.getExpiryDate()).append(")\n");
                found=true;
            }
        }

        if(found) 
        {
            showAlert(alertText.toString(),Alert.AlertType.WARNING);
            tts.speak(alertText.toString());  
        } 
        else 
        {
            String msg="No medicines nearing expiry.";
            showAlert(msg,Alert.AlertType.INFORMATION);
            tts.speak(msg);
        }
    }

    
    
    private void showAlert(String msg,Alert.AlertType type) 
    {
        Alert alert=new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    
    
    
    @FXML
    private void handleWikiSearch() 
    {
        String query=wikiSearchField.getText().trim();
        if(query.isEmpty()) 
        {
            Medicine selected=tableView.getSelectionModel().getSelectedItem();
            if(selected!=null) 
            {
                query=selected.getName();
                String info="Medicine "+selected.getName() +
                        ", Category "+selected.getCategory() +
                        ", Price "+selected.getPrice() +
                        ", Quantity "+selected.getQuantity() +
                        ", Expiry "+selected.getExpiryDate();
                tts.speak(info);
                return;
            } 
            else 
            {
                showAlert("Please select a medicine or type a search term.", Alert.AlertType.WARNING);
                return;
            }
        }
        
        // Wiki summary fetch
        String summary=WikiFetcher.fetchSummary(query);
        showAlert(summary,Alert.AlertType.INFORMATION);
        tts.speak(summary);
    }

    
    
    
    
    @FXML
    private void handleStopVoice() 
    {
        tts.stop();
    }
}
