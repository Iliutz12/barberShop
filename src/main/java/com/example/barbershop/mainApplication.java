package com.example.barbershop;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.stage.StageStyle;

import java.awt.*;
import java.net.URI;


public class mainApplication {

    @FXML
    private Button makeAppointmentButton;
    @FXML
    private Label welcomeBackLabel;
    @FXML
    private MenuItem logoutButton;
    @FXML
    private MenuItem myAccountButton;
    @FXML
    private MenuButton menuButton;

    public String username1 = "";

    @FXML
    private void logOutButtonOnAction(ActionEvent event) {
        goToLoginController();
    }

    public void setWelcomeMessage(String username) {
        username1 = username;
        welcomeBackLabel.setText("Hello! " + username);
    }

    public void makeAppointment(ActionEvent event) {
        selectAppoiniment();
    }

    public void myAccountButtonOnAction(ActionEvent event) {
        goToMyAccountController();
    }


    public void goToMyAccountController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("myAccount.fxml"));
            Parent root = loader.load();

            myAccount controller = loader.getController();
            controller.fetchUserDetails(username1);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.DECORATED);
            stage.setScene(new Scene(root, 520, 400));
            stage.show();

            Stage currentStage = (Stage) menuButton.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    public void selectAppoiniment() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("selectAppoiment.fxml"));
            Parent root = loader.load();


            selectAppointment controller = loader.getController();
            controller.getUsername(username1);

            Stage registerStage = new Stage();
            registerStage.initStyle(StageStyle.DECORATED);
            registerStage.setScene(new Scene(root, 874, 646));
            registerStage.show();

            Stage mainApplication = (Stage) makeAppointmentButton.getScene().getWindow();
            mainApplication.close();

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }


    public void goToLoginController() {
        try {

            Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));

            Stage registerStage = new Stage();
            registerStage.initStyle(StageStyle.DECORATED);
            registerStage.setScene(new Scene(root, 520,400));
            registerStage.show();

            Stage currentStage = (Stage) menuButton.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    @FXML
    private void openInstagram() {
        openWebsite("https://www.instagram.com");
    }

    @FXML
    private void openFacebook() {
        openWebsite("https://www.facebook.com");
    }

    @FXML
    private void openTikTok() {
        openWebsite("https://www.tiktok.com");
    }

    private void openWebsite(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                System.out.println("Desktop not supported!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
