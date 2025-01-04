package com.example.barbershop;

import javafx.beans.property.*;

public class Appointments {

    private IntegerProperty appointmentID;
    private IntegerProperty specialistId;
    private StringProperty startTime;
    private StringProperty dateCreated;
    private StringProperty appointmentType;
    private IntegerProperty price;
    private BooleanProperty canceled;
    private StringProperty username;
    private String specialistName;

    public Appointments() {
        this.appointmentID = new SimpleIntegerProperty();
        this.specialistId = new SimpleIntegerProperty();
        this.startTime = new SimpleStringProperty();
        this.appointmentType = new SimpleStringProperty();
        this.price = new SimpleIntegerProperty();
        this.canceled = new SimpleBooleanProperty();
        this.dateCreated = new SimpleStringProperty();
        this.username = new SimpleStringProperty();
    }

    public String getSpecialistName() {
        return specialistName;
    }

    public void setSpecialistName(String specialistName) {
        this.specialistName = specialistName;
    }

    public int getAppointmentID() {
        return appointmentID.get();
    }
    public void setAppointmentID(int id) {
        this.appointmentID.set(id);
    }
    public IntegerProperty appointmentIDProperty() {
        return appointmentID;
    }

    public String getDateTimeCreated() {
        return dateCreated.get();
    }
    public void setDateTimeCreated(String dateCreated) {
        this.dateCreated.set(dateCreated);
    }
    public StringProperty dateCreatedProperty() {
        return dateCreated;
    }

    public int getSpecialistId() {
        return specialistId.get();
    }
    public void setSpecialistId(int id) {
        this.specialistId.set(id);
    }
    public IntegerProperty specialistIdProperty() {
        return specialistId;
    }

    public String getStartTime() {
        return startTime.get();
    }
    public void setStartTime(String time) {
        this.startTime.set(time);
    }
    public StringProperty startTimeProperty() {
        return startTime;
    }


    public String getAppointmentType() {
        return appointmentType.get();
    }
    public void setAppointmentType(String type) {
        this.appointmentType.set(type);
    }
    public StringProperty appointmentTypeProperty() {
        return appointmentType;
    }

    public int getPrice() {
        return price.get();
    }
    public void setPrice(int price) {
        this.price.set(price);
    }
    public IntegerProperty priceProperty() {
        return price;
    }

    public boolean isCanceled() {
        return canceled.get();
    }
    public void setCanceled(boolean canceled) {
        this.canceled.set(canceled);
    }
    public BooleanProperty canceledProperty() {
        return canceled;
    }

    public String getUsername(){
        return username.get();
    }
    public void setUsername(String username) {
        this.username.set(username);
    }
    public StringProperty usernameProperty() {
        return username;
    }

}
