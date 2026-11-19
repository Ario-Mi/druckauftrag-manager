package de.ario.druckauftrag.controller;

import de.ario.druckauftrag.service.AuftragService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Controller
public class DashboardController {

    private final AuftragService auftragService;

    public DashboardController(AuftragService auftragService) {
        this.auftragService = auftragService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        long anzahlHeute = auftragService.anzahlHeuteFaellig();
        long anzahlUeberfaellig = auftragService.anzahlUeberfaellig();

        // z. B. "Donnerstag, 19. November" – Locale.GERMAN sorgt für deutsche Wochentage
        DateTimeFormatter format = DateTimeFormatter.ofPattern("EEEE, d. MMMM", Locale.GERMAN);
        model.addAttribute("datumText", LocalDate.now().format(format));

        // Teile für den Satz oben auf der Seite
        if (anzahlHeute == 0) {
            model.addAttribute("heuteVerb", "geht");
            model.addAttribute("heuteText", "kein Auftrag");
        } else if (anzahlHeute == 1) {
            model.addAttribute("heuteVerb", "geht");
            model.addAttribute("heuteText", "ein Auftrag");
        } else {
            model.addAttribute("heuteVerb", "gehen");
            model.addAttribute("heuteText", anzahlHeute + " Aufträge");
        }
        if (anzahlUeberfaellig == 1) {
            model.addAttribute("ueberfaelligText", "einer");
        } else {
            model.addAttribute("ueberfaelligText", String.valueOf(anzahlUeberfaellig));
        }

        model.addAttribute("anzahlOffen", auftragService.anzahlOffen());
        model.addAttribute("anzahlHeute", anzahlHeute);
        model.addAttribute("anzahlWoche", auftragService.anzahlDieseWocheFaellig());
        model.addAttribute("anzahlUeberfaellig", anzahlUeberfaellig);
        model.addAttribute("baldFaellig", auftragService.baldFaellig(7));
        model.addAttribute("statusZaehler", auftragService.anzahlProStatus());
        return "dashboard";
    }
}
