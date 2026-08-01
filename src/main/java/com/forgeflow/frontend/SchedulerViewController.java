package com.forgeflow.frontend;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class SchedulerViewController {

    private final VBox container;

    public SchedulerViewController() {
        container = new VBox(20);
        container.setPadding(new Insets(25));

        // 1. Header Toolbar
        HBox toolbar = new HBox(15);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Distributed Task Scheduler Management");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #f1f5f9;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnCreateJob = new Button("+ Create New Job");
        btnCreateJob.getStyleClass().add("btn-primary");

        toolbar.getChildren().addAll(title, spacer, btnCreateJob);

        // 2. Scheduled Jobs Table
        VBox tableCard = new VBox(10);
        tableCard.getStyleClass().add("card");

        Label tableTitle = new Label("Scheduled Priority Jobs Queue");
        tableTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #f1f5f9;");

        TableView<String[]> jobTable = new TableView<>();
        jobTable.setPrefHeight(220);

        TableColumn<String[], String> colId = new TableColumn<>("ID");
        colId.setPrefWidth(50);
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[0]));

        TableColumn<String[], String> colName = new TableColumn<>("Job Name");
        colName.setPrefWidth(180);
        colName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[1]));

        TableColumn<String[], String> colCron = new TableColumn<>("Cron Expression");
        colCron.setPrefWidth(140);
        colCron.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[2]));

        TableColumn<String[], String> colType = new TableColumn<>("Type");
        colType.setPrefWidth(140);
        colType.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[3]));

        TableColumn<String[], String> colPriority = new TableColumn<>("Priority");
        colPriority.setPrefWidth(90);
        colPriority.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[4]));

        TableColumn<String[], String> colStatus = new TableColumn<>("Status");
        colStatus.setPrefWidth(110);
        colStatus.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[5]));

        jobTable.getColumns().addAll(colId, colName, colCron, colType, colPriority, colStatus);

        jobTable.getItems().add(new String[]{"1", "Nightly DB Backup", "0 0 2 * * ?", "SHELL_COMMAND", "HIGH (10)", "SCHEDULED"});
        jobTable.getItems().add(new String[]{"2", "Sync File Repository", "0 */15 * * * ?", "FILE_SYNC", "MEDIUM (5)", "SCHEDULED"});
        jobTable.getItems().add(new String[]{"3", "WebHook Telemetry Ping", "*/30 * * * * ?", "HTTP_REQUEST", "LOW (1)", "SCHEDULED"});

        tableCard.getChildren().addAll(tableTitle, jobTable);

        // 3. Dead Letter Queue (DLQ) Inspector & Retry Section
        VBox dlqCard = new VBox(10);
        dlqCard.getStyleClass().add("card");

        HBox dlqHeader = new HBox(10);
        Label dlqTitle = new Label("Dead Letter Queue (DLQ) Exception Handler");
        dlqTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #f87171;");

        Region dlqSpacer = new Region();
        HBox.setHgrow(dlqSpacer, Priority.ALWAYS);

        Button btnRetryDlq = new Button("↻ Re-queue Failed Jobs");
        btnRetryDlq.getStyleClass().add("btn-secondary");

        dlqHeader.getChildren().addAll(dlqTitle, dlqSpacer, btnRetryDlq);

        TableView<String[]> dlqTable = new TableView<>();
        dlqTable.setPrefHeight(150);

        TableColumn<String[], String> dlqId = new TableColumn<>("Job ID");
        dlqId.setPrefWidth(70);
        dlqId.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[0]));

        TableColumn<String[], String> dlqName = new TableColumn<>("Failed Job");
        dlqName.setPrefWidth(180);
        dlqName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[1]));

        TableColumn<String[], String> dlqReason = new TableColumn<>("Failure Exception");
        dlqReason.setPrefWidth(350);
        dlqReason.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[2]));

        TableColumn<String[], String> dlqRetries = new TableColumn<>("Retries Exceeded");
        dlqRetries.setPrefWidth(130);
        dlqRetries.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[3]));

        dlqTable.getColumns().addAll(dlqId, dlqName, dlqReason, dlqRetries);

        dlqTable.getItems().add(new String[]{"4", "Remote FTP Backup", "Connection refused: ftp.backup-server.internal:21", "3 / 3 Max Retries"});

        dlqCard.getChildren().addAll(dlqHeader, dlqTable);

        container.getChildren().addAll(toolbar, tableCard, dlqCard);
    }

    public Parent getView() {
        return container;
    }
}
