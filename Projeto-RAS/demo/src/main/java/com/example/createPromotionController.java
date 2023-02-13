package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.example.Entities.Evento;
import com.example.Entities.Promocao;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class createPromotionController {

    private List<Evento> eventos;
    private List<Group> promos;
    private double layX;
    private double layY;
    private double width;
    private double height;

    @FXML
    private AnchorPane anchorEvents;
    @FXML
    private AnchorPane anchorPromos;
    @FXML
    private Pane promotionPane;

    public createPromotionController() {
        width = 210;
        height = 24;
        layX = 14;
        layY = 14;
        eventos = new ArrayList<>();
        promos = new ArrayList<>();
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
            Button event_button = new Button();
            TextArea win1 = new TextArea();
            TextField win1Odd = new TextField(); 
            TextArea tie = new TextArea();
            TextField tieOdd = new TextField();
            TextArea win2 = new TextArea();
            TextField win2Odd = new TextField();
            Text teams = new Text();
            TextField date = new TextField();
            List<Button> buttons = new ArrayList<>();

            /* Evento */

            event.setLayoutX(eventX);
            event.setLayoutY(eventY);
            //eventY += dif;
            event.setPrefWidth(655);
            event.setPrefHeight(82);
            event.setStyle("-fx-background-color: #0e451d; -fx-background-radius: 20");

            anchorEvents.getChildren().add(event);

            event_button.setLayoutX(eventX);
            event_button.setLayoutY(eventY);
            eventY += dif;
            event_button.setPrefWidth(655);
            event_button.setPrefHeight(82);
            event_button.setStyle("-fx-background-color: transparent; -fx-background-radius: 20");
            event_button.setOnMouseMoved(e -> { event.setStyle("-fx-background-color: orange; -fx-background-radius: 20");});
            event_button.setOnMouseExited(e -> { event.setStyle("-fx-background-color: #0e451d; -fx-background-radius: 20");});

            event_button.setOnMouseClicked(e -> {addEventPromo(evento);});

            anchorEvents.getChildren().add(event_button);

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
            //win1.setTextAlignment(TextAlignment.CENTER);
            //win1.setOnMouseClicked(e -> { win1.setTextFill(Paint.valueOf("orange")); addBet(evento, "Home");});
            event.getChildren().add(win1);
            //buttons.add(win1);

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
            //tie.setOnMouseClicked(e -> { tie.setTextFill(Paint.valueOf("orange")); addBet(evento, "Draw");});
            event.getChildren().add(tie);
            //buttons.add(tie);

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
            //win2.setTextAlignment(TextAlignment.CENTER);
            //win2.setOnMouseClicked(e -> { win2.setTextFill(Paint.valueOf("orange")); addBet(evento, "Away");});
            //buttons.add(win2);
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
            //event_buttons.add(buttons);
            //se não der, descomentar isto
        }
    }

    @FXML
    public void addEventPromo(Evento evento) {

        //Utilizador user = InitialPageController.user;

        if (!eventos.contains(evento)) { //não sei se resulta assim, probably not

            eventos.add(evento);

            Group event = new Group();
            TextField teams = new TextField();
            TextField bonus = new TextField();
            TextField montante = new TextField();
            Button cancelEvent = new Button();

            teams.setText(evento.getHomeTeam() + "-" + evento.getAwayTeam());
            teams.setStyle("-fx-text-fill: white; -fx-background-color: #0e451d; -fx-font-weight: bold; -fx-border-color: white"); 
            teams.setFont(Font.font ("System", 13));
            teams.setEditable(false);
            teams.setLayoutX(layX);
            teams.setLayoutY(layY);
            teams.setPrefWidth(182);
            teams.setPrefHeight(height);

            cancelEvent.setText("X");
            cancelEvent.setStyle("-fx-background-color:  #0e451d; -fx-border-color: white; -fx-text-fill: white; -fx-font-weight: bold");
            cancelEvent.setFont(Font.font ("System", 12));
            cancelEvent.setLayoutX(195);
            cancelEvent.setLayoutY(layY);
            layY += 90;
            cancelEvent.setPrefWidth(28);
            cancelEvent.setPrefHeight(height);

            bonus.setText("Bonus: ");
            bonus.setStyle("-fx-text-fill: white; -fx-background-color: orange; -fx-border-color: white");
            bonus.setFont(Font.font ("System", 11));
            bonus.setEditable(false);
            bonus.setLayoutX(layX);
            bonus.setLayoutY(teams.getLayoutY() + 24);
            bonus.setPrefWidth(105);
            bonus.setPrefHeight(26);

            montante.setPromptText("Bonus");
            montante.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-text-fill: black");
            montante.setFont(Font.font ("System", 11));
            montante.setEditable(true);
            montante.setLayoutX(118);
            montante.setLayoutY(teams.getLayoutY() + 23);
            montante.setPrefWidth(105);
            montante.setPrefHeight(26);
            montante.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.isEmpty()) {
                    Float mont = Float.valueOf(newValue);
                    Promocao promocao = new Promocao(mont);
                    evento.setPromocao(promocao);

                    //cancelEvent.setOnMouseClicked(e -> removeBet(bet, aposta));
                }
                else {
                    //caso em que se apaga o valor
                    //e provavelmente se volta a adicionar um novo
                    //penso que só falta isto
                }
            });

            anchorPromos.setPrefHeight(anchorPromos.getHeight() + 90);
            event.getChildren().add(teams);
            event.getChildren().add(cancelEvent);
            event.getChildren().add(bonus);
            event.getChildren().add(montante);
            anchorEvents.getChildren().add(event);
            promos.add(event);
        }
    }
}
