package com.coldchain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDateTime;

public class ComplianceAuditor {

    private static final long BREACH_LIMIT_MINUTES = 15;

    private static final double ALERT_RADIUS_METERS = 25000.0;

    private LocalDateTime breachStartTime;

    private double peakTemperature;

    private boolean violationLogged;

    public ComplianceAuditor() {

        breachStartTime = null;
        peakTemperature = 0.0;
        violationLogged = false;
    }

    public int createShipment(
            String consignmentCode,
            String origin,
            String destinationName,
            double latitude,
            double longitude,
            BigDecimal minTemp,
            BigDecimal maxTemp,
            BigDecimal penaltyRate) {

        String sql =
                "INSERT INTO shipments " +
                "(consignment_code, origin, destination_name, " +
                "destination_location, min_temp, max_temp, " +
                "status, base_penalty_rate_per_min) " +
                "VALUES (?, ?, ?, ST_PointFromText(?, 4326), ?, ?, ?, ?)";

        String point =
                "POINT(" + longitude + " " + latitude + ")";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                sql,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            statement.setString(
                    1,
                    consignmentCode
            );

            statement.setString(
                    2,
                    origin
            );

            statement.setString(
                    3,
                    destinationName
            );

            statement.setString(
                    4,
                    point
            );

            statement.setBigDecimal(
                    5,
                    minTemp
            );

            statement.setBigDecimal(
                    6,
                    maxTemp
            );

            statement.setString(
                    7,
                    "IN_TRANSIT"
            );

            statement.setBigDecimal(
                    8,
                    penaltyRate
            );

            int rows =
                    statement.executeUpdate();

            if (rows == 1) {

                try (
                        ResultSet resultSet =
                                statement.getGeneratedKeys()
                ) {

                    if (resultSet.next()) {

                        return resultSet.getInt(1);
                    }
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Shipment creation failed."
            );

            e.printStackTrace();
        }

        return -1;
    }

    public Shipment getShipmentById(
            int shipmentId) {

        String sql =
                "SELECT id, consignment_code, origin, " +
                "destination_name, " +
                "ST_Y(destination_location) AS latitude, " +
                "ST_X(destination_location) AS longitude, " +
                "min_temp, max_temp, status, " +
                "base_penalty_rate_per_min " +
                "FROM shipments " +
                "WHERE id = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    shipmentId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (resultSet.next()) {

                    return new Shipment(
                            resultSet.getInt("id"),
                            resultSet.getString(
                                    "consignment_code"
                            ),
                            resultSet.getString(
                                    "origin"
                            ),
                            resultSet.getString(
                                    "destination_name"
                            ),
                            resultSet.getDouble(
                                    "latitude"
                            ),
                            resultSet.getDouble(
                                    "longitude"
                            ),
                            resultSet.getBigDecimal(
                                    "min_temp"
                            ),
                            resultSet.getBigDecimal(
                                    "max_temp"
                            ),
                            resultSet.getString(
                                    "status"
                            ),
                            resultSet.getBigDecimal(
                                    "base_penalty_rate_per_min"
                            )
                    );
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Failed to retrieve shipment."
            );

            e.printStackTrace();
        }

        return null;
    }

    public void saveTelemetry(
            TelemetryPacket packet) {

        String sql =
                "INSERT INTO sensor_telemetry " +
                "(shipment_id, temperature, gps_location, recorded_at) " +
                "VALUES (?, ?, ST_PointFromText(?, 4326), ?)";

        String point =
                "POINT(" +
                packet.getLongitude() +
                " " +
                packet.getLatitude() +
                ")";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    packet.getShipmentId()
            );

            statement.setDouble(
                    2,
                    packet.getTemperature()
            );

            statement.setString(
                    3,
                    point
            );

            statement.setObject(
                    4,
                    packet.getRecordedAt()
            );

            statement.executeUpdate();

            System.out.println(
                    "Telemetry saved successfully."
            );

        } catch (Exception e) {

            System.out.println(
                    "Failed to save telemetry."
            );

            e.printStackTrace();
        }
    }

    public double calculateDistanceToDestination(
            int shipmentId,
            double latitude,
            double longitude) {

        String sql =
                "SELECT ST_Distance_Sphere(" +
                "ST_PointFromText(?, 4326), " +
                "destination_location" +
                ") AS distance_meters " +
                "FROM shipments " +
                "WHERE id = ?";

        String point =
                "POINT(" +
                longitude +
                " " +
                latitude +
                ")";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    point
            );

            statement.setInt(
                    2,
                    shipmentId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (resultSet.next()) {

                    return resultSet.getDouble(
                            "distance_meters"
                    );
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Distance calculation failed."
            );

            e.printStackTrace();
        }

        return -1;
    }

    public void checkTemperature(
            TelemetryPacket packet,
            Shipment shipment) {

        double temperature =
                packet.getTemperature();

        boolean normal =
                temperature >=
                        shipment.getMinTemp().doubleValue()
                &&
                temperature <=
                        shipment.getMaxTemp().doubleValue();

        if (normal) {

            System.out.println(
                    "Temperature Status : NORMAL"
            );

            resetBreach();

        } else {

            System.out.println(
                    "Temperature Status : BREACH"
            );

            try {

                handleBreach(
                        packet,
                        shipment
                );

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    private void handleBreach(
            TelemetryPacket packet,
            Shipment shipment)
            throws Exception {

        if (breachStartTime == null) {

            breachStartTime =
                    packet.getRecordedAt();

            peakTemperature =
                    packet.getTemperature();

            violationLogged = false;

            System.out.println(
                    "Breach Started At : "
                            + breachStartTime
            );

            System.out.println(
                    "Breach Duration    : 0 minutes"
            );

            return;
        }

        if (packet.getTemperature() >
                peakTemperature) {

            peakTemperature =
                    packet.getTemperature();
        }

        long duration =
                Duration.between(
                        breachStartTime,
                        packet.getRecordedAt()
                ).toMinutes();

        System.out.println(
                "Breach Started At : "
                        + breachStartTime
        );

        System.out.println(
                "Breach Duration    : "
                        + duration
                        + " minutes"
        );

        System.out.println(
                "Peak Temperature   : "
                        + peakTemperature
                        + " °C"
        );

        if (duration ==
                BREACH_LIMIT_MINUTES) {

            System.out.println(
                    "15-minute threshold reached."
            );
        }

        if (duration >
                BREACH_LIMIT_MINUTES
                &&
                !violationLogged) {

            triggerViolation(
                    packet,
                    shipment,
                    duration
            );
        }
    }

    private void triggerViolation(
            TelemetryPacket packet,
            Shipment shipment,
            long duration)
            throws Exception {

        long penaltyMinutes =
                duration -
                        BREACH_LIMIT_MINUTES;

        BigDecimal penalty =
                BigDecimal.valueOf(
                        penaltyMinutes
                )
                .multiply(
                        shipment.getPenaltyRate()
                )
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                );

        updateShipmentStatus(
                shipment.getId(),
                "COMPROMISED"
        );

        AuditBreachLog log =
                new AuditBreachLog(
                        shipment.getId(),
                        breachStartTime,
                        packet.getRecordedAt(),
                        (int) duration,
                        BigDecimal.valueOf(
                                peakTemperature
                        ).setScale(
                                2,
                                RoundingMode.HALF_UP
                        ),
                        penalty
                );

        saveBreachLog(log);

        violationLogged = true;

        System.out.println();
        System.out.println(
                "======================================"
        );

        System.out.println(
                "       COLD CHAIN VIOLATION"
        );

        System.out.println(
                "======================================"
        );

        System.out.println(
                "Shipment Status : COMPROMISED"
        );

        System.out.println(
                "Breach Duration : "
                        + duration
                        + " minutes"
        );

        System.out.println(
                "Peak Temperature: "
                        + peakTemperature
                        + " °C"
        );

        System.out.println(
                "Penalty Minutes : "
                        + penaltyMinutes
        );

        System.out.println(
                "Total Penalty   : $"
                        + penalty
        );

        System.out.println(
                "======================================"
        );
    }

    private void updateShipmentStatus(
            int shipmentId,
            String status)
            throws Exception {

        String sql =
                "UPDATE shipments " +
                "SET status = ? " +
                "WHERE id = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    status
            );

            statement.setInt(
                    2,
                    shipmentId
            );

            statement.executeUpdate();
        }
    }

    private void saveBreachLog(
            AuditBreachLog log)
            throws Exception {

        String sql =
                "INSERT INTO audit_breach_logs " +
                "(shipment_id, breach_start, breach_end, " +
                "duration_minutes, peak_temp, total_penalty) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    log.getShipmentId()
            );

            statement.setObject(
                    2,
                    log.getBreachStart()
            );

            statement.setObject(
                    3,
                    log.getBreachEnd()
            );

            statement.setInt(
                    4,
                    log.getDurationMinutes()
            );

            statement.setBigDecimal(
                    5,
                    log.getPeakTemp()
            );

            statement.setBigDecimal(
                    6,
                    log.getTotalPenalty()
            );

            statement.executeUpdate();

            System.out.println(
                    "Audit breach log saved successfully."
            );
        }
    }

    public void processTelemetry(
            TelemetryPacket packet) {

        try {

            Shipment shipment =
                    getShipmentById(
                            packet.getShipmentId()
                    );

            if (shipment == null) {

                System.out.println(
                        "Shipment not found."
                );

                return;
            }

            System.out.println();
            System.out.println(
                    "------------------------------------------"
            );

            System.out.println(
                    "Time        : "
                            + packet.getRecordedAt()
            );

            System.out.println(
                    "Temperature : "
                            + packet.getTemperature()
                            + " °C"
            );

            System.out.println(
                    "Latitude    : "
                            + packet.getLatitude()
            );

            System.out.println(
                    "Longitude   : "
                            + packet.getLongitude()
            );

            saveTelemetry(packet);

            checkTemperature(
                    packet,
                    shipment
            );

            checkSpatialAlert(
                    packet,
                    shipment
            );

        } catch (Exception e) {

            System.out.println(
                    "Telemetry processing failed."
            );

            e.printStackTrace();
        }
    }

    private void checkSpatialAlert(
            TelemetryPacket packet,
            Shipment shipment) {

        try {

            Shipment currentShipment =
                    getShipmentById(
                            shipment.getId()
                    );

            double distance =
                    calculateDistanceToDestination(
                            packet.getShipmentId(),
                            packet.getLatitude(),
                            packet.getLongitude()
                    );

            double distanceKm =
                    distance / 1000.0;

            System.out.printf(
                    "Distance to Destination : %.2f km%n",
                    distanceKm
            );

            if (currentShipment != null
                    &&
                    "COMPROMISED".equals(
                            currentShipment.getStatus())
                    &&
                    distance < ALERT_RADIUS_METERS) {

                System.out.println();
                System.out.println(
                        "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
                );

                System.out.println(
                        "       URGENT WAREHOUSE ALERT"
                );

                System.out.println(
                        "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
                );

                System.out.printf(
                        "Compromised shipment is %.2f km "
                                + "from %s.%n",
                        distanceKm,
                        currentShipment
                                .getDestinationName()
                );

                System.out.println(
                        "Notify warehouse personnel immediately."
                );

                System.out.println(
                        "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "Spatial alert check failed."
            );

            e.printStackTrace();
        }
    }

    private void resetBreach() {

        breachStartTime = null;

        peakTemperature = 0.0;

        violationLogged = false;
    }

    public void printFinalShipment(
            int shipmentId) {

        try {

            Shipment shipment =
                    getShipmentById(
                            shipmentId
                    );

            System.out.println();
            System.out.println(
                    "=========================================="
            );

            System.out.println(
                    "          FINAL SHIPMENT STATE"
            );

            System.out.println(
                    "=========================================="
            );

            if (shipment == null) {

                System.out.println(
                        "Shipment not found."
                );

                return;
            }

            System.out.println(
                    "ID                : "
                            + shipment.getId()
            );

            System.out.println(
                    "Consignment Code   : "
                            + shipment
                            .getConsignmentCode()
            );

            System.out.println(
                    "Origin             : "
                            + shipment.getOrigin()
            );

            System.out.println(
                    "Destination        : "
                            + shipment
                            .getDestinationName()
            );

            System.out.println(
                    "Required Temp      : "
                            + shipment.getMinTemp()
                            + "°C - "
                            + shipment.getMaxTemp()
                            + "°C"
            );

            System.out.println(
                    "Final Status       : "
                            + shipment.getStatus()
            );

            System.out.println(
                    "Penalty Rate       : $"
                            + shipment
                            .getPenaltyRate()
                            + "/minute"
            );

            System.out.println(
                    "=========================================="
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void printAuditLogs() {

        String sql =
                "SELECT id, shipment_id, breach_start, " +
                "breach_end, duration_minutes, peak_temp, " +
                "total_penalty, logged_at " +
                "FROM audit_breach_logs " +
                "ORDER BY id";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            System.out.println();
            System.out.println(
                    "=========================================="
            );

            System.out.println(
                    "          AUDIT BREACH LOGS"
            );

            System.out.println(
                    "=========================================="
            );

            boolean found = false;

            while (resultSet.next()) {

                found = true;

                System.out.println(
                        "Audit ID        : "
                                + resultSet.getInt("id")
                );

                System.out.println(
                        "Shipment ID     : "
                                + resultSet.getInt(
                                "shipment_id")
                );

                System.out.println(
                        "Breach Start    : "
                                + resultSet.getTimestamp(
                                "breach_start")
                );

                System.out.println(
                        "Breach End      : "
                                + resultSet.getTimestamp(
                                "breach_end")
                );

                System.out.println(
                        "Duration        : "
                                + resultSet.getInt(
                                "duration_minutes")
                                + " minutes"
                );

                System.out.println(
                        "Peak Temperature: "
                                + resultSet.getBigDecimal(
                                "peak_temp")
                                + " °C"
                );

                System.out.println(
                        "Total Penalty   : $"
                                + resultSet.getBigDecimal(
                                "total_penalty")
                );

                System.out.println(
                        "Logged At       : "
                                + resultSet.getTimestamp(
                                "logged_at")
                );

                System.out.println(
                        "------------------------------------------"
                );
            }

            if (!found) {

                System.out.println(
                        "No audit breach records found."
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "Failed to read audit logs."
            );

            e.printStackTrace();
        }
    }

    public void printTelemetryCount(
            int shipmentId) {

        String sql =
                "SELECT COUNT(*) AS total " +
                "FROM sensor_telemetry " +
                "WHERE shipment_id = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    shipmentId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (resultSet.next()) {

                    System.out.println(
                            "Telemetry Records : "
                                    + resultSet.getInt(
                                    "total")
                    );
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}