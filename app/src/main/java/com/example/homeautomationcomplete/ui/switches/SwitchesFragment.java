package com.example.homeautomationcomplete.ui.switches;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.homeautomationcomplete.R;
import com.example.homeautomationcomplete.databinding.FragmentSwitchesBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

public class SwitchesFragment extends Fragment {

    private SwitchesViewModel switchesViewModel;
    private FragmentSwitchesBinding binding;

    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    private Button light;
    private Button fan;
    private ImageButton lock;

    private boolean isFanOn = false;
    private boolean isLightOn = false;
    private boolean isLockOpen = true;
    private boolean isFingerprintAvailable = true;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("test");

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String URL = "URL";
    private String url = "http://192.168.0.0";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        switchesViewModel =
                new ViewModelProvider(this).get(SwitchesViewModel.class);

        binding = FragmentSwitchesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        switchesViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        light = view.findViewById(R.id.btn_light);
        fan = view.findViewById(R.id.btn_fan);
        lock = view.findViewById(R.id.btn_lock);
        incomingCalls();
        checkFingerprintSensor();
        fingerprintCallback();

        light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLightOn) {
                    myRef.child("Light").setValue(0);
                } else {
                    myRef.child("Light").setValue(1);
                }

                isLightOn = !isLightOn;
                Toast.makeText(getActivity(), "Light", Toast.LENGTH_SHORT).show();

            }
        });

        fan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFanOn) {
                    myRef.child("Fan").setValue(0);
                } else {
                    myRef.child("Fan").setValue(1);
                }

                isFanOn = !isFanOn;
                Toast.makeText(getActivity(), "Fan", Toast.LENGTH_SHORT).show();
            }
        });

        lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFingerprintAvailable) {
                    showLongToast("Fingerprint Scanner is not available on your device!");
                    return;
                }
                biometricPrompt.authenticate(promptInfo);
            }
        });


    }


    private void incomingCalls() {
        myRef.child("IP").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                url = "http://" + dataSnapshot.getValue(String.class);
                httpGetRequest(url);
                Toast.makeText(getActivity(), "IP Changed: " + url, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                httpGetRequest(url);
                Toast.makeText(getActivity(), "IP Changed Failed: ", Toast.LENGTH_SHORT).show();
            }
        });

        myRef.child("Fan").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String temp = dataSnapshot.getValue().toString();
                    isFanOn = temp.equals("1") ? true : false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        myRef.child("Light").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String temp = dataSnapshot.getValue().toString();
                    isLightOn = temp.equals("1") ? true : false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        myRef.child("Lock").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (!isFingerprintAvailable) {
//                    lock.setImageResource(R.drawable.lock_unsupported);
//                    return;
//                }
                if (dataSnapshot.exists()) {
                    String temp = dataSnapshot.getValue().toString();
                    isLockOpen = temp.equals("1") ? true : false;
                    lock.setImageResource(isLockOpen ? R.drawable.lock_closed : R.drawable.lock_open);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void checkFingerprintSensor() {
        BiometricManager biometricManager = androidx.biometric.BiometricManager.from(getActivity());
        switch (biometricManager.canAuthenticate()) {

            // this means we can use biometric sensor
            case BiometricManager.BIOMETRIC_SUCCESS:
                showShortToast("You can use the fingerprint");
                isFingerprintAvailable = true;
                break;

            // this means that the device doesn't have fingerprint sensor
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                showShortToast("This device doesnot have a fingerprint sensor");
                isFingerprintAvailable = false;
                break;

            // this means that biometric sensor is not available
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                showShortToast("The biometric sensor is currently unavailable");
                isFingerprintAvailable = false;
                break;

            // this means that the device doesn't contain your fingerprint
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                showShortToast("Your device doesn't have fingerprint saved,please check your security settings");
                isFingerprintAvailable = false;
                break;
        }
    }

    private void fingerprintCallback() {

        Executor executor = ContextCompat.getMainExecutor(getActivity());
        // this will give us result of AUTHENTICATION
        biometricPrompt = new BiometricPrompt(getActivity(), executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            // THIS METHOD IS CALLED WHEN AUTHENTICATION IS SUCCESS
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                isLockOpen = !isLockOpen;
                lock.setImageResource(isLockOpen ? R.drawable.lock_closed : R.drawable.lock_open);

                if (isLockOpen) {
                    myRef.child("Lock").setValue(1);
                    showShortToast("Lock Opened");
                } else {
                    myRef.child("Lock").setValue(0);
                    showShortToast("Lock Closed");
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("Door")
                .setDescription("Use your fingerprint to " + (isLockOpen ? "close" : "open") + " the lock")
                .setNegativeButtonText("Cancel")
                .build();
    }

    private void httpGetRequest(String url1) {
//        startLoading();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url1, response -> {
            //mTextView.setText(response);

//            stopLoading();
            if (response.equals("Hello World!!")) {
                Toast.makeText(getActivity(), "Ready..!", Toast.LENGTH_SHORT).show();
            } else if (response.equals("FAN")) {
                Toast.makeText(getActivity(), "FAN Switched", Toast.LENGTH_SHORT).show();
            } else if (response.equals("LIGHT")) {
                Toast.makeText(getActivity(), "LIGHT Switched", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "" + response, Toast.LENGTH_SHORT).show();
            }


        }, error -> {
//            stopLoading();
            Toast.makeText(getActivity(), "Change Your WiFi Network", Toast.LENGTH_LONG).show();

        });
        queue.add(stringRequest);
    }

    private void showShortToast(String message) {
        Toast.makeText(getActivity(), "" + message, Toast.LENGTH_SHORT).show();
    }

    private void showLongToast(String message) {
        Toast.makeText(getActivity(), "" + message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}