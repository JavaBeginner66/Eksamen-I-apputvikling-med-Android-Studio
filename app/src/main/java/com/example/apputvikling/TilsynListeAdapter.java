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
 * Adapter klasse
 */
public class TilsynListeAdapter  extends RecyclerView.Adapter<TilsynListeAdapter.TilsynListeHolder>{

    private LayoutInflater inflater;
    private Context context;
    private final LinkedList<Tilsyn> tilsynListe;

    public TilsynListeAdapter(Context context, LinkedList<Tilsyn> tilsynListe){
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.tilsynListe = tilsynListe;

    }

    @NonNull
    @Override
    public TilsynListeAdapter.TilsynListeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.info_kort, parent, false);

        return new TilsynListeHolder(itemView, this);
    }

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

    @Override
    public int getItemCount() {
        return tilsynListe.size();
    }

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
