package de.ario.druckauftrag.model;

import java.math.BigDecimal;

public enum Papiersorte {

    // Aufschlag als Faktor: 1.0 = kein Aufschlag, 1.5 = 50 % teurer
    NORMAL_90G("Normalpapier 90 g/m²", new BigDecimal("1.0")),
    BILDERDRUCK_135G("Bilderdruck matt 135 g/m²", new BigDecimal("1.2")),
    BILDERDRUCK_250G("Bilderdruck glänzend 250 g/m²", new BigDecimal("1.5")),
    KARTON_350G("Karton 350 g/m²", new BigDecimal("1.8"));

    private final String bezeichnung;
    private final BigDecimal faktor;

    Papiersorte(String bezeichnung, BigDecimal faktor) {
        this.bezeichnung = bezeichnung;
        this.faktor = faktor;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public BigDecimal getFaktor() {
        return faktor;
    }
}
