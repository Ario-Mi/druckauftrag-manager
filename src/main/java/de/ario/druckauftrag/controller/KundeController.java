package de.ario.druckauftrag.controller;

import de.ario.druckauftrag.model.Kunde;
import de.ario.druckauftrag.service.AuftragService;
import de.ario.druckauftrag.service.KundeService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/kunden")
public class KundeController {

    private final KundeService kundeService;
    private final AuftragService auftragService;

    public KundeController(KundeService kundeService, AuftragService auftragService) {
        this.kundeService = kundeService;
        this.auftragService = auftragService;
    }

    @GetMapping
    public String liste(@RequestParam(name = "suche", required = false) String suche, Model model) {
        model.addAttribute("kunden", kundeService.sucheKunden(suche));
        model.addAttribute("suche", suche);
        return "kunden/liste";
    }

    @GetMapping("/neu")
    public String neu(Model model) {
        model.addAttribute("kunde", new Kunde());
        return "kunden/formular";
    }

    @GetMapping("/{id}")
    public String anzeigen(@PathVariable("id") Long id, Model model) {
        Kunde kunde = kundeService.findeKunde(id);
        model.addAttribute("kunde", kunde);
        model.addAttribute("auftraege", auftragService.auftraegeVonKunde(kunde));
        return "kunden/detail";
    }

    @GetMapping("/{id}/bearbeiten")
    public String bearbeiten(@PathVariable("id") Long id, Model model) {
        model.addAttribute("kunde", kundeService.findeKunde(id));
        return "kunden/formular";
    }

    // Ein Formular für Neu und Bearbeiten: ist die ID leer, wird ein neuer Kunde angelegt
    @PostMapping("/speichern")
    public String speichern(@Valid @ModelAttribute("kunde") Kunde kunde, BindingResult ergebnis,
                            RedirectAttributes redirect) {
        if (ergebnis.hasErrors()) {
            return "kunden/formular";
        }
        Kunde gespeichert = kundeService.speichern(kunde);
        redirect.addFlashAttribute("meldung", "Kunde „" + gespeichert.getFirma() + "“ wurde gespeichert.");
        return "redirect:/kunden/" + gespeichert.getId();
    }
}
