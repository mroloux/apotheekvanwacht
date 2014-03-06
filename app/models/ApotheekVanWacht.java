package models;

import java.util.Date;

public class ApotheekVanWacht {

    public final String naam;
    public final String straatEnNummer;
    public final String stad;
    public final String tel;
    public final String url;
    public final Double afstand;
    public final Date eindDatum;

    public ApotheekVanWacht(String naam, String straatEnNummer, String stad, String tel, String url, Double afstand, Date eindDatum) {
        this.naam = naam;
        this.straatEnNummer = straatEnNummer;
        this.stad = stad;
        this.tel = tel;
        this.url = url;
        this.afstand = afstand;
        this.eindDatum = eindDatum;
    }
}
