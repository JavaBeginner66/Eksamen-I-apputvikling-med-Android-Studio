package com.example.apputvikling;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.util.LinkedList;

/**
 * Fragment klasse som holder på recycleview for tilsyns-kort
 */

public class TilsynsListeFragment extends Fragment {

    public final static String REST_ENDPOINT =  "https://hotell.difi.no/api/json/mattilsynet/smilefjes/tilsyn";

    private View view;

    private RecyclerView tilsynRecyclerView;
    private TilsynListeAdapter tilsynAdapter;
    private LinkedList<Tilsyn> tilsynListe = new LinkedList<>();

    private Button søk;
    private Button finn_nærme_tilsyn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.tilsyns_liste_fragment, container, false);

/**/

        søk = view.findViewById(R.id.sok_knapp);
        finn_nærme_tilsyn = view.findViewById(R.id.finn_tilsyn_på_kart);

        tilsynRecyclerView = view.findViewById(R.id.tilsyn_recycleView);
        tilsynAdapter = new TilsynListeAdapter(view.getContext(), tilsynListe);
        tilsynRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        tilsynRecyclerView.setAdapter(tilsynAdapter);

        // Recycleviewet med tilsyn objekt blir oprettet når bruker trykker søk
        søk.setOnClickListener((View v) -> {
            lesTilsynObjekt();
        });

        return view;
    }

    public void lesTilsynObjekt()
    {
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET, REST_ENDPOINT,
                response -> {

                    try {
                        tilsynListe = Tilsyn.lagTilsynListe(response);
                        tilsynAdapter = new TilsynListeAdapter(view.getContext(), tilsynListe);
                        tilsynRecyclerView.setAdapter(tilsynAdapter);
                        tilsynRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                        tilsynAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, error -> {
            Log.d("tilsyn" , error.getMessage());
        });
        Volley.newRequestQueue(view.getContext()).add(stringRequest);
    }
}
