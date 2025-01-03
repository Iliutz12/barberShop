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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.*;
import java.time.format.DateTimeParseException;

public class myAppointmentsController {

    public static int userId;
    public static String username1 = "";

    @FXML private TableColumn<Appointments, String> colStartTime;
    @FXML private TableColumn<Appointments, String> colDateCreated;
    @FXML private TableColumn<Appointments, String> colType;
    @FXML private TableColumn<Appointments, Integer> colPrice;
    @FXML private TableColumn<Appointments, Boolean> colCancel;
    @FXML private TableColumn<Appointments, String> colId;
    @FXML private TableColumn<Appointments, String> colSpecialist;
    @FXML private Button backButton;


    @FXML private TableView<Appointments> appointmentsTable;
    @FXML private Button btnDelete;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getAppointmentID())));
        colSpecialist.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getSpecialistId())));
        colStartTime.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStartTime()));
        colDateCreated.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDateTimeCreated()));
        colType.setCellValueFactory(data -> data.getValue().appointmentTypeProperty());
        colPrice.setCellValueFactory(data -> data.getValue().priceProperty().asObject());
        colCancel.setCellValueFactory(data -> data.getValue().canceledProperty().asObject());

        btnDelete.setOnAction(event -> deleteSelectedAppointment());
    }

    public void getUsername(String username) throws SQLException {
        username1 = username;

        try {
            appointmentsTable.setItems(getRecords());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ObservableList<Appointments> getRecords() throws SQLException, ClassNotFoundException {
        DataBaseConnection dataBaseConnection = new DataBaseConnection();
        Connection connection = dataBaseConnection.getConnection();

        String sql2 = "SELECT id from user_account where username = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql2);
        preparedStatement.setString(1, username1);

        ResultSet resultSet2 = preparedStatement.executeQuery();
        if (resultSet2.next()) {
            userId = resultSet2.getInt("id");
        }

        String sql = "SELECT id AS appointment_id, date, employee_id, start_time, appointment_type, price_final, canceled " +
                "FROM appointments WHERE user_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, userId);

        ResultSet resultSet = statement.executeQuery();
        return getAppointmentsObjects(resultSet);
    }

    private static ObservableList<Appointments> getAppointmentsObjects(ResultSet resultSet) throws SQLException {
        ObservableList<Appointments> appList = FXCollections.observableArrayList();
        while (resultSet.next()) {
            Appointments app = new Appointments();
            app.setAppointmentID(resultSet.getInt("appointment_id"));
            app.setSpecialistId(resultSet.getInt("employee_id"));
            app.setDateTimeCreated(resultSet.getString("date"));
            app.setStartTime(resultSet.getString("start_time"));
            app.setAppointmentType(resultSet.getString("appointment_type"));
            app.setPrice(resultSet.getInt("price_final"));
            app.setCanceled(resultSet.getBoolean("canceled"));
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
                LocalDate startDate = startDateTime.toLocalDate();

                if (startDate.isAfter(LocalDate.now())) {
                    DataBaseConnection dataBaseConnection = new DataBaseConnection();
                    Connection connection = dataBaseConnection.getConnection();

                    String sql = "DELETE FROM appointments WHERE id = ?";
                    String sql2 = "UPDATE user_account SET appointments = appointments - 1 WHERE id = ?";

                    PreparedStatement statement = connection.prepareStatement(sql);
                    PreparedStatement statement2 = connection.prepareStatement(sql2);

                    statement.setInt(1, selectedAppointment.getAppointmentID());
                    statement2.setInt(1, userId);

                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected > 0) {
                        appointmentsTable.getItems().remove(selectedAppointment);
                        statement2.executeUpdate();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Success");
                        alert.setHeaderText("Appointment Deleted");
                        alert.setContentText("Appointment has been successfully deleted.");
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
            controller.fetchUserDetails(username1);

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
