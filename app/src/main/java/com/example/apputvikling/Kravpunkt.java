package com.example.apputvikling;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.LinkedList;
/**
 * (Klassen er nærmest identisk til Tilsyn uten comparable metodene, og jeg har valgt å bare kopiere-lime inn dokumentasjon)
 * Klassen har ansvar for å representere hvert Kravpunkt objekt som blir hentet ut fra datasettet.
 * Den sørger også for å gjøre om Json objekt til lister av dette objektet.
 *
 * Vi implementerer Serializable for å kunne lagre lister av denne objekt-typen
 * gjennom konfigurerings-forandringer som rotering av enhet.
 */

public class Kravpunkt implements Serializable {

    // Deklarerer og initialiserer alle felt etter det de heter i datasettet. Alle felt må matche med json-properties.
    static final String OBJEKT_HEADER           = "entries";
    static final String OBJEKT_NAVN             = "kravpunktnavn_no";
    static final String OBJEKT_DATO             = "dato";
    static final String OBJEKT_ORDNINGSVERDI    = "ordningsverdi";
    static final String OBJEKT_TEKST            = "tekst_no";
    static final String OBJEKT_KARAKTER         = "karakter";

    // Deklarer alle objekt variabler.
    private String kravpunktnavn;
    private String dato;
    private String ordningsverdi;
    private String tekst;
    private String karakter;


    // Konstruktør for å ta imot JSonObjekt fra metoden lagTilsynListe, og sette felt.
    public Kravpunkt(JSONObject jsonObject){
        this.kravpunktnavn = jsonObject.optString(OBJEKT_NAVN);
        this.dato = jsonObject.optString(OBJEKT_DATO);
        this.ordningsverdi = jsonObject.optString(OBJEKT_ORDNINGSVERDI);
        this.tekst = jsonObject.optString(OBJEKT_TEKST);
        this.karakter = jsonObject.optString(OBJEKT_KARAKTER);
    }

    /**
     * Metoden har ansvar for å ta inn en streng med JSON
     * og hente ut de felt som passer med klassens felt via konstruktør.
     * Dette blir gjort ved å først gjør om strengen til et JSON objekt, deretter
     * til et JSON array og gå gjennom de via en for loop.
     *
     * @param jsonKravpunktString   Json-objekt strengen som inneholder dataen som skal gjøres om til objekt av denne klassen.
     * @return                      Returnerer en liste av dette objektet
     * @throws JSONException        Kan kastes av å bruke JSONObject og JSONArray
     * @throws NullPointerException Kan kastes om variabler ikke er initialisert
     */
    public static LinkedList<Kravpunkt> lagKravpunktListe(String jsonKravpunktString)
            throws JSONException, NullPointerException {
        // Initialiserer listen som skal fylles og returneres etter loop.
        LinkedList<Kravpunkt> kravpunktListe = new LinkedList<>();
        // Gjør input parameter om til et json objekt.
        JSONObject jsonData  = new JSONObject(jsonKravpunktString);
        // Gjøre json objektet om til et json array (som er et array av json objekt).
        JSONArray jsonKravpunktTabell = jsonData.optJSONArray(OBJEKT_HEADER);
        // Gå gjennom alle objekt med en for loop.
        assert jsonKravpunktTabell != null;
        for(int i = 0; i < jsonKravpunktTabell.length(); i++) {
            // Få tak i objektet som ligger i posisjon(i), og typetvinger det til et json objekt.
            // Dette går greit så lenge vi vet at datasettet har satt det opp i objekt->array->objekt
            JSONObject jsonKravpunkt = (JSONObject) jsonKravpunktTabell.get(i);
            // Kaller på konstruktøren og legger de inn i tabellen.
            kravpunktListe.add(new Kravpunkt(jsonKravpunkt));
        }
        return kravpunktListe;
    }

    // Get metoder
    public String getNavn() {
        return kravpunktnavn;
    }

    public String getDato() {
        return dato;
    }

    public String getOrdningsverdi() {
        return ordningsverdi;
    }

    public String getTekst() {
        return tekst;
    }

    public String getKarakter() {
        return karakter;
    }
}
