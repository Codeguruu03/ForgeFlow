package com.forgeflow.frontend;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class DependencyViewController {

    private final VBox container;

    public DependencyViewController() {
        container = new VBox(20);
        container.setPadding(new Insets(25));

        // 1. Directory Selector Row
        HBox topBox = new HBox(15);
        topBox.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Java Static Code Dependency Explorer");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #f1f5f9;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        TextField pathInput = new TextField("c:\\Users\\naman\\Desktop\\ForgeFlow");
        pathInput.setPrefWidth(300);

        Button btnAnalyze = new Button("🔍 Analyze Project");
        btnAnalyze.getStyleClass().add("btn-primary");

        topBox.getChildren().addAll(title, spacer, new Label("Project Path:"), pathInput, btnAnalyze);

        // 2. Metrics Overview Cards
        HBox metricsRow = new HBox(20);

        VBox m1 = createMetricCard("Parsed Files", "55 .java files", "#38bdf8");
        VBox m2 = createMetricCard("Total Lines of Code (LOC)", "4,820 LOC", "#818cf8");
        VBox m3 = createMetricCard("Avg Cyclomatic Complexity", "3.4 (Optimal)", "#34d399");
        VBox m4 = createMetricCard("Circular Dependencies", "0 Cycles Detected", "#4ade80");

        HBox.setHgrow(m1, Priority.ALWAYS);
        HBox.setHgrow(m2, Priority.ALWAYS);
        HBox.setHgrow(m3, Priority.ALWAYS);
        HBox.setHgrow(m4, Priority.ALWAYS);

        metricsRow.getChildren().addAll(m1, m2, m3, m4);

        // 3. Graph Dependency Table & Dead Code Candidates
        HBox tablesRow = new HBox(20);

        VBox depCard = new VBox(10);
        depCard.getStyleClass().add("card");
        HBox.setHgrow(depCard, Priority.ALWAYS);

        Label depTitle = new Label("Class Coupling Metrics (Fan-In / Fan-Out)");
        depTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #f1f5f9;");

        TableView<String[]> depTable = new TableView<>();
        depTable.setPrefHeight(250);

        TableColumn<String[], String> colClass = new TableColumn<>("Class Name");
        colClass.setPrefWidth(220);
        colClass.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[0]));

        TableColumn<String[], String> colPkg = new TableColumn<>("Package");
        colPkg.setPrefWidth(180);
        colPkg.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[1]));

        TableColumn<String[], String> colFanIn = new TableColumn<>("Fan-In");
        colFanIn.setPrefWidth(70);
        colFanIn.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[2]));

        TableColumn<String[], String> colFanOut = new TableColumn<>("Fan-Out");
        colFanOut.setPrefWidth(70);
        colFanOut.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[3]));

        depTable.getColumns().addAll(colClass, colPkg, colFanIn, colFanOut);

        depTable.getItems().add(new String[]{"WorkflowEngineService", "com.forgeflow.backend.workflow.engine", "3", "5"});
        depTable.getItems().add(new String[]{"DistributedSchedulerService", "com.forgeflow.backend.scheduler", "2", "4"});
        depTable.getItems().add(new String[]{"DependencyAnalyzerService", "com.forgeflow.backend.dependency", "1", "3"});
        depTable.getItems().add(new String[]{"LocalSyncService", "com.forgeflow.backend.sync", "1", "2"});

        depCard.getChildren().addAll(depTitle, depTable);

        // Dead Code Candidates Card
        VBox deadCodeCard = new VBox(10);
        deadCodeCard.getStyleClass().add("card");
        deadCodeCard.setPrefWidth(320);

        Label deadTitle = new Label("Dead Code Candidates (Unreferenced)");
        deadTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #fbbf24;");

        ListView<String> deadCodeList = new ListView<>();
        deadCodeList.setPrefHeight(250);
        deadCodeList.getItems().add("com.forgeflow.backend.util.LegacyHashUtil");
        deadCodeList.getItems().add("com.forgeflow.backend.model.OldJobConfig");

        deadCodeCard.getChildren().addAll(deadTitle, deadCodeList);

        tablesRow.getChildren().addAll(depCard, deadCodeCard);

        container.getChildren().addAll(topBox, metricsRow, tablesRow);
    }

    private VBox createMetricCard(String title, String value, String colorHex) {
        VBox card = new VBox(8);
        card.getStyleClass().add("card");

        Label titleLbl = new Label(title);
        titleLbl.getStyleClass().add("card-title");

        Label valLbl = new Label(value);
        valLbl.getStyleClass().add("card-value");
        valLbl.setStyle("-fx-text-fill: " + colorHex + ";");

        card.getChildren().addAll(titleLbl, valLbl);
        return card;
    }

    public Parent getView() {
        return container;
    }
}
