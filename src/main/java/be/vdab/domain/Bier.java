package be.vdab.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Bier {
    private final long id;
    private final String naam;
    private final BigDecimal alcohol;
    private final LocalDate sinds;

    public Bier(long id, String naam, BigDecimal alcohol, LocalDate sinds) {
        this.id = id;
        this.naam = naam;
        this.alcohol = alcohol;
        this.sinds = sinds;
    }
}
