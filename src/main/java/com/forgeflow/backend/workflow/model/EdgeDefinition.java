package com.forgeflow.backend.workflow.model;

public class EdgeDefinition {
    private String from;
    private String to;
    private String condition;

    public EdgeDefinition() {}

    public EdgeDefinition(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }

    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
}
