package de.ario.druckauftrag.service;

import de.ario.druckauftrag.model.Auftrag;
import de.ario.druckauftrag.model.Format;
import de.ario.druckauftrag.model.Kunde;
import de.ario.druckauftrag.model.Papiersorte;
import de.ario.druckauftrag.model.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Startet die ganze Anwendung mit der Test-Datenbank im Arbeitsspeicher
@SpringBootTest
class AuftragServiceTest {

    @Autowired
    private AuftragService auftragService;

    @Autowired
    private KundeService kundeService;

    private Auftrag neuerTestauftrag(String firma) {
        Kunde kunde = kundeService.speichern(new Kunde(firma, "Max Muster", "max@test.example", "069 123"));
        Auftrag auftrag = new Auftrag(kunde, "Flyer", 100, Papiersorte.NORMAL_90G, Format.A5,
                LocalDate.now().plusDays(3));
        return auftragService.anlegen(auftrag);
    }

    @Test
    void neuerAuftragBekommtNummerPreisUndStatus() {
        Auftrag auftrag = neuerTestauftrag("Testfirma Preis");

        assertNotNull(auftrag.getId());
        assertEquals(Status.EINGEGANGEN, auftrag.getStatus());
        assertEquals(new BigDecimal("27.00"), auftrag.getPreis());
    }

    @Test
    void statusWirdWeitergeschaltet() {
        Auftrag auftrag = neuerTestauftrag("Testfirma Status");

        auftragService.naechsterStatus(auftrag.getId());

        Auftrag geladen = auftragService.findeAuftrag(auftrag.getId());
        assertEquals(Status.IN_GESTALTUNG, geladen.getStatus());
    }

    @Test
    void sucheFindetAuftragUeberDenKundennamen() {
        neuerTestauftrag("Druckhaus Zeppelin Test");

        List<Auftrag> treffer = auftragService.sucheAuftraege(null, "zeppelin");

        assertFalse(treffer.isEmpty());
        for (Auftrag a : treffer) {
            assertTrue(a.getKunde().getFirma().contains("Zeppelin"));
        }
    }

    @Test
    void erledigterAuftragIstNichtUeberfaellig() {
        Auftrag auftrag = neuerTestauftrag("Testfirma Erledigt");
        auftrag.setLiefertermin(LocalDate.now().minusDays(5));
        auftrag.setStatus(Status.ERLEDIGT);

        assertFalse(auftrag.isUeberfaellig());
    }
}
