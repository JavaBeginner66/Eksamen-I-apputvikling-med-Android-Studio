package com.example.apputvikling;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class Tilsyn {

    static final String OBJEKT_NAVN          = "navn";
    static final String OBJEKT_ORGNR         = "orgnummer";
    static final String OBJEKT_ADRESSE       = "adrlinje1";
    static final String OBJEKT_POSTNR        = "postnr";
    static final String OBJEKT_POSTSTED      = "poststed";

    private String navn;
    private String orgNr;
    private String adresse;
    private String postNr;
    private String postSted;

    public Tilsyn(String navn, String orgNr, String adresse, String postNr, String postSted) {
        this.navn = navn;
        this.orgNr = orgNr;
        this.adresse = adresse;
        this.postNr = postNr;
        this.postSted = postSted;
    }

    // Konstruktør for å ta imot JSonObjekt fra metoden lagTilsynListe, og sette felt.
    public Tilsyn(JSONObject jsonObject){
        this.navn = jsonObject.optString(OBJEKT_NAVN);
        this.orgNr = jsonObject.optString(OBJEKT_ORGNR);
        this.adresse = jsonObject.optString(OBJEKT_ADRESSE);
        this.postNr = jsonObject.optString(OBJEKT_POSTNR);
        this.postSted = jsonObject.optString(OBJEKT_POSTSTED);
    }

    public static LinkedList<Tilsyn> lagTilsynListe(String jsonTilsynString)
            throws JSONException, NullPointerException {
        LinkedList<Tilsyn> tilsynListe = new LinkedList<>();
        JSONObject jsonData  = new JSONObject(jsonTilsynString);
        JSONArray jsonTilsynTabell = jsonData.optJSONArray("entries");
        for(int i = 0; i < jsonTilsynTabell.length(); i++) {

            JSONObject jsonTilsyn = (JSONObject) jsonTilsynTabell.get(i);

            Tilsyn tilsyn = new Tilsyn(jsonTilsyn);
            tilsynListe.add(tilsyn);


           // Log.d("yo", jsonTilsyn.toString());

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
}
