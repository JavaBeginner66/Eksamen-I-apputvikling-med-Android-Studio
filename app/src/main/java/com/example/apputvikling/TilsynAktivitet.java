package com.example.apputvikling;

import android.app.Activity;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class TilsynAktivitet extends AppCompatActivity {

    public final static String REST_ENDPOINT_KRAVPUNKT = "https://hotell.difi.no/api/json/mattilsynet/smilefjes/kravpunkter?";
    public final static String RECYCLEVIEW_OPPRETTELSE_NOKKEL_KRAVPUNKT = "kravpunktListe";
    public final static String TILSYN_JSON_STRING = "jsonString";

    private TextView tilsynNavn;
    private TextView tilsynInformasjon;

    private RecyclerView kravpunktRecyclerView;
    private KravpunktListeAdapter kravpunktAdapter;
    private LinkedList<Kravpunkt> kravpunktListe = new LinkedList<>();

    private String tilsynObjektString;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tilsyn);

        tilsynNavn = findViewById(R.id.tilsyn_informasjon_navn);
        tilsynInformasjon = findViewById(R.id.tilsyn_informasjon_alt);
        kravpunktRecyclerView = findViewById(R.id.kravpunkt_recycleView);




        // Henter inn tilsynid og navn som er sendt fra TilsynListeAdapter i OnClick
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            /* Hvis extra ikke er null, betyr det at verdier har blitt overført, og vi kan legge inn
            overskrift og tilsyns objekt før vi fortsetter. */
            tilsynNavn.setText(extras.getString("navn"));
            if (savedInstanceState != null) {
                /* Hvis savedInstanceState ikke er null, betyr det at bruker har rotert skjermen,
                og alt vi vil gjøre er å hente inn lista og stringen vi allerede har oppretta fra før fra oncreate, og som
                som vi lagra med onSaveInstanceState før rotering. */
                kravpunktListe = (LinkedList<Kravpunkt>) savedInstanceState.getSerializable(RECYCLEVIEW_OPPRETTELSE_NOKKEL_KRAVPUNKT);
                // Henter inn json string som er lagret på rotasjon, så vi slepper å gjøre et Volley kall hver rotasjon.
                formaterTilsynObjekt(savedInstanceState.getString(TILSYN_JSON_STRING));
                oppdaterKravpunktListe();
            } else {
                // Om ikke skjermen er rotert, betyr det at aktiviteten akkurat har starta, og vi kan hente inn json fra url
                lesKravpunktObjekt(extras.getString("id"));
                lesTilsynObjekt(extras.getString("id"));
            }
        }
    }

    public void lesTilsynObjekt(String tilsynId)
    {
        String query = MainActivity.REST_ENDPOINT_TILSYN + "tilsynid=" + tilsynId;
        if(isOnline()) {
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET, query,
                    this::formaterTilsynObjekt, error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show());
            Volley.newRequestQueue(this).add(stringRequest);
        }
    }

    public void lesKravpunktObjekt(String tilsynId)
    {
        String query = REST_ENDPOINT_KRAVPUNKT + "tilsynid=" + tilsynId;
        if(isOnline()) {
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET, query,
                    this::formaterKravpunktObjekt, error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show());
            Volley.newRequestQueue(this).add(stringRequest);
        }
    }

    void formaterKravpunktObjekt(String response){
        try {
            kravpunktListe = Kravpunkt.lagKravpunktListe(response);
            oppdaterKravpunktListe();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void formaterTilsynObjekt(String response){
        try {
            // Lagrer json string her så vi kan igjen lagre den når bruker roterer
            this.tilsynObjektString = response;
            // (Dette er en forkorta versjon av samme koden som ligger i Tilsyn(lagtilsynliste()).
            // I response får jeg et objekt som ligger i et array, som ligger i et objekt, og som jeg henter ut med denne linja.
            JSONObject  jsonTilsyn = (JSONObject) new JSONArray(new JSONObject(response).getString("entries")).get(0);

            // Legger opp all relevant informasjon fra tilsyn objektet inn i et textview i et scrollview
            String dato = jsonTilsyn.getString("dato");
            String datoFormatert = dato.substring(0,2) + "/" + dato.substring(2,4) + "/" + dato.substring(4,8);
            String info =
                    "Poststed: " + jsonTilsyn.getString("poststed") + "\n \n" +
                    "Org nummer: " + jsonTilsyn.getString("orgnummer") + "\n \n" +
                    "Post nummer: " + jsonTilsyn.getString("postnr") + "\n \n" +
                    "Dato: " + datoFormatert + "\n \n" +
                    "Tema 1: " + jsonTilsyn.getString("tema1_no") + "\n \n" +
                    "Karakter 1: " + jsonTilsyn.getString("karakter1") + "\n \n" +
                    "Tema 2: " + jsonTilsyn.getString("tema2_no") + "\n \n" +
                    "Karakter 2: " + jsonTilsyn.getString("karakter2") + "\n \n" +
                    "Tema 3: " + jsonTilsyn.getString("tema3_no") + "\n \n" +
                    "Karakter 3: " + jsonTilsyn.getString("karakter3") + "\n \n" +
                    "Tema 4: " + jsonTilsyn.getString("tema4_no") + "\n \n" +
                    "Karakter 4: " + jsonTilsyn.getString("karakter4") + "\n \n" +
                    "Total karakter: " + jsonTilsyn.getString("total_karakter") + "\n \n" +
                    "Adresse linje 1: " + jsonTilsyn.getString("adrlinje1") + "\n \n" +
                    "Status: " + jsonTilsyn.getString("status") + "\n \n" +
                    "Tilsyns besøks-type: " + jsonTilsyn.getString("tilsynsbesoektype");

            tilsynInformasjon.setText(info);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void oppdaterKravpunktListe(){

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            kravpunktRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        else
            kravpunktRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        kravpunktAdapter = new KravpunktListeAdapter(this, kravpunktListe);
        kravpunktRecyclerView.setAdapter(kravpunktAdapter);
        kravpunktAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TILSYN_JSON_STRING, this.tilsynObjektString);
        outState.putSerializable(RECYCLEVIEW_OPPRETTELSE_NOKKEL_KRAVPUNKT, kravpunktListe);
    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

}
