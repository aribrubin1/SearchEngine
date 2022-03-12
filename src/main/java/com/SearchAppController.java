package com;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;

public class SearchAppController extends Application {
    String root;
    boolean boost;
    Tester tester;
    public SearchAppController() throws IOException {
        root = javax.swing.JOptionPane.showInputDialog("Please input root page to search from: ");
        boost =false;
        tester = new Tester();
        tester.initialize();
        tester.crawl(root);
    }

    public void start(Stage primarystage){
        Pane apane = new Pane();
        SearchAppView view = new SearchAppView();
        apane.getChildren().add(view);
        primarystage.setTitle("Search Engine");
        primarystage.setResizable(false);
        primarystage.setScene(new Scene(apane));
        primarystage.show();

        view.getBoost().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                boost=!boost;
                view.updateboost(boost);
            }
        });

        view.getSearch().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String querie = view.getSearchquerie().getText();
                List<SearchResult> results = null;
                try {
                    results = tester.search(querie,boost,10);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                view.updatelists(results);
            }
        });

    }
    public static void main(String[] args){
        launch(args);
    }
}
