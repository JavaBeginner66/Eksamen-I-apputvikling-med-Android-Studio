package com.example.apputvikling;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class Tilsyn {

    static final String OBJEKT_HEADER        = "entries";
    static final String OBJEKT_NAVN          = "navn";
    static final String OBJEKT_ORGNR         = "orgnummer";
    static final String OBJEKT_ADRESSE       = "adrlinje1";
    static final String OBJEKT_POSTNR        = "postnr";
    static final String OBJEKT_POSTSTED      = "poststed";
    static final String OBJEKT_KARAKTER      = "total_karakter";

    private String navn;
    private String orgNr;
    private String adresse;
    private String postNr;
    private String postSted;
    private String karakter;

    public static int staticCount;

    // Konstruktør for å ta imot JSonObjekt fra metoden lagTilsynListe, og sette felt.
    public Tilsyn(JSONObject jsonObject){
        this.navn = jsonObject.optString(OBJEKT_NAVN);
        this.orgNr = jsonObject.optString(OBJEKT_ORGNR);
        this.adresse = jsonObject.optString(OBJEKT_ADRESSE);
        this.postNr = jsonObject.optString(OBJEKT_POSTNR);
        this.postSted = jsonObject.optString(OBJEKT_POSTSTED);
        this.karakter = jsonObject.optString(OBJEKT_KARAKTER);
        staticCount++;
        Log.d("count", "Number of objects: " + staticCount);
    }

    public static LinkedList<Tilsyn> lagTilsynListe(String jsonTilsynString, String årsFilter)
            throws JSONException, NullPointerException {
        LinkedList<Tilsyn> tilsynListe = new LinkedList<>();
        JSONObject jsonData  = new JSONObject(jsonTilsynString);
        JSONArray jsonTilsynTabell = jsonData.optJSONArray(OBJEKT_HEADER);

        for(int i = 0; i < jsonTilsynTabell.length(); i++) {
            JSONObject jsonTilsyn = (JSONObject) jsonTilsynTabell.get(i);
            // Finner årstall fra dato for å sjekke med årsfilter
            String sub = jsonTilsyn.optString("dato");
            String år = sub.substring(4, 8);
            // Hvis årtall er det samme som filter ligger på, eller default, lag Tilsyn objekt.
            if(år.equals(årsFilter) || årsFilter.equals("alle") || årsFilter.equals("filtrer"))
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
}
