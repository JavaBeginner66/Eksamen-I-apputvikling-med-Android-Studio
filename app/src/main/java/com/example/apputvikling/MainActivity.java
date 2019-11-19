package com.example.apputvikling;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
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
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.util.LinkedList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity {

    public final static String REST_ENDPOINT =  "https://hotell.difi.no/api/json/mattilsynet/smilefjes/tilsyn?";

    private RecyclerView tilsynRecyclerView;
    private TilsynListeAdapter tilsynAdapter;
    private LinkedList<Tilsyn> tilsynListe = new LinkedList<>();

    private EditText sook_navn;
    private EditText sook_poststed;
    private  Spinner filtrer_aarstall;

    private Button sook_knapp;
    private Button finn_noerme_tilsyn; // Det her skjer med norske variabel navn


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
        finn_noerme_tilsyn = findViewById(R.id.finn_tilsyn_på_kart);

        // Setter opp spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.aarstall_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filtrer_aarstall.setAdapter(adapter);

        // Setter opp adapter og recycleview
        tilsynRecyclerView = findViewById(R.id.tilsyn_recycleView);
        tilsynAdapter = new TilsynListeAdapter(this, tilsynListe);
        tilsynRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tilsynRecyclerView.setAdapter(tilsynAdapter);

        sook_knapp.setOnClickListener((View v) -> {
            hideKeyboard(this);
            lesTilsynObjekt();
        });

        swipeFunksjon();
    }

    public void lesTilsynObjekt()
    {
        String qNavn = sook_navn.getText().toString();
        String qPostSted = sook_poststed.getText().toString();
         /*
         * Lager url for stringRequest. Om enten navn er poststed er tomme, vil fortsatt
         * spørringa utføres på de felt som er fylt ut.
         */
        String query = REST_ENDPOINT + "navn=" + qNavn + "&" + "poststed=" + qPostSted;
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET, query,
                response -> {
                    try {
                        // Fyller listen med formatert json
                        tilsynListe = Tilsyn.lagTilsynListe(response, filtrer_aarstall.getSelectedItem().toString());
                        // Oppdater recycleview
                        oppdaterRecycleview();
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
                Tilsyn tilsyn = tilsynListe.get(viewHolder.getAdapterPosition());
                int posisjon = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {
                    tilsynListe.remove(tilsyn);
                    tilsynAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                    final Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content), "Tilsyn slettet", Snackbar.LENGTH_LONG);
                    snackBar.setAction("Gjenopprett tilsyn", v -> {
                        tilsynListe.add(posisjon, tilsyn);
                        tilsynAdapter.notifyItemInserted(posisjon);
                        snackBar.dismiss();
                    });
                    snackBar.show();
                }
            }

            /*
             * Bibliotek fra github for å sette ikoner og tekst bak elementer i recyclerview som vises på swipe
             * https://github.com/xabaras/RecyclerViewSwipeDecorator
             */

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftActionIcon(R.drawable.delete_foreground)
                        .setSwipeLeftActionIconTint(Color.RED)
                        .addSwipeLeftLabel("Slett Tilsyn")
                        .setSwipeLeftLabelColor(Color.WHITE)
                        .addBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryBackground))
                        .create()
                        .decorate();
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

}
