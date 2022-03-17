package com.example.homeautomationcomplete.ui.electricity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ElectricityMonitorViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ElectricityMonitorViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Electricity Monitor fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}