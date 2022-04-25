package com.example.homeautomationcomplete.ui.plant;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.content.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.homeautomationcomplete.PlantTimer;
import com.example.homeautomationcomplete.R;
import com.example.homeautomationcomplete.databinding.FragmentPlantWateringBinding;
import com.example.homeautomationcomplete.TimerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PlantWateringFragment extends Fragment {

    private PlantWateringViewModel plantWateringViewModel;
    private FragmentPlantWateringBinding binding;

    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    private ListView listView;
    private TextView plantTxt;
    private TextView timerTxt;
    private FloatingActionButton addTimerbtn;
    private ImageButton waterBtn;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private TextView dialogMessage;
    private Dialog dialogDelete;
    private Button yesBtn, noBtn;

    private ArrayList<PlantTimer> plantTimers;
    private TimerAdapter adapter;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("test");

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String URL = "URL";
    private String url = "http://192.168.0.0";
    private String dateTime = "";
    String finalDate = "";
    String finalTime = "";
    private int month = 0;
    private int lastTimerIndex = 0;
    private String deletedSrNumber;
    private boolean isWateringOn = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        plantWateringViewModel =
                new ViewModelProvider(this).get(PlantWateringViewModel.class);

        binding = FragmentPlantWateringBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        plantWateringViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        plantTxt = view.findViewById(R.id.tv_plant);
        timerTxt = view.findViewById(R.id.tv_timer);
        waterBtn = view.findViewById(R.id.btn_plant);
        addTimerbtn = view.findViewById(R.id.btn_add_timer);
        listView = view.findViewById(R.id.list_view);

        dialogDelete = new Dialog(getActivity());
        dialogDelete.setContentView(R.layout.dialog_delete);
        dialogDelete.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.background));
        dialogDelete.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogDelete.setCancelable(true);
        dialogDelete.getWindow().getAttributes().windowAnimations = R.style.animation;

        yesBtn = dialogDelete.findViewById(R.id.btn_yes);
        noBtn = dialogDelete.findViewById(R.id.btn_no);
        dialogMessage = dialogDelete.findViewById(R.id.tv_message);

        plantTimers = new ArrayList<>();
        adapter = new TimerAdapter(getActivity(), plantTimers);
        listView.setAdapter(adapter);

        initDateTimePicker();
        incomingCalls();

        waterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isWateringOn) {
                    myRef.child("Plant").setValue(0);
                    plantTxt.setText("Start");
                } else {
                    myRef.child("Plant").setValue(1);
                    plantTxt.setText("Stop");
                }

                isWateringOn = !isWateringOn;
                Toast.makeText(getActivity(), "Plant", Toast.LENGTH_SHORT).show();
            }
        });

        addTimerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                deletedSrNumber = plantTimers.get(position).getSrNumber();
                dialogDelete.show();
                return true;
            }
        });

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myRef.child("Timers").child(deletedSrNumber).get().toString() != null){
                    myRef.child("Timers").child(deletedSrNumber).removeValue();
                }
                dialogDelete.dismiss();
            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDelete.dismiss();
            }
        });
    }

    private void incomingCalls() {
        myRef.child("Water").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String temp = dataSnapshot.getValue().toString();
                    if (temp.equals("1")) {
                        isWateringOn = true;
                        plantTxt.setText("Stop");
                    } else {
                        isWateringOn = false;
                        plantTxt.setText("Start");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        myRef.child("Timers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    plantTimers.clear();
                    lastTimerIndex = 0;
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot item : dataSnapshot.getChildren()) {
                            String srNumber = item.getKey();
                            String oldDate = item.getValue().toString();
                            Date fullDate = new SimpleDateFormat("dd-mm-yy HH:mm:ss").parse(oldDate);
                            String time = new SimpleDateFormat("hh.mm aa").format(fullDate);
                            String date = new SimpleDateFormat("E, dd MMMM").format(fullDate);
                            plantTimers.add(new PlantTimer(time, date, srNumber));
                            int srNum = Integer.parseInt(srNumber);
                            lastTimerIndex = (srNum > lastTimerIndex) ? srNum : lastTimerIndex;
                        }
                        adapter.notifyDataSetChanged();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void initDateTimePicker() {
        Calendar calendar = Calendar.getInstance();
        Calendar selectedCalendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int style = AlertDialog.THEME_HOLO_LIGHT;

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        boolean is24HourFormat = true;

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month += 1;
                year = year % 100;
                dateTime = addLeadingZeors(day) + "-" + addLeadingZeors(month) + "-" + addLeadingZeors(year);
                selectedCalendar.set(Calendar.YEAR, year);
                selectedCalendar.set(Calendar.MONTH, month);
                selectedCalendar.set(Calendar.DAY_OF_MONTH, day);
                SimpleDateFormat formatter = new SimpleDateFormat("E, dd MMMM");
                finalDate = formatter.format(selectedCalendar.getTime());
                timePickerDialog.show();
            }
        };

        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                selectedCalendar.set(Calendar.HOUR, hour);
                selectedCalendar.set(Calendar.MINUTE, minute);
                dateTime += " " + addLeadingZeors(hour) + ":" + addLeadingZeors(minute) + ":00";
                SimpleDateFormat formatter = new SimpleDateFormat("hh.mm aa");
                finalTime = formatter.format(selectedCalendar.getTime());
                addTimer();
            }
        };

        timePickerDialog = new TimePickerDialog(getActivity(), style, timeSetListener, hour, minute, is24HourFormat);
        datePickerDialog = new DatePickerDialog(getActivity(), style, dateSetListener, year, month, day);
    }

    private void addTimer() {
        String srNumber = String.valueOf(lastTimerIndex + 1);
        plantTimers.add(new PlantTimer(finalTime, finalDate, srNumber));
        adapter.notifyDataSetChanged();
        myRef.child("Timers").child(srNumber).setValue(dateTime);
    }

    private String addLeadingZeors(int number) {
        String num = Integer.toString(number);
        return num.length() == 1 ? ("0" + num) : num;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}