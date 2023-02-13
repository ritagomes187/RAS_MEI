package com.example;

import java.io.IOException;

import javafx.fxml.FXML;

import java.time.LocalDate;
import java.sql.SQLException;
import javafx.scene.control.TextField;

import com.example.Entities.Promocao;
import com.example.Entities.Evento;

public class adminPageController {

    @FXML
    private TextField id;
    @FXML
    private TextField bonus;
    @FXML
    private TextField infoPromo;
    @FXML
    private TextField idEvento;
    @FXML
    private TextField desporto;
    @FXML
    private TextField home_team;
    @FXML
    private TextField away_team;
    @FXML
    private TextField estado;
    @FXML
    private TextField resultadoFinal;
    @FXML
    private TextField equipaVencedora;
    @FXML
    private TextField data;
    @FXML
    private TextField odd_home;
    @FXML
	private TextField odd_away;
    @FXML
	private TextField odd_draw;
    @FXML
    private TextField infoEvent;
    
    @FXML
    public void logout() throws IOException {
        App.dl.logout(InitialPageController.user.getEmail());
        App.setRoot("initialPage");
    }

    @FXML
    public void createPromotion() throws IOException {

        App.setRoot("createPromotion");

        // String id;
        // LocalDate startDate = startDatePicker.getStartDate();
        // LocalDate endDate = endDatePicker.getEndDate();
        // float bonus;

        // if (id.isEmpty() || bonus <= 0) {
        if (checkNoNullValues()) {
            // Mostrar mensagem de erro se algum campo obrigatório estiver vazio ou inválido
            System.out.println("Por favor, preencha todos os campos obrigatórios e verifique se os valores são válidos.");
            return;
        }

        // Verificar se a data de início é anterior à data de fim
        /*
         * if (startDate.isAfter(endDate)) {
         * System.out.
         * println("A data de início da promoção deve ser anterior à data de fim.");
         * return;
         * }
         */

        TextField bonusS = new TextField();
        bonusS.setText(String.valueOf(bonus));
        // Criar objeto da promoção com os dados fornecidos
        //App.dl.createPromo(Float.valueOf(bonusS.getText()));

        // Salvar a promoção na base de dados

        // Mostrar mensagem de sucesso
        System.out.println("Promoção criada com sucesso!");
        id.clear();
        bonus.clear();
        /*
         * startDate.clear();
         * endDate.clear();
         */
    }

    public boolean checkNoNullValues() {
        if (id.getText().isEmpty() || bonus.getText().isEmpty()) {
            infoPromo.setText("Please fill in all fields!");
            return false;
        }
        return true;
    }

    public boolean checkNoNullValuesEvent() {
        if (idEvento.getText().isEmpty() || desporto.getText().isEmpty() || home_team.getText().isEmpty() || away_team.getText().isEmpty() || estado.getText().isEmpty() || resultadoFinal.getText().isEmpty() || data.getText().isEmpty() || odd_home.getText().isEmpty() || odd_away.getText().isEmpty() || odd_draw.getText().isEmpty() ) {
            infoEvent.setText("Please fill in all fields!");
            return false;
        }
        return true;
    }

    @FXML
    public void createEvent() {
       
        if (checkNoNullValuesEvent()) {
            // Mostrar mensagem de erro se algum campo obrigatório estiver vazio ou inválido
            System.out.println("Por favor, preencha todos os campos obrigatórios e verifique se os valores são válidos.");
            return;
        }

        
        TextField desporto = new TextField();
        desporto.setText(String.valueOf(desporto));
        TextField home_team = new TextField();
        home_team.setText(String.valueOf(home_team));
        TextField away_team = new TextField();
        away_team.setText(String.valueOf(away_team));
        TextField data = new TextField();
        data.setText(String.valueOf(data));

        //App.dl.createEvent(desporto.getText(),home_team.getText(),away_team.getText(),estado.getText(),resultadoFinal.getText(),data.getText());
		App.dl.createEvent(desporto.getText(),home_team.getText(),away_team.getText(),data.getText());


        // Mostrar mensagem de sucesso
        System.out.println("Evento criada com sucesso!");
        idEvento.clear();
        desporto.clear();
        home_team.clear();
        away_team.clear();
        estado.clear();
        resultadoFinal.clear();
		equipaVencedora.clear();
        data.clear();     
        odd_home.clear();
        odd_away.clear();
        odd_draw.clear();    
    }

    @FXML
    public void changeEventState() {
        if (checkNoNullValuesEvent()) {
            // Mostrar mensagem de erro se algum campo obrigatório estiver vazio ou inválido
            System.out.println("Por favor, preencha todos os campos obrigatórios e verifique se os valores são válidos.");
            return;
        }

        
        TextField idEvento = new TextField();
        idEvento.setText(String.valueOf(idEvento));
        TextField estado = new TextField();
        estado.setText(String.valueOf(estado));
        App.dl.changeEventState(idEvento.getText(),estado.getText());


        // Mostrar mensagem de sucesso
        System.out.println("Estado do evento alterado com sucesso!");
        idEvento.clear();
        desporto.clear();
        home_team.clear();
        away_team.clear();
        estado.clear();
        resultadoFinal.clear();
		equipaVencedora.clear();
        data.clear();     
        odd_home.clear();
        odd_away.clear();
        odd_draw.clear();    
    }

    @FXML
    public void closeEvent() {
        if (checkNoNullValuesEvent()) {
            // Mostrar mensagem de erro se algum campo obrigatório estiver vazio ou inválido
            System.out.println("Por favor, preencha todos os campos obrigatórios e verifique se os valores são válidos.");
            return;
        }

        
        TextField idEvento = new TextField();
        idEvento.setText(String.valueOf(idEvento));
		TextField resultadoFinal = new TextField();
		resultadoFinal.setText(String.valueOf(resultadoFinal));
		TextField equipaVencedora = new TextField();
		equipaVencedora.setText(String.valueOf(equipaVencedora));
        
        App.dl.closeEvent(idEvento.getText(), resultadoFinal.getText(), equipaVencedora.getText());


        // Mostrar mensagem de sucesso
        System.out.println("Evento fechado com sucesso!");
        idEvento.clear();
        desporto.clear();
        home_team.clear();
        away_team.clear();
        estado.clear();
        resultadoFinal.clear();
		equipaVencedora.clear();
        data.clear();     
        odd_home.clear();
        odd_away.clear();
        odd_draw.clear();    
    }
}
