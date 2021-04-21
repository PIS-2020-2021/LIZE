package com.example.lize.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lize.data.Folder;
import com.example.lize.data.Note;

/* FolderViewModel. Modeliza el Folder seleccionado en el FolderHostFragment. */
public class FolderViewModel extends ViewModel{ //implements DatabaseAdapter.vmInterface{

    private final MutableLiveData<Folder> mFolderSelected;
    private final MutableLiveData<String> mToast;

    public FolderViewModel() {
        mFolderSelected = new MutableLiveData<>();
        mToast = new MutableLiveData<>();
    }

    public LiveData<Folder> getFolderSelected(){
        return mFolderSelected;
    }

    public void setFolderSelected(Folder folderSelected){
        mFolderSelected.setValue(folderSelected);
    }

    public void addNote(String noteName){
        Note note = new Note(noteName, "");
        mFolderSelected.getValue().addNote(note);
        mFolderSelected.setValue(mFolderSelected.getValue());
        // TODO: note database connection: note.saveNote();
    }

    public LiveData<String> getToast(){
        return mToast;
    }

    /* communicates user inputs and updates the result in the viewModel
    @Override
    public void setCollection(ArrayList<Folder> folders) {
        mFolderList.setValue(folders);
    }
    public void setToast(String t) {
        mToast.setValue(t);
    }
    */

    /*
        mFolderList = new MutableLiveData<>();
        mToast = new MutableLiveData<>();
        //DatabaseAdapter da= new DatabaseAdapter(this);
        //da.getCollection();
        initializeData();
    }

    Método para inicializar los DataSets a partir de los MOCKUPS definidos en strings.xml
    private void initializeData() {
        //Get the resources from the XML file
        String[] notesNames = getResources().getStringArray(R.array.notes_names);
        String[] notesBody = getResources().getStringArray(R.array.notes_body);
        Folder general = new Folder("General");
        Folder folder1 = new Folder("Folder1");
        Folder folder2 = new Folder("Folder2");

        //Create the ArrayList of Notes objects with the titles and text data
        for(int i=0; i < notesNames.length; i++){
            Note newNote = new Note(notesNames[i], notesBody[i]);
            general.add(newNote);
            if(i < 5) folder1.add(newNote);
            else folder2.add(newNote);
        }

        //Create the ArrayList of Folder objects with their names and notes
        ArrayList<Folder> folders = new ArrayList<>();
        mFoldersData.add(folder1);
        mFoldersData.add(folder2);
        mFoldersData.add(folder2);
        mFoldersData.add(folder2);

        //Notify the adapters of the changes
        mFolderAdapter.notifyDataSetChanged();
    }

    /*public Folder getFolder(int idx){
        return mFolderList.getValue().get(idx);
    }**/
}
