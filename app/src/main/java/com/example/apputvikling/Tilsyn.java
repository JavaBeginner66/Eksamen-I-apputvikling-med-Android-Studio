package com.example.apputvikling;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * Klassen har ansvar for å representere hvert Tilsyn objekt som blir hentet ut fra datasettet.
 * Den sørger også for å gjøre om Json objekt til lister av dette objektet.
 *
 * Vi implementerer Serializable for å kunne lagre lister av denne objekt-typen
 * gjennom konfigurerings-forandringer som rotering av enhet.
 */

public class Tilsyn implements Serializable {

    // Deklarerer og initialiserer alle felt etter det de heter i datasettet. Alle felt må matche med json-properties.
    static final String OBJEKT_HEADER        = "entries";
    static final String OBJEKT_ID            = "tilsynid";
    static final String OBJEKT_NAVN          = "navn";
    static final String OBJEKT_ORGNR         = "orgnummer";
    static final String OBJEKT_ADRESSE       = "adrlinje1";
    static final String OBJEKT_POSTNR        = "postnr";
    static final String OBJEKT_POSTSTED      = "poststed";
    static final String OBJEKT_KARAKTER      = "total_karakter";

    // Deklarer alle objekt variabler.
    private String tilsynId;
    private String navn;
    private String orgNr;
    private String adresse;
    private String postNr;
    private String postSted;
    private String karakter;

    // Konstruktør for å ta imot JSonObjekt fra metoden lagTilsynListe, og sette felt.
    public Tilsyn(JSONObject jsonObject){
        this.tilsynId = jsonObject.optString(OBJEKT_ID);
        this.navn = jsonObject.optString(OBJEKT_NAVN);
        this.orgNr = jsonObject.optString(OBJEKT_ORGNR);
        this.adresse = jsonObject.optString(OBJEKT_ADRESSE);
        this.postNr = jsonObject.optString(OBJEKT_POSTNR);
        this.postSted = jsonObject.optString(OBJEKT_POSTSTED);
        this.karakter = jsonObject.optString(OBJEKT_KARAKTER);
    }

    /**
     * Metoden har ansvar for å ta inn en streng med JSON
     * og hente ut de felt som passer med klassens felt via konstruktør.
     * Dette blir gjort ved å først gjør om strengen til et JSON objekt, deretter
     * til et JSON array og gå gjennom de via en for loop.
     *
     * @param jsonTilsynString      Json-objekt strengen som inneholder dataen som skal gjøres om til objekt av denne klassen.
     * @return                      Returnerer en liste av dette objektet
     * @throws JSONException        Kan kastes av å bruke JSONObject og JSONArray
     * @throws NullPointerException Kan kastes om variabler ikke er initialisert
     */
    public static LinkedList<Tilsyn> lagTilsynListe(String jsonTilsynString)
            throws JSONException, NullPointerException {
        // Initialiserer listen som skal fylles og returneres etter loop.
        LinkedList<Tilsyn> tilsynListe = new LinkedList<>();
        // Gjør input parameter om til et json objekt.
        JSONObject jsonData  = new JSONObject(jsonTilsynString);
        // Gjøre json objektet om til et json array (som er et array av json objekt).
        JSONArray jsonTilsynTabell = jsonData.optJSONArray(OBJEKT_HEADER);
        // Gå gjennom alle objekt med en for loop.
        for(int i = 0; i < jsonTilsynTabell.length(); i++) {
            // Få tak i objektet som ligger i posisjon(i), og typetvinger det til et json objekt.
            // Dette går greit så lenge vi vet at datasettet har satt det opp i objekt->array->objekt
            JSONObject jsonTilsyn = (JSONObject) jsonTilsynTabell.get(i);
            // Kaller på konstruktøren og legger de inn i tabellen.
            tilsynListe.add(new Tilsyn(jsonTilsyn));
        }
        return tilsynListe;
    }

    // Get metoder
    public String getNavn() {
        return navn;
    }

    public String getOrgNr() {
        return orgNr;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getPostNr() {
        return postNr;
    }

    public String getPostSted() {
        return postSted;
    }

    public String getKarakter() {
        return karakter;
    }

    public String getTilsynId() {
        return tilsynId;
    }

/*
    ** KILDE / inspirasjon **
    * https://stackoverflow.com/questions/4432774/how-do-i-make-2-comparable-methods-in-only-one-class
    *
    * Metodene sorterer (dette) objektet på navn og poststed.
    * Jeg har brukt comparator med metode-implementasjon og ikke standard comparable siden jeg ville kunne sortere klassen på 2 felt.
 */
    static Comparator<Tilsyn> getNameComparator() {
        return (tilsyn, t1) -> tilsyn.navn.compareTo(t1.navn);
    }

    static Comparator<Tilsyn> getPoststedComparator() {
        return (tilsyn, t1) -> tilsyn.postSted.compareTo(t1.postSted);
    }
}
