package de.ario.druckauftrag.model;

public enum Status {

    EINGEGANGEN("Eingegangen"),
    IN_GESTALTUNG("In Gestaltung"),
    IM_DRUCK("Im Druck"),
    ABHOLBEREIT("Abholbereit"),
    ERLEDIGT("Erledigt");

    // Text für die Anzeige in der Oberfläche
    private final String bezeichnung;

    Status(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    // CSS-Klasse für das farbige Label, z. B. "status-im-druck"
    public String getCssKlasse() {
        return "status-" + name().toLowerCase().replace('_', '-');
    }

    // Gibt den nächsten Schritt im Ablauf zurück. ERLEDIGT bleibt ERLEDIGT.
    public Status naechsterStatus() {
        switch (this) {
            case EINGEGANGEN:
                return IN_GESTALTUNG;
            case IN_GESTALTUNG:
                return IM_DRUCK;
            case IM_DRUCK:
                return ABHOLBEREIT;
            default:
                return ERLEDIGT;
        }
    }
}
