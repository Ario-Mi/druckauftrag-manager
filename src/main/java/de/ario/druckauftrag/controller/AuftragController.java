package de.ario.druckauftrag.controller;

import de.ario.druckauftrag.model.Auftrag;
import de.ario.druckauftrag.model.Format;
import de.ario.druckauftrag.model.Papiersorte;
import de.ario.druckauftrag.model.Status;
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

import java.time.LocalDate;

@Controller
@RequestMapping("/auftraege")
public class AuftragController {

    private final AuftragService auftragService;
    private final KundeService kundeService;

    public AuftragController(AuftragService auftragService, KundeService kundeService) {
        this.auftragService = auftragService;
        this.kundeService = kundeService;
    }

    @GetMapping
    public String liste(@RequestParam(name = "status", required = false) Status status,
                        @RequestParam(name = "suche", required = false) String suche,
                        Model model) {
        model.addAttribute("auftraege", auftragService.sucheAuftraege(status, suche));
        model.addAttribute("statusZaehler", auftragService.anzahlProStatus());
        model.addAttribute("anzahlGesamt", auftragService.anzahlGesamt());
        model.addAttribute("gewaehlterStatus", status);
        model.addAttribute("suche", suche);
        return "auftraege/liste";
    }

    @GetMapping("/neu")
    public String neu(@RequestParam(name = "kundeId", required = false) Long kundeId, Model model) {
        Auftrag auftrag = new Auftrag();
        auftrag.setLiefertermin(LocalDate.now().plusDays(7));
        model.addAttribute("auftrag", auftrag);
        formularDatenHinzufuegen(model, kundeId);
        return "auftraege/formular";
    }

    @GetMapping("/{id}")
    public String anzeigen(@PathVariable("id") Long id, Model model) {
        model.addAttribute("auftrag", auftragService.findeAuftrag(id));
        model.addAttribute("alleStatus", Status.values());
        return "auftraege/detail";
    }

    @GetMapping("/{id}/bearbeiten")
    public String bearbeiten(@PathVariable("id") Long id, Model model) {
        Auftrag auftrag = auftragService.findeAuftrag(id);
        model.addAttribute("auftrag", auftrag);
        formularDatenHinzufuegen(model, auftrag.getKunde().getId());
        return "auftraege/formular";
    }

    // Der Kunde kommt als eigene ID aus der Auswahlliste (kundeId)
    @PostMapping("/speichern")
    public String speichern(@Valid @ModelAttribute("auftrag") Auftrag auftrag, BindingResult ergebnis,
                            @RequestParam(name = "kundeId", required = false) Long kundeId,
                            Model model, RedirectAttributes redirect) {
        if (kundeId == null) {
            ergebnis.rejectValue("kunde", "fehlt", "Bitte einen Kunden auswählen.");
        }
        if (ergebnis.hasErrors()) {
            formularDatenHinzufuegen(model, kundeId);
            return "auftraege/formular";
        }

        auftrag.setKunde(kundeService.findeKunde(kundeId));

        Auftrag gespeichert;
        if (auftrag.getId() == null) {
            gespeichert = auftragService.anlegen(auftrag);
        } else {
            gespeichert = auftragService.aktualisieren(auftrag.getId(), auftrag);
        }
        redirect.addFlashAttribute("meldung", "Auftrag " + gespeichert.getNummer() + " wurde gespeichert.");
        return "redirect:/auftraege/" + gespeichert.getId();
    }

    @PostMapping("/{id}/weiter")
    public String statusWeiter(@PathVariable("id") Long id, RedirectAttributes redirect) {
        Auftrag auftrag = auftragService.naechsterStatus(id);
        redirect.addFlashAttribute("meldung", "Status geändert: " + auftrag.getStatus().getBezeichnung());
        return "redirect:/auftraege/" + id;
    }

    @PostMapping("/{id}/status")
    public String statusSetzen(@PathVariable("id") Long id, @RequestParam("status") Status status, RedirectAttributes redirect) {
        auftragService.statusSetzen(id, status);
        redirect.addFlashAttribute("meldung", "Status korrigiert: " + status.getBezeichnung());
        return "redirect:/auftraege/" + id;
    }

    // Daten für die Auswahllisten im Formular
    private void formularDatenHinzufuegen(Model model, Long kundeId) {
        model.addAttribute("kunden", kundeService.alleKunden());
        model.addAttribute("gewaehlteKundeId", kundeId);
        model.addAttribute("formate", Format.values());
        model.addAttribute("papiersorten", Papiersorte.values());
    }
}
