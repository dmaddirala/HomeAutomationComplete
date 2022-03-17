package com.example.homeautomationcomplete.ui.electricity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.homeautomationcomplete.databinding.FragmentElectricityMonitorBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ElectricityMonitorFragment extends Fragment {

    private ElectricityMonitorViewModel electricityMonitorViewModel;
    private FragmentElectricityMonitorBinding binding;

    private Button light;
    private Button fan;
    private SeekBar seekBar;

    private String url = "http://192.168.0.0";

    boolean FAN_FLAG = false;
    boolean LIGHT_FLAG = false;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("test");

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String URL = "URL";

    private String totalPower = "";
    private String currentPower = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        electricityMonitorViewModel =
                new ViewModelProvider(this).get(ElectricityMonitorViewModel.class);

        binding = FragmentElectricityMonitorBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        electricityMonitorViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        myRef.child("TotalPower").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String temp = dataSnapshot.getValue().toString();
                    totalPower = temp;
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
                    String temp = dataSnapshot.getValue().toString();
                    currentPower = temp;
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