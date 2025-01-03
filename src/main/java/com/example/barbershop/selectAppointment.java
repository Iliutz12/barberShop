package com.example.barbershop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.ResourceBundle;

public class selectAppointment implements Initializable {
    @FXML
    private MenuButton locationMenuButton;
    @FXML
    private MenuButton beardMenuButton;
    @FXML
    private MenuButton haircutMenuButton;
    @FXML
    private MenuButton beardAndHaircutMenuButton;
    @FXML
    private Button backButton;
    @FXML
    private MenuItem streetVictorFelea;
    @FXML
    private MenuItem streetAvramIancu;
    @FXML
    private MenuItem GroomedBeard;
    @FXML
    private MenuItem groomedBeardWithHotTowel;
    @FXML
    private MenuItem shavingWithWarmTowel;
    @FXML
    private MenuItem trimmed;
    @FXML
    private MenuItem trimmedAndWashed;
    @FXML
    private MenuItem beardTrimmedAndHairTrimmed;
    @FXML
    private MenuItem cutWashedTrimmedBeard;
    @FXML
    private MenuItem cutWashedTrimmedBeardWithHotTowel;
    @FXML
    private Button continueButtton;
    @FXML
    private WebView googleMapView;
    @FXML
    private Label selectAnAppointment;
    String username1 = "";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String mapHtmlContent = """
    <html>
        <body style="margin: 0; padding: 0; height: 100%; width: 100%;">
        <iframe
            src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2734.004596511278!2d23.56701267680827!3d46.74509154707019!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x47490e772e580df1%3A0xe822ad370f6fafe3!2sStrada%20C%C3%A2mpului%2C%20Cluj-Napoca!5e0!3m2!1sro!2sro!4v1734002857325!5m2!1sro!2sro"
            width="100%"
            height="100%"
            style="border:0;"
            allowfullscreen=""
            loading="lazy">
        </iframe>
        </body>
    </html>
    """;
        googleMapView.getEngine().loadContent(mapHtmlContent);

        streetVictorFelea.setOnAction(event -> locationMenuButton.setText(streetVictorFelea.getText()));

        setMenuAction(beardMenuButton, GroomedBeard, groomedBeardWithHotTowel, shavingWithWarmTowel);
        setMenuAction(haircutMenuButton, trimmed, trimmedAndWashed);
        setMenuAction(beardAndHaircutMenuButton, beardTrimmedAndHairTrimmed, cutWashedTrimmedBeard, cutWashedTrimmedBeardWithHotTowel);
    }


    public void getUsername(String username) {
        username1 = username;
    }

    private void setMenuAction(MenuButton menuButton, MenuItem... menuItems) {
        for (MenuItem menuItem : menuItems) {
            menuItem.setOnAction(event -> {
                menuButton.setText(menuItem.getText());
                resetOtherMenus(menuButton);
            });
        }
    }

    private void resetOtherMenus(MenuButton activeMenu) {
        if (activeMenu != beardMenuButton) beardMenuButton.setText("Beard");
        if (activeMenu != haircutMenuButton) haircutMenuButton.setText("Haircut");
        if (activeMenu != beardAndHaircutMenuButton) beardAndHaircutMenuButton.setText("Beard And Haircut");
    }

    public void backButtonOnAction(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Appoiment.fxml"));
            Parent root = loader.load();

            mainApplication controller = loader.getController();
            controller.setWelcomeMessage(username1);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.DECORATED);
            stage.setScene(new Scene(root, 907, 650));
            stage.show();

            Stage appointmentStage = (Stage) backButton.getScene().getWindow();
            appointmentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }

        Stage currentStage = (Stage) backButton.getScene().getWindow();
        currentStage.close();
    }

    @FXML
    private void continueButtonOnAction(ActionEvent event) {
        String location = locationMenuButton.getText();
        String beardService = beardMenuButton.getText();
        String haircutService = haircutMenuButton.getText();
        String beardAndHaircutService = beardAndHaircutMenuButton.getText();

        String selectedService = "";
        String selectedLocation = "";

        if(!location.equals("Choose The Location")) {
            selectedLocation = location;
        }
        if (!beardService.equals("Beard")) {
            selectedService = beardService;
        } else if (!haircutService.equals("Haircut")) {
            selectedService = haircutService;
        } else if (!beardAndHaircutService.equals("Beard And Haircut")) {
            selectedService = beardAndHaircutService;
        }

        if (!selectedService.isEmpty() && !selectedLocation.isEmpty()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("BarberDateAndTime.fxml"));
                Parent root = loader.load();

                BarberDateAndTimeController controller = loader.getController();
                controller.setAppointmentType(selectedService);
                controller.getUsername(username1);

                Stage registerStage = new Stage();
                registerStage.initStyle(StageStyle.DECORATED);
                registerStage.setScene(new Scene(root, 874, 470));
                registerStage.show();

                Stage currentStage = (Stage) continueButtton.getScene().getWindow();
                currentStage.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            selectAnAppointment.setText("Please select a service and a location.");
        }
    }


}
