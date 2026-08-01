package com.forgeflow.frontend;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class MainController {

    private final BorderPane rootLayout;
    private final Label headerTitleLabel;
    private final Label userRoleBadge;
    private String currentUser = "admin";
    private String currentRole = "ADMINISTRATOR";

    private final Button btnDashboard;
    private final Button btnProcessFlow;
    private final Button btnScheduler;
    private final Button btnCodeExplorer;
    private final Button btnLocalSync;

    private final DashboardViewController dashboardView;
    private final ProcessFlowViewController processFlowView;
    private final SchedulerViewController schedulerView;
    private final DependencyViewController dependencyView;
    private final LocalSyncViewController localSyncView;

    public MainController() {
        rootLayout = new BorderPane();

        dashboardView = new DashboardViewController();
        processFlowView = new ProcessFlowViewController();
        schedulerView = new SchedulerViewController();
        dependencyView = new DependencyViewController();
        localSyncView = new LocalSyncViewController();

        // 1. Sidebar Navigation
        VBox sidebar = new VBox(10);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(240);

        Label titleLabel = new Label("ForgeFlow");
        titleLabel.getStyleClass().add("sidebar-title");

        btnDashboard = createNavButton("📊 Dashboard", true);
        btnProcessFlow = createNavButton("⚡ ProcessFlow Studio", false);
        btnScheduler = createNavButton("⏱ Task Scheduler", false);
        btnCodeExplorer = createNavButton("🔍 Code Explorer", false);
        btnLocalSync = createNavButton("🔄 LocalSync", false);

        btnDashboard.setOnAction(e -> selectView("Dashboard", dashboardView.getView(), btnDashboard));
        btnProcessFlow.setOnAction(e -> selectView("ProcessFlow Studio", processFlowView.getView(), btnProcessFlow));
        btnScheduler.setOnAction(e -> selectView("Distributed Task Scheduler", schedulerView.getView(), btnScheduler));
        btnCodeExplorer.setOnAction(e -> selectView("Code Dependency Explorer", dependencyView.getView(), btnCodeExplorer));
        btnLocalSync.setOnAction(e -> selectView("LocalSync LAN Manager", localSyncView.getView(), btnLocalSync));

        sidebar.getChildren().addAll(
                titleLabel,
                btnDashboard,
                btnProcessFlow,
                btnScheduler,
                btnCodeExplorer,
                btnLocalSync
        );

        // 2. Header Bar
        HBox headerBar = new HBox(15);
        headerBar.getStyleClass().add("header-bar");
        headerBar.setAlignment(Pos.CENTER_LEFT);

        headerTitleLabel = new Label("Dashboard");
        headerTitleLabel.getStyleClass().add("header-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusDot = new Label("● Cluster Online");
        statusDot.setStyle("-fx-text-fill: #34d399; -fx-font-weight: bold; -fx-font-size: 12px;");

        userRoleBadge = new Label(currentUser + " (" + currentRole + ")");
        userRoleBadge.getStyleClass().add("user-badge");

        headerBar.getChildren().addAll(headerTitleLabel, spacer, statusDot, userRoleBadge);

        rootLayout.setLeft(sidebar);
        rootLayout.setTop(headerBar);
        rootLayout.setCenter(dashboardView.getView());
    }

    private Button createNavButton(String text, boolean isSelected) {
        Button btn = new Button(text);
        btn.getStyleClass().add("nav-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        if (isSelected) {
            btn.getStyleClass().add("nav-button-selected");
        }
        return btn;
    }

    private void selectView(String title, Parent view, Button selectedBtn) {
        headerTitleLabel.setText(title);
        rootLayout.setCenter(view);

        btnDashboard.getStyleClass().remove("nav-button-selected");
        btnProcessFlow.getStyleClass().remove("nav-button-selected");
        btnScheduler.getStyleClass().remove("nav-button-selected");
        btnCodeExplorer.getStyleClass().remove("nav-button-selected");
        btnLocalSync.getStyleClass().remove("nav-button-selected");

        selectedBtn.getStyleClass().add("nav-button-selected");
    }

    public Parent getView() {
        return rootLayout;
    }
}
