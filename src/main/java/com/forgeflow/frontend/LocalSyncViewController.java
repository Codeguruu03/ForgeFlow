package com.forgeflow.frontend;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class LocalSyncViewController {

    private final VBox container;

    public LocalSyncViewController() {
        container = new VBox(20);
        container.setPadding(new Insets(25));

        // 1. Title Header
        Label title = new Label("LocalSync LAN Directory & File Synchronizer");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #f1f5f9;");

        // 2. Sync Settings Form Card
        VBox formCard = new VBox(15);
        formCard.getStyleClass().add("card");

        Label formTitle = new Label("Sync Pair Configuration & Security Settings");
        formTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #38bdf8;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);

        TextField srcField = new TextField("./sync_source");
        srcField.setPrefWidth(320);

        TextField tgtField = new TextField("./sync_target");
        tgtField.setPrefWidth(320);

        CheckBox chkEncryption = new CheckBox("Enable AES-256 Payload Encryption");
        chkEncryption.setSelected(true);
        chkEncryption.setStyle("-fx-text-fill: #e2e8f0;");

        CheckBox chkCompression = new CheckBox("Enable GZIP Payload Compression");
        chkCompression.setSelected(true);
        chkCompression.setStyle("-fx-text-fill: #e2e8f0;");

        grid.add(new Label("Source Directory:"), 0, 0);
        grid.add(srcField, 1, 0);
        grid.add(new Label("Target Directory:"), 0, 1);
        grid.add(tgtField, 1, 1);
        grid.add(chkEncryption, 0, 2, 2, 1);
        grid.add(chkCompression, 0, 3, 2, 1);

        Button btnSyncNow = new Button("🔄 Synchronize Folders Now");
        btnSyncNow.getStyleClass().add("btn-primary");

        formCard.getChildren().addAll(formTitle, grid, btnSyncNow);

        // 3. Synced Files & Version Audit Table
        VBox tableCard = new VBox(10);
        tableCard.getStyleClass().add("card");

        Label tableTitle = new Label("Synchronized Files & SHA-256 Version History");
        tableTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #f1f5f9;");

        TableView<String[]> syncTable = new TableView<>();
        syncTable.setPrefHeight(250);

        TableColumn<String[], String> colPath = new TableColumn<>("File Relative Path");
        colPath.setPrefWidth(220);
        colPath.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[0]));

        TableColumn<String[], String> colHash = new TableColumn<>("SHA-256 Checksum");
        colHash.setPrefWidth(320);
        colHash.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[1]));

        TableColumn<String[], String> colSize = new TableColumn<>("Size (Bytes)");
        colSize.setPrefWidth(110);
        colSize.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[2]));

        TableColumn<String[], String> colStatus = new TableColumn<>("Status");
        colStatus.setPrefWidth(120);
        colStatus.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[3]));

        syncTable.getColumns().addAll(colPath, colHash, colSize, colStatus);

        syncTable.getItems().add(new String[]{"documents/specs.pdf", "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", "1,048,576", "IN_SYNC"});
        syncTable.getItems().add(new String[]{"source/app_build.jar", "8f4e2a1b9c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f", "15,728,640", "IN_SYNC"});
        syncTable.getItems().add(new String[]{"config/database.yml", "a1b2c3d4e5f67890123456789abcdef0123456789abcdef0123456789abcdef0", "4,096", "UPDATED"});

        tableCard.getChildren().addAll(tableTitle, syncTable);

        container.getChildren().addAll(title, formCard, tableCard);
    }

    public Parent getView() {
        return container;
    }
}
