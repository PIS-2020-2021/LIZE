package com.example.lize.models;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lize.adapters.DatabaseAdapter;
import com.example.lize.data.Ambito;
import com.example.lize.data.Folder;
import com.example.lize.data.Note;
import com.example.lize.data.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainViewModel extends ViewModel {

    private static final String TAG = "MainViewModel";

    private final MutableLiveData<User> mUserSelected;
    private final MutableLiveData<Ambito> mAmbitoSelected;
    private final MutableLiveData<Folder> mFolderSelected;
    private final MutableLiveData<Note> mNoteSelected;
    private final MutableLiveData<String> mToast;

    public MainViewModel() {
        mUserSelected = new MutableLiveData<>();
        mAmbitoSelected = new MutableLiveData<>();
        mFolderSelected = new MutableLiveData<>();
        mNoteSelected = new MutableLiveData<>();
        mToast = new MutableLiveData<>();
        DatabaseAdapter da = new DatabaseAdapter(new UserBuilder());
        da.getUser();           // Build User process
    }

    public MutableLiveData<User> getUserSelected() { return mUserSelected; }

    public MutableLiveData<Ambito> getAmbitoSelected() {
        return mAmbitoSelected;
    }

    public MutableLiveData<Folder> getFolderSelected() {
        return mFolderSelected;
    }

    public MutableLiveData<Note> getNoteSelected() {
        return mNoteSelected;
    }

    public MutableLiveData<String> getToast() {
        return mToast;
    }

    /* Selects the application user mUserSelected*/
    public void selectUser(User user){
        setToast("User " + user.getMail() + " selected.");
        mUserSelected.setValue(user);
        selectAmbito(Ambito.BASE_AMBITO_NAME);
    }

    /* Selects an Ambito of the current user mUserSelected */
    public void selectAmbito(String ambitoName) {
        try {
            for (Ambito ambito : mUserSelected.getValue().getAmbitos()) {
                if (ambito.getName().equals(ambitoName)) {
                    setToast("Ambito " + ambitoName + " selected.");
                    mAmbitoSelected.setValue(ambito);
                    selectFolder(Folder.BASE_FOLDER_NAME);
                    return;
                }
            }
            Log.w(TAG, "Failed to select ambito " + ambitoName + ": invalid ID.");

        }catch(NullPointerException exception){
            Log.w(TAG, "Failed to select ambito " + ambitoName + ": null pointer exception.");
            Log.w(TAG, "Exception message: " + exception.getMessage());
        }
    }

    /* Selects a Folder of the current Ambito mAmbitoSelected */
    public void selectFolder(String folderName) {
        try{
            for (Folder folder : mAmbitoSelected.getValue().getFolders()) {
                if (folder.getName().equals(folderName)) {
                    setToast("Folder " + folderName + " selected.");
                    mFolderSelected.setValue(folder);
                    return;
                }
            }
            Log.w(TAG, "Failed to select folder " + folderName + ": invalid ID.");

        }catch(NullPointerException exception){
            Log.w(TAG, "Failed to select folder " + folderName + ": null pointer exception.");
            Log.w(TAG, "Exception message: " + exception.getMessage());
        }
    }

    /* Selects a Note of the current Folder mFolderSelected. */
    public void selectNote(String noteID) {
        try{
            for (Note note : mFolderSelected.getValue().getNotes()) {
                if (note.getSelfID().equals(noteID)) {
                    setToast("Note " + noteID + " selected.");
                    mNoteSelected.setValue(note);
                    return;
                }
            }
            Log.w(TAG, "Failed to select note " + noteID + ": invalid ID.");

        }catch(NullPointerException exception){
            Log.w(TAG, "Failed to select note " + noteID + ": null pointer exception.");
            Log.w(TAG, "Exception message: " + exception.getMessage());
        }
    }

    public void addAmbito(String ambitoName, int ambitoColor) {
        for (Ambito ambito: mUserSelected.getValue().getAmbitos())
            if (ambito.getName().equals(ambitoName)) {
                Log.w(TAG, "Failed to create ambito " + ambitoName + ": ambito already exists. ");
                return;
            }
        Ambito newAmbito = new Ambito(ambitoName, ambitoColor, mUserSelected.getValue().getSelfID());
        DatabaseAdapter.databaseAdapter.saveAmbito(newAmbito);
        newAmbito.addFolder(new Folder(Folder.BASE_FOLDER_NAME, newAmbito.getSelfID()));
        mUserSelected.getValue().addAmbito(newAmbito);
        mUserSelected.setValue(mUserSelected.getValue());
        setToast("Ambito " + ambitoName + " correctly created.");
        mAmbitoSelected.setValue(newAmbito);
    }

    public void addFolder(String folderName) {
        for (Folder folder: mAmbitoSelected.getValue().getFolders())
            if (folder.getName().equals(folderName)) {
                Log.w(TAG, "Failed to create folder " + folderName + ": folder already exists. ");
                return;
            }
        Folder folder = new Folder(folderName, mAmbitoSelected.getValue().getSelfID());
        mAmbitoSelected.getValue().addFolder(folder);
        mAmbitoSelected.setValue(mAmbitoSelected.getValue());
        setToast("Folder " + folderName + " correctly created.");
    }

    public void addNote(String noteName, String noteText) {
        if (noteText == null) {
            Log.w(TAG, "Failed to create note " + noteName + ": empty note. ");
            return;
        }
        Note newNote = new Note(noteName, noteText, mAmbitoSelected.getValue().getSelfID(), mFolderSelected.getValue().getName());
        DatabaseAdapter.databaseAdapter.saveNote(newNote);
        mFolderSelected.getValue().addNote(newNote);
        mFolderSelected.setValue(mFolderSelected.getValue());
        setToast("Note " + noteName + " correctly created.");
    }

    public void setToast(String s) {
        Log.w(TAG, s);
        mToast.setValue(s);
    }

    /** TODO: Métodos para editar la Nota desde la NoteActivity! + DataBase connection: note.saveNote()
     public void editNoteText(String noteText);
     public void addImageOnNote(String imageURL);
     public void addDocumentOnNote(String documentURL);
     public void addAudioOnNote(String audioURL); */


    /* BUILDER PATTERN FOR DATABASE INTERFACE */
    protected class UserBuilder implements DatabaseAdapter.vmInterface{

        private User currentUser;
        private int loadingCounter;

        public UserBuilder(){
            Log.w("UserBuilder", "Beginning user building process...");
            currentUser = null;
            loadingCounter = 0;
        }

        @Override
        public void setUser(User user) {
            currentUser = user;
            Log.w("UserBuilder", "Step 1 succes: user correctly loaded from Database.");
            DatabaseAdapter.databaseAdapter.getAmbitos();
        }

        @Override
        public void setUserAmbitos(ArrayList<Ambito> userAmbitos) {
            if (currentUser != null) {
                currentUser.setAmbitos(userAmbitos);
                Log.w("UserBuilder", "Step 2 succes: ambitos of user " + currentUser.getSelfID() + " correctly loaded from Database.");
                for (Ambito ambito : userAmbitos) {
                    DatabaseAdapter.databaseAdapter.getNotes(ambito.getSelfID());
                }
            } else Log.w("UserBuilder", "Step 2 failure. User " + currentUser.getSelfID() + " it's unitiallized.");
        }

        @Override
        public void setAmbitoNotes(String ambitoID, ArrayList<Note> ambitoNotes) {
            if (currentUser.getAmbitos().isEmpty())
                Log.w("UserBuilder", "Step 3 failure: Ambito " + ambitoID + " it's unitiallized.");
            else{
                for (Ambito ambito : currentUser.getAmbitos()) {
                    if (ambito.getSelfID().equals(ambitoID)) {

                        // Build the ambito's folders using a Map collection for avoiding repeated names
                        Map<String, Folder> ambitoFolders = new HashMap<>();
                        ambitoFolders.put(Folder.BASE_FOLDER_NAME, new Folder(Folder.BASE_FOLDER_NAME, ambitoID));
                        for (Note note : ambitoNotes) {
                            String folderTAG = note.getFolderTAG();
                            if (ambitoFolders.get(folderTAG) == null)
                                ambitoFolders.put(folderTAG, new Folder(folderTAG, ambitoID));

                            ambitoFolders.get(Folder.BASE_FOLDER_NAME).addNote(note);
                            ambitoFolders.get(folderTAG).addNote(note);
                        }

                        // Finally, sets the ambito notes structured by their folder containers
                        ambito.setFolders(new ArrayList(ambitoFolders.values()));
                        loadingCounter++;
                        break;
                    }
                }

                // If notes for all user ambitos have been set, call the owner class for setting the user
                if (loadingCounter == currentUser.getAmbitos().size()) {
                    Log.w("UserBuilder", "Step 3 succes: all notes of user " + currentUser.getSelfID() + " correctly loaded from Database.");
                    selectUser(currentUser);
                }
            }
        }

        @Override
        public void setToast(String s) {
            MainViewModel.this.setToast(s);
        }
    }
}
