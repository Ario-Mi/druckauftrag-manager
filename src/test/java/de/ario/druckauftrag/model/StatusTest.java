package de.ario.druckauftrag.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatusTest {

    @Test
    void statusLaeuftInDerRichtigenReihenfolge() {
        assertEquals(Status.IN_GESTALTUNG, Status.EINGEGANGEN.naechsterStatus());
        assertEquals(Status.IM_DRUCK, Status.IN_GESTALTUNG.naechsterStatus());
        assertEquals(Status.ABHOLBEREIT, Status.IM_DRUCK.naechsterStatus());
        assertEquals(Status.ERLEDIGT, Status.ABHOLBEREIT.naechsterStatus());
    }

    @Test
    void erledigtBleibtErledigt() {
        assertEquals(Status.ERLEDIGT, Status.ERLEDIGT.naechsterStatus());
    }

    @Test
    void cssKlasseWirdAusDemNamenGebildet() {
        assertEquals("status-im-druck", Status.IM_DRUCK.getCssKlasse());
    }
}
