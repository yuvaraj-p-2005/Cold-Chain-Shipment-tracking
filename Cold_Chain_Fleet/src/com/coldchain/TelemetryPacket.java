package com.coldchain;

import java.time.LocalDateTime;

public class TelemetryPacket {

    private int shipmentId;
    private double temperature;
    private double latitude;
    private double longitude;
    private LocalDateTime recordedAt;

    public TelemetryPacket(
            int shipmentId,
            double temperature,
            double latitude,
            double longitude,
            LocalDateTime recordedAt) {

        this.shipmentId = shipmentId;
        this.temperature = temperature;
        this.latitude = latitude;
        this.longitude = longitude;
        this.recordedAt = recordedAt;
    }

    public int getShipmentId() {
        return shipmentId;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    @Override
    public String toString() {

        return "TelemetryPacket{" +
                "shipmentId=" + shipmentId +
                ", temperature=" + temperature +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", recordedAt=" + recordedAt +
                '}';
    }
}
