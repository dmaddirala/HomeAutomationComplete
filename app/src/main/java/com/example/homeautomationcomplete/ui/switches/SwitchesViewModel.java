package com.example.homeautomationcomplete.ui.switches;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SwitchesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SwitchesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Switches fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}