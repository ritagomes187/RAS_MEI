package com.example;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;

import com.example.Entities.Utilizador;
import com.example.Entities.Aposta;
import com.example.Entities.Historico;

public class profilePageController {
    private static String nameValue;
    private static Float balanceValue;
    private Boolean deposit;

    @FXML
    private TextField name;
    @FXML
    private TextField balance;
    @FXML
    private Pane payPane;
    @FXML
    private Text payText;
    @FXML
    private Button payCard;
    @FXML
    private Button payBank;
    @FXML
    private Button payCancel;
    @FXML
    private TextField email;
    @FXML
    private TextField ammount;
    @FXML
    private TextField infoWith;
    @FXML
    private Button editProfile;
    @FXML
    private TextField ageField;
    @FXML
    private TextField nifField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField cellNoField;
    @FXML
    private TextField ccField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField languageField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField usernameField;
    @FXML
    private TableView openBetsTable;
    @FXML
    private TableView closedBetsTable;
    @FXML
    private Label totalInvestedLabel;
    @FXML
    private Label totalProfitLabel;
    @FXML
    private Label totalWinsLabel;
    @FXML
    private TextField ammount_box;
    @FXML
    private Text ammount_text;
    @FXML
    private Button confirm;

    public profilePageController() {
    }

    public void initialize() {
        name.setText(nameValue);
        balance.setText("BALANCE: " + balanceValue);
    }

    public static void setName(String username) {
        nameValue = username.toUpperCase();
    }

    public static void setBalance(Float saldo) {
        balanceValue = saldo;
    }

    @FXML
    public void logout() throws IOException {
        App.dl.logout(InitialPageController.user.getEmail());
        App.setRoot("initialPage");
    }

    @FXML
    public void switchToMainPage() throws IOException {
        App.setRoot("mainPage");
    }

    public boolean checkNoNullValuesWithdraw() {
        if (email.getText().isEmpty() || ammount.getText().isEmpty()) {
            infoWith.setText("Please fill in all fields!");
            return false;
        }
        return true;
    }

    @FXML
    public void withdrawMoney() {

        if (checkNoNullValuesWithdraw()) {
            // Mostrar mensagem de erro se algum campo obrigatório estiver vazio ou inválido
            System.out
                    .println("Por favor, preencha todos os campos obrigatórios e verifique se os valores são válidos.");
            return;
        }

        TextField email = new TextField();
        email.setText(String.valueOf(email));
        TextField ammount = new TextField();
        ammount.setText(String.valueOf(ammount));
        App.dl.withdrawMoney(email.getText(), Float.valueOf(ammount.getText()));

        // Mostrar mensagem de sucesso
        System.out.println("Dinheiro levantado com sucesso!");
        email.clear();
        ammount.clear();

    }

    @FXML
    public void depositMoney() {
        deposit = true;
        payPane.setVisible(true);
        payBank.setOnMouseClicked(e -> {
            ammount_box.setVisible(true);
            ammount_text.setVisible(true);
            confirm.setVisible(true);
        });
        payCard.setOnMouseClicked(e -> {
            ammount_box.setVisible(true);
            ammount_text.setVisible(true);
            confirm.setVisible(true);
        });
        payCancel.setOnMouseClicked(e -> {
            ammount_box.setVisible(false);
            ammount_text.setVisible(false);
            confirm.setVisible(false);
            payPane.setVisible(false);
        });
    }

    @FXML
    public void confirmAction() {
        if (deposit) App.dl.depositMoney(InitialPageController.user.getIdUser(), Float.parseFloat(ammount_box.getText()));
        else App.dl.withdrawMoney(InitialPageController.user.getIdUser(), Float.parseFloat(ammount_box.getText()));
        ammount_box.setVisible(false);
        ammount_text.setVisible(false);
        confirm.setVisible(false);
        payPane.setVisible(false);
        balanceValue += Float.parseFloat(ammount_box.getText());
        balance.setText("BALANCE: " + balanceValue);
    }

    @FXML
    public void transactionsHistory() {
        Utilizador user = InitialPageController.user;
        Historico historico = user.getHistorico();

        // Preenche a tabela de apostas abertas
        for (Aposta bet : historico.getOpenBets().values()) {
            openBetsTable.getItems().add(bet);
        }

        // Preenche a tabela de apostas fechadas
        for (Aposta bet : historico.getClosedBets().values()) {
            closedBetsTable.getItems().add(bet);
        }

        // Atualiza os labels com o total investido, lucro e vitórias
        totalInvestedLabel.setText(String.valueOf(historico.getTotalInvested()));
        totalProfitLabel.setText(String.valueOf(historico.getTotalProfit()));
        totalWinsLabel.setText(String.valueOf(historico.getTotalWins()));
    }

    @FXML
    public void betsHistory() {

    }

    @FXML
    public void notificationsHistory() {

    }

    @FXML
    private void handleSaveButton(ActionEvent event) {
        // Recupera os valores dos campos de texto
        int age = Integer.parseInt(ageField.getText());
        int nif = Integer.parseInt(nifField.getText());
        String address = addressField.getText();
        String cellNo = cellNoField.getText();
        String cc = ccField.getText();
        String name = nameField.getText();
        String language = languageField.getText();
        String email = emailField.getText();
        String username = usernameField.getText();

        // Atualiza o perfil do utilizador com os novos valores
        Utilizador user = new Utilizador(age, nif, address, cellNo, cc, name, language, email, username);

        // updateUserProfile(user);
        user.setAge(Integer.valueOf(ageField.getText()));
        user.setNIF(Integer.valueOf(nifField.getText()));
        user.setAddress(addressField.getText());
        user.setCellNo(cellNoField.getText());
        user.setCC(ccField.getText());
        user.setName(nameField.getText());
        user.setLanguage(languageField.getText());
        user.setEmail(emailField.getText());
        user.setUsername(usernameField.getText());

        // Limpa os campos de texto
        ageField.clear();
        nifField.clear();
        addressField.clear();
        cellNoField.clear();
        ccField.clear();
        nameField.clear();
        languageField.clear();
        emailField.clear();
        usernameField.clear();
    }

    @FXML
    private void handleCancelButton(ActionEvent event) {
        // Limpa os campos de texto
        ageField.clear();
        nifField.clear();
        addressField.clear();
        cellNoField.clear();
        ccField.clear();
        nameField.clear();
        languageField.clear();
        emailField.clear();
        usernameField.clear();
    }


    @FXML
    public void setPayInvisible() {
        payPane.setVisible(false);
        payText.setVisible(false);
        payCard.setVisible(false);
        payBank.setVisible(false);
        payCancel.setVisible(false);
    }

    @FXML 
    public void editProfile() {
        
    }

}
