package com.example.lize.adapters;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.lize.data.Ambito;
import com.example.lize.data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
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
        //initFirebase();
    }

    public interface vmInterface {
        void setCollection(User ac);

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

    public void getCollection(String userID) {
        Log.d(TAG, "update user " + userID);
        DatabaseAdapter.db.collection("users/" + userID + "/ambitos").get()
                // OnCompleteListener for the get() process: setCollection of the retrieved Data
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            ArrayList<Ambito> retrieved_ambitos = new ArrayList<Ambito>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                // Get the notes subcollection for each document
                                db.collection("users/" + userID + "/ambitos/" + document.getId() + "/notes").get();
                            }
                            listener.setCollection(new User(userID, "0000000", "Pep", "Guardiola"));
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /* TODO: Métodos para guardar Ámbitos, Carpetas y Notas, respectivamente */

    public void saveAmbito(String id, String description, String userid, String url) {
    }

        /* Create a new user with a first and last name
        Map<String, Object> note = new HashMap<>();
        note.put("id", id);
        note.put("description", description);
        note.put("userid", userid);
        note.put("url", url);

        Log.d(TAG, "saveDocument");
        // Add a new document with a generated ID
        db.collection("audioCards")
                .add(note)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
       */


    public void saveFolder(String id, String description, String userid, String url) {
    }

        /* Create a new user with a first and last name
        Map<String, Object> note = new HashMap<>();
        note.put("id", id);
        note.put("description", description);
        note.put("userid", userid);
        note.put("url", url);

        Log.d(TAG, "saveDocument");
        // Add a new document with a generated ID
        db.collection("audioCards")
                .add(note)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
       */


    public void saveNote(String id, String description, String userid, String url) {
    }

        /* Create a new user with a first and last name
        Map<String, Object> note = new HashMap<>();
        note.put("id", id);
        note.put("description", description);
        note.put("userid", userid);
        note.put("url", url);

        Log.d(TAG, "saveDocument");
        // Add a new document with a generated ID
        db.collection("audioCards")
                .add(note)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
       */

    public void saveNoteWithFile(String id, String description, String userid, String path) {
    }

        /*
        Uri file = Uri.fromFile(new File(path));
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