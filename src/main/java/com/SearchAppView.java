package com;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.util.List;

public class SearchAppView extends Pane {
    private Button search;
    private TextField searchquerie;
    private ToggleButton boost;
    private ListView<String> titles;
    private ListView<Double> scores;


    public SearchAppView(){
        Label enter = new Label("Enter Search Querie Here:");
        enter.relocate(10,10);

        searchquerie = new TextField();
        searchquerie.relocate(10,30);
        searchquerie.setPrefSize(320,30);

        search = new Button("Search");
        search.relocate(195,70);
        search.setPrefSize(110,40);

        boost = new ToggleButton("Boost: false");
        boost.relocate(10,70);
        boost.setPrefSize(90,20);

        titles = new ListView<>();
        titles.relocate(60,140);
        titles.setPrefSize(90,250);

        scores = new ListView<>();
        scores.relocate(150,140);
        scores.setPrefSize(150,250);

        getChildren().addAll(enter,searchquerie,search,boost,titles,scores);
        setPrefSize(380,400);
    }

    public void updateboost(boolean booster){
        boost.setText("BOOST: " + booster);
    }

    public void updatelists(List<SearchResult> listing){
        String[] titlelist = new String[10];
        Double[] scorelist = new Double[10];
        int index = 0;
        for(SearchResult resultant : listing){
            titlelist[index]=resultant.getTitle();
            System.out.println(resultant.getTitle());
            scorelist[index]=resultant.getScore();
            System.out.println(resultant.getScore());
            index+=1;
        }
        ObservableList<String> observeTitles = FXCollections.observableArrayList(titlelist);
        ObservableList<Double> observeScores = FXCollections.observableArrayList(scorelist);
        titles.setItems(observeTitles);
        scores.setItems(observeScores);

    }

    public Button getSearch(){return search;}
    public TextField getSearchquerie(){return searchquerie;}
    public ToggleButton getBoost(){return boost;}
    public ListView<String> getTitles(){return titles;}
    public ListView<Double> getScores(){return scores;}

}
