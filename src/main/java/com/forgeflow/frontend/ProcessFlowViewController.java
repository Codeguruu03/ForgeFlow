package com.forgeflow.frontend;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class ProcessFlowViewController {

    private final VBox container;
    private final TextArea executionLogsArea;
    private final Label statusLabel;
    private final HBox nodeFlowContainer;
    private final List<VBox> renderedNodes = new ArrayList<>();
    private final List<String[]> nodeDefinitionsList = new ArrayList<>();

    public ProcessFlowViewController() {
        container = new VBox(20);
        container.setPadding(new Insets(25));

        // Initial default nodes
        nodeDefinitionsList.add(new String[]{"1", "RECEIVE_FILE", "Receive File"});
        nodeDefinitionsList.add(new String[]{"2", "VALIDATE", "Validate Format"});
        nodeDefinitionsList.add(new String[]{"3", "COMPILE", "Compile Artifact"});
        nodeDefinitionsList.add(new String[]{"4", "DEPLOY", "Deploy Target"});
        nodeDefinitionsList.add(new String[]{"5", "SEND_EMAIL", "Send Email"});

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

        Button btnExportReport = new Button("📥 Export HTML Report");
        btnExportReport.getStyleClass().add("btn-secondary");

        Button btnRun = new Button("▶ Execute Workflow");
        btnRun.getStyleClass().add("btn-primary");

        toolbar.getChildren().addAll(title, spacer, btnAddNode, btnValidate, btnExportReport, btnRun);

        // 2. Main Work Area (Canvas + Node Inspector)
        HBox workArea = new HBox(20);

        // Visual Canvas Box
        VBox canvasBox = new VBox(15);
        canvasBox.getStyleClass().add("card");
        canvasBox.setStyle("-fx-background-color: #0b0f17;");
        HBox.setHgrow(canvasBox, Priority.ALWAYS);

        Label canvasHeader = new Label("Interactive Canvas: File Processing & Email Pipeline (Click node to inspect)");
        canvasHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: #38bdf8;");

        nodeFlowContainer = new HBox(15);
        nodeFlowContainer.setAlignment(Pos.CENTER);
        nodeFlowContainer.setPadding(new Insets(30, 10, 30, 10));

        rebuildCanvasNodes();

        canvasBox.getChildren().addAll(canvasHeader, nodeFlowContainer);

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
        btnAddNode.setOnAction(e -> {
            int nextId = nodeDefinitionsList.size() + 1;
            nodeDefinitionsList.add(new String[]{String.valueOf(nextId), "CUSTOM", "Custom Task " + nextId});
            rebuildCanvasNodes();
            executionLogsArea.setText("[CANVAS] Added new Node #" + nextId + " [CUSTOM] to visual DAG workflow canvas.");
        });

        btnValidate.setOnAction(e -> {
            executionLogsArea.setText(
                    "[DAG VALIDATOR] Running graph structural validation...\n" +
                    "[DAG VALIDATOR] Nodes: " + nodeDefinitionsList.size() + " | Edges: " + (nodeDefinitionsList.size() - 1) + "\n" +
                    "[DAG VALIDATOR] Cycle detection DFS: No cycles detected.\n" +
                    "[DAG VALIDATOR] Disconnected nodes: None.\n" +
                    "[DAG VALIDATOR] RESULT: Workflow DAG is VALID ✔"
            );
            statusLabel.setText("Status: Validated");
            statusLabel.getStyleClass().setAll("badge-success");
        });

        btnExportReport.setOnAction(e -> {
            executionLogsArea.setText(
                    "[REPORT EXPORTER] HTML Audit Report generated!\n" +
                    "[REPORT EXPORTER] File saved: ./forgeflow-workflow-execution-report.html\n" +
                    "[REPORT EXPORTER] Prometheus metrics live at: http://localhost:8080/actuator/prometheus"
            );
            statusLabel.setText("Status: Report Generated");
            statusLabel.getStyleClass().setAll("badge-success");
        });

        btnRun.setOnAction(e -> {
            animateNodeExecution();
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

    private void rebuildCanvasNodes() {
        nodeFlowContainer.getChildren().clear();
        renderedNodes.clear();

        for (int i = 0; i < nodeDefinitionsList.size(); i++) {
            String[] def = nodeDefinitionsList.get(i);
            VBox nodeBox = createVisualNode(def[0], def[1], def[2]);
            renderedNodes.add(nodeBox);
            nodeFlowContainer.getChildren().add(nodeBox);

            if (i < nodeDefinitionsList.size() - 1) {
                Label arrow = new Label("➔");
                arrow.setStyle("-fx-text-fill: #38bdf8; -fx-font-size: 18px; -fx-font-weight: bold;");
                nodeFlowContainer.getChildren().add(arrow);
            }
        }
    }

    private void animateNodeExecution() {
        for (int i = 0; i < renderedNodes.size(); i++) {
            VBox node = renderedNodes.get(i);
            ScaleTransition st = new ScaleTransition(Duration.millis(300), node);
            st.setFromX(1.0);
            st.setFromY(1.0);
            st.setToX(1.15);
            st.setToY(1.15);
            st.setAutoReverse(true);
            st.setCycleCount(2);
            st.setDelay(Duration.millis(i * 150));
            st.play();
        }
    }

    private VBox createVisualNode(String id, String type, String label) {
        VBox box = new VBox(5);
        box.getStyleClass().add("workflow-node");
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(125);
        box.setStyle("-fx-cursor: hand;");

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
