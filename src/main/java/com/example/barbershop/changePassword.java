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

public class changePassword {

    @FXML
    private PasswordField oldPasswordField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confrimNewPasswordField;
    @FXML
    private Label passwordNotMatchingLabel;
    @FXML
    private Button backButton;
    String currentUsername = "";

    public void getUsername(String username) {
        currentUsername = username;
    }

    public void changePasswordOnAction(ActionEvent event) {
        changePasswordIfOk();
    }

    public void backButtonOnAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("myAccount.fxml"));
            Parent root = loader.load();

            myAccount controller = loader.getController();
            controller.fetchUserDetails(currentUsername);

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

    public void changePasswordIfOk() {
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confrimNewPasswordField.getText();

        if(oldPassword.equals(confirmPassword)) {
            passwordNotMatchingLabel.setText("Password cannot be the same");
            return;
        }else if(oldPassword.equals("")){
            passwordNotMatchingLabel.setText("Password cannot be empty");
            return;
        } else if (!newPassword.equals(confirmPassword)) {
            passwordNotMatchingLabel.setText("Passwords do not match");
            return;
        } else if(newPassword.equals("") || confirmPassword.equals("")) {
            passwordNotMatchingLabel.setText("Password cannot be empty");
            return;
        }

        try {
            DataBaseConnection connectNow = new DataBaseConnection();
            Connection connectDb = connectNow.getConnection();

            if (connectDb != null) {
                // Verifică parola curentă în baza de date
                String query = "SELECT password FROM user_account WHERE username = ?";
                PreparedStatement preparedStatement = connectDb.prepareStatement(query);
                preparedStatement.setString(1, currentUsername);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    String currentPassword = resultSet.getString("password");

                    if (currentPassword.equals(oldPassword)) {
                        String updateQuery = "UPDATE user_account SET password = ? WHERE username = ?";
                        PreparedStatement updateStatement = connectDb.prepareStatement(updateQuery);
                        updateStatement.setString(1, newPassword);
                        updateStatement.setString(2, currentUsername);

                        int rowsUpdated = updateStatement.executeUpdate();

                        if (rowsUpdated > 0) {
                            passwordNotMatchingLabel.setText("Password updated successfully.");
                        } else {
                            passwordNotMatchingLabel.setText("Failed to update password.");
                        }
                    } else {
                        passwordNotMatchingLabel.setText("Old password is incorrect.");
                    }
                }
            }
            connectDb.close();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
