package com.example.apputvikling;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

public class InnstillingerAktivitet extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_innstillinger);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.innstillinger, new InnstillingerFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

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
            aarstall.setOnPreferenceChangeListener((preference, newValue) -> {
                ListPreference listPref = (ListPreference) preference;
                listPref.getValue();
                return true;
            });
        }
    }
}
