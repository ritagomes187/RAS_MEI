package com.example;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.geometry.Pos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.sql.SQLException;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import com.example.Entities.Aposta;
import com.example.Entities.Evento;

public class specPageController {
    private List<List<Button>> event_buttons;
    private Evento current_event;

    @FXML
    private TextField odd_change;
    @FXML
    private TextField teams;
    @FXML
    private Text message;
    @FXML
    private Pane odd_pane;
    @FXML
    private Text team_name;
    @FXML
    private TextField odd_insert;
    @FXML
    private Button yes;
    @FXML 
    private Button no;
    @FXML 
    private Button change_button;
    @FXML
    private AnchorPane anchorEvents;

    public specPageController() {
        event_buttons = new ArrayList<>(new ArrayList<>());
    }
    
    
    @FXML
    public void logout() throws IOException {
        App.dl.logout(InitialPageController.user.getEmail());
        App.setRoot("initialPage");
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
                showFirstInfo(evento, evento.getHomeTeam());
                win1.setStyle("-fx-background-color: orange; -fx-background-radius: 20; -fx-text-fill: #0e451d; -fx-font-weight: bold; -fx-text-alignment: center");});
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
                showFirstInfo(evento, "Draw"); 
                tie.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-text-fill: #0e451d; -fx-font-weight: bold; -fx-text-alignment: center");});
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
                showFirstInfo(evento, evento.getAwayTeam()); 
                win2.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-text-fill: #0e451d; -fx-font-weight: bold; -fx-text-alignment: center");});
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

    public void showFirstInfo(Evento evento, String chosen_team) {
        current_event = evento;
        odd_change.setVisible(true);
        teams.setVisible(true);
        teams.setText(evento.getHomeTeam() + "-" + evento.getAwayTeam());
        message.setVisible(true);
        message.setText("The game " + evento.getHomeTeam() + "-" + evento.getAwayTeam() + " is " + App.dl.getEventState(evento.getIdEvento()) + 
        ". Are you sure you want to continue?");
        yes.setVisible(true);
        yes.setOnMouseClicked(e -> {
            App.dl.changeEventState(evento.getIdEvento(), "Suspended");
            message.setText("The game " + evento.getHomeTeam() + "-" + evento.getAwayTeam() + " has been suspended. Insert new odd.");
            //mudar estado evento
            odd_pane.setVisible(true);
            team_name.setText(chosen_team);
            change_button.setVisible(true);});
        no.setVisible(true);
        no.setOnMouseClicked(e -> {
            odd_change.setVisible(false);
            teams.setVisible(false);
            message.setVisible(false);
            odd_pane.setVisible(false);
            yes.setVisible(false);
            no.setVisible(false);
        });
    }

    @FXML
    public void changeOdd() {
        //App.dl.fixOdd(null, null);
        message.setText("Odd has been successfully changed! Do you want to reactivate the game?");
        odd_pane.setVisible(false);
        change_button.setVisible(false);
        showEvents();
        yes.setOnMouseClicked(e -> {
            App.dl.changeEventState(current_event.getIdEvento(), "Open");
            odd_change.setVisible(false);
            teams.setVisible(false);
            message.setVisible(false);
            odd_pane.setVisible(false);
            change_button.setVisible(false);
            yes.setVisible(false);
            no.setVisible(false);});
        no.setOnMouseClicked(e -> {
            odd_change.setVisible(false);
            teams.setVisible(false);
            message.setVisible(false);
            odd_pane.setVisible(false);
            change_button.setVisible(false);
            yes.setVisible(false);
            no.setVisible(false);});
    }
}