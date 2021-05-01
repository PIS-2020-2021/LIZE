package com.example.lize.adapters;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.lize.data.Ambito;
import com.example.lize.data.Note;
import com.example.lize.data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.concurrent.Executor;


public class DatabaseAdapter {
    public static final String TAG = "DatabaseAdapter";

    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user;

    public static DatabaseAdapter databaseAdapter;  // Singleton implementation
    public static vmInterface listener;

    public DatabaseAdapter(vmInterface listener) {
        this.listener = listener;
        databaseAdapter = this;
        FirebaseFirestore.setLoggingEnabled(true);
        initFirebase();
    }

    public interface vmInterface {
        void setUser(User user);
        void setUserAmbitos(ArrayList<Ambito> userAmbitos);
        void setAmbitoNotes(String ambitoID, ArrayList<Note> ambitoNotes);
        void setToast(String s);
    }

    /* Firebase sign in */
    public void initFirebase() {
        user = mAuth.getCurrentUser();
        if (user == null) {
            mAuth.signInAnonymously()
                    // OnCompleteListener: when sign in, Authentication successful
                    .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInAnonymously:success");
                                listener.setToast("Authentication successful.");
                                user = mAuth.getCurrentUser();
                                //getUser(user.getEmail());
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInAnonymously:failure", task.getException());
                                listener.setToast("Authentication failed.");

                            }
                        }
                    });
        } else {
            listener.setToast("Authentication with current user.");
        }
    }

    // Method for getting a base User from the 'users' collection
    public void getUser() {
        DocumentReference userRef = DatabaseAdapter.db.collection("users").document(user.getEmail());
        Log.d(TAG, "Getting current user document...");
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Log.d(TAG, document.getId() + " => " + document.getData());
                    listener.setUser(document.toObject(User.class));
                } else Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    // Method for getting an Ambito from the 'ambitos' collection
    public void getAmbitos() {
        Query ambitosRef = DatabaseAdapter.db.collection("ambitos").whereEqualTo("userID", user.getEmail());
        Log.d(TAG, "Getting current user ambitos collection...");
        ambitosRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Ambito> userAmbitos = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        userAmbitos.add(document.toObject(Ambito.class));
                    }
                    listener.setUserAmbitos(userAmbitos);
                } else Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    // Method for getting a Note from the 'notes' collection
    public void getNotes(String ambitoID){
        Query notasRef = DatabaseAdapter.db.collection("notes").whereEqualTo("ambitoID", ambitoID);
        Log.d(TAG, "Getting ambito " + ambitoID + "'s notes collection...");
        notasRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Note> ambitoNotes = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        ambitoNotes.add(document.toObject(Note.class));
                    }
                    listener.setAmbitoNotes(ambitoID, ambitoNotes);
                } else Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    public void saveAmbito(Ambito ambito) {
        DocumentReference ambitoRef = db.collection("ambitos").document();
        ambito.setSelfID(ambitoRef.getId());
        Log.d(TAG, "Saving ambito with ID: " + ambitoRef.getId());
        ambitoRef.set(ambito).addOnCompleteListener(new OnCompleteListener(){
            @Override
            public void onComplete(@NonNull Task task) {
                DocumentSnapshot document = (DocumentSnapshot) task.getResult();
                if (task.isSuccessful()) Log.d(TAG, "Ambito" + document.getId() + " correctly saved.");
                else Log.d(TAG, "Error saving ambito " + document.getId(), task.getException());
            }
        });
    }

    public void saveNote(Note note) {
        DocumentReference noteRef = db.collection("notes").document();
        note.setSelfID(noteRef.getId());
        Log.d(TAG, "Saving note with ID: " + noteRef.getId());
        noteRef.set(note).addOnCompleteListener(new OnCompleteListener(){
            @Override
            public void onComplete(@NonNull Task task) {
                DocumentSnapshot document = (DocumentSnapshot) task.getResult();
                if (task.isSuccessful()) Log.d(TAG, "Note" + document.getId() + " correctly saved.");
                else Log.d(TAG, "Error saving note " + document.getId(), task.getException());
            }
        });
    }



    public void saveNoteWithFile(String id, String description, String userid, String path) {
    /*  Uri file = Uri.fromFile(new File(path));
        StorageReference storageRef = storage.getReference();
        StorageReference audioRef = storageRef.child("audio"+File.separator+file.getLastPathSegment());
        UploadTask uploadTask = audioRef.putFile(file);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return audioRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    saveDocument(id, description, userid, downloadUri.toString());
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d(TAG, "Upload is " + progress + "% done");
            }
        }); */
    }

}