module com.example.barbershop {
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires javafx.web;
    requires java.desktop;


    opens com.example.barbershop to javafx.fxml;
    exports com.example.barbershop;
}