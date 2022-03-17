package com.example.homeautomationcomplete.ui.plant;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PlantWateringViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PlantWateringViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Plant Watering fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}