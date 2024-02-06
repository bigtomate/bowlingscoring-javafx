package org.example;// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BowlingScoring extends Application {

    private int frameCounter = 1;
    Map<Integer, List<Integer>> scoreMap = new HashMap<>();
    Map<Integer, List<Integer>> strikeMap = new HashMap<>();

    Frame[] frames = new Frame[]{
            new Frame(1),
            new Frame(2),
            new Frame(3),
            new Frame(4),
            new Frame(5),
            new Frame(6),
            new Frame(7),
            new Frame(8),
            new Frame(9),
            new Frame(10)};

    Button[] pinButton = new Button[]{
            new Button("0"),
            new Button("1"),
            new Button("2"),
            new Button("3"),
            new Button("4"),
            new Button("5"),
            new Button("6"),
            new Button("7"),
            new Button("8"),
            new Button("9"),
            new Button("10")};
    Label[] labels = new Label[]{
            new Label("1"),
            new Label("2"),
            new Label("3"),
            new Label("4"),
            new Label("5"),
            new Label("6"),
            new Label("7"),
            new Label("8"),
            new Label("9"),
            new Label("10")
    };

    GridPane[] framePane = new GridPane[] {
            frames[0].gridPane,
            frames[1].gridPane,
            frames[2].gridPane,
            frames[3].gridPane,
            frames[4].gridPane,
            frames[5].gridPane,
            frames[6].gridPane,
            frames[7].gridPane,
            frames[8].gridPane,
            frames[9].gridPane
    };

    GridPane pane = new GridPane();
    EventHandler<ActionEvent> buttonHandler = new EventHandler<ActionEvent>() {
        public void handle(ActionEvent event) {
            Button clickedbutton = (Button) event.getSource();
            int pinDown = Integer.valueOf(clickedbutton.getText());
            addFurtherThrowToStrikeMap(pinDown);
            checkIfTimeForStrikeScore();
            Frame frame = frames[frameCounter - 1];
            //first throw this frame
            if (!scoreMap.containsKey(frameCounter - 1)) {
                scoreMap.put(frameCounter - 1, new ArrayList<>());
                frame.setButtonContentByIdx(0, clickedbutton.getText());
                //the previous has spare
                if(frameCounter >=2 && frames[frameCounter - 2].hasSpare){
                    frames[frameCounter - 2].getScorCell().setText(String.valueOf(frames[frameCounter - 2].caculateScore(pinDown)));
                }
                disableButtonByLeftedPins(pinDown);
            }
            if (scoreMap.get(frameCounter - 1).size() == 1) {
                frame.setButtonContentByIdx(2, clickedbutton.getText());
            }

            scoreMap.get(frameCounter - 1).add(pinDown);

            if(frame.hasStrike && frameCounter != 10){
                strikeMap.put(frameCounter - 1, new ArrayList<>());
                frameCounter++;
                return;
            }
            //after two throws, time to give the score for this frame
            if (scoreMap.get(frameCounter - 1).size() >= 2) {
                resetPinButton();
                if(!frame.hasStrike && !frame.hasSpare){
                    int scores = frame.caculateScore(pinDown);
                    frame.getScorCell().setText(String.valueOf(scores));
                }
                if (frameCounter != 10) {
                    frameCounter++;
                } else {
                    //the 10 th frame
                    if(frame.hasStrike){
                        disableButtonByLeftedPins(pinDown);
                    }
                    if (scoreMap.get(frameCounter - 1).size() == 3) {
                        frame.setButtonContentByIdx(3, clickedbutton.getText());
                        frame.getScorCell().setText(String.valueOf(frame.caculateScore(pinDown)));
                        for (Button pinButton : pinButton) {
                            pinButton.removeEventHandler(ActionEvent.ACTION, this);
                        }
                    }
                }
            }
        }
    };

    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/sample.fxml"));
        Label label = new Label("Bowling Scoring");
        Scene scene = new Scene(root);
        stage.setTitle("Bowling Sheet");
        resetPinButton();
        GridPane inputButtonGrid = new GridPane();
        inputButtonGrid.addRow(1, pinButton);

        ((GridPane) pane).addRow(2,labels);

        ((GridPane) pane).addRow(3, framePane);

        VBox vbox = new VBox();
        vbox.setSpacing(5.0D);
        vbox.setPadding(new Insets(10.0D, 0.0D, 0.0D, 10.0D));
        vbox.getChildren().addAll(new Node[]{label, inputButtonGrid, pane});
        ((GridPane) scene.getRoot()).getChildren().addAll(new Node[]{vbox});
        stage.setScene(scene);
        stage.show();
    }

    class Frame {
        private int idx;
        private GridPane gridPane;
        private Button[] cells;
        private boolean hasStrike = false;
        private boolean hasSpare = false;

        Frame(int idx) {
            this.gridPane = new GridPane();
            cells = new Button[4];
            for (int i = 0; i < cells.length; ++i) {
                cells[i] = new Button();
                cells[i].setMinHeight(50);
                cells[i].setMinWidth(50);
            }
            gridPane.add(cells[0], 0, 0);
            gridPane.add(cells[1], 0, 1);
            gridPane.add(cells[2], 1, 0);
            if (idx < 10) {
                gridPane.add(cells[3], 1, 1);
            } else {
                gridPane.add(cells[3], 2, 0);
            }
        }

        Button getScorCell() {
            return cells[1];
        }

        void setButtonContentByIdx(int cellIdx, String pinDown) {
            cells[cellIdx].setText(pinDown);
            if (cellIdx == 2) {
                int scoresThrow1 = scoreMap.get(frameCounter - 1).stream().mapToInt(Integer::intValue).sum();
                int scores = scoresThrow1 + Integer.valueOf(pinDown);
                if (scores == 10) {
                    cells[cellIdx].setText("/");
                    hasSpare = true;
                    return;
                }
            }
            if (Integer.valueOf(pinDown) == 0) {
                cells[cellIdx].setText("-");
                return;
            }
            if (Integer.valueOf(pinDown) == 10) {
                cells[cellIdx].setText("X");
                hasStrike = true;
                return;
            }
        }

        private int caculateScore(int pinDown) {
            if(hasSpare){
                int scorePreviousValue = frameCounter >= 3?  Integer.valueOf(frames[frameCounter - 3].getScorCell().getText()) : 0;
                int scoreBonus = 10 + pinDown;
                return  scoreBonus + scorePreviousValue;
            }
            else{
                int scores = scoreMap.get(frameCounter - 1).stream().mapToInt(Integer::intValue).sum();
                int scorePreviousValue = frameCounter >= 2 ? Integer.valueOf(frames[frameCounter - 2].getScorCell().getText()) : 0;
                return scores + scorePreviousValue;
            }
        }
    }

    private void disableButtonByLeftedPins(int pinDown){
        int leftedPins = 10 - Integer.valueOf(pinDown);
        for(Button pb : pinButton){
            if(Integer.valueOf(pb.getText()) > leftedPins && pinDown != 10){
                pb.setDisable(true);
                pb.setStyle("-fx-background-color: #ff0000;");
            }
        }
    }

    private void resetPinButton(){
        for(Button pb : pinButton){
            pb.setDisable(false);
            pb.addEventHandler(ActionEvent.ACTION, this.buttonHandler);
            pb.setStyle("-fx-background-color: #FFFFFF;");
            pb.setStyle("-fx-border-color: black;");
        }
    }

    private void addFurtherThrowToStrikeMap(int pinDown) {
        for(Map.Entry entry : strikeMap.entrySet()) {
            ((List)entry.getValue()).add(pinDown);
        }
    }

    private void checkIfTimeForStrikeScore(){
        for(Map.Entry entry : strikeMap.entrySet()) {
            if(((List)entry.getValue()).size() == 2){
                int frameIdx = (int) entry.getKey();
                int scoreNextThrows =  ((List<Integer>)entry.getValue()).stream().mapToInt(Integer::intValue).sum();
                int previousScore = (frameIdx >=1 && !frames[frameIdx - 1].getScorCell().getText().isEmpty())?
                        Integer.valueOf(frames[frameIdx - 1].getScorCell().getText()) : 0;
                frames[frameIdx].getScorCell().setText(String.valueOf(scoreNextThrows + 10 + previousScore));
            }
        }
    }

    private void reset(){
        frameCounter = 1;
        scoreMap = new HashMap<>();
        strikeMap = new HashMap<>();
        resetPinButton();
        resetFrame();
    }


    private void resetFrame(){
        for(Frame frame : frames){
            for(Button cell : frame.cells){
                cell.setText("");
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
