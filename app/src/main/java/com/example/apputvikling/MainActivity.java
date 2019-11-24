package com.example.apputvikling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Hoved-klassa for appen, og har ansvar for diverse funksjoner som
 * > å hente Tilsyn objekt informasjon via et Volley kall
 * > Opprettholde et recycleview som viser Tilsyn objekt
 * > Flere onclick kall og metoder
 * > Swipe funksjon
 * > Funksjoner og variabler for å finne enhetens postnummer
 *
 * MainActivity er base-klasse andre klasser utvider fra, og jeg lot navnet være engelsk for lettere oversikt gjennom kode-prosessen.
 *
 */
public class MainActivity extends AppCompatActivity {

    public final static String REST_ENDPOINT_TILSYN =
            "https://hotell.difi.no/api/json/mattilsynet/smilefjes/tilsyn?";// Endepunkt for json objekt for Tilsyn fra datasettet
    public final static String RECYCLEVIEW_OPPRETTELSE_NOKKEL_TILSYN =  "recycleView_nokkel";// Nøkkel-variabel for lagring av tilsyn tabell gjennom rotering av skjerm.
    public final static String TILSYN_TIL_KRAVPUNKT_ID =  "id_tilsyn"; // Nøkkel-variabel for å overføre id på tilsyn objekt fra onclick i TilsynListeAdapter til TilsynAktivitet
    public final static String TILSYN_TIL_KRAVPUNKT_NAVN =  "navn_tilsyn";// Nøkkel-variabel for å overføre navn på tilsyn objekt fra onclick i TilsynListeAdapter til TilsynAktivitet

    public final static int MY_REQUEST_LOCATION = 1; // Konstant Sjekk-variabel for gps funksjoner

    SharedPreferences minePreferanser; // Mine preferanser objekt. Brukes til å lagre/hente opplysninger vi ta vare på gjennom app-kjøringer.

    private LocationManager locationManager; // posisjons-manager
    private String locationProvider = LocationManager.GPS_PROVIDER; // posisjons-forsørger
    private Location myLocation; // objekt som holder informasjon angående enhetens posisjon

    private RecyclerView tilsynRecyclerView; // Recycleview for Tilsyn objekt.
    private TilsynListeAdapter tilsynAdapter;// Adapter objekt for Tilsyn tabell.
    private LinkedList<Tilsyn> tilsynListe = new LinkedList<>(); // Tilsyn tabell som holder på tilsyn objekt.

    private EditText sook_navn; // Tekstfelt for å skrive inn et søk på navn på tilsyn
    private EditText sook_poststed; // Tekstfelt for å skrive inn et søk på poststed på tilsyn

    private  Spinner filtrer_aarstall; // Valg-liste for filtrering av årstall på tilsyn
    private  Spinner filtrer_smilefjes; // Valg-liste for filtrering av karakter på tilsyn
    private  Spinner sortering; // Valg-liste for sortering av navn eller poststed på tilsyn

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialiserer mine preferanser objekt
        minePreferanser = PreferenceManager.getDefaultSharedPreferences(this);

        // Toolbar for instillinger
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Finner og setter alle felt
        Button sook_knapp = findViewById(R.id.sok_knapp);
        Button finn_tilsyn_i_nearheten = findViewById(R.id.finn_tilsyn_i_nearheten);
        sook_navn = findViewById(R.id.sokefelt_navn);
        sook_poststed = findViewById(R.id.sokefelt_adresse);
        filtrer_aarstall = findViewById(R.id.spinner_filter_aarstall);
        filtrer_smilefjes = findViewById(R.id.spinner_filter_smilefjes);
        sortering = findViewById(R.id.spinner_sortering);
        tilsynRecyclerView = findViewById(R.id.tilsyn_recycleView);

        // Setter opp alle spinners
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.aarstall_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filtrer_aarstall.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.smilefjes_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filtrer_smilefjes.setAdapter(adapter2);

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                R.array.sortering_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortering.setAdapter(adapter3);

        // Klikk-listener for søke knappen. Kaller først på metode for å gjemme tastatur, også et volley kall for å lese tilsyn objekt.
        sook_knapp.setOnClickListener((View v) -> {
            hideKeyboard(this);
            lesTilsynObjekt(null);
        });

        /* Klikk-listener for å finne tilsyn på samme postnummer som den enheten ligger på.
           Jeg kaller først på sjekkTillatelser som henter opp et vindu med gps-betingelser bruker må godta, også
           kan bruker trykke igne for å finne adresse og deretter gjør et volley kall på post-nummer som matcher */
        finn_tilsyn_i_nearheten.setOnClickListener((View v) ->{
            sjekkTillatelser();
            finnAdresse();
        });

        /* Om savedInstanceState ikke er null, betyr det at bruker bare har rotert skjermen, og alt vi vil gjøre er å
        * gjenopprette inn tabellen som ble lagra i onSaveInstanceState og oppdatere den. */
        if(savedInstanceState != null){
            tilsynListe = (LinkedList<Tilsyn>)savedInstanceState.getSerializable(RECYCLEVIEW_OPPRETTELSE_NOKKEL_TILSYN);
            oppdaterTilsynListe();
        }

        // Metode med swipe funksjonen.
        swipeFunksjon();
    }

    /**
     *  Metoden har ansvar for å sette opp en spørring mot Tilsyn json-objekt url, og kjøre en
     *  volley for å hente de objekt vi spør etter. Den står også for å sjekke om listen skal sorteres.
     *  Metoden blir brukt i sammenheng med 2 ulike kall, et ordinert søk og et postnummer-match søk (forklart via @param).
     *
     * @param postNr    Blir brukt i tilfellet hvor bruker trykker på "finn tilsyn i nærheten" knappen,
     *                  der postNr går inn i spørringen mot url. Om postNr er null, som den er i tilfeller
     *                  der metodekallet kommer fra et ordinert søk, blir postnummer feltet i spørringa tom, og vi får
     *                  et søk uten leting etter spesifikt postnummer.
     */
    public void lesTilsynObjekt(String postNr)
    {
        // Deklarer alle stringer vi potensielt trenger i spørringa. Spørringa vil ignorere tomme strenger,
        // som passer oss bra på måten vi har lagt det opp på.
        String qNavn = sook_navn.getText().toString();
        String qPostSted = sook_poststed.getText().toString();
        String qFilter_aarstall = filtrer_aarstall.getSelectedItem().toString();
        String qFilter_smilefjes = filtrer_smilefjes.getSelectedItem().toString();
        String qPostNr = "";
        String query;

        // Om filteret ikke ligger på et bestemt element, default søket til ingen filter med en tom string
        if(qFilter_aarstall.equals(getString(R.string.aarstal_filter_sammenligning)))
            qFilter_aarstall = "";

        if(qFilter_smilefjes.equals(getString(R.string.karakter_filter_sammenligning)))
            qFilter_smilefjes = "";

        /* Om postNr er initialisert, betyr det at kallet kommer fra finnadresse metoden,
        og da finner vi tilsyn med post nummer som treffer postnr fra posisjonen vi søker fra. */
        if(postNr != null)
            qPostNr = postNr;

        // Initialiserer spørre-strengen. Tomme strenger vil ikke hindre/skape errors, de vil bare ignoreres.
        query = REST_ENDPOINT_TILSYN + "navn=" + qNavn + "&poststed=" + qPostSted + "&dato=*" +
                qFilter_aarstall + "&postnr=" + qPostNr + "&total_karakter=" + qFilter_smilefjes;
        // Hvis enheten har nett tilgang, fortsett.
        if(isOnline()) {
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET, query,
                    response -> {
                        try {
                            // Fyller listen med formatert json
                            tilsynListe = Tilsyn.lagTilsynListe(response);
                            // Sorterer lista basert på bruker input fra sortering spinner
                            switch ((String) sortering.getSelectedItem()) {
                                case "Navn":
                                    Collections.sort(tilsynListe, Tilsyn.getNameComparator());
                                    break;
                                case "Poststed":
                                    Collections.sort(tilsynListe, Tilsyn.getPoststedComparator());
                                    break;
                            }
                            // Oppdater recycleview
                            oppdaterTilsynListe();
                            // Hvis listen er tom etter søk, betyr det at brukeren skrev noe inn i søkefeltene som ikke matcha med noe.
                            if (tilsynListe.isEmpty()) {
                                Toast.makeText(getApplicationContext(), R.string.tilsyn_liste_tom_message, Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show());
            Volley.newRequestQueue(this).add(stringRequest);
        }else{
            Toast.makeText(getApplicationContext(), R.string.ingen_nettverk_message, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Lytter etter klikk på checkboksene for navn og poststed
     *        onClick kallene er lagt inn i xml fila (activity_main)
     *
     * @param view  Brukes til å finne checkboksene, og hvilken checkbox id i xml fila (activity_main) er trykket på.
     */
    public void paaCheckBoxTrykk(View view){
        // Initialiserer avKrysset via view parameter
        boolean avKrysset = ((CheckBox) view).isChecked();
        // Sjekker hvilken checkbox er trykket på, og om den er avKrysset eller ikke.
        // Er den avkrysset, fyller jeg feltet med felt satt i innstillinger.
        switch(view.getId()){
            case R.id.navn_checkbox:
                if(avKrysset)
                    sook_navn.setText(minePreferanser.getString(getString(R.string.navn_favoritt_instillinger), ""));
                else
                    sook_navn.setText("");
                break;
            case R.id.poststed_checkbox:
                if(avKrysset)
                    sook_poststed.setText(minePreferanser.getString(getString(R.string.poststed_favoritt_instillinger), ""));
                else
                    sook_poststed.setText("");
        }
    }

    /**
     * Metode for å inflate menu_main.xml og vise ''instillinger'' i høyre hjørne.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Metode for å lytte etter klikk på meny
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Om brukeren trykker på 'instillinger', starter vi ny activity (instillinger)
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, InnstillingerAktivitet.class);
            this.startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * KILDE: https://stackoverflow.com/questions/1109022/how-to-set-visibility-android-soft-keyboard
     * (Topp svar)
     * Legger ned tastatur på kall.
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * KILDE/Inspirasjon
     * https://codelabs.developers.google.com/codelabs/android-training-cards-and-colors/index.html?index=..%2F..android-training#4
     *
     * Metode har ansvar for å lytte etter swipes på tilsyn objekt fra bruker, og hva som skal skje.
     */
    void swipeFunksjon(){
        //Swipe listener
        ItemTouchHelper.SimpleCallback helper = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT)
        {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT) {
                    // Henter inn hvilket tilsyn objekt og posisjon som er swipa.
                    Tilsyn tilsyn = tilsynListe.get(viewHolder.getAdapterPosition());
                    int posisjon = viewHolder.getAdapterPosition();
                    /* ** KILDE **
                    * https://stackoverflow.com/questions/2478517/how-to-display-a-yes-no-dialog-box-on-android */
                    DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                        tilsynAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            // Hvis bruker trykker "Bekreft", fjern tilsyn fra listen og kall på notifyitemremoved som sier ifra til adapter at det er fjernet.
                            // Om bruker trykker nei, går dialog boksen vekk siden vi bare lytter etter bekreft knappen.
                            tilsynListe.remove(tilsyn);
                            tilsynAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                            // Her setter jeg opp en snackbar med et valg i å gjenopprette tilsyn-objektet i tilfelle bruker ombestemmer seg.
                            final Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content), getString(R.string.tilsyn_slettet_message), Snackbar.LENGTH_LONG);
                            snackBar.setActionTextColor(getResources().getColor(R.color.snackbarColor));
                            snackBar.setAction(getString(R.string.gjenopprett_tilsyn_message), v -> {
                                tilsynListe.add(posisjon, tilsyn);
                                tilsynAdapter.notifyItemInserted(posisjon);
                                snackBar.dismiss();
                            });
                            snackBar.show();
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(getString(R.string.vil_du_slette_tilsyn_message)).setPositiveButton(getString(R.string.tilsyn_fjern_bekreft), dialogClickListener)
                            .setNegativeButton(getString(R.string.tilsyn_angre_slett), dialogClickListener).show();
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(helper);
        itemTouchHelper.attachToRecyclerView(tilsynRecyclerView);
    }

    /**
     *  Metoden har ansvar for å oppdatere tilsyn lista.
     */
    void oppdaterTilsynListe(){
        tilsynAdapter = new TilsynListeAdapter(this, tilsynListe);
        tilsynRecyclerView.setAdapter(tilsynAdapter);
        tilsynRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tilsynAdapter.notifyDataSetChanged();
    }

    /**
     * Metoden er tatt fra deler av DPS_Demo i canvas
     * Den finner post nummer og setter mittPostNr felt.
     */
    public void finnAdresse() {
        if (myLocation != null) {
            Geocoder coder = new Geocoder(getApplicationContext());
            List<Address> geocodeResults=null;
            try {
                if (Geocoder.isPresent()) {
                    geocodeResults = coder.getFromLocation(myLocation.getLatitude(), myLocation.getLongitude(), 10);
                    if (geocodeResults != null && geocodeResults.size() > 0) {
                        Address adresse = geocodeResults.get(0);
                        lesTilsynObjekt(adresse.getPostalCode());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    /*
     * Metoden er hentet fra GPS_Demo på canvas
     * Sjekker gps tillatelser og setter myLocation variabel som vi bruker til å finne postnummeret enheten ligger på.
     */
    void sjekkTillatelser(){
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (! locationManager.isProviderEnabled(locationProvider)) {
            Toast.makeText(this, "Aktiver " + locationProvider + " under Location i Settings",
                    Toast.LENGTH_LONG).show();
        } else {
            int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                // Appen har ikke tillatelse, spør bruker
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_REQUEST_LOCATION);
            } else {
                try {
                    myLocation = locationManager.getLastKnownLocation(locationProvider);
                }
                catch (SecurityException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /*
     * Metoden er hentet fra GPS_Demo på canvas
     * Metoden sier hva som skal skje etter bruker har valgt et alternativ på tillatelses-vinduet
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_REQUEST_LOCATION) {
            // Hvis bruker avviser tillatelsen vil arrayen grantResults være tom.
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                finnAdresse();
            } else {
                Toast.makeText(this, "Denne appen krever tilgang til " + locationProvider + " Settings.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     *             Henter verdier fra instillinger via nøkler
     *             Gjør det i onResume siden når man går tilbake fra
     *             instillinger vinduet, vil ikke onCreate kjøres.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Henter årstall-preferanse fra instillinger. Om bruker ikke har satt noe der, vil en tom streng komme.
        int verdi = minePreferanser.getInt(getString(R.string.aarstall_liste_verdi), 0);
        filtrer_aarstall.setSelection(verdi);
    }

    /**
     * Metoden lagrer variabler som vi vil ta vare på om aktiviteten skulle brytes eller avsluttes.
     * Vi bruker den til å lagre tilsyns-lista for å slippe og kalle på
     * en ny volley for hver gang brukeren roterer enheten.
     *
     * @param outState  Bundle der vi lagrer variabler.
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(RECYCLEVIEW_OPPRETTELSE_NOKKEL_TILSYN, tilsynListe);
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

}
