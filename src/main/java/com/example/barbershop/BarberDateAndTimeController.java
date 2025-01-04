package com.example.barbershop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public class BarberDateAndTimeController {

        @FXML
        private ComboBox hourPicker;
        @FXML
        private Label typeAppointmentLabel;
        private String appointmentType;
        String username1 = "";
        @FXML
        private MenuItem Ionel;
        @FXML
        private MenuItem Costi;
        @FXML
        private MenuItem Adam;
        @FXML
        private MenuButton selectSpecialistButton;
        @FXML
        private DatePicker datePicker;
        @FXML
        private Label notValidOrNotAbleLabel;
        @FXML
        private Button confirmButton;
        @FXML
        private Button backButton;
        String appointmentType1;
        Integer serviceID;
        Integer price;
        Integer appointmentTime;


    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
        typeAppointmentLabel.setText(appointmentType);
        switch (appointmentType) {
            case "Groomed Beard                      30 min      45.00 RON" -> {
                price = 45;
                serviceID = 1;
                appointmentType1 = "Groomed Beard";
                appointmentTime = 30;
            }
            case "Groomed Beard (With Hot Towel)     30 min      55.00 RON" -> {
                price = 55;
                serviceID = 2;
                appointmentType1 = "Groomed Beard (With Hot Towel)";
                appointmentTime = 30;
            }
            case "Shaving With A Warm Towel          40 min      60.00 RON" -> {
                price = 60;
                serviceID = 3;
                appointmentType1 = "Shaving With A Warm Towel";
                appointmentTime = 40;
            }
            case "Trimmed             45 min        70.00 RON" -> {
                price = 70;
                serviceID = 4;
                appointmentType1 = "Trimmed";
                appointmentTime = 45;
            }
            case "Trimmed And Washed  60 min        80.00 RON" -> {
                price = 80;
                serviceID = 5;
                appointmentType1 = "Trimmed And Washed";
                appointmentTime = 60;
            }
            case "Beard Trimmed And Trimmed                60 min    95.00 RON" -> {
                price = 95;
                serviceID = 6;
                appointmentType1 = "Beard Trimmed And Trimmed";
                appointmentTime = 60;
            }
            case "Cut, Washed, Trimmed Beard               70 min   105.00 RON" -> {
                price = 105;
                serviceID = 7;
                appointmentType1 = "Cut, Washed, Trimmed Beard";
                appointmentTime = 70;
            }
            case "Cut, Washed, Trimmed Beard (Warm Towel)  70 min   120.00 RON" -> {
                price = 120;
                serviceID = 8;
                appointmentType1 = "Cut, Washed, Trimmed Beard (With Hot Towel)";
                appointmentTime = 70;
            }
        }
    }

    public void getUsername(String username) {
        this.username1 = username;
    }
    public void initialize() {
        Ionel.setOnAction(event -> selectSpecialistButton.setText(Ionel.getText()));
        Costi.setOnAction(event -> selectSpecialistButton.setText(Costi.getText()));
        Adam.setOnAction(event -> selectSpecialistButton.setText(Adam.getText()));
        hourPicker.setVisible(false);
        confirmButton.setVisible(false);

        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #EEEEEE;");
                }
            }
        });
    }


    public void datePickerOnAction(ActionEvent event) {
        if (datePicker.getValue() != null) {
            DayOfWeek selectedDay = datePicker.getValue().getDayOfWeek();

            if (selectedDay == DayOfWeek.SATURDAY || selectedDay == DayOfWeek.SUNDAY) {
                notValidOrNotAbleLabel.setText("Not a valid date");
                hourPicker.setVisible(false);
                confirmButton.setVisible(false);
            } else {
                notValidOrNotAbleLabel.setText("");

                String selectedSpecialist = selectSpecialistButton.getText();
                if (selectedSpecialist != null && !selectedSpecialist.isEmpty()) {
                    checkValidTimeForEmployee(selectedSpecialist, datePicker.getValue().toString());
                } else {
                    notValidOrNotAbleLabel.setText("Select a specialist first.");
                }
            }
        } else {
            notValidOrNotAbleLabel.setText("Select a valid date");
            hourPicker.setVisible(false);
            confirmButton.setVisible(false);
        }
    }

    private void checkValidTimeForEmployee(String barberName, String selectedDate) {
        DataBaseConnection connectNow = new DataBaseConnection();
        Connection connectDb = connectNow.getConnection();

        LocalTime startWorkingTime = LocalTime.of(8, 0);
        LocalTime endWorkingTime = LocalTime.of(18, 0);

        List<LocalTime> availableTimes = new ArrayList<>();
        LocalTime currentTime = startWorkingTime;
        while (currentTime.plusMinutes(appointmentTime).isBefore(endWorkingTime.plusMinutes(1))) {
            availableTimes.add(currentTime);
            currentTime = currentTime.plusMinutes(15);
        }

        String query = "SELECT start_time, end_time FROM appointments a " +
                "JOIN employee e ON a.employee_id = e.id " +
                "WHERE e.firstname = ? AND a.date = ?::DATE";

        try {
            PreparedStatement preparedStatement = connectDb.prepareStatement(query);
            preparedStatement.setString(1, barberName);
            preparedStatement.setString(2, selectedDate);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                LocalTime reservedStartTime = LocalTime.from(resultSet.getTimestamp("start_time").toLocalDateTime());
                LocalTime reservedEndTime = resultSet.getTimestamp("end_time").toLocalDateTime().toLocalTime();

                availableTimes.removeIf(time ->
                        !time.plusMinutes(appointmentTime).isBefore(reservedStartTime) &&
                                !time.isAfter(reservedEndTime)
                );
            }

            hourPicker.getItems().clear();
            for (LocalTime time : availableTimes) {
                hourPicker.getItems().add(time.toString());
            }

            if (!availableTimes.isEmpty()) {
                hourPicker.setVisible(true);
                confirmButton.setVisible(true);
            } else {
                notValidOrNotAbleLabel.setText("No available times for this date.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void confirmButtonOnAction(ActionEvent event) {
        String selectedSpecialist = selectSpecialistButton.getText();
        String selectedDate = datePicker.getValue().toString();
        String selectedTime = (String) hourPicker.getValue();
        int discount = 0;
        boolean canceled = false;
        int priceFinal = price;

        LocalTime startTime = LocalTime.parse(selectedTime);
        LocalDate selectedLocalDate = LocalDate.parse(selectedDate);
        LocalDateTime endDateTime = LocalDateTime.of(selectedLocalDate, startTime.plusMinutes(appointmentTime));

        String insertQuery = "INSERT INTO appointments (user_id, employee_id, date, start_time, end_time, " +
                "date_created, user_username, discount, canceled, price, price_final, service_id, appointment_type) " +
                "VALUES ((SELECT id FROM user_account WHERE username = ?), " +
                "(SELECT id FROM employee WHERE firstname = ?), ?, ?, ?, now(), ?, ?, ?, ?, ?, ?, ?)";
        String updateAppointmentCount = "UPDATE user_account SET appointments = appointments + 1 WHERE username = ?";
        String getAppointmentsCountQuery = "SELECT appointments FROM user_account WHERE username = ?";

        try {
            DataBaseConnection connectNow = new DataBaseConnection();
            Connection connectDb = connectNow.getConnection();

            PreparedStatement updateAppointmentCountStatement = connectDb.prepareStatement(updateAppointmentCount);
            PreparedStatement preparedStatement = connectDb.prepareStatement(insertQuery);

            PreparedStatement getAppointmentsStatement = connectDb.prepareStatement(getAppointmentsCountQuery);
            getAppointmentsStatement.setString(1, username1);
            ResultSet resultSet = getAppointmentsStatement.executeQuery();

            int numberOfAppointments = 0;
            if (resultSet.next()) {
                numberOfAppointments = resultSet.getInt("appointments");
            }


            if (numberOfAppointments % 15 == 0 && numberOfAppointments != 0) {
                discount = price / 2;
                priceFinal = price - discount;
            }

            if(numberOfAppointments % 15 == 0 && numberOfAppointments != 0) {
                discount = price / 2;
                priceFinal = price - discount;
            }

            preparedStatement.setString(1, username1);
            preparedStatement.setString(2, selectedSpecialist);
            preparedStatement.setDate(3, java.sql.Date.valueOf(selectedLocalDate));
            preparedStatement.setTimestamp(4, java.sql.Timestamp.valueOf(LocalDateTime.of(selectedLocalDate, startTime)));
            preparedStatement.setTimestamp(5, java.sql.Timestamp.valueOf(endDateTime));
            preparedStatement.setString(6, username1);
            preparedStatement.setInt(7, discount);
            preparedStatement.setBoolean(8, canceled);
            preparedStatement.setInt(9, price);
            preparedStatement.setInt(10, priceFinal);
            preparedStatement.setInt(11, serviceID);
            preparedStatement.setString(12, appointmentType1);
            updateAppointmentCountStatement.setString(1, username1);

            int rowsInserted = preparedStatement.executeUpdate();
            int rowsChanges = updateAppointmentCountStatement.executeUpdate();
            if (rowsInserted > 0 && rowsChanges > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Appointment Confirmed");
                alert.setContentText("Appointment has been saved successfully having the price: " + priceFinal + " RON");
                alert.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void backButtonOnAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("selectAppoiment.fxml"));
            Parent root = loader.load();

            selectAppointment controller = loader.getController();
            controller.getUsername(username1);

            Stage registerStage = new Stage();
            registerStage.initStyle(StageStyle.DECORATED);
            registerStage.setScene(new Scene(root, 874, 646));
            registerStage.show();

            Stage mainApplication = (Stage) backButton.getScene().getWindow();
            mainApplication.close();

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

}
