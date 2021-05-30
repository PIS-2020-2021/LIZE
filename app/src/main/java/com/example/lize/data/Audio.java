package com.example.lize.data;

import java.util.UUID;

public class Audio {

    private String ID;
    private final String address;
    private long duration;

    // Description, uri, duration, format, owner
    public Audio(String localPath, long duration) {
        //this.noteId = id;
        this.address = localPath;
        UUID uuid = UUID.randomUUID();
        this.ID = uuid.toString();
        this.duration = duration;
    }
    public String getAddress () {
        return this.address;
    }

    private void setID(String id) {
        this.ID = id;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    /*    public void saveCard() {

        Log.d("saveCard", "saveCard-> saveDocument");
        adapter.saveDocumentWithFile(this.noteId, this.audioDesc, this.owner,this.address);
    }

    public AudioCard getCard() {
        // ask database and if true, return audioCard
        HashMap<String, String> hm = adapter.getDocuments();
        Boolean answer = false;
        if (hm != null) {
            AudioCard ac = new AudioCard(hm.get("description"), "", hm.get("owner"));
            ac.setNoteId(hm.get("noteid"));
            return ac;
        } else {
            return null;
        }
    }*/
}
