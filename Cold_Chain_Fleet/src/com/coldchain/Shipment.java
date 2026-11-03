package com.coldchain;



import java.math.BigDecimal;

public class Shipment {

    private int id;
    private String consignmentCode;
    private String origin;
    private String destinationName;
    private double destinationLatitude;
    private double destinationLongitude;
    private BigDecimal minTemp;
    private BigDecimal maxTemp;
    private String status;
    private BigDecimal penaltyRate;

    public Shipment(
            int id,
            String consignmentCode,
            String origin,
            String destinationName,
            double destinationLatitude,
            double destinationLongitude,
            BigDecimal minTemp,
            BigDecimal maxTemp,
            String status,
            BigDecimal penaltyRate) {

        this.id = id;
        this.consignmentCode = consignmentCode;
        this.origin = origin;
        this.destinationName = destinationName;
        this.destinationLatitude = destinationLatitude;
        this.destinationLongitude = destinationLongitude;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.status = status;
        this.penaltyRate = penaltyRate;
    }

    public int getId() {
        return id;
    }

    public String getConsignmentCode() {
        return consignmentCode;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public double getDestinationLatitude() {
        return destinationLatitude;
    }

    public double getDestinationLongitude() {
        return destinationLongitude;
    }

    public BigDecimal getMinTemp() {
        return minTemp;
    }

    public BigDecimal getMaxTemp() {
        return maxTemp;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getPenaltyRate() {
        return penaltyRate;
    }

    @Override
    public String toString() {

        return "Shipment{" +
                "id=" + id +
                ", consignmentCode='" +
                consignmentCode + '\'' +
                ", origin='" +
                origin + '\'' +
                ", destinationName='" +
                destinationName + '\'' +
                ", destinationLatitude=" +
                destinationLatitude +
                ", destinationLongitude=" +
                destinationLongitude +
                ", minTemp=" + minTemp +
                ", maxTemp=" + maxTemp +
                ", status='" + status + '\'' +
                ", penaltyRate=" + penaltyRate +
                '}';
    }
}
