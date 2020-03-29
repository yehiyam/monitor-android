package com.example.android.camera2basic;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ResolutionViewModel extends ViewModel {

    private MutableLiveData<Integer> resolutionIndex;

    public ResolutionViewModel() {
        resolutionIndex = new MutableLiveData<>();
        resolutionIndex.setValue(0);
    }

    public MutableLiveData<Integer> getResolutionIndex() {
        return resolutionIndex;
    }

    public void setResolutionIndex(MutableLiveData<Integer> resolutionIndex) {
        this.resolutionIndex = resolutionIndex;
    }
}
