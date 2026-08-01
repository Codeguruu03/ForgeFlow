package com.forgeflow.backend.workflow.model;

import java.util.Map;

public class NodeDefinition {
    private String id;
    private String type; // RECEIVE_FILE, VALIDATE, COMPILE, DEPLOY, SEND_EMAIL, CUSTOM
    private String label;
    private Map<String, Object> config;
    private double positionX;
    private double positionY;

    public NodeDefinition() {}

    public NodeDefinition(String id, String type, String label) {
        this.id = id;
        this.type = type;
        this.label = label;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public Map<String, Object> getConfig() { return config; }
    public void setConfig(Map<String, Object> config) { this.config = config; }

    public double getPositionX() { return positionX; }
    public void setPositionX(double positionX) { this.positionX = positionX; }

    public double getPositionY() { return positionY; }
    public void setPositionY(double positionY) { this.positionY = positionY; }
}
