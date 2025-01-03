package com.example.barbershop;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.stage.StageStyle;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;



public class myAccount {

    @FXML
    private Label fullNameLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private Button logOutButton;
    @FXML
    private Button backButton;
    @FXML
    private Button changePasswordButton;
    @FXML
    private Label numberOfAppointmentsLabel;
    @FXML
    private Button myAppointmentsButton;
    @FXML
    private Button seeApointmentsButton;
    String currentUsername = " ";
    Integer employeeId;

    public void changePasswordOnAction(ActionEvent event) {
        changePassword();
    }

    public void myAppointmentsOnAction(ActionEvent event) {
        myAppointments();
    }

    public void myAppointments() {
        if(currentUsername.equals("Ionel") || currentUsername.equals("adam") || usernameLabel.getText().equals("costi")) {
            if(currentUsername.equals("Ionel")) {
                employeeId = 1;
            }else if(currentUsername.equals("adam")) {
                employeeId = 2;
            }else if(currentUsername.equals("costi")) {
                employeeId = 3;
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("myAppointmentsForEmployee.fxml"));
                Parent root = loader.load();

                MyAppointmentsForEmployee controller = loader.getController();
                controller.setUsernameSpecialist(currentUsername,employeeId);

                Stage registerStage = new Stage();
                registerStage.initStyle(StageStyle.DECORATED);
                registerStage.setScene(new Scene(root, 839, 400));
                registerStage.show();

                Stage currentStage = (Stage) seeApointmentsButton.getScene().getWindow();
                currentStage.close();

            } catch (Exception e) {
                e.printStackTrace();
                e.getCause();
            }
        } else {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("myAppointments.fxml"));
            Parent root = loader.load();

            myAppointmentsController controller = loader.getController();
            controller.getUsername(currentUsername);

            Stage registerStage = new Stage();
            registerStage.initStyle(StageStyle.DECORATED);
            registerStage.setScene(new Scene(root, 839, 400));
            registerStage.show();

            Stage currentStage = (Stage) seeApointmentsButton.getScene().getWindow();
            currentStage.close();

        }   catch (Exception e) {
            e.printStackTrace();
            e.getCause();
            }
        }
    }

    public void changePassword() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("changePassword.fxml"));
            Parent root = loader.load();

            changePassword controller = loader.getController();
            controller.getUsername(currentUsername);

            Stage registerStage = new Stage();
            registerStage.initStyle(StageStyle.DECORATED);
            registerStage.setScene(new Scene(root, 520, 400));
            registerStage.show();

            Stage currentStage = (Stage) changePasswordButton.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }


    public void logOutButtonOnAction(ActionEvent event) {
        logOut();
    }

    public void backButtonOnAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Appoiment.fxml"));
            Parent root = loader.load();


            mainApplication controller = loader.getController();
            controller.setWelcomeMessage(currentUsername);


            Stage stage = new Stage();
            stage.initStyle(StageStyle.DECORATED);
            stage.setScene(new Scene(root, 907, 650));
            stage.show();

            Stage loginStage = (Stage) backButton.getScene().getWindow();
            loginStage.close();

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    public void logOut(){
        try {
            Stage currentStage = (Stage) logOutButton.getScene().getWindow();
            currentStage.close();

            Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
            Stage registerStage = new Stage();
            registerStage.initStyle(StageStyle.DECORATED);
            registerStage.setScene(new Scene(root, 520,400));
            registerStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    public void fetchUserDetails(String username) {
        DataBaseConnection connectNow = new DataBaseConnection();
        Connection connectDb = connectNow.getConnection();

        String query = "SELECT firstname, lastname, appointments FROM user_account WHERE username = ?";

        try {
            PreparedStatement preparedStatement = connectDb.prepareStatement(query);
            preparedStatement.setString(1, username);
            currentUsername = username;
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String firstname = resultSet.getString("firstname");
                String lastname = resultSet.getString("lastname");
                String appointments = resultSet.getString("appointments");

                Platform.runLater(() -> {
                    fullNameLabel.setText(firstname + " " + lastname);
                    usernameLabel.setText(username);
                    numberOfAppointmentsLabel.setText("Number of appointments so far: " + appointments);
                });

            } else {
                Platform.runLater(() -> {
                    fullNameLabel.setText("User not found");
                    usernameLabel.setText("");
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                fullNameLabel.setText("An error occurred");
                usernameLabel.setText("");
            });
        } finally {
            try {
                if (connectDb != null) {
                    connectDb.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}

