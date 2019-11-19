package com.example.apputvikling;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import org.json.JSONException;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    public final static String REST_ENDPOINT =  "https://hotell.difi.no/api/json/mattilsynet/smilefjes/tilsyn?page=1";

    private RecyclerView tilsynRecyclerView;
    private TilsynListeAdapter tilsynAdapter;
    private LinkedList<Tilsyn> tilsynListe = new LinkedList<>();

    private EditText søk_navn;
    private EditText søk_poststed;
    private  Spinner filtrer_årstall;

    private Button søk;
    private Button finn_nærme_tilsyn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar for instillinger
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Finner og setter alle felt
        søk_navn = findViewById(R.id.sokefelt_navn);
        søk_poststed = findViewById(R.id.sokefelt_adresse);
        filtrer_årstall = findViewById(R.id.spinner_filter);
        søk = findViewById(R.id.sok_knapp);
        finn_nærme_tilsyn = findViewById(R.id.finn_tilsyn_på_kart);

        // Setter opp spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.aarstall_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filtrer_årstall.setAdapter(adapter);

        // Setter opp adapter og recycleview
        tilsynRecyclerView = findViewById(R.id.tilsyn_recycleView);
        tilsynAdapter = new TilsynListeAdapter(this, tilsynListe);
        tilsynRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tilsynRecyclerView.setAdapter(tilsynAdapter);

        søk.setOnClickListener((View v) -> {
            hideKeyboard(this);
            lesTilsynObjekt();
        });

    }

    public void lesTilsynObjekt()
    {
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET, REST_ENDPOINT,
                response -> {
                    try {
                        // Fyller listen med formatert json
                        tilsynListe = Tilsyn.lagTilsynListe(response, søk_navn.getText().toString(), søk_poststed.getText().toString(), filtrer_årstall.getSelectedItem().toString());
                        // Oppdater recycleview
                        tilsynAdapter = new TilsynListeAdapter(this, tilsynListe);
                        tilsynRecyclerView.setAdapter(tilsynAdapter);
                        tilsynRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                        tilsynAdapter.notifyDataSetChanged();
                        if(tilsynListe.isEmpty())
                            Toast.makeText(getApplicationContext(), "Ingen treff", Toast.LENGTH_LONG).show();
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
}
