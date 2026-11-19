package de.ario.druckauftrag.service;

import de.ario.druckauftrag.model.Format;
import de.ario.druckauftrag.model.Papiersorte;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/*
 * Berechnet den Preis eines Druckauftrags:
 * Preis = Grundpreis(Format) × Faktor(Papier) × Auflage, minus Mengenrabatt, plus Einrichtungskosten
 */
@Service
public class Preisrechner {

    // Einmalige Kosten für Druckvorbereitung und Maschine einrichten
    public static final BigDecimal EINRICHTUNGSKOSTEN = new BigDecimal("15.00");

    public BigDecimal berechnePreis(int auflage, Format format, Papiersorte papiersorte) {
        if (auflage < 1) {
            throw new IllegalArgumentException("Die Auflage muss mindestens 1 sein.");
        }
        if (format == null || papiersorte == null) {
            throw new IllegalArgumentException("Format und Papiersorte müssen angegeben werden.");
        }

        BigDecimal stueckpreis = format.getPreisProStueck().multiply(papiersorte.getFaktor());
        BigDecimal druckkosten = stueckpreis.multiply(BigDecimal.valueOf(auflage));

        // Rabatt abziehen: z. B. 10 % Rabatt -> mal 0.90
        BigDecimal rabatt = ermittleRabatt(auflage);
        druckkosten = druckkosten.multiply(BigDecimal.ONE.subtract(rabatt));

        BigDecimal gesamt = druckkosten.add(EINRICHTUNGSKOSTEN);
        return gesamt.setScale(2, RoundingMode.HALF_UP);
    }

    // Mengenrabatt als Anteil: 0.10 = 10 %
    public BigDecimal ermittleRabatt(int auflage) {
        if (auflage >= 1000) {
            return new BigDecimal("0.20");
        } else if (auflage >= 500) {
            return new BigDecimal("0.10");
        } else {
            return BigDecimal.ZERO;
        }
    }
}
