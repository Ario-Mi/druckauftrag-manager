package de.ario.druckauftrag.repository;

import de.ario.druckauftrag.model.Kunde;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Spring Data erzeugt die SQL-Abfragen automatisch aus den Methodennamen
public interface KundeRepository extends JpaRepository<Kunde, Long> {

    List<Kunde> findAllByOrderByFirmaAsc();

    List<Kunde> findByFirmaContainingIgnoreCaseOrAnsprechpartnerContainingIgnoreCaseOrderByFirmaAsc(
            String firma, String ansprechpartner);
}
