package thiovan.submission5.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tv_show_table")
public class TvShow implements Parcelable {

    @PrimaryKey
    @NonNull
    private String id;
    private String photo;
    private String name;
    private String description;

    public TvShow() {
    }

    private TvShow(Parcel in) {
        id = in.readString();
        photo = in.readString();
        name = in.readString();
        description = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(photo);
        dest.writeString(name);
        dest.writeString(description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TvShow> CREATOR = new Creator<TvShow>() {
        @Override
        public TvShow createFromParcel(Parcel in) {
            return new TvShow(in);
        }

        @Override
        public TvShow[] newArray(int size) {
            return new TvShow[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
