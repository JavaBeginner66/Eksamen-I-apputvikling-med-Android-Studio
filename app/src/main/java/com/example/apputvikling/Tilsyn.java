package com.example.apputvikling;

import android.util.Log;
import android.widget.ImageView;

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

    public Tilsyn(String navn, String orgNr, String adresse, String postNr, String postSted, String karakter) {
        this.navn = navn;
        this.orgNr = orgNr;
        this.adresse = adresse;
        this.postNr = postNr;
        this.postSted = postSted;
        this.karakter = karakter;
    }

    // Konstruktør for å ta imot JSonObjekt fra metoden lagTilsynListe, og sette felt.
    public Tilsyn(JSONObject jsonObject){
        this.navn = jsonObject.optString(OBJEKT_NAVN);
        this.orgNr = jsonObject.optString(OBJEKT_ORGNR);
        this.adresse = jsonObject.optString(OBJEKT_ADRESSE);
        this.postNr = jsonObject.optString(OBJEKT_POSTNR);
        this.postSted = jsonObject.optString(OBJEKT_POSTSTED);
        this.karakter = jsonObject.optString(OBJEKT_KARAKTER);
    }

    public static LinkedList<Tilsyn> lagTilsynListe(String jsonTilsynString, String søkeNavn, String søkePoststed, String årsFilter)
            throws JSONException, NullPointerException {
        LinkedList<Tilsyn> tilsynListe = new LinkedList<>();
        JSONObject jsonData  = new JSONObject(jsonTilsynString);
        JSONArray jsonTilsynTabell = jsonData.optJSONArray(OBJEKT_HEADER);

        for(int i = 0; i < jsonTilsynTabell.length(); i++) {
            JSONObject jsonTilsyn = (JSONObject) jsonTilsynTabell.get(i);

            String sub = jsonTilsyn.optString("dato");
            String år = sub.substring(4, 8);



            if(!søkeNavn.equals("") && !søkePoststed.equals("")){
                if(jsonTilsyn.getString(OBJEKT_NAVN).equals(søkeNavn) && jsonTilsyn.getString(OBJEKT_POSTSTED).equals(søkePoststed))
                    tilsynListe.add(new Tilsyn(jsonTilsyn));
            }
            else if(!søkeNavn.equals("")){
                if(jsonTilsyn.getString(OBJEKT_NAVN).equals(søkeNavn))
                    tilsynListe.add(new Tilsyn(jsonTilsyn));
            }
            else if(!søkePoststed.equals("")){
                if(jsonTilsyn.getString(OBJEKT_POSTSTED).equals(søkePoststed))
                    tilsynListe.add(new Tilsyn(jsonTilsyn));
            }
            else{
                tilsynListe.add(new Tilsyn(jsonTilsyn));
            }
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
