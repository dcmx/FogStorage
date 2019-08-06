package com.fonkwill.fogstorage.client.domain;

public class ProcessingResult {

    private Placement placement;

    public Placement getPlacement() {
        return placement;
    }

    public void setPlacement(Placement placement) {
        this.placement = placement;
    }

    public Measurement getMeasurement() {
        return measurement;
    }

    public void setMeasurement(Measurement measurement) {
        this.measurement = measurement;
    }

    private Measurement measurement;
}
