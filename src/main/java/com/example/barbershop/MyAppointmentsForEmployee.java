package com.example.barbershop;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.*;
import java.time.format.DateTimeParseException;

public class MyAppointmentsForEmployee {

    Integer employeeId;
    String usernameSpecialist;

    @FXML private TableColumn<Appointments, String> colStartTime;
    @FXML private TableColumn<Appointments, String> colDateCreated;
    @FXML private TableColumn<Appointments, String> colType;
    @FXML private TableColumn<Appointments, Integer> colPrice;
    @FXML private TableColumn<Appointments, Boolean> colCancel;
    @FXML private TableColumn<Appointments, String> colId;
    @FXML private TableColumn<Appointments, String> colUsername;
    @FXML private Button backButton;


    @FXML private TableView<Appointments> appointmentsTable;
    @FXML private Button btnDelete;


    @FXML
    public void initialize() {
        colId.setCellValueFactory(data -> data.getValue().appointmentIDProperty().asObject().asString());
        colUsername.setCellValueFactory(data -> data.getValue().usernameProperty());
        colDateCreated.setCellValueFactory(data -> data.getValue().dateCreatedProperty());
        colStartTime.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStartTime()));
        colType.setCellValueFactory(data -> data.getValue().appointmentTypeProperty());
        colPrice.setCellValueFactory(data -> data.getValue().priceProperty().asObject());
        colCancel.setCellValueFactory(data -> data.getValue().canceledProperty().asObject());

        btnDelete.setOnAction(event -> deleteSelectedAppointment());
    }

    public void setUsernameSpecialist(String usernameSpecialist, int employeeId) {
        this.usernameSpecialist = usernameSpecialist;
        this.employeeId = employeeId;
        System.out.println(usernameSpecialist);
        System.out.println(employeeId);
        try {
            appointmentsTable.setItems(getRecords());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ObservableList<Appointments> getRecords() throws SQLException, ClassNotFoundException {
        DataBaseConnection dataBaseConnection = new DataBaseConnection();
        Connection connection = dataBaseConnection.getConnection();

        String sql = "SELECT a.id AS appointment_id, " +
                "a.date, " +
                "u.username, " +
                "a.start_time, " +
                "a.appointment_type, " +
                "a.price_final, " +
                "a.canceled " +
                "FROM appointments a " +
                "JOIN user_account u ON a.user_id = u.id " +
                "WHERE a.employee_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setInt(1, employeeId);

        ResultSet resultSet = statement.executeQuery();

        if (!resultSet.isBeforeFirst()) {
            System.out.println("No appointments found for employee ID: " + employeeId);
        }

        return getAppointmentsObjects(resultSet);
    }


    private static ObservableList<Appointments> getAppointmentsObjects(ResultSet resultSet) throws SQLException {
        ObservableList<Appointments> appList = FXCollections.observableArrayList();

        while (resultSet.next()) {
            Appointments app = new Appointments();

            app.setAppointmentID(resultSet.getInt("appointment_id"));  // Matches "id AS appointment_id"
            app.setUsername(resultSet.getString("username"));
            app.setDateTimeCreated(resultSet.getDate("date").toString());  // Matches "date"
            app.setStartTime(resultSet.getString("start_time"));
            app.setAppointmentType(resultSet.getString("appointment_type"));  // Matches "appointment_type"
            app.setPrice(resultSet.getInt("price_final"));  // Matches "price_final"
            app.setCanceled(resultSet.getBoolean("canceled"));  // Matches "canceled"

            appList.add(app);
        }
        return appList;
    }

    private void deleteSelectedAppointment() {
        Appointments selectedAppointment = appointmentsTable.getSelectionModel().getSelectedItem();

        if (selectedAppointment != null) {
            try {
                String startTimeString = selectedAppointment.getStartTime();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime startDateTime = LocalDateTime.parse(startTimeString, formatter);

                if (startDateTime.isAfter(LocalDateTime.now())) {
                    DataBaseConnection dataBaseConnection = new DataBaseConnection();
                    Connection connection = dataBaseConnection.getConnection();

                    String sql = "DELETE FROM appointments WHERE id = ?";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setInt(1, selectedAppointment.getAppointmentID());

                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected > 0) {
                        appointmentsTable.getItems().remove(selectedAppointment);
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Success");
                        alert.setHeaderText("Appointment Deleted");
                        alert.setContentText("Appointment has been successfully deleted.");
                        alert.show();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("No Rows Affected");
                        alert.setContentText("The appointment could not be deleted. Please try again.");
                        alert.show();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid Appointment Date");
                    alert.setContentText("Cannot delete past or same-day appointments. Please select a future appointment.");
                    alert.show();
                }
            } catch (DateTimeParseException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid Date Format");
                alert.setContentText("The date format for the appointment is invalid: " + e.getMessage());
                alert.show();
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Database Error");
                alert.setHeaderText("Failed to Delete Appointment");
                alert.setContentText("An error occurred while accessing the database. Please try again.");
                alert.show();
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Appointment Selected");
            alert.setContentText("Please select an appointment to delete.");
            alert.show();
        }
    }



    public void backButtonOnAction(ActionEvent actionEvent) {
        goToMyAccount();
    }

    private void goToMyAccount() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("myAccount.fxml"));
            Parent root = loader.load();

            myAccount controller = loader.getController();
            controller.fetchUserDetails(usernameSpecialist);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.DECORATED);
            stage.setScene(new Scene(root, 520, 400));
            stage.show();

            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }
}
