package uk.co.zac_h.message.mmssms;

import android.graphics.Bitmap;

public class Message {

    public static final int TYPE_SMSMMS = 0;
    public static final int TYPE_VOICE = 1;

    private String text;
    private String subject;
    private String[] addresses;
    private Bitmap[] images;
    private String[] imageNames;
    private byte[] media;
    private String mediaMimeType;
    private boolean save;
    private int type;
    private int delay;

    public Message() {
        this("", new String[]{""});
    }

    public Message(String text, String address) {
        this(text, address.trim().split(" "));
    }

    public Message(String text, String[] addresses) {
        this.text = text;
        this.addresses = addresses;
        this.images = new Bitmap[0];
        this.subject = null;
        this.media = new byte[0];
        this.mediaMimeType = null;
        this.save = true;
        this.type = TYPE_SMSMMS;
        this.delay = 0;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setAddresses(String[] addresses) {
        this.addresses = addresses;
    }

    public void setImages(Bitmap[] images) {
        this.images = images;
    }

    public void setImageNames(String[] imageNames) {
        this.imageNames = imageNames;
    }

    public void setImage(Bitmap image) {
        this.images = new Bitmap[1];
        this.images[0] = image;
    }

    public void setAudio(byte[] audio) {
        this.media = audio;
        this.mediaMimeType = "audio/wav";
    }

    public void setVideo(byte[] video) {
        this.media = video;
        this.mediaMimeType = "video/3gpp";
    }

    public void setMedia(byte[] media, String mimeType) {
        this.media = media;
        this.mediaMimeType = mimeType;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setSave(boolean save) {
        this.save = save;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public String[] getAddresses() {
        return addresses;
    }

    public Bitmap[] getImages() {
        return images;
    }

    public String[] getImageNames() {
        return imageNames;
    }

    public byte[] getMedia() {
        return media;
    }

    public String getMediaMimeType() {
        return mediaMimeType;
    }

    public String getSubject() {
        return subject;
    }

    public boolean getSave() {
        return this.save;
    }

    public int getType() {
        return type;
    }

    public int getDelay() {
        return delay;
    }
}
