package de.ario.druckauftrag.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
public class Auftrag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Viele Aufträge gehören zu einem Kunden
    @ManyToOne
    @JoinColumn(name = "kunde_id", nullable = false)
    private Kunde kunde;

    @NotBlank(message = "Bitte ein Produkt eingeben.")
    @Size(max = 100, message = "Maximal 100 Zeichen.")
    private String produkt;          // z. B. "Flyer", "Plakat", "Broschüre"

    @NotNull(message = "Bitte eine Auflage eingeben.")
    @Min(value = 1, message = "Die Auflage muss mindestens 1 sein.")
    @Max(value = 100000, message = "Die Auflage darf höchstens 100.000 sein.")
    private Integer auflage;

    // Enums als Text speichern, nicht als Zahl (siehe README)
    @NotNull(message = "Bitte eine Papiersorte wählen.")
    @Enumerated(EnumType.STRING)
    private Papiersorte papiersorte;

    @NotNull(message = "Bitte ein Format wählen.")
    @Enumerated(EnumType.STRING)
    private Format format;

    @NotNull(message = "Bitte einen Liefertermin angeben.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate liefertermin;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(precision = 10, scale = 2)
    private BigDecimal preis;

    private LocalDate erstelltAm;

    @Size(max = 1000, message = "Maximal 1000 Zeichen.")
    @Column(length = 1000)
    private String notiz;

    public Auftrag() {
        // Jeder neue Auftrag startet als EINGEGANGEN
        this.status = Status.EINGEGANGEN;
        this.erstelltAm = LocalDate.now();
    }

    public Auftrag(Kunde kunde, String produkt, Integer auflage, Papiersorte papiersorte,
                   Format format, LocalDate liefertermin) {
        this();
        this.kunde = kunde;
        this.produkt = produkt;
        this.auflage = auflage;
        this.papiersorte = papiersorte;
        this.format = format;
        this.liefertermin = liefertermin;
    }

    // Auftragsnummer für die Anzeige, z. B. "A-0007"
    public String getNummer() {
        if (id == null) {
            return "neu";
        }
        return String.format("A-%04d", id);
    }

    public boolean isUeberfaellig() {
        return status != Status.ERLEDIGT
                && liefertermin != null
                && liefertermin.isBefore(LocalDate.now());
    }

    // Kurzer Text für die Tabellen: "heute", "morgen", "in 3 Tagen" ...
    public String getFaelligkeitText() {
        if (liefertermin == null) {
            return "";
        }
        long tage = ChronoUnit.DAYS.between(LocalDate.now(), liefertermin);
        if (tage < -1) {
            return (-tage) + " Tage überfällig";
        } else if (tage == -1) {
            return "1 Tag überfällig";
        } else if (tage == 0) {
            return "heute";
        } else if (tage == 1) {
            return "morgen";
        } else {
            return "in " + tage + " Tagen";
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Kunde getKunde() {
        return kunde;
    }

    public void setKunde(Kunde kunde) {
        this.kunde = kunde;
    }

    public String getProdukt() {
        return produkt;
    }

    public void setProdukt(String produkt) {
        this.produkt = produkt;
    }

    public Integer getAuflage() {
        return auflage;
    }

    public void setAuflage(Integer auflage) {
        this.auflage = auflage;
    }

    public Papiersorte getPapiersorte() {
        return papiersorte;
    }

    public void setPapiersorte(Papiersorte papiersorte) {
        this.papiersorte = papiersorte;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public LocalDate getLiefertermin() {
        return liefertermin;
    }

    public void setLiefertermin(LocalDate liefertermin) {
        this.liefertermin = liefertermin;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public BigDecimal getPreis() {
        return preis;
    }

    public void setPreis(BigDecimal preis) {
        this.preis = preis;
    }

    public LocalDate getErstelltAm() {
        return erstelltAm;
    }

    public void setErstelltAm(LocalDate erstelltAm) {
        this.erstelltAm = erstelltAm;
    }

    public String getNotiz() {
        return notiz;
    }

    public void setNotiz(String notiz) {
        this.notiz = notiz;
    }
}
