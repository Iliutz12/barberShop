package com.example.barbershop;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterController {

    @FXML
    private Button closeButton;
    @FXML
    private Label registrationLabelMessage;
    @FXML
    private TextField firstnameTextField;
    @FXML
    private TextField lastnameTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField setPasswordField;
    @FXML
    private TextField confirmPasswordField;
    @FXML
    private Label confirmPasswordLabel;
    @FXML
    private Button backButton;

    public void registerButtonOnAction(ActionEvent event) {
        String firstName = firstnameTextField.getText().trim();
        String lastName = lastnameTextField.getText().trim();
        String username = usernameTextField.getText().trim();
        String password = setPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            registrationLabelMessage.setText("All fields must be filled!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordLabel.setText("Passwords do not match!");
            return;
        }

        registerUser(firstName, lastName, username, password);
    }

    public void closeButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
        Platform.exit();
    }

    public void goBackButtonOnAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Parent root = loader.load();

        Stage registerStage = new Stage();
        registerStage.initStyle(StageStyle.DECORATED);
        registerStage.setScene(new Scene(root, 520, 400));
        registerStage.show();

        Stage primaryStage = (Stage) backButton.getScene().getWindow();
        primaryStage.close();
    }

    public boolean checkUsername(String username) {
        DataBaseConnection connectNow = new DataBaseConnection();
        Connection connectDb = connectNow.getConnection();
        boolean usernameExists = false;

        String query = "SELECT username FROM user_account WHERE username = ?";
        try (PreparedStatement preparedStatement = connectDb.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            usernameExists = resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connectDb.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return usernameExists;
    }

    public void registerUser(String firstName, String lastName, String username, String password) {
        DataBaseConnection connectNow = new DataBaseConnection();
        Connection connectDb = connectNow.getConnection();

        if (checkUsername(username)) {
            registrationLabelMessage.setText("Username is already taken!");
            return;
        }

        String insertQuery = "INSERT INTO user_account (firstname, lastname, username, password, appointments) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connectDb.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, username);
            preparedStatement.setString(4, password);
            preparedStatement.setInt(5, 0);

            preparedStatement.executeUpdate();
            registrationLabelMessage.setText("User registered successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            registrationLabelMessage.setText("An error occurred while registering.");
        } finally {
            try {
                connectDb.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}