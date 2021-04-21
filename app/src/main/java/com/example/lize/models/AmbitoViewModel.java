package com.example.lize.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lize.data.Ambito;
import com.example.lize.data.Folder;

/* AmbitoViewModel. Modeliza el Ambito seleccionado en el AmbitoHostFragment. */
public class AmbitoViewModel extends ViewModel {
    private final MutableLiveData<Ambito> mAmbitoSelected;
    private final MutableLiveData<String> mToast;

    public AmbitoViewModel() {
        mAmbitoSelected = new MutableLiveData<>();
        mToast = new MutableLiveData<>();
    }

    public LiveData<Ambito> getAmbitoSelected(){
        return mAmbitoSelected;
    }

    public void setAmbitoSelected(Ambito ambitoSelected){
        mAmbitoSelected.setValue(ambitoSelected);
    }

    public void addFolder(String folderName){
        Folder folder = new Folder(folderName);
        mAmbitoSelected.getValue().addFolder(folder);
        mAmbitoSelected.setValue(mAmbitoSelected.getValue());
        // TODO: ambito database connection: folder.saveFolder();
    }

    public LiveData<String> getToast(){
        return mToast;
    }
}
