package com.example.apputvikling;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

/**
 * Klassen har ansvar for alle funksjoner instillinger vinduet har.
 * Består for det meste av template kode som er fra pensum.
 */

public class InnstillingerAktivitet extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_innstillinger);
        // Tilbake knapp til MainActivity enabled
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        // Starter fragment som er en indre klasse
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.innstillinger, new InnstillingerFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Metoden sørger for at appen går tilbake til MainActivity og avslutter instillinger aktiviteten.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if(id==android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class InnstillingerFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.innstillinger, rootKey);
            /**
             *  ** KILDE **
             *  https://stackoverflow.com/questions/6669482/value-of-a-listpreference-never-updates
             */
            ListPreference aarstall = findPreference("aarstall_list");
            assert aarstall != null;
            // Sier hva som skal skje om årstall lista blir satt til en ny verdi
            aarstall.setOnPreferenceChangeListener((preference, newValue) -> {
                // Setter opp preferences
                ListPreference listPref = (ListPreference) preference;
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = prefs.edit();
                // Finner posisjon verdien til den nye liste-verdien
                int verdi = listPref.findIndexOfValue((String) newValue);
                // Putter verdien inn i preferences
                editor.putInt("aarstall_liste_verdi", verdi);
                editor.apply();
                listPref.getValue();
                return true;
            });
        }
    }
}
