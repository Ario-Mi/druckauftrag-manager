package de.ario.druckauftrag.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Kunde {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Bitte Firma oder Namen eingeben.")
    @Size(max = 100, message = "Maximal 100 Zeichen.")
    private String firma;

    @Size(max = 100, message = "Maximal 100 Zeichen.")
    private String ansprechpartner;

    @Email(message = "Bitte eine gültige E-Mail-Adresse eingeben.")
    @Size(max = 100, message = "Maximal 100 Zeichen.")
    private String email;

    @Size(max = 30, message = "Maximal 30 Zeichen.")
    private String telefon;

    // JPA braucht einen leeren Konstruktor
    public Kunde() {
    }

    public Kunde(String firma, String ansprechpartner, String email, String telefon) {
        this.firma = firma;
        this.ansprechpartner = ansprechpartner;
        this.email = email;
        this.telefon = telefon;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirma() {
        return firma;
    }

    public void setFirma(String firma) {
        this.firma = firma;
    }

    public String getAnsprechpartner() {
        return ansprechpartner;
    }

    public void setAnsprechpartner(String ansprechpartner) {
        this.ansprechpartner = ansprechpartner;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }
}
