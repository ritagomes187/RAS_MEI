package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.example.Entities.Aposta;
import com.example.Entities.Evento;
import com.example.Entities.Utilizador;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class mainPageController {
    private List<List<Button>> event_buttons; //nem sei se é preciso isto
    private List<Aposta> apostas;
    private List<Group> bets;
    private double layX;
    private double layY;
    private double width;
    private double height;
    private boolean simple;
    private boolean multiple;
    private Float multiple_odd;
    private Map<Aposta, Float> bet_amounts;
    private Map<Aposta, Float> bet_gains;

    @FXML
    private AnchorPane anchorBets;
    @FXML
    private AnchorPane anchorEvents;
    @FXML
    private TextField gains;
    @FXML
    private TextField total;
    @FXML
    private TextField total_quota;
    @FXML
    private Button simpleButton;
    @FXML
    private Button multipleButton;
    @FXML
    private Text info_submit;

    public mainPageController() {
        width = 210;
        height = 24;
        layX = 14;
        layY = 14;
        event_buttons = new ArrayList<>(new ArrayList<>());
        apostas = new ArrayList<>();
        bets = new ArrayList<>();
        simple = true;
        multiple = false;
        multiple_odd = (float) 1;
        bet_amounts = new HashMap<>();
        bet_gains = new HashMap<>();
    }

    @FXML
    public void switchToProfilePage() throws IOException {
        profilePageController.setName(InitialPageController.user.getName());
        profilePageController.setBalance(InitialPageController.user.getSaldo().getBalance());
        App.setRoot("profilePage");
    }

    @FXML
    public void addBet(Evento evento, String outcome) {
        //outcome: home, draw, away
        Utilizador user = InitialPageController.user;
        String chosen_team  = "Draw";
        Float chosen_odd = (float) 0;
        
        if (outcome.equals("Home")) {
            chosen_team = evento.getHomeTeam();
            chosen_odd = evento.getHomeOdd();
        }
        if (outcome.equals("Draw")) chosen_odd = evento.getDrawOdd();
        if (outcome.equals("Away")) {
            chosen_team = evento.getAwayTeam();
            chosen_odd = evento.getAwayOdd();
        }

        //Variáveis criadas apenas para serem usadas nas lambda expressions
        Float odd = chosen_odd; 
        String team = chosen_team;

        if (!betAlreadyinBetSlip(evento, team)) {

            Aposta aposta = new Aposta(odd, 0, team, evento);
            apostas.add(aposta);
            multiple_odd *= odd;
            total_quota.setText("Quota: " + String.format(Locale.ROOT, "%.02f", multiple_odd));

            Group bet = new Group();
            TextField teams = new TextField();
            TextField betChoice = new TextField();
            TextField quota = new TextField();
            TextField montante = new TextField();
            Button cancelBet = new Button();

            teams.setText(evento.getHomeTeam() + "-" + evento.getAwayTeam());
            teams.setStyle("-fx-text-fill: white; -fx-background-color: #0e451d; -fx-font-weight: bold; -fx-border-color: white"); 
            teams.setFont(Font.font ("System", 13));
            teams.setEditable(false);
            teams.setLayoutX(layX);
            teams.setLayoutY(layY);
            teams.setPrefWidth(182);
            teams.setPrefHeight(height);

            cancelBet.setText("X");
            cancelBet.setStyle("-fx-background-color:  #0e451d; -fx-border-color: white; -fx-text-fill: white; -fx-font-weight: bold");
            cancelBet.setFont(Font.font ("System", 12));
            cancelBet.setLayoutX(195);
            cancelBet.setLayoutY(layY);
            layY += 90;
            cancelBet.setPrefWidth(28);
            cancelBet.setPrefHeight(height);
            cancelBet.setOnMouseClicked(e -> removeBet(bet, aposta));
            
            betChoice.setText("Result: " + chosen_team);
            betChoice.setStyle("-fx-text-fill: white; -fx-background-color: #0e451d; -fx-border-color: white");
            betChoice.setFont(Font.font ("System", 11));
            betChoice.setEditable(false);
            betChoice.setLayoutX(layX);
            betChoice.setLayoutY(teams.getLayoutY() + 24);
            betChoice.setPrefWidth(width);
            betChoice.setPrefHeight(height);

            quota.setText("Quota: " + String.format("%.02f", chosen_odd));
            quota.setStyle("-fx-text-fill: white; -fx-background-color: orange; -fx-border-color: white");
            quota.setFont(Font.font ("System", 11));
            quota.setEditable(false);
            quota.setLayoutX(layX);
            quota.setLayoutY(betChoice.getLayoutY() + 24);

            if (simple) { //apostas simples
                quota.setPrefWidth(105);
                quota.setPrefHeight(26);

                montante.setPromptText("Amount");
                montante.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-text-fill: black");
                montante.setFont(Font.font ("System", 11));
                montante.setEditable(true);
                montante.setLayoutX(118);
                montante.setLayoutY(betChoice.getLayoutY() + 23);
                montante.setPrefWidth(105);
                montante.setPrefHeight(26);
                montante.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (!newValue.isEmpty()) { //valor foi preenchido
                        Float mont = Float.valueOf(newValue);
                        Float new_total;
                        if (bet_amounts.containsKey(aposta)) new_total = Float.valueOf(total.getText()) + mont - bet_amounts.get(aposta);
                        else new_total = total.getText().equals("") ? mont : Float.valueOf(total.getText()) + mont;
                        total.setText(String.format(Locale.ROOT, "%.02f", new_total));

                        Float bet_gain = mont * odd; //provavelmente depois adicionar este valor ao lado da aposta
                        Float new_gains;
                        if (bet_gains.containsKey(aposta)) new_gains = Float.valueOf(gains.getText()) + bet_gain - bet_gains.get(aposta);
                        else new_gains = gains.getText().equals("") ? bet_gain : Float.valueOf(gains.getText()) + bet_gain;
                        gains.setText(String.format(Locale.ROOT, "%.02f", new_gains));

                        aposta.setQuantia(mont);
                        bet_amounts.put(aposta, mont);
                        bet_gains.put(aposta, bet_gain);
                    }
                });
            }
            else { //apostas múltiplas
                quota.setPrefWidth(width);
                quota.setPrefHeight(height);

                total.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (!newValue.isEmpty()) {
                        Float bet_value = Float.valueOf(newValue);
                        bet_value = bet_value * multiple_odd;

                        gains.setText(String.format(Locale.ROOT, "%.02f", bet_value));
                    }
                    else {
                        gains.setText("");
                    }        
                });

                //não sei exatamente como fazer aqui, pq é um conjunto de apostas
            }

            total.setEditable(multiple);

            anchorBets.setPrefHeight(anchorBets.getHeight() + 90);
            bet.getChildren().add(teams);
            bet.getChildren().add(cancelBet);
            bet.getChildren().add(betChoice);
            bet.getChildren().add(quota);
            if (simple) bet.getChildren().add(montante);
            anchorBets.getChildren().add(bet);
            bets.add(bet);
        }
    }

    public void removeBet(Group bet, Aposta aposta) {
        anchorBets.getChildren().remove(bet);
        int index = bets.indexOf(bet);
        bets.remove(bet);
        apostas.remove(aposta);
        bet_amounts.remove(aposta);

        //Apostas Simples
        if (!total.getText().isEmpty() && !gains.getText().isEmpty()) {
            total.setText(String.format(Locale.ROOT, "%.02f", Float.valueOf(total.getText()) - aposta.getQuantia()));
            gains.setText(String.format(Locale.ROOT, "%.02f", Float.valueOf(gains.getText()) - aposta.getQuantia()*aposta.getOdd()));
        }

        //Apostas múltiplas
        if (!total_quota.getText().isEmpty()) {
            multiple_odd = multiple_odd / aposta.getOdd();
            total_quota.setText("Quota: " + String.format(Locale.ROOT, "%.02f", multiple_odd));
        }

        //dar reload
        if (bets.size() == 0) anchorBets.setPrefHeight(anchorBets.getPrefHeight() - 90);
        else refreshBets(index); 
        layY-=90;
    }

    @FXML
    public void submitBets() {
        if(enoughBalance()) {
            for (Aposta aposta : apostas) {
                Utilizador user = InitialPageController.user;
                Evento evento = aposta.getEvento();
                App.dl.makeBet(Float.valueOf(aposta.getQuantia()), aposta.getOdd(), user.getIdUser(), evento.getIdEvento(), aposta.getEquipa());
            }
            apostas.clear();

            total.setText("");
            gains.setText("");
            total_quota.setText("");
        }
        else info_submit.setText("Not enough money on wallet! Please insert a different ammount or deposit more money.");
    }
    
    @FXML
    public void showEvents() {
        int dif = 106;
        int teamsY = 25;
        int dateY = 39;
        int winOrTieY = 12;
        int winOddY = 52;
        int eventX = 14;
        int eventY = 14;

        List<Evento> eventos = App.dl.getGames("Football"); //para futebol para já

        for (Evento evento : eventos) {
            Pane event = new Pane();
            Button win1 = new Button();
            TextField win1Odd = new TextField(); 
            Button tie = new Button();
            TextField tieOdd = new TextField();
            Button win2 = new Button();
            TextField win2Odd = new TextField();
            Text teams = new Text();
            TextField date = new TextField();
            List<Button> buttons = new ArrayList<>();

            /* Evento */

            event.setLayoutX(eventX);
            event.setLayoutY(eventY);
            eventY += dif;
            event.setPrefWidth(655);
            event.setPrefHeight(82);
            event.setStyle("-fx-background-color: #0e451d; -fx-background-radius: 20");

            anchorEvents.getChildren().add(event);

            /* Equipas do Evento */

            teams.setText(evento.getHomeTeam().toUpperCase() + " - " + evento.getAwayTeam().toUpperCase());
            teams.setStyle("-fx-text-fill: white; -fx-background-color: transparent;  -fx-font-weight: bold");
            teams.setFont(Font.font("SansSerif", 13));
            teams.setFill(Paint.valueOf("white"));
            teams.setWrappingWidth(155);
            teams.setLayoutX(40);
            teams.setLayoutY(teamsY);

            event.getChildren().add(teams);

            /* Data do Evento */

            String[] date_parts = evento.getData().split("T");
            String[] time_parts = date_parts[1].split("\\.");
            date.setText(date_parts[0] + " " + time_parts[0]);
            date.setStyle("-fx-text-fill:  #939694; -fx-background-color: transparent");
            date.setFont(Font.font("System", 13));
            date.setEditable(false);
            date.setAlignment(Pos.CENTER);
            date.setLayoutX(0);
            date.setLayoutY(dateY);
            date.setPrefWidth(208);
            date.setPrefHeight(24);

            event.getChildren().add(date);

            /* Primeiro botão - Equipa da Casa */

            win1.setText(evento.getHomeTeam());
            win1.setLayoutX(212);
            win1.setLayoutY(winOrTieY);
            win1.setPrefWidth(115);
            win1.setPrefHeight(40);
            win1.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-text-fill: #0e451d; -fx-font-weight: bold; -fx-text-alignment: center");
            win1.setWrapText(true);
            win1.setTextAlignment(TextAlignment.CENTER);
            win1.setOnMouseClicked(e -> { 
                //win1.setStyle("-fx-background-color: orange; -fx-background-radius: 20; -fx-text-fill: #0e451d; -fx-font-weight: bold; -fx-text-alignment: center"); 
                addBet(evento, "Home");});
            event.getChildren().add(win1);
            buttons.add(win1);

            win1Odd.setText(String.format(Locale.ROOT, "%.02f", evento.getHomeOdd()));
            win1Odd.setStyle("-fx-text-fill: white; -fx-background-color: transparent");
            win1Odd.setFont(Font.font("System", 13));
            win1Odd.setEditable(false);
            win1Odd.setAlignment(Pos.CENTER);
            win1Odd.setLayoutX(215);
            win1Odd.setLayoutY(winOddY);
            win1Odd.setPrefWidth(109);
            win1Odd.setPrefHeight(24);
            event.getChildren().add(win1Odd);

             /* Segundo botão - Empate */

            tie.setText("Draw");
            tie.setLayoutX(352);
            tie.setLayoutY(winOrTieY);
            tie.setPrefWidth(115);
            tie.setPrefHeight(40);
            tie.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-text-fill: #0e451d; -fx-font-weight: bold; -fx-text-alignment: center");
            tie.setOnMouseClicked(e -> { 
                //tie.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-text-fill: #0e451d; -fx-font-weight: bold; -fx-text-alignment: center");
                addBet(evento, "Draw");});
            event.getChildren().add(tie);
            buttons.add(tie);

            tieOdd.setText(String.format(Locale.ROOT, "%.02f", evento.getDrawOdd()));
            tieOdd.setStyle("-fx-text-fill: white; -fx-background-color: transparent");
            tieOdd.setFont(Font.font("System", 13));
            tieOdd.setEditable(false);
            tieOdd.setAlignment(Pos.CENTER);
            tieOdd.setLayoutX(355);
            tieOdd.setLayoutY(winOddY);
            tieOdd.setPrefWidth(109);
            tieOdd.setPrefHeight(24);
            event.getChildren().add(tieOdd);

             /* Terceiro botão - Equipa de Fora */

            win2.setText(evento.getAwayTeam());
            win2.setLayoutX(492);
            win2.setLayoutY(winOrTieY);
            win2.setPrefWidth(115);
            win2.setPrefHeight(40);
            win2.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-text-fill: #0e451d; -fx-font-weight: bold; -fx-text-alignment: center");
            win2.setWrapText(true);
            win2.setTextAlignment(TextAlignment.CENTER);
            win2.setOnMouseClicked(e -> { 
                //win2.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-text-fill: #0e451d; -fx-font-weight: bold; -fx-text-alignment: center"); 
                addBet(evento, "Away");});
            buttons.add(win2);
            event.getChildren().add(win2);

            win2Odd.setText(String.format(Locale.ROOT, "%.02f", evento.getAwayOdd()));
            win2Odd.setStyle("-fx-text-fill: white; -fx-background-color: transparent");
            win2Odd.setFont(Font.font("System", 13));
            win2Odd.setEditable(false);
            win2Odd.setAlignment(Pos.CENTER);
            win2Odd.setLayoutX(495);
            win2Odd.setLayoutY(winOddY);
            win2Odd.setPrefWidth(109);
            win2Odd.setPrefHeight(24);
            event.getChildren().add(win2Odd);

            anchorEvents.setPrefHeight(anchorEvents.getPrefHeight() + dif);
            event_buttons.add(buttons);
        }
    }

    @FXML
    public void changeTypeBets() {
        if(simple) {
            simple = false;
            simpleButton.setStyle("-fx-background-color: white");
            multiple = true;
            multipleButton.setStyle("-fx-background-color: orange");
            total_quota.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: white; -fx-border-radius: 10");
        }
        else if (multiple) {
            multiple = false;
            multipleButton.setStyle("-fx-background-color: white");
            simple = true;
            simpleButton.setStyle("-fx-background-color: orange");
            total_quota.setStyle("-fx-background-color: transparent; -fx-text-fill: transparent; -fx-border-color: transparent; -fx-border-radius: 10");
        }
        addBetsAgain();
        total.clear();
    }

    public void addBetsAgain() {
        List<Aposta> save_apostas = new ArrayList<>(apostas);
        while(!bets.isEmpty()) removeBet(bets.get(0), apostas.get(0));

        for (int i=0; i<save_apostas.size(); i++) {
            Aposta aposta = save_apostas.get(i);
            String outcome = "Draw";
            if (aposta.getEquipa().equals(aposta.getEvento().getAwayTeam())) outcome = "Away";
            if (aposta.getEquipa().equals(aposta.getEvento().getHomeTeam())) outcome = "Home";
            addBet(aposta.getEvento(), outcome);
        }
    }

    public void refreshBets(int index) {
        for (int i=index; i<bets.size(); i++) {
            List<Node> nodes = bets.get(i).getChildren();
            for (int j=0; j<nodes.size(); j++) nodes.get(j).setLayoutY(nodes.get(j).getLayoutY()-90);
        }
        anchorBets.setPrefHeight(anchorBets.getPrefHeight() - 90);
    }

    public boolean betAlreadyinBetSlip(Evento evento, String team) {
        for (Aposta a : apostas) {
            if (a.getEvento().getIdEvento().equals(evento.getIdEvento()) && a.getEquipa().equals(team)) return true;
        }
        return false;
    }

    public boolean enoughBalance() {
        return (App.dl.getUserBalance(InitialPageController.user.getEmail()).getBalance() >= Float.valueOf(total.getText()));
    }
}
