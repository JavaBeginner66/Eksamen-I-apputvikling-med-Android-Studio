package com.example.apputvikling;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        String kravpunktNavn = "Kravpunkt navn: ";
        holder.kravpunktnavn.setText(kravpunktNavn + kravpunktListe.get(position).getNavn());
        holder.dato.setText(kravpunktListe.get(position).getDato());
        holder.ordningsverdi.setText(kravpunktListe.get(position).getOrdningsverdi());
        holder.tekst.setText(kravpunktListe.get(position).getTekst());
        switch (kravpunktListe.get(position).getKarakter()){
            case "0": case "1": holder.kort_info_karakter.setImageResource(R.drawable.smil); break;
            case "2": holder.kort_info_karakter.setImageResource(R.drawable.noytral); break;
            case "3": holder.kort_info_karakter.setImageResource(R.drawable.sur); break;
            case "4": case "5": holder.kort_info_karakter.setImageResource(R.drawable.ikke_vurdert); break;
        }
    }

    @Override
    public int getItemCount() {
        return kravpunktListe.size();
    }

    class KravpunktListeHolder extends RecyclerView.ViewHolder {

        public final TextView kravpunktnavn;
        public final TextView dato;
        public final TextView ordningsverdi;
        public final TextView tekst;
        public final TextView karakter;
        public final ImageView kort_info_karakter;

        public KravpunktListeHolder(@NonNull View itemView, KravpunktListeAdapter adapter) {
            super(itemView);
            kravpunktnavn = itemView.findViewById(R.id.kort_text_1);
            dato = itemView.findViewById(R.id.kort_text_2);
            ordningsverdi = itemView.findViewById(R.id.kort_text_3);
            tekst = itemView.findViewById(R.id.kort_text_4);
            karakter = itemView.findViewById(R.id.kort_text_5);
            this.kort_info_karakter = itemView.findViewById(R.id.kort_bilde);
        }
    }
}
