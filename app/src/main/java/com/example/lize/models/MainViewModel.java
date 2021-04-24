package com.example.lize.models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lize.adapters.DatabaseAdapter;
import com.example.lize.data.Ambito;
import com.example.lize.data.Folder;
import com.example.lize.data.Note;
import com.example.lize.data.User;

public class MainViewModel extends ViewModel implements DatabaseAdapter.vmInterface {

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
        DatabaseAdapter da = new DatabaseAdapter(this);
        da.getCollection("user_002@gmail.com");
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

    /*public void selectUser(String userID, String password, String first, String last) {
        mUserSelected.setValue(new User(userID, password, first, last));
        mToast.setValue("User " + userID + " selected.");
    }*/

    public void selectAmbito(String ambitoID) {
        if (mUserSelected.getValue() != null) {
            for (Ambito ambito : mUserSelected.getValue().getUserAmbitos()) {
                if (ambito.getAmbitoName().equals(ambitoID)) {
                    mAmbitoSelected.setValue(ambito);
                    mToast.setValue("Ambito " + ambitoID + " selected.");
                    return;
                }
            }
            mToast.setValue("Failed to select ambito " + ambitoID + ".");
        } else mToast.setValue("Ambito list unitiallized. Invalid Operation");
    }

    public void selectFolder(String folderID) {
        if (mAmbitoSelected != null) {
            for (Folder folder : mAmbitoSelected.getValue().getAmbitoFolders()) {
                if (folder.getFolderName().equals(folderID)) {
                    mFolderSelected.setValue(folder);
                    mToast.setValue("Folder " + folderID + " selected.");
                    return;
                }
            }
            mToast.setValue("Failed to select folder " + folderID + ".");
        } else mToast.setValue("Ambito unitiallized. Invalid Operation");
    }

    public void selectNote(String noteID) {
        if (mNoteSelected != null) {
            for (Note note : mFolderSelected.getValue().getFolderNotes()) {
                if (note.getID().equals(noteID)) {
                    // TODO: Note Selection implementation (call DB?)
                    mToast.setValue("Note " + noteID + " selected.");
                    return;
                }
            }
            mToast.setValue("Failed to select note " + noteID + ".");
        } else mToast.setValue("Folder unitiallized. Invalid Operation");
    }

    public void addAmbito(String ambitoName) {
        Ambito ambito = new Ambito(ambitoName);
        mUserSelected.getValue().addAmbito(ambito);
        mUserSelected.setValue(mUserSelected.getValue());
        // TODO: note database connection: ambito.saveAmbito();
    }

    public void addFolder(String folderName) {
        Folder folder = new Folder(folderName);
        mAmbitoSelected.getValue().addFolder(folder);
        mAmbitoSelected.setValue(mAmbitoSelected.getValue());
        // TODO: note database connection: folder.saveFolder();
    }

    public void addNote(String noteName, String noteText) {
        Note note = new Note(noteName, noteText);
        mFolderSelected.getValue().addNote(note);
        mFolderSelected.setValue(mFolderSelected.getValue());
        // TODO: note database connection: note.saveNote();
    }

    /** TODO: Métodos para editar la Nota desde la NoteActivity! + DataBase connection: note.saveNote()
     public void editNoteText(String noteText);
     public void addImageOnNote(String imageURL);
     public void addDocumentOnNote(String documentURL);
     public void addAudioOnNote(String audioURL);
     * @param user*/

    /* DATABASE INTERFACE IMPLEMENTATION */
    @Override
    public void setCollection(User user) {
        mUserSelected.setValue(user);
    }

    @Override
    public void setToast(String t) {
        mToast.setValue(t);
    }

}
