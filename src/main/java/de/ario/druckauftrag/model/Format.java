package de.ario.druckauftrag.model;

import java.math.BigDecimal;

public enum Format {

    // Grundpreis pro Stück in Euro (Normalpapier, ohne Rabatt)
    A6("A6 (105 × 148 mm)", new BigDecimal("0.08")),
    A5("A5 (148 × 210 mm)", new BigDecimal("0.12")),
    A4("A4 (210 × 297 mm)", new BigDecimal("0.20")),
    A3("A3 (297 × 420 mm)", new BigDecimal("0.40"));

    private final String bezeichnung;
    private final BigDecimal preisProStueck;

    Format(String bezeichnung, BigDecimal preisProStueck) {
        this.bezeichnung = bezeichnung;
        this.preisProStueck = preisProStueck;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public BigDecimal getPreisProStueck() {
        return preisProStueck;
    }
}
