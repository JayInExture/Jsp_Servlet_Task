package model;


public class UserImage {
    private int id;
    private byte[] imageData;


    public UserImage(){

    }

    public UserImage(int id, byte[] imageData) {
        this.id = id;
        this.imageData = imageData;
    }

    public void setImgId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }
}