package com.forgeflow.frontend;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class DashboardViewController {

    private final VBox container;

    public DashboardViewController() {
        container = new VBox(20);
        container.setPadding(new Insets(25));

        // 1. Metric Cards Row
        HBox cardsRow = new HBox(20);

        VBox card1 = createCard("Active Workflows", "1 Active", "#38bdf8");
        VBox card2 = createCard("Scheduled Jobs", "2 Active", "#818cf8");
        VBox card3 = createCard("Worker Cluster", "2 Nodes (Leader: 1)", "#34d399");
        VBox card4 = createCard("Memory Telemetry", "42.8 MB / 512 MB", "#f43f5e");

        HBox.setHgrow(card1, Priority.ALWAYS);
        HBox.setHgrow(card2, Priority.ALWAYS);
        HBox.setHgrow(card3, Priority.ALWAYS);
        HBox.setHgrow(card4, Priority.ALWAYS);

        cardsRow.getChildren().addAll(card1, card2, card3, card4);

        // 2. Active Workers & System Cluster Health Table
        VBox tableSection = new VBox(10);
        tableSection.getStyleClass().add("card");

        Label tableTitle = new Label("Distributed Worker Cluster Telemetry");
        tableTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #f1f5f9;");

        TableView<String[]> workerTable = new TableView<>();
        workerTable.setPrefHeight(180);

        TableColumn<String[], String> colId = new TableColumn<>("Worker ID");
        colId.setPrefWidth(180);
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[0]));

        TableColumn<String[], String> colHost = new TableColumn<>("Host IP");
        colHost.setPrefWidth(160);
        colHost.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[1]));

        TableColumn<String[], String> colStatus = new TableColumn<>("Status");
        colStatus.setPrefWidth(140);
        colStatus.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[2]));

        TableColumn<String[], String> colCpu = new TableColumn<>("CPU Load");
        colCpu.setPrefWidth(140);
        colCpu.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[3]));

        TableColumn<String[], String> colMem = new TableColumn<>("Memory Usage");
        colMem.setPrefWidth(160);
        colMem.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[4]));

        workerTable.getColumns().addAll(colId, colHost, colStatus, colCpu, colMem);

        workerTable.getItems().add(new String[]{"worker-node-1", "127.0.0.1:8081", "LEADER", "12.5%", "42.8 MB"});
        workerTable.getItems().add(new String[]{"worker-node-2", "192.168.1.102:8082", "IDLE", "5.1%", "28.4 MB"});

        tableSection.getChildren().addAll(tableTitle, workerTable);

        // 3. System Audit Log Stream
        VBox auditSection = new VBox(10);
        auditSection.getStyleClass().add("card");

        Label auditTitle = new Label("Real-time Platform Audit Log Stream");
        auditTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #f1f5f9;");

        TextArea terminalLogs = new TextArea();
        terminalLogs.getStyleClass().add("terminal-area");
        terminalLogs.setPrefHeight(200);
        terminalLogs.setEditable(false);
        terminalLogs.setText(
                "[2026-08-01 23:30:00] [SYSTEM] ForgeFlow platform cluster initialized.\n" +
                "[2026-08-01 23:30:01] [SECURITY] JWT Authentication Filter registered.\n" +
                "[2026-08-01 23:30:02] [SCHEDULER] Worker Node 'worker-node-1' elected cluster LEADER.\n" +
                "[2026-08-01 23:30:05] [PROCESSFLOW] Sample workflow 'File Processing & Email Pipeline' registered.\n" +
                "[2026-08-01 23:30:10] [LOCAL_SYNC] Storage directory initialized at ./sync_data.\n"
        );

        auditSection.getChildren().addAll(auditTitle, terminalLogs);

        container.getChildren().addAll(cardsRow, tableSection, auditSection);
    }

    private VBox createCard(String title, String value, String colorHex) {
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
