package de.ario.druckauftrag.service;

import de.ario.druckauftrag.model.Auftrag;
import de.ario.druckauftrag.model.Format;
import de.ario.druckauftrag.model.Kunde;
import de.ario.druckauftrag.model.Papiersorte;
import de.ario.druckauftrag.model.Status;
import de.ario.druckauftrag.repository.AuftragRepository;
import de.ario.druckauftrag.repository.KundeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/*
 * Legt beim ersten Start ein paar Beispielkunden und -aufträge an.
 * CommandLineRunner: Spring ruft run() einmal auf, nachdem die Anwendung gestartet ist.
 */
@Component
public class Beispieldaten implements CommandLineRunner {

    private final KundeRepository kundeRepository;
    private final AuftragRepository auftragRepository;
    private final AuftragService auftragService;

    public Beispieldaten(KundeRepository kundeRepository, AuftragRepository auftragRepository,
                         AuftragService auftragService) {
        this.kundeRepository = kundeRepository;
        this.auftragRepository = auftragRepository;
        this.auftragService = auftragService;
    }

    @Override
    public void run(String... args) {
        // Nur wenn die Datenbank noch leer ist
        if (kundeRepository.count() > 0) {
            return;
        }

        Kunde baeckerei = kundeRepository.save(new Kunde("Bäckerei Sonnenschein", "Petra Krüger",
                "info@baeckerei-sonnenschein.example", "069 4711 20"));
        Kunde kanzlei = kundeRepository.save(new Kunde("Kanzlei Weber & Partner", "Dr. Thomas Weber",
                "weber@kanzlei-weber.example", "069 5566 100"));
        Kunde turnverein = kundeRepository.save(new Kunde("Turnverein Bornheim", "Jana Schulz",
                "vorstand@tv-bornheim.example", "0151 2345 678"));
        Kunde cafe = kundeRepository.save(new Kunde("Café Mainblick", "Lena Hoffmann",
                "hallo@cafe-mainblick.example", "069 9988 77"));
        Kunde fahrschule = kundeRepository.save(new Kunde("Fahrschule Schneider", "Markus Schneider",
                "kontakt@fahrschule-schneider.example", "069 3344 55"));

        // tage = Liefertermin relativ zu heute (negativ = in der Vergangenheit)
        anlegen(baeckerei, "Flyer Wochenangebot", 1000, Format.A5, Papiersorte.BILDERDRUCK_135G, 2, Status.IM_DRUCK,
                "Beidseitig 4/4-farbig, Druckdaten kommen per E-Mail.");
        anlegen(baeckerei, "Postkarten", 250, Format.A6, Papiersorte.KARTON_350G, 10, Status.EINGEGANGEN, null);
        anlegen(kanzlei, "Briefpapier", 2000, Format.A4, Papiersorte.NORMAL_90G, 5, Status.IN_GESTALTUNG,
                "Logo neu setzen, Korrekturabzug an Dr. Weber.");
        anlegen(kanzlei, "Imagebroschüre", 150, Format.A4, Papiersorte.BILDERDRUCK_250G, -1, Status.IM_DRUCK, null);
        anlegen(turnverein, "Plakat Sommerfest", 50, Format.A3, Papiersorte.BILDERDRUCK_250G, 0, Status.ABHOLBEREIT,
                "Abholung durch Frau Schulz.");
        anlegen(turnverein, "Flyer Kursprogramm", 500, Format.A6, Papiersorte.NORMAL_90G, 3, Status.EINGEGANGEN, null);
        anlegen(cafe, "Speisekarten", 40, Format.A4, Papiersorte.KARTON_350G, 1, Status.IN_GESTALTUNG, null);
        anlegen(cafe, "Flyer Eröffnung", 300, Format.A5, Papiersorte.BILDERDRUCK_135G, -6, Status.ERLEDIGT, null);
        anlegen(fahrschule, "Plakat Ferienkurs", 20, Format.A3, Papiersorte.NORMAL_90G, 14, Status.EINGEGANGEN, null);
        anlegen(fahrschule, "Flyer Führerschein", 1000, Format.A6, Papiersorte.BILDERDRUCK_135G, -12, Status.ERLEDIGT, null);
    }

    private void anlegen(Kunde kunde, String produkt, int auflage, Format format, Papiersorte papiersorte,
                         int tage, Status status, String notiz) {
        Auftrag auftrag = new Auftrag(kunde, produkt, auflage, papiersorte, format, LocalDate.now().plusDays(tage));
        auftrag.setNotiz(notiz);
        // anlegen() berechnet den Preis und setzt den Status auf EINGEGANGEN
        auftrag = auftragService.anlegen(auftrag);
        auftrag.setStatus(status);
        auftragRepository.save(auftrag);
    }
}
