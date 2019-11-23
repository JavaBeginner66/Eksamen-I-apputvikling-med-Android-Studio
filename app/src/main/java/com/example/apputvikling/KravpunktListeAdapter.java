package com.example.apputvikling;

import android.content.Context;
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
        String datoFersk = kravpunktListe.get(position).getDato();
        String datoFormatert = datoFersk.substring(0,2) + "/" + datoFersk.substring(2,4) + "/" + datoFersk.substring(4,8);
        String kravpunktNavn = context.getString(R.string.kravpunkt_navn) + kravpunktListe.get(position).getNavn();
        String dato = context.getString(R.string.kravpunkt_dato) + datoFormatert;
        String ordningsverdi = context.getString(R.string.kravpunkt_ordningsverdi) + kravpunktListe.get(position).getOrdningsverdi();
        String tekst = context.getString(R.string.kravpunkt_tekst) + kravpunktListe.get(position).getTekst();
        holder.kravpunktnavn.setText(kravpunktNavn);
        holder.dato.setText(dato);
        holder.ordningsverdi.setText(ordningsverdi);
        holder.tekst.setText(tekst);
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
