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

/*
    Klassen har ansvaret for funksjoner i aktivitet som blir kjørt når man trykker på
    et Tilsyn objekt. Dette inneholder og kjøre to Volley kall for mer informasjon om
    tilsyn objekt, og hente inn kravpunkt objekt fra tilsyn objekt id.
 */

public class TilsynAktivitet extends AppCompatActivity {

    public final static String REST_ENDPOINT_KRAVPUNKT =
            "https://hotell.difi.no/api/json/mattilsynet/smilefjes/kravpunkter?"; // Endepunkt for json objekt for kravpunkt med tilhørende tilsyn-id.
    public final static String RECYCLEVIEW_OPPRETTELSE_NOKKEL_KRAVPUNKT = "kravpunktListe"; // Nøkkel-variabel for lagring av kravpunkt-objekt recycleview tabell gjennom rotering av skjerm.
    public final static String TILSYN_JSON_STRING = "jsonString"; // Nøkkel-variabel for lagring av en string med json objekt som har tilsyn informasjon (gjennom rotering)

    private TextView tilsynNavn; // Overskrift for tilsyn informasjon (Tilsyn navn). Denne blir overført via bundle.
    private TextView tilsynInformasjon; // Textview hvor all informasjon om tilsyn blir vist. Denne ligger i et scrollview i xml fila.

    private RecyclerView kravpunktRecyclerView; // Recycleview for kravpunkt objekt.
    private KravpunktListeAdapter kravpunktAdapter; // Adapter objekt for kravpunkt liste.
    private LinkedList<Kravpunkt> kravpunktListe = new LinkedList<>(); // Lista som inneholder kravpunkt objekt.

    private String tilsynObjektString; // Tilsyn informasjon fra url blir lagra etter lesing fra url, og brukes i onSaveInstanceState for gjenoppretting.

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tilsyn);

        // Finner og setter alle felt
        tilsynNavn = findViewById(R.id.tilsyn_informasjon_navn);
        tilsynInformasjon = findViewById(R.id.tilsyn_informasjon_alt);
        kravpunktRecyclerView = findViewById(R.id.kravpunkt_recycleView);


        // Henter inn tilsynid og navn som er sendt fra TilsynListeAdapter i OnClick
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            /* Hvis extra ikke er null, betyr det at verdier har blitt overført, og vi kan legge inn
            overskrift før vi fortsetter. */
            tilsynNavn.setText(extras.getString(MainActivity.TILSYN_TIL_KRAVPUNKT_NAVN));
            if (savedInstanceState != null) {
                /* Hvis savedInstanceState ikke er null, betyr det at bruker har rotert skjermen,
                og alt vi vil gjøre er å hente inn lista og strengen vi allerede har oppretta fra før i oncreate, og som
                som vi lagra med onSaveInstanceState før rotering. */
                kravpunktListe = (LinkedList<Kravpunkt>) savedInstanceState.getSerializable(RECYCLEVIEW_OPPRETTELSE_NOKKEL_KRAVPUNKT);
                // Henter inn json string som er lagret på rotasjon, så vi slepper å gjøre et Volley kall hver rotasjon.
                formaterTilsynObjekt(savedInstanceState.getString(TILSYN_JSON_STRING));
                // Oppdaterer lista for å koble den mot recycleview og adapter
                oppdaterKravpunktListe();
            } else {
                // Om ikke skjermen er rotert, betyr det at aktiviteten akkurat har starta, og vi kan hente inn ny json fra url
                // nøkkel "id" er feltet ''tilsynid'' i datasettet tilsyn, overført fra TilsynListeAdapter
                lesKravpunktObjekt(extras.getString(MainActivity.TILSYN_TIL_KRAVPUNKT_ID));
                lesTilsynObjekt(extras.getString(MainActivity.TILSYN_TIL_KRAVPUNKT_ID));
            }
        }
    }


    /**
     * Metoden gjør et volley kall som henter ut alle jsonObjekt som tilhører kortet bruker trykket på i main aktivitet.
     * Her implementerer vi også begge metodene (response og error) fra grensesnittet i metoden med lambda, for å fortkorte ned på kode.
     * Dette blir også gjort for å kunne få 2 forskjellige responser fra 2 volley kall i en og samme klasse.
     * (En annen måte blir nærmere utforsket i rapporten)
     * Vi sender responsen videre til lesKravPunktObjekt for videre behandling.
     *
     * @param tilsynId  Blir brukt til å feste på url endpoint, og gjøre et query søk for å finne alle tilsyn objekt med overført id.
     */
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

    /**
     * Metoden gjør et Volley kall som leser alle kravpunkt objekt med tilhørende tilsynID.
     * Som i metoden lesTilsynObjekt over, setter vi denne opp på samme måte og grunn.
     *
     * @param tilsynId Blir brukt for å finne alle kravpunkt objekt som tilhører tilsynid som bruker trykket på i mainActivity
     */
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

    /**
     * Denne metoden lager kravpunkt lista fra response strengen, og oppdaterer den ferdige lista med recycleview og adapter.
     * Grunnen til at denne metoden fins, er for å lage et mellom-ledd for å skille mellom de forskjellige kallene i
     * if-blokken i onCreate(). Hadde jeg i stedet valgt å lage kravpunktlista i metoden oppdaterKravpunktListe under,
     * hadde lista blitt lagd på nytt for hver rotasjon.
     * Jeg kunne naturligvis også gjort det i lagKravpunktObjekt, men jeg foretrekte en mer kompakt kode der.
     *
     * @param response  Inneholder kravpunkt json-objekt i streng, og blir brukt til å lage kravpunkt-
     *                  lista ved hjelp av lagKravPunktListe() i Kravpunkt klassa.
     */
    void formaterKravpunktObjekt(String response){
        try {
            kravpunktListe = Kravpunkt.lagKravpunktListe(response);
            oppdaterKravpunktListe();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoden formaterer @param response og henter ut verdiene vi vil ha ved hjelp av json filtrering og getString() med felta vi trenger.
     * Vi formaterer alt inn i en string, og deretter legger det inn i variabel(TextField) tilsynsInformasjon.
     *
     * @param response  Inneholder tilsyn json-objekt som er henta fra enten volley eller bundle, avhengig av rotasjon eller ny aktivitet.
     */
    void formaterTilsynObjekt(String response){
        try {
            // Lagrer json string her så vi kan igjen lagre den når bruker roterer
            this.tilsynObjektString = response;
            // I response får jeg et objekt som ligger i et array, som ligger i et objekt. Denne linja filtrerer det ned objektet vi trenger.
            JSONObject  jsonTilsyn = (JSONObject) new JSONArray(new JSONObject(response).getString(Tilsyn.OBJEKT_HEADER)).get(0);
            // Datoen ligger i objektet som 8 rene siffer, så vi må formatere inn '/' for å vise tydlig hvilken dato det er.
            String dato = jsonTilsyn.getString(getString(R.string.tilsyn_objekt_dato));
            String datoFormatert = dato.substring(0,2) + "/" + dato.substring(2,4) + "/" + dato.substring(4,8);
            // Legger opp all relevant informasjon fra tilsyn objektet inn i et textview i et scrollview
            String info =
                    getString(R.string.tilsyn_label_poststed) + jsonTilsyn.getString(getString(R.string.tilsyn_objekt_poststed)) + "\n \n" +
                    getString(R.string.tilsyn_label_orgnr) + jsonTilsyn.getString(getString(R.string.tilsyn_objekt_orgnummer)) + "\n \n" +
                    getString(R.string.tilsyn_label_postnummer) + jsonTilsyn.getString(getString(R.string.tilsyn_objekt_postnr)) + "\n \n" +
                    getString(R.string.tilsyn_label_dato) + datoFormatert + "\n \n" +
                    getString(R.string.tilsyn_label_tema1) + jsonTilsyn.getString(getString(R.string.tilsyn_objekt_tema1)) + "\n \n" +
                    getString(R.string.tilsyn_label_karakter1) + jsonTilsyn.getString(getString(R.string.tilsyn_objekt_karakter1)) + "\n \n" +
                    getString(R.string.tilsyn_label_tema2) + jsonTilsyn.getString(getString(R.string.tilsyn_objekt_tema2)) + "\n \n" +
                    getString(R.string.tilsyn_label_karakter2) + jsonTilsyn.getString(getString(R.string.tilsyn_objekt_karakter2)) + "\n \n" +
                    getString(R.string.tilsyn_label_tema3) + jsonTilsyn.getString(getString(R.string.tilsyn_objekt_tema3)) + "\n \n" +
                    getString(R.string.tilsyn_label_karakter3) + jsonTilsyn.getString(getString(R.string.tilsyn_objekt_karakter3)) + "\n \n" +
                    getString(R.string.tilsyn_label_tema4) + jsonTilsyn.getString(getString(R.string.tilsyn_objekt_tema4)) + "\n \n" +
                    getString(R.string.tilsyn_label_karakter4) + jsonTilsyn.getString(getString(R.string.tilsyn_objekt_karakter4)) + "\n \n" +
                    getString(R.string.tilsyn_label_totalkarakter) + jsonTilsyn.getString(getString(R.string.tilsyn_objekt_totalkarakter)) + "\n \n" +
                    getString(R.string.tilsyn_label_adrlinje1) + jsonTilsyn.getString(getString(R.string.tilsyn_objekt_adrlinje1)) + "\n \n" +
                    getString(R.string.tilsyn_label_status) + jsonTilsyn.getString(getString(R.string.tilsyn_objekt_status)) + "\n \n" +
                    getString(R.string.tilsyn_label_tilsynsbesoekstype) + jsonTilsyn.getString(getString(R.string.tilsyn_objekt_tilsynsbesoektype));

            tilsynInformasjon.setText(info);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Metoden har ansvar for å oppdatere kravpunkt lista og sjekke hvilken orientasjon enheten har.
     */
    private void oppdaterKravpunktListe(){
        // Vi sjekker hvilken orientasjon bruker har på enheten, og forandrer hvilken orientasjon listen har basert på det.
        // Om den er i portrait modus, vil listen kunne scrolle horisontalt, om den ligger i landscape, vil listen kunne scrolle vertikalt.
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            kravpunktRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        else
            kravpunktRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Initialiserer adapter
        kravpunktAdapter = new KravpunktListeAdapter(this, kravpunktListe);
        // Setter adapter til recycleview
        kravpunktRecyclerView.setAdapter(kravpunktAdapter);
        // Gjør adapteren klar over at dataene har forandra seg
        kravpunktAdapter.notifyDataSetChanged();
    }

    /**
     * Metoden sjekker om enheten har nett-tilgang.
     *
     * @return Returnerer en boolean som forteller oss om enheten har nett tilgang eller ikke.
     */
    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Metoden lagrer variabler som vi vil ta vare på om aktiviteten skulle brytes eller avsluttes.
     * Vi bruker den til å lagre kravpunkt lista og tilsyn informasjon for å slippe å kalle på
     * en ny volley for hver gang brukeren roterer enheten.
     *
     * @param outState  Bundle der vi lagrer variabler.
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Lagrer stringen tilsynsObjektString som inneholder tilsyns-objekt.
        outState.putString(TILSYN_JSON_STRING, this.tilsynObjektString);
        // Lagrer tabellen kravpunktListe som inneholder kravpunkt objekt.
        outState.putSerializable(RECYCLEVIEW_OPPRETTELSE_NOKKEL_KRAVPUNKT, kravpunktListe);
    }

}
