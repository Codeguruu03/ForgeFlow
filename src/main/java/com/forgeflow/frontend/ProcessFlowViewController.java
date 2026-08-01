package com.forgeflow.frontend;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class ProcessFlowViewController {

    private final VBox container;
    private final TextArea executionLogsArea;
    private final Label statusLabel;

    public ProcessFlowViewController() {
        container = new VBox(20);
        container.setPadding(new Insets(25));

        // 1. Toolbar Row
        HBox toolbar = new HBox(15);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Visual Workflow Canvas & Simulator");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #f1f5f9;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnAddNode = new Button("+ Add Node");
        btnAddNode.getStyleClass().add("btn-secondary");

        Button btnValidate = new Button("✔ Validate DAG");
        btnValidate.getStyleClass().add("btn-secondary");

        Button btnRun = new Button("▶ Execute Workflow");
        btnRun.getStyleClass().add("btn-primary");

        toolbar.getChildren().addAll(title, spacer, btnAddNode, btnValidate, btnRun);

        // 2. Main Work Area (Canvas + Node Inspector)
        HBox workArea = new HBox(20);

        // Visual Canvas Box
        VBox canvasBox = new VBox(15);
        canvasBox.getStyleClass().add("card");
        canvasBox.setStyle("-fx-background-color: #0b0f17;");
        HBox.setHgrow(canvasBox, Priority.ALWAYS);

        Label canvasHeader = new Label("Workflow Diagram: File Processing & Email Pipeline");
        canvasHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: #38bdf8;");

        // Visual Nodes flow
        HBox nodeFlow = new HBox(15);
        nodeFlow.setAlignment(Pos.CENTER);
        nodeFlow.setPadding(new Insets(30, 10, 30, 10));

        VBox n1 = createVisualNode("1", "RECEIVE_FILE", "Receive File");
        Label arrow1 = new Label("➔");
        arrow1.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 18px;");

        VBox n2 = createVisualNode("2", "VALIDATE", "Validate Format");
        Label arrow2 = new Label("➔");
        arrow2.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 18px;");

        VBox n3 = createVisualNode("3", "COMPILE", "Compile Artifact");
        Label arrow3 = new Label("➔");
        arrow3.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 18px;");

        VBox n4 = createVisualNode("4", "DEPLOY", "Deploy Target");
        Label arrow4 = new Label("➔");
        arrow4.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 18px;");

        VBox n5 = createVisualNode("5", "SEND_EMAIL", "Send Email");

        nodeFlow.getChildren().addAll(n1, arrow1, n2, arrow2, n3, arrow3, n4, arrow4, n5);
        canvasBox.getChildren().addAll(canvasHeader, nodeFlow);

        // Node Inspector Sidebar
        VBox inspectorBox = new VBox(12);
        inspectorBox.getStyleClass().add("card");
        inspectorBox.setPrefWidth(280);

        Label inspHeader = new Label("Node Inspector");
        inspHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: #f1f5f9;");

        TextField nodeNameField = new TextField("Receive File");
        ComboBox<String> nodeTypeCombo = new ComboBox<>();
        nodeTypeCombo.getItems().addAll("RECEIVE_FILE", "VALIDATE", "COMPILE", "DEPLOY", "SEND_EMAIL", "CUSTOM");
        nodeTypeCombo.setValue("RECEIVE_FILE");
        nodeTypeCombo.setMaxWidth(Double.MAX_VALUE);

        TextArea configJsonArea = new TextArea("{\n  \"allowedExtensions\": [\"zip\", \"jar\", \"pdf\"],\n  \"maxSizeBytes\": 52428800\n}");
        configJsonArea.setPrefHeight(100);

        inspectorBox.getChildren().addAll(
                inspHeader,
                new Label("Node Label:"), nodeNameField,
                new Label("Component Type:"), nodeTypeCombo,
                new Label("Configuration JSON:"), configJsonArea
        );

        workArea.getChildren().addAll(canvasBox, inspectorBox);

        // 3. Execution Log Terminal Area
        VBox logBox = new VBox(10);
        logBox.getStyleClass().add("card");

        HBox logHeader = new HBox(10);
        Label logTitle = new Label("Execution Logs & Output Terminal");
        logTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #f1f5f9;");

        statusLabel = new Label("Status: Ready");
        statusLabel.getStyleClass().add("badge-success");

        Region logSpacer = new Region();
        HBox.setHgrow(logSpacer, Priority.ALWAYS);

        logHeader.getChildren().addAll(logTitle, logSpacer, statusLabel);

        executionLogsArea = new TextArea();
        executionLogsArea.getStyleClass().add("terminal-area");
        executionLogsArea.setPrefHeight(160);
        executionLogsArea.setEditable(false);
        executionLogsArea.setText("[ENGINE] Click 'Execute Workflow' to trigger ProcessFlow simulation engine...");

        logBox.getChildren().addAll(logHeader, executionLogsArea);

        // Action Handlers
        btnValidate.setOnAction(e -> {
            executionLogsArea.setText(
                    "[DAG VALIDATOR] Running graph structural validation...\n" +
                    "[DAG VALIDATOR] Nodes: 5 | Edges: 4\n" +
                    "[DAG VALIDATOR] Cycle detection DFS: No cycles detected.\n" +
                    "[DAG VALIDATOR] Disconnected nodes: None.\n" +
                    "[DAG VALIDATOR] RESULT: Workflow DAG is VALID ✔"
            );
            statusLabel.setText("Status: Validated");
            statusLabel.getStyleClass().setAll("badge-success");
        });

        btnRun.setOnAction(e -> {
            executionLogsArea.setText(
                    "[ENGINE] Initiating workflow: File Processing & Email Pipeline (v1)\n" +
                    "[ENGINE] Executing Node ID: 1 [RECEIVE_FILE - Receive File]\n" +
                    "[RECEIVE_FILE] Step started: Checking incoming file payload...\n" +
                    "[RECEIVE_FILE] File 'artifact_bundle.zip' (10485760 bytes) successfully received and buffered.\n" +
                    "[ENGINE] Executing Node ID: 2 [VALIDATE - Validate Format]\n" +
                    "[VALIDATE] Integrity checksum OK. Security policy scan passed.\n" +
                    "[ENGINE] Executing Node ID: 3 [COMPILE - Compile Artifact]\n" +
                    "[COMPILE] Generated artifact bytecodes: 142 classes compiled.\n" +
                    "[ENGINE] Executing Node ID: 4 [DEPLOY - Deploy Target]\n" +
                    "[DEPLOY] Deploying target build to staging environment...\n" +
                    "[ENGINE] Executing Node ID: 5 [SEND_EMAIL - Send Email]\n" +
                    "[SEND_EMAIL] Dispatching deployment notification email via SMTP...\n" +
                    "[ENGINE] Workflow executed successfully in 142ms."
            );
            statusLabel.setText("Status: Executed Successfully");
            statusLabel.getStyleClass().setAll("badge-success");
        });

        container.getChildren().addAll(toolbar, workArea, logBox);
    }

    private VBox createVisualNode(String id, String type, String label) {
        VBox box = new VBox(5);
        box.getStyleClass().add("workflow-node");
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(130);

        Label idLbl = new Label("Node #" + id);
        idLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #94a3b8;");

        Label typeLbl = new Label(type);
        typeLbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #38bdf8;");

        Label nameLbl = new Label(label);
        nameLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #f1f5f9;");

        box.getChildren().addAll(idLbl, typeLbl, nameLbl);
        return box;
    }

    public Parent getView() {
        return container;
    }
}
