package de.ario.druckauftrag.service;

import de.ario.druckauftrag.model.Kunde;
import de.ario.druckauftrag.repository.KundeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class KundeService {

    private final KundeRepository kundeRepository;

    // Spring übergibt das Repository automatisch (Constructor Injection)
    public KundeService(KundeRepository kundeRepository) {
        this.kundeRepository = kundeRepository;
    }

    public List<Kunde> alleKunden() {
        return kundeRepository.findAllByOrderByFirmaAsc();
    }

    public List<Kunde> sucheKunden(String suche) {
        if (suche == null || suche.isBlank()) {
            return alleKunden();
        }
        String text = suche.trim();
        return kundeRepository.findByFirmaContainingIgnoreCaseOrAnsprechpartnerContainingIgnoreCaseOrderByFirmaAsc(text, text);
    }

    public Kunde findeKunde(Long id) {
        // Wenn es die ID nicht gibt, zeigt Spring eine 404-Fehlerseite
        return kundeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Kunde mit der Nummer " + id + " wurde nicht gefunden."));
    }

    public Kunde speichern(Kunde kunde) {
        return kundeRepository.save(kunde);
    }

    public long anzahlKunden() {
        return kundeRepository.count();
    }
}
