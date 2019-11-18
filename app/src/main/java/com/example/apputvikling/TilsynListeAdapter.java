package com.example.apputvikling;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

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
        View itemView = inflater.inflate(R.layout.tilsyn_fragment_kort, parent, false);

        return new TilsynListeHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull TilsynListeAdapter.TilsynListeHolder holder, int position) {
        holder.kort_info_navn.setText(tilsynListe.get(position).getNavn());
    }

    @Override
    public int getItemCount() {
        return tilsynListe.size();
    }

    class TilsynListeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView container;
        public final TextView kort_info_navn;
        public final TextView kort_info_orgNr;

        public TilsynListeHolder(@NonNull View itemView, TilsynListeAdapter adapter) {
            super(itemView);
            this.container = itemView.findViewById(R.id.tilsyns_kort);
            this.kort_info_navn = itemView.findViewById(R.id.tilsyn_objekt_navn);
            this.kort_info_orgNr = itemView.findViewById(R.id.tilsyn_objekt_orgnr);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

        }
    }
}
