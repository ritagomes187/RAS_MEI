package com.example;

import java.io.IOException;

import com.example.Entities.Utilizador;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class InitialPageController {
    public static Utilizador user;

    @FXML
    private TextField email;
    @FXML
    private TextField password;
    @FXML
    private TextField info;

    public InitialPageController() {}

    @FXML
    private void switchToRegisterPage() throws IOException {
        App.setRoot("registerPage");
    }

    private void switchToMainPage() throws IOException {
        if (user.getPrivileges() == 0) App.setRoot("mainPage");
        if (user.getPrivileges() == 1) App.setRoot("specPage");
        if (user.getPrivileges() == 2) App.setRoot("adminPage");
    }

    @FXML
    private void login() throws IOException { 
        if (checkNoNullValues()) {
            user = App.dl.login(email.getText(), password.getText());
            
            if(user == null) {
                info.setText("Wrong e-mail or password.");
            }
            else switchToMainPage();
        }
    }

    public boolean checkNoNullValues() {
        if(email.getText().isEmpty() || password.getText().isEmpty()) {
            info.setText("Please fill in all fields!");
            return false;
        }
        return true;
    }

    @FXML
    private void clear() {
        email.clear();
        password.clear();
        info.clear();
    }
}
