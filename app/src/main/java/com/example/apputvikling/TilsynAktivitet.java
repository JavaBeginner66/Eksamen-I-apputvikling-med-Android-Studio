package com.example.apputvikling;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TilsynAktivitet extends AppCompatActivity {

    private TextView tilsynNavn;
    private TextView tilsynInformasjon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tilsyn);

        tilsynNavn = findViewById(R.id.tilsyn_informasjon_navn);
        tilsynInformasjon = findViewById(R.id.tilsyn_informasjon_alt);

        // Henter inn tilsynid og navn som er sendt fra TilsynListeAdapter i OnClick
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            tilsynNavn.setText(extras.getString("navn"));
            lesTilsynObjekt(extras.getString("id"));
        }
    }

    public void lesTilsynObjekt(String tilsynId)
    {
        String query = MainActivity.REST_ENDPOINT + "tilsynid=" + tilsynId;
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET, query,
                this::formaterTilsynObjekt, error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show());
        Volley.newRequestQueue(this).add(stringRequest);
    }

    void formaterTilsynObjekt(String response){
        try {
            // (Dette er en forkorta versjon av samme koden som ligger i Tilsyn(lagtilsynliste()).
            // I response får jeg et objekt som ligger i et array, som ligger i et objekt, og som jeg henter ut med denne linja.
            JSONObject  jsonTilsyn = (JSONObject) new JSONArray(new JSONObject(response).getString("entries")).get(0);

            // Legger opp all relevant informasjon fra tilsyn objektet inn i et textview i et scrollview
            String info =
                    "Poststed: " + jsonTilsyn.getString("poststed") + "\n \n" +
                    "Org nummer: " + jsonTilsyn.getString("orgnummer") + "\n \n" +
                    "Post nummer: " + jsonTilsyn.getString("postnr") + "\n \n" +
                    "Dato: " + jsonTilsyn.getString("dato") + "\n \n" +
                    "Tema 1: " + jsonTilsyn.getString("tema1_no") + "\n \n" +
                    "Tema 2: " + jsonTilsyn.getString("tema2_no") + "\n \n" +
                    "Tema 3: " + jsonTilsyn.getString("tema3_no") + "\n \n" +
                    "Tema 4: " + jsonTilsyn.getString("tema4_no") + "\n \n" +
                    "Adresse linje 1: " + jsonTilsyn.getString("adrlinje1") + "\n \n" +
                    "Status: " + jsonTilsyn.getString("status") + "\n \n" +
                    "Karakter 1: " + jsonTilsyn.getString("karakter1") + "\n \n" +
                    "Karakter 2: " + jsonTilsyn.getString("karakter2") + "\n \n" +
                    "Karakter 3: " + jsonTilsyn.getString("karakter3") + "\n \n" +
                    "Karakter 4: " + jsonTilsyn.getString("karakter4") + "\n \n" +
                    "Total karakter: " + jsonTilsyn.getString("total_karakter") + "\n \n" +
                    "Tilsyns besøks-type: " + jsonTilsyn.getString("tilsynsbesoektype");

            tilsynInformasjon.setText(info);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
