package com.example.apputvikling;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

/**
 * (Nærmest identisk til KravpunktListeAdapter utenom onclick metoden)
 * (Klassen er tatt/inspirert fra adapter klassen som ligger ute på canvas.)
 * Adapter klasse for Tilsyn recyclerview
 * Viser informasjon for hvert Tilsyn objekt i recycleviewet.
 */
public class TilsynListeAdapter  extends RecyclerView.Adapter<TilsynListeAdapter.TilsynListeHolder>{

    // Deklarerer alle felt;
    private LayoutInflater inflater;
    private Context context;
    private final LinkedList<Tilsyn> tilsynListe;

    // Konstruktør
    public TilsynListeAdapter(Context context, LinkedList<Tilsyn> tilsynListe){
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.tilsynListe = tilsynListe;

    }

    /**
     * Metode som inflater hvert Tilsyn objekt med "info_kort" layout
     */
    @NonNull
    @Override
    public TilsynListeAdapter.TilsynListeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.info_kort, parent, false);

        return new TilsynListeHolder(itemView, this);
    }

    /**
     * Metoden henter ut felt-variablene fra dette tilsyn-objektet, og setter alle tekstfeltene til info_kort.xml
     *
     * @param holder    Referanse til indre klasse som holder på referanser til "info_kort.xml" views
     * @param position  Posisjonen til dette Tilsyn-elementet
     */
    @Override
    public void onBindViewHolder(@NonNull TilsynListeAdapter.TilsynListeHolder holder, int position) {
        holder.kort_info_id.setText(tilsynListe.get(position).getTilsynId());
        holder.kort_info_navn.setText(tilsynListe.get(position).getNavn());
        holder.kort_info_orgNr.setText(tilsynListe.get(position).getOrgNr());
        holder.kort_info_adresse.setText(tilsynListe.get(position).getAdresse());
        holder.kort_info_postNr.setText(tilsynListe.get(position).getPostNr());
        holder.kort_info_postSted.setText(tilsynListe.get(position).getPostSted());
        String karakter = tilsynListe.get(position).getKarakter();
        // Setter bilde basert på hvilken total-karakter kortet i position har
        switch (karakter){
            case "0": case "1": holder.kort_info_karakter.setImageResource(R.drawable.smil); break;
            case "2": holder.kort_info_karakter.setImageResource(R.drawable.noytral); break;
            case "3": holder.kort_info_karakter.setImageResource(R.drawable.sur); break;
        }
    }

    /**
     *
     * @return størrelse på tilsynListe
     */
    @Override
    public int getItemCount() {
        return tilsynListe.size();
    }

    /**
     * Klassen har ansvar for å deklarere og sette alle TextView i info_kort.xml
     * med felt som er lagret som Tilsyn objekt.
     */
    class TilsynListeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView container;
        public final TextView kort_info_id;
        public final TextView kort_info_navn;
        public final TextView kort_info_orgNr;
        public final TextView kort_info_adresse;
        public final TextView kort_info_postNr;
        public final TextView kort_info_postSted;
        public final ImageView kort_info_karakter;

        public TilsynListeHolder(@NonNull View itemView, TilsynListeAdapter adapter) {
            super(itemView);
            this.container = itemView.findViewById(R.id.kort);
            this.kort_info_id = itemView.findViewById(R.id.kort_id);
            this.kort_info_navn = itemView.findViewById(R.id.kort_text_1);
            this.kort_info_orgNr = itemView.findViewById(R.id.kort_text_5);
            this.kort_info_adresse = itemView.findViewById(R.id.kort_text_2);
            this.kort_info_postNr = itemView.findViewById(R.id.kort_text_4);
            this.kort_info_postSted = itemView.findViewById(R.id.kort_text_3);
            this.kort_info_karakter = itemView.findViewById(R.id.kort_bilde);
            itemView.setOnClickListener(this);
        }

        /**
         * Når bruker trykker på et Tilsyn kort, pakker vi id til kortet og overskrift
         * inn i en bundle, og starter aktiviteten TilsynAktivitet.
         * Vi sender id så TilsynAktivitet skal kunne finne tilhørende kravpunkt til tilsynet.
         */
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), TilsynAktivitet.class);
            // Sender tilsynid til neste aktivitet så jeg kan finne igjen objektet og vise informasjon
            intent.putExtra("id", kort_info_id.getText().toString());
            intent.putExtra("navn", kort_info_navn.getText().toString());
            view.getContext().startActivity(intent);
        }


    }
}
