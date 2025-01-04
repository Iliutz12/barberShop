package com.example.barbershop;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.stage.StageStyle;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private Button cancelButton;
    @FXML
    private Button loginButton;
    @FXML
    private Label loginMessageLabel;
    @FXML
    private ImageView brandingImageView;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField enterPasswordField;
    @FXML
    private Button createAccountButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        File brandingFile = new File("src/main/resources/images/download.png");
        Image brandingImage = new Image(brandingFile.toURI().toString());
        brandingImageView.setImage(brandingImage);
    }


    public void loginButtonOnAction() {
        if (usernameTextField.getText().isEmpty() || enterPasswordField.getText().isEmpty()) {
            loginMessageLabel.setText("Please enter both username and password.");
        } else {
            BarberDateAndTimeController barberController  = new BarberDateAndTimeController();
            barberController.getUsername(usernameTextField.getText());
            validateLoginButtonOnAction();
        }
    }


    public void cancelButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void createAccountButtonOnAction(ActionEvent event) {
        createAccountForm();
    }

    public void validateLoginButtonOnAction() {
        DataBaseConnection connectNow = new DataBaseConnection();
        Connection connectDb = connectNow.getConnection();

        String verifyLoginQuery = "SELECT password FROM user_account WHERE username = ?";

        try {
            PreparedStatement preparedStatement = connectDb.prepareStatement(verifyLoginQuery);
            preparedStatement.setString(1, usernameTextField.getText());

            ResultSet queryResultSet = preparedStatement.executeQuery();

            if (queryResultSet.next()) {
                String hashedPassword = queryResultSet.getString("password");

                if (BCrypt.checkpw(enterPasswordField.getText(), hashedPassword)) {
                    mainApplication();
                } else {
                    loginMessageLabel.setText("Invalid username or password.");
                }
            } else {
                loginMessageLabel.setText("Invalid username or password.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            loginMessageLabel.setText("An error occurred while logging in.");
        } finally {
            try {
                connectDb.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void createAccountForm() {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("Register.fxml"));
            Stage registerStage = new Stage();
            registerStage.initStyle(StageStyle.DECORATED);
            registerStage.setScene(new Scene(root, 520, 472));
            registerStage.show();

            Stage currentStage = (Stage) createAccountButton.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    public void mainApplication() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Appoiment.fxml"));
            Parent root = loader.load();

            mainApplication controller = loader.getController();
            controller.setWelcomeMessage(usernameTextField.getText());

            Stage stage = new Stage();
            stage.initStyle(StageStyle.DECORATED);
            stage.setScene(new Scene(root, 907, 650));
            stage.show();

            Stage loginStage = (Stage) loginButton.getScene().getWindow();
            loginStage.close();

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }
}