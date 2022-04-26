package com.example.homeautomationcomplete.ui.readings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.homeautomationcomplete.R;
import com.example.homeautomationcomplete.databinding.FragmentReadingsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReadingsFragment extends Fragment {

    private ReadingsViewModel readingsViewModel;
    private FragmentReadingsBinding binding;

    private TextView currentPowerTxt;
    private TextView totalPowerTxt;
    private TextView temperatureTxt;
    private TextView humidityTxt;


    private Button light;
    private Button fan;
    private SeekBar seekBar;

    private String url = "http://192.168.0.0";

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("test");

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String URL = "URL";

    private String totalPower = "";
    private String currentPower = "";
    private String temperature = "";
    private String humidity = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        readingsViewModel =
                new ViewModelProvider(this).get(ReadingsViewModel.class);

        binding = FragmentReadingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        readingsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        totalPowerTxt = view.findViewById(R.id.tv_total_power);
        currentPowerTxt = view.findViewById(R.id.tv_current_power);
        temperatureTxt = view.findViewById(R.id.tv_temperature);
        humidityTxt = view.findViewById(R.id.tv_humidity);

        myRef.child("TotalPower").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    totalPower = dataSnapshot.getValue().toString();
                    totalPowerTxt.setText(totalPower + " W");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        myRef.child("CurrentPower").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentPower = dataSnapshot.getValue().toString();
                    currentPowerTxt.setText(currentPower + " W");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        myRef.child("Temperature").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    temperature = dataSnapshot.getValue().toString();
                    temperatureTxt.setText(temperature + " ºC");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        myRef.child("Humidity").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    humidity = dataSnapshot.getValue().toString();
                    humidityTxt.setText(humidity + " %");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}