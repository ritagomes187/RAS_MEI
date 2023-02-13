package com.example;

import java.io.IOException;

import com.example.Entities.Utilizador;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

public class RegisterPageController {

    private String language;
    private String currency;
    @FXML
    private TextField age;
    @FXML
    private TextField name;
    @FXML
    private TextField username;
    @FXML
    private TextField email;
    @FXML
    private TextField password;
    @FXML
    private TextField phone_number;
    @FXML
    private TextField cc_number;
    @FXML
    private TextField address;
    @FXML
    private TextField ageInfo;
    @FXML
    private TextField passInfo;
    @FXML
    private TextField emailInfo;
    @FXML 
    private TextField usernameInfo;
    @FXML
    private TextField nif;
    @FXML
    private TextField nifInfo;
    @FXML
    private TextField info;
    @FXML
    private MenuItem english;

    /*TODO: Check das novas variáveis na base de dados !*/

    public RegisterPageController() {
        language = "English";
        currency = "Euro";
    }

    @FXML
    public void switchToInitalPage() throws IOException {
        App.setRoot("initialPage");
    }

    @FXML
    public void register() {
        if (checkNoNullValues() && checkMinimumAge() && checkNIFnotBlocked() && checkMinimumChars() && checkNotExistsEmail() && checkNotExistsUsername()) {

            Utilizador new_user = new Utilizador(Integer.valueOf(age.getText()), Integer.valueOf(nif.getText()), address.getText(), phone_number.getText(), cc_number.getText(), name.getText(), language, email.getText(), username.getText());
            App.dl.register(new_user, password.getText(), currency);
            info.setText("Register done successfully!");
            clearRegisterInfo();
        }
    }

    public boolean checkMinimumAge() {
        int ageNumber = Integer.valueOf(age.getText());
        if (ageNumber < 18) {
            ageInfo.setText("You should be at least 18 to register!");
            return false;
        }
        return true;
    }

    public boolean checkMinimumChars() {
        if (password.getText().length() < 5) {
            passInfo.setText("Should have at least 5 characters!");
            return false;
        }
        return true;
    }

    public boolean checkNotExistsEmail() {
        if(App.dl.existsUser(email.getText())) {
            emailInfo.setText("E-mail already in use.");
            return false;
        }
        return true;
    }

    public boolean checkNotExistsUsername() {
        if (App.dl.existsUsername(username.getText())) {
            usernameInfo.setText("Username already exists.");
            return false;
        }
        return true;
    }

    public boolean checkNIFnotBlocked() {
        int NIFnumber = Integer.parseInt(nif.getText());
        if (App.dl.isNIFBlocked(NIFnumber)) {
            nifInfo.setText("Register denied! This NIF is blocked.");
            return false;
        }
        return true;
    }

    public boolean checkNoNullValues() {
        if(age.getText().isEmpty() || name.getText().isEmpty() || username.getText().isEmpty() ||
        email.getText().isEmpty() || password.getText().isEmpty()) {
            info.setText("Please fill in all fields!");
            return false;
        }
        return true;
    }

    public void clearRegisterInfo() {
        email.clear();
        name.clear();
        password.clear();
        username.clear();
        age.clear();
        nif.clear();
        phone_number.clear();
        cc_number.clear();
        address.clear();
    }

    @FXML
    private void clearAge() {
        age.clear();
        ageInfo.clear();
        info.clear();
    }

    @FXML
    private void clearPassword() {
        password.clear();
        passInfo.clear();
        info.clear();
    }

    @FXML
    private void clearInfo() {
        info.clear();
    }

    @FXML
    private void setLanguageEnglish() {
        language = "English";
    }

    @FXML
    private void setCurrencyEuro() {
        currency = "Euro";
    }

    @FXML
    private void setCurrencyDollar() {
        currency = "Dollar";
    }
    
}
