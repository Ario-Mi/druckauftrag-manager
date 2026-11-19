package de.ario.druckauftrag.repository;

import de.ario.druckauftrag.model.Auftrag;
import de.ario.druckauftrag.model.Kunde;
import de.ario.druckauftrag.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AuftragRepository extends JpaRepository<Auftrag, Long> {

    List<Auftrag> findAllByOrderByLieferterminAsc();

    List<Auftrag> findByStatusOrderByLieferterminAsc(Status status);

    List<Auftrag> findByKundeOrderByLieferterminDesc(Kunde kunde);

    // Offene Aufträge (nicht ERLEDIGT) mit Liefertermin bis zu einem Datum
    List<Auftrag> findByStatusNotAndLieferterminLessThanEqualOrderByLieferterminAsc(Status status, LocalDate datum);

    long countByStatus(Status status);

    long countByStatusNot(Status status);

    long countByStatusNotAndLiefertermin(Status status, LocalDate datum);

    long countByStatusNotAndLieferterminBefore(Status status, LocalDate datum);

    long countByStatusNotAndLieferterminBetween(Status status, LocalDate von, LocalDate bis);
}
