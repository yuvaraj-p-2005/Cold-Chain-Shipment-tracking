package com.coldchain;

import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {

        ComplianceAuditor auditor =
                new ComplianceAuditor();

        int shipmentId = 1;

        Shipment shipment =
                auditor.getShipmentById(shipmentId);

        if (shipment == null) {

            System.out.println(
                    "Shipment not found."
            );

            return;
        }

        System.out.println(
                "=========================================="
        );

        System.out.println(
                "     COLD CHAIN FLEET TRACKING SYSTEM"
        );

        System.out.println(
                "=========================================="
        );

        System.out.println(
                "Shipment ID       : "
                        + shipment.getId()
        );

        System.out.println(
                "Consignment Code  : "
                        + shipment.getConsignmentCode()
        );

        System.out.println(
                "Origin            : "
                        + shipment.getOrigin()
        );

        System.out.println(
                "Destination       : "
                        + shipment.getDestinationName()
        );

        System.out.println(
                "Required Temp     : "
                        + shipment.getMinTemp()
                        + "°C - "
                        + shipment.getMaxTemp()
                        + "°C"
        );

        System.out.println(
                "Penalty Rate      : $"
                        + shipment.getPenaltyRate()
                        + "/minute"
        );

        System.out.println(
                "=========================================="
        );


        /*
         * Packet 1
         * 10:00 AM
         * Normal temperature
         */

        TelemetryPacket packet1 =
                new TelemetryPacket(
                        shipmentId,
                        4.5,
                        31.0000,
                        77.2090,
                        LocalDateTime.of(
                                2026,
                                9,
                                9,
                                10,
                                0
                        )
                );

        auditor.processTelemetry(packet1);


        /*
         * Packet 2
         * 10:05 AM
         * Temperature breach starts
         */

        TelemetryPacket packet2 =
                new TelemetryPacket(
                        shipmentId,
                        9.2,
                        30.0000,
                        77.2090,
                        LocalDateTime.of(
                                2026,
                                9,
                                9,
                                10,
                                5
                        )
                );

        auditor.processTelemetry(packet2);


        /*
         * Packet 3
         * 10:15 AM
         * Breach continues
         */

        TelemetryPacket packet3 =
                new TelemetryPacket(
                        shipmentId,
                        10.5,
                        29.5000,
                        77.2090,
                        LocalDateTime.of(
                                2026,
                                9,
                                9,
                                10,
                                15
                        )
                );

        auditor.processTelemetry(packet3);


        /*
         * Packet 4
         * 10:25 AM
         * Continuous breach exceeds 15 minutes
         */

        TelemetryPacket packet4 =
                new TelemetryPacket(
                        shipmentId,
                        11.0,
                        28.9500,
                        77.2090,
                        LocalDateTime.of(
                                2026,
                                9,
                                9,
                                10,
                                25
                        )
                );

        auditor.processTelemetry(packet4);


        /*
         * Packet 5
         * 10:35 AM
         * Temperature returns to normal
         * Shipment remains COMPROMISED
         */

        TelemetryPacket packet5 =
                new TelemetryPacket(
                        shipmentId,
                        5.0,
                        28.8000,
                        77.2090,
                        LocalDateTime.of(
                                2026,
                                9,
                                9,
                                10,
                                35
                        )
                );

        auditor.processTelemetry(packet5);


        /*
         * Packet 6
         * 10:45 AM
         * Temperature remains normal
         */

        TelemetryPacket packet6 =
                new TelemetryPacket(
                        shipmentId,
                        4.8,
                        28.6600,
                        77.2090,
                        LocalDateTime.of(
                                2026,
                                9,
                                9,
                                10,
                                45
                        )
                );

        auditor.processTelemetry(packet6);


        /*
         * Final database verification
         */

        auditor.printFinalShipment(
                shipmentId
        );

        auditor.printTelemetryCount(
                shipmentId
        );

        auditor.printAuditLogs();


        System.out.println();

        System.out.println(
                "=========================================="
        );

        System.out.println(
                "           SIMULATION COMPLETED"
        );

        System.out.println(
                "=========================================="
        );
    }
}