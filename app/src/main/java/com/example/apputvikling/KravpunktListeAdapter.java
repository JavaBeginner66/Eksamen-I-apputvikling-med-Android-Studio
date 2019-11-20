package com.example.apputvikling;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

public class KravpunktListeAdapter extends RecyclerView.Adapter<KravpunktListeAdapter.KravpunktListeHolder>{

    private LayoutInflater inflater;
    private Context context;
    private final LinkedList<Kravpunkt> kravpunktListe;

    public KravpunktListeAdapter(Context context, LinkedList<Kravpunkt> kravpunktListe){
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.kravpunktListe = kravpunktListe;

    }

    @NonNull
    @Override
    public KravpunktListeAdapter.KravpunktListeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.info_kort, parent, false);
        return new KravpunktListeAdapter.KravpunktListeHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull KravpunktListeAdapter.KravpunktListeHolder holder, int position) {
        holder.karakter.setText(kravpunktListe.get(position).getKarakter());
    }

    @Override
    public int getItemCount() {
        return kravpunktListe.size();
    }

    class KravpunktListeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView karakter;

        public KravpunktListeHolder(@NonNull View itemView, KravpunktListeAdapter adapter) {
            super(itemView);
            karakter = itemView.findViewById(R.id.kort_text_3);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }


    }
}
