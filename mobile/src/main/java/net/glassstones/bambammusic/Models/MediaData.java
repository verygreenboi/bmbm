package net.glassstones.bambammusic.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("unused")
public class MediaData implements Parcelable {

    public static final Creator<MediaData> CREATOR = new Creator<MediaData>() {
        @Override
        public MediaData createFromParcel(Parcel in) {
            return new MediaData(in);
        }

        @Override
        public MediaData[] newArray(int size) {
            return new MediaData[size];
        }
    };
    String title, artist, duration, desc, path, artPath;
    boolean checked, forSale;
    int mediaType;
    Uri imageUri;

    public MediaData() {
    }

    protected MediaData(Parcel in) {
        title = in.readString();
        artist = in.readString();
        duration = in.readString();
        desc = in.readString();
        path = in.readString();
        artPath = in.readString();
        checked = in.readByte() != 0;
        forSale = in.readByte() != 0;
        mediaType = in.readInt();
        imageUri = Uri.parse(in.readString());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getArtPath() {
        return artPath;
    }

    public void setArtPath(String artPath) {
        this.artPath = artPath;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isForSale() {
        return forSale;
    }

    public void setForSale(boolean forSale) {
        this.forSale = forSale;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(duration);
        dest.writeString(desc);
        dest.writeString(path);
        dest.writeString(artPath);
        dest.writeByte((byte) (checked ? 1 : 0));
        dest.writeByte((byte) (forSale ? 1 : 0));
        dest.writeInt(mediaType);
        dest.writeString(String.valueOf(imageUri));
    }
}
