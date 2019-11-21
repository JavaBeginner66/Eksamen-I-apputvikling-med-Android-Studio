package com.example.apputvikling;

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
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public final static String REST_ENDPOINT_TILSYN =  "https://hotell.difi.no/api/json/mattilsynet/smilefjes/tilsyn?";

    public final static int MY_REQUEST_LOCATION = 1;

    private LocationManager locationManager;
    private String locationProvider = LocationManager.GPS_PROVIDER;
    private Location myLocation;

    private RecyclerView tilsynRecyclerView;
    private TilsynListeAdapter tilsynAdapter;
    private LinkedList<Tilsyn> tilsynListe = new LinkedList<>();

    private EditText sook_navn;
    private EditText sook_poststed;
    private  Spinner filtrer_aarstall;

    private Button sook_knapp;
    private Button finn_tilsyn_i_nearheten;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar for instillinger
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Finner og setter alle felt
        sook_navn = findViewById(R.id.sokefelt_navn);
        sook_poststed = findViewById(R.id.sokefelt_adresse);
        filtrer_aarstall = findViewById(R.id.spinner_filter);
        sook_knapp = findViewById(R.id.sok_knapp);
        finn_tilsyn_i_nearheten = findViewById(R.id.finn_tilsyn_i_nearheten);

        // Setter opp spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.aarstall_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filtrer_aarstall.setAdapter(adapter);

        // Setter opp adapter og recycleview
        tilsynRecyclerView = findViewById(R.id.tilsyn_recycleView);
        oppdaterRecycleview();

        sook_knapp.setOnClickListener((View v) -> {
            hideKeyboard(this);
            lesTilsynObjekt(null);
        });

        finn_tilsyn_i_nearheten.setOnClickListener((View v) ->{
            sjekkTillatelser();
            finnAdresse();
        });

        swipeFunksjon();
    }

    public void lesTilsynObjekt(String postNr)
    {
        String qNavn = sook_navn.getText().toString();
        String qPostSted = sook_poststed.getText().toString();
        String qFilter = filtrer_aarstall.getSelectedItem().toString();
        String qPostNr = "";
        String query;
        // Om filteret ikke ligger på et årstall, default søket til alle årstall med en tom string
        if(qFilter.equals("alle") || qFilter.equals("filtrer"))
            qFilter = "";

        /* Om postNr er initialisert, betyr det at kallet kommer fra finnadresse metoden,
         /* og da finner vi tilsyn med post nummer som treffer postnr fra posisjonen vi søker fra.
          */
        if(postNr != null)
            qPostNr = postNr;

        query = REST_ENDPOINT_TILSYN + "navn=" + qNavn + "&poststed=" + qPostSted + "&dato=*" + qFilter + "&postnr=" + qPostNr;
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET, query,
                response -> {
                    try {
                        // Fyller listen med formatert json
                        tilsynListe = Tilsyn.lagTilsynListe(response);
                        // Oppdater recycleview
                        oppdaterRecycleview();
                        if(tilsynListe.isEmpty()){
                            Toast.makeText(getApplicationContext(), "Ingen treff", Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show());
        Volley.newRequestQueue(this).add(stringRequest);
    }

    /**
     * Metode for å inflate menu_main og vise ''instillinger'' oppslaget.
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
                    Tilsyn tilsyn = tilsynListe.get(viewHolder.getAdapterPosition());
                    int posisjon = viewHolder.getAdapterPosition();
                    /* ** KILDE **
                    * https://stackoverflow.com/questions/2478517/how-to-display-a-yes-no-dialog-box-on-android */
                    DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                        tilsynAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            tilsynListe.remove(tilsyn);
                            tilsynAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                            final Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content), "Tilsyn slettet", Snackbar.LENGTH_LONG);
                            snackBar.setActionTextColor(getResources().getColor(R.color.snackbarColor));
                            snackBar.setAction("Gjenopprett tilsyn", v -> {
                                tilsynListe.add(posisjon, tilsyn);
                                tilsynAdapter.notifyItemInserted(posisjon);
                                snackBar.dismiss();
                            });
                            snackBar.show();
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Vil du slette tilsyn fra listen?").setPositiveButton("Bekreft", dialogClickListener)
                            .setNegativeButton("Nei", dialogClickListener).show();
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(helper);
        itemTouchHelper.attachToRecyclerView(tilsynRecyclerView);
    }

    void oppdaterRecycleview(){
        tilsynAdapter = new TilsynListeAdapter(this, tilsynListe);
        tilsynRecyclerView.setAdapter(tilsynAdapter);
        tilsynRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tilsynAdapter.notifyDataSetChanged();
    }

    /**
     * Metoden er hentet fra DPS_Demo i canvas
     * Finner post nummer og setter mittPostNr felt.
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

    @Override
    protected void onResume() {
        super.onResume();
        /* Henter verdier fra instillinger via nøkler
            Gjør det i onResume siden når man går tilbake fra
            instillinger vinduet, vil ikke onCreate kjøres.*/
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int verdi = myPreferences.getInt("aarstall_liste_verdi", 0);
        filtrer_aarstall.setSelection(verdi);
    }
}
