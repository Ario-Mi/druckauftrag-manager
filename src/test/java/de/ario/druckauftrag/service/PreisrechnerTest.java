package de.ario.druckauftrag.service;

import de.ario.druckauftrag.model.Format;
import de.ario.druckauftrag.model.Papiersorte;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PreisrechnerTest {

    private final Preisrechner preisrechner = new Preisrechner();

    @Test
    void kleineAuflageOhneRabatt() {
        // 100 × 0.12 € × 1.0 = 12.00 € + 15.00 € Einrichtung
        BigDecimal preis = preisrechner.berechnePreis(100, Format.A5, Papiersorte.NORMAL_90G);
        assertEquals(new BigDecimal("27.00"), preis);
    }

    @Test
    void auflageAb500Bekommt10ProzentRabatt() {
        // 500 × 0.20 € × 1.2 = 120.00 €, minus 10 % = 108.00 € + 15.00 €
        BigDecimal preis = preisrechner.berechnePreis(500, Format.A4, Papiersorte.BILDERDRUCK_135G);
        assertEquals(new BigDecimal("123.00"), preis);
    }

    @Test
    void auflageAb1000Bekommt20ProzentRabatt() {
        // 1000 × 0.08 € × 1.8 = 144.00 €, minus 20 % = 115.20 € + 15.00 €
        BigDecimal preis = preisrechner.berechnePreis(1000, Format.A6, Papiersorte.KARTON_350G);
        assertEquals(new BigDecimal("130.20"), preis);
    }

    @Test
    void rabattGrenzeBei500() {
        assertEquals(BigDecimal.ZERO, preisrechner.ermittleRabatt(499));
        assertEquals(new BigDecimal("0.10"), preisrechner.ermittleRabatt(500));
    }

    @Test
    void einzelstueckKostetMindestensDieEinrichtung() {
        // 1 × 0.40 € × 1.5 = 0.60 € + 15.00 €
        BigDecimal preis = preisrechner.berechnePreis(1, Format.A3, Papiersorte.BILDERDRUCK_250G);
        assertEquals(new BigDecimal("15.60"), preis);
    }

    @Test
    void auflageNullIstNichtErlaubt() {
        assertThrows(IllegalArgumentException.class,
                () -> preisrechner.berechnePreis(0, Format.A4, Papiersorte.NORMAL_90G));
    }

    @Test
    void ohneFormatGibtEsEinenFehler() {
        assertThrows(IllegalArgumentException.class,
                () -> preisrechner.berechnePreis(100, null, Papiersorte.NORMAL_90G));
    }
}
