package com.example.lize.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lize.data.Note;

/* NoteViewModel. Modeliza el Note seleccionado en el NoteHostFragment. */
public class NoteViewModel extends ViewModel {
    private final MutableLiveData<Note> mNoteSelected;
    private final MutableLiveData<String> mToast;

    public NoteViewModel() {
        mNoteSelected = new MutableLiveData<>();
        mToast = new MutableLiveData<>();
    }

    public LiveData<Note> getNoteSelected(){
        return mNoteSelected;
    }

    public LiveData<String> getToast(){
        return mToast;
    }

    /** TODO: Métodos para editar la Nota desde la NoteActivity! + DataBase connection: note.saveNote()
    public void editNoteText(String noteText);
    public void addImageOnNote(String imageURL);
    public void addDocumentOnNote(String documentURL);
    public void addAudioOnNote(String audioURL); */




}
