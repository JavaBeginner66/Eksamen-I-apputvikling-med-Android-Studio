package com.example.apputvikling;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class Tilsyn {

    static final String OBJEKT_HEADER        = "entries";
    static final String OBJEKT_ID            = "tilsynid";
    static final String OBJEKT_NAVN          = "navn";
    static final String OBJEKT_ORGNR         = "orgnummer";
    static final String OBJEKT_ADRESSE       = "adrlinje1";
    static final String OBJEKT_POSTNR        = "postnr";
    static final String OBJEKT_POSTSTED      = "poststed";
    static final String OBJEKT_KARAKTER      = "total_karakter";

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

    public static LinkedList<Tilsyn> lagTilsynListe(String jsonTilsynString)
            throws JSONException, NullPointerException {
        LinkedList<Tilsyn> tilsynListe = new LinkedList<>();
        JSONObject jsonData  = new JSONObject(jsonTilsynString);
        JSONArray jsonTilsynTabell = jsonData.optJSONArray(OBJEKT_HEADER);

        for(int i = 0; i < jsonTilsynTabell.length(); i++) {
            JSONObject jsonTilsyn = (JSONObject) jsonTilsynTabell.get(i);
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
}
