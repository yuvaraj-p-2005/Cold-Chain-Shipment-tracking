package com.coldchain;



import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AuditBreachLog {

    private int shipmentId;
    private LocalDateTime breachStart;
    private LocalDateTime breachEnd;
    private int durationMinutes;
    private BigDecimal peakTemp;
    private BigDecimal totalPenalty;

    public AuditBreachLog(
            int shipmentId,
            LocalDateTime breachStart,
            LocalDateTime breachEnd,
            int durationMinutes,
            BigDecimal peakTemp,
            BigDecimal totalPenalty) {

        this.shipmentId = shipmentId;
        this.breachStart = breachStart;
        this.breachEnd = breachEnd;
        this.durationMinutes = durationMinutes;
        this.peakTemp = peakTemp;
        this.totalPenalty = totalPenalty;
    }

    public int getShipmentId() {
        return shipmentId;
    }

    public LocalDateTime getBreachStart() {
        return breachStart;
    }

    public LocalDateTime getBreachEnd() {
        return breachEnd;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public BigDecimal getPeakTemp() {
        return peakTemp;
    }

    public BigDecimal getTotalPenalty() {
        return totalPenalty;
    }
}
