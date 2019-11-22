package com.example.apputvikling;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.LinkedList;

public class Kravpunkt implements Serializable {

    static final String OBJEKT_HEADER           = "entries";
    static final String OBJEKT_NAVN             = "kravpunktnavn_no";
    static final String OBJEKT_DATO             = "dato";
    static final String OBJEKT_ORDNINGSVERDI    = "ordningsverdi";
    static final String OBJEKT_TEKST            = "tekst_no";
    static final String OBJEKT_KARAKTER         = "karakter";

    private String kravpunktnavn;
    private String dato;
    private String ordningsverdi;
    private String tekst;
    private String karakter;


    // Konstruktør for å ta imot JSonObjekt fra metoden lagKravpunktListe, og sette felt.
    public Kravpunkt(JSONObject jsonObject){
        this.kravpunktnavn = jsonObject.optString(OBJEKT_NAVN);
        this.dato = jsonObject.optString(OBJEKT_DATO);
        this.ordningsverdi = jsonObject.optString(OBJEKT_ORDNINGSVERDI);
        this.tekst = jsonObject.optString(OBJEKT_TEKST);
        this.karakter = jsonObject.optString(OBJEKT_KARAKTER);
    }

    public static LinkedList<Kravpunkt> lagKravpunktListe(String jsonKravpunktString)
            throws JSONException, NullPointerException {
        LinkedList<Kravpunkt> kravpunktListe = new LinkedList<>();
        JSONObject jsonData  = new JSONObject(jsonKravpunktString);
        JSONArray jsonKravpunktTabell = jsonData.optJSONArray(OBJEKT_HEADER);

        for(int i = 0; i < jsonKravpunktTabell.length(); i++) {
            JSONObject jsonKravpunkt = (JSONObject) jsonKravpunktTabell.get(i);
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
