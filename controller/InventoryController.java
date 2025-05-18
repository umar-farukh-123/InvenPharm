package controller;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.Scanner;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import org.json.JSONObject;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Medicine;
import service.ExcelService;


public class InventoryController {

    @FXML private TextField wikiSearchField, searchField, nameField, categoryField, priceField, qtyField;
    @FXML private TextArea wikiInfoArea;
    @FXML private TableView<Medicine> tableView;
    @FXML private TableColumn<Medicine, String> colName, colCategory;
    @FXML private TableColumn<Medicine, Double> colPrice;
    @FXML private TableColumn<Medicine, Integer> colQty;
    @FXML private TableColumn<Medicine, LocalDate> colExpiry;
    @FXML private DatePicker expiryPicker;

    private ObservableList<Medicine> inventory;
    private Voice voice;

    public void initialize() {
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        VoiceManager vm = VoiceManager.getInstance();
        voice = vm.getVoice("kevin16");
        if (voice != null) voice.allocate();

        setupTable();
    }

    private void speak(String message) {
        if (voice != null && message != null && !message.isEmpty()) {
            new Thread(() -> voice.speak(message)).start();
        }
    }

    private void setupTable() {
        inventory = FXCollections.observableArrayList(ExcelService.loadInventory());
        tableView.setItems(inventory);

        colName.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getName()));
        colCategory.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getCategory()));
        colPrice.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getPrice()));
        colQty.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getQuantity()));
        colExpiry.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getExpiryDate()));

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                nameField.setText(newVal.getName());
                categoryField.setText(newVal.getCategory());
                priceField.setText(String.valueOf(newVal.getPrice()));
                qtyField.setText(String.valueOf(newVal.getQuantity()));
                expiryPicker.setValue(newVal.getExpiryDate());
            }
        });
    }

    @FXML
    private void handleWikiSearch() {
        String medicine = wikiSearchField.getText().trim();
        if (medicine.isEmpty()) {
            wikiInfoArea.setText("Please enter a medicine name.");
            return;
        }

        new Thread(() -> {
            try {
                String summary = fetchWikipediaSummary(medicine);
                if (summary == null || summary.isEmpty()) {
                    summary = "No information found on Wikipedia.";
                }

                String finalSummary = summary;
                Platform.runLater(() -> wikiInfoArea.setText(finalSummary));
                speak(finalSummary);
            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> wikiInfoArea.setText("Error fetching information: " + ex.getMessage()));
            }
        }).start();
    }


    private String fetchWikipediaSummary(String query) throws Exception {
        String encoded = URLEncoder.encode(query, "UTF-8");
        String urlStr = "https://en.wikipedia.org/w/api.php?action=query&prop=extracts&exintro&explaintext&format=json&titles=" + encoded;

        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "InvenPharmApp/1.0");

        Scanner sc = new Scanner(conn.getInputStream());
        StringBuilder jsonText = new StringBuilder();
        while (sc.hasNext()) jsonText.append(sc.nextLine());
        sc.close();

        JSONObject pages = new JSONObject(jsonText.toString()).getJSONObject("query").getJSONObject("pages");
        for (String key : pages.keySet()) {
            JSONObject page = pages.getJSONObject(key);
            if (page.has("extract")) {
                return page.getString("extract");
            }
        }
        return null;
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().toLowerCase();
        speak("Showing search results for " + query);

        ObservableList<Medicine> filtered = inventory.filtered(med ->
            med.getName().toLowerCase().contains(query) ||
            med.getCategory().toLowerCase().contains(query)
        );
        tableView.setItems(filtered);
    }

    @FXML
    private void handleClearSearch() {
        searchField.clear();
        tableView.setItems(inventory);
        speak("Search cleared. Showing full inventory.");
    }

    @FXML
    private void handleUpdate() {
        Medicine selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a row to update.");
            return;
        }

        try {
            selected.setName(nameField.getText());
            selected.setCategory(categoryField.getText());
            selected.setPrice(Double.parseDouble(priceField.getText()));
            selected.setQuantity(Integer.parseInt(qtyField.getText()));
            selected.setExpiryDate(expiryPicker.getValue());

            tableView.refresh();
            ExcelService.saveInventory(inventory);

            showAlert("Update Success", "Medicine details updated.");
        } catch (Exception e) {
            showAlert("Invalid Input", "Please enter valid data.");
        }
    }

    @FXML
    private void handleDelete() {
        Medicine selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a row to delete.");
            return;
        }

        inventory.remove(selected);
        tableView.refresh();
        ExcelService.saveInventory(inventory);

        showAlert("Delete Success", "Medicine deleted from inventory.");
    }

    @FXML
    private void handleExpiryAlert() {
        StringBuilder alertList = new StringBuilder();
        LocalDate today = LocalDate.now();

        for (Medicine med : inventory) {
            LocalDate expiry = med.getExpiryDate();
            if (expiry != null && expiry.isBefore(today.plusDays(30))) {
                alertList.append(med.getName()).append(" (Expires on: ")
                         .append(expiry).append(")\n");
            }
        }

        if (alertList.length() == 0) {
            showAlert("All Good", "No medicines expiring within 30 days.");
        } else {
            showAlert("Expiry Alert", "Medicines expiring soon:\n" + alertList);
        }
    }

    @FXML
    private void handleStopVoice() {
        if (voice != null && voice.getAudioPlayer() != null) {
            voice.getAudioPlayer().cancel(); // Stop ongoing audio
        }
    }

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });

        speak(message);
    }
}
