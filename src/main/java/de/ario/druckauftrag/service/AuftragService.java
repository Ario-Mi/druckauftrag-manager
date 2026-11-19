package de.ario.druckauftrag.service;

import de.ario.druckauftrag.model.Auftrag;
import de.ario.druckauftrag.model.Kunde;
import de.ario.druckauftrag.model.Status;
import de.ario.druckauftrag.repository.AuftragRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuftragService {

    private final AuftragRepository auftragRepository;
    private final Preisrechner preisrechner;

    public AuftragService(AuftragRepository auftragRepository, Preisrechner preisrechner) {
        this.auftragRepository = auftragRepository;
        this.preisrechner = preisrechner;
    }

    // Filter nach Status (null = alle) und Suche nach Nummer, Produkt oder Kunde
    public List<Auftrag> sucheAuftraege(Status status, String suche) {
        List<Auftrag> auftraege;
        if (status == null) {
            auftraege = auftragRepository.findAllByOrderByLieferterminAsc();
        } else {
            auftraege = auftragRepository.findByStatusOrderByLieferterminAsc(status);
        }

        if (suche == null || suche.isBlank()) {
            return auftraege;
        }

        String text = suche.trim().toLowerCase();
        List<Auftrag> treffer = new ArrayList<>();
        for (Auftrag auftrag : auftraege) {
            boolean passt = auftrag.getNummer().toLowerCase().contains(text)
                    || auftrag.getProdukt().toLowerCase().contains(text)
                    || auftrag.getKunde().getFirma().toLowerCase().contains(text);
            if (passt) {
                treffer.add(auftrag);
            }
        }
        return treffer;
    }

    public List<Auftrag> auftraegeVonKunde(Kunde kunde) {
        return auftragRepository.findByKundeOrderByLieferterminDesc(kunde);
    }

    public Auftrag findeAuftrag(Long id) {
        return auftragRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Auftrag mit der Nummer " + id + " wurde nicht gefunden."));
    }

    public Auftrag anlegen(Auftrag auftrag) {
        auftrag.setId(null);
        auftrag.setStatus(Status.EINGEGANGEN);
        auftrag.setErstelltAm(LocalDate.now());
        berechnePreis(auftrag);
        return auftragRepository.save(auftrag);
    }

    // Nur die Felder aus dem Formular übernehmen, Status und Erstelldatum bleiben
    public Auftrag aktualisieren(Long id, Auftrag formularDaten) {
        Auftrag auftrag = findeAuftrag(id);
        auftrag.setKunde(formularDaten.getKunde());
        auftrag.setProdukt(formularDaten.getProdukt());
        auftrag.setAuflage(formularDaten.getAuflage());
        auftrag.setFormat(formularDaten.getFormat());
        auftrag.setPapiersorte(formularDaten.getPapiersorte());
        auftrag.setLiefertermin(formularDaten.getLiefertermin());
        auftrag.setNotiz(formularDaten.getNotiz());
        berechnePreis(auftrag);
        return auftragRepository.save(auftrag);
    }

    public Auftrag naechsterStatus(Long id) {
        Auftrag auftrag = findeAuftrag(id);
        auftrag.setStatus(auftrag.getStatus().naechsterStatus());
        return auftragRepository.save(auftrag);
    }

    // Zum Korrigieren, falls ein Status aus Versehen weitergeschaltet wurde
    public Auftrag statusSetzen(Long id, Status status) {
        Auftrag auftrag = findeAuftrag(id);
        auftrag.setStatus(status);
        return auftragRepository.save(auftrag);
    }

    private void berechnePreis(Auftrag auftrag) {
        auftrag.setPreis(preisrechner.berechnePreis(
                auftrag.getAuflage(), auftrag.getFormat(), auftrag.getPapiersorte()));
    }

    // ---- Zahlen für das Dashboard ----

    public long anzahlOffen() {
        return auftragRepository.countByStatusNot(Status.ERLEDIGT);
    }

    public long anzahlHeuteFaellig() {
        return auftragRepository.countByStatusNotAndLiefertermin(Status.ERLEDIGT, LocalDate.now());
    }

    // Von heute bis einschließlich Sonntag dieser Woche
    public long anzahlDieseWocheFaellig() {
        LocalDate heute = LocalDate.now();
        LocalDate sonntag = heute.with(DayOfWeek.SUNDAY);
        return auftragRepository.countByStatusNotAndLieferterminBetween(Status.ERLEDIGT, heute, sonntag);
    }

    public long anzahlUeberfaellig() {
        return auftragRepository.countByStatusNotAndLieferterminBefore(Status.ERLEDIGT, LocalDate.now());
    }

    // Offene Aufträge der nächsten Tage, überfällige stehen oben
    public List<Auftrag> baldFaellig(int tage) {
        LocalDate bis = LocalDate.now().plusDays(tage);
        return auftragRepository.findByStatusNotAndLieferterminLessThanEqualOrderByLieferterminAsc(Status.ERLEDIGT, bis);
    }

    public Map<Status, Long> anzahlProStatus() {
        // LinkedHashMap behält die Reihenfolge der Status bei
        Map<Status, Long> zaehler = new LinkedHashMap<>();
        for (Status status : Status.values()) {
            zaehler.put(status, auftragRepository.countByStatus(status));
        }
        return zaehler;
    }

    public long anzahlGesamt() {
        return auftragRepository.count();
    }
}
