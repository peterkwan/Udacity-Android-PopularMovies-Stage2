package org.peterkwan.udacity.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class MovieTrailer implements Parcelable {

    private long id;
    private String name;
    private String type;
    private String videoUrl;
    private String imageUrl;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(videoUrl);
        dest.writeString(imageUrl);
    }

    public static final MovieTrailer.Creator<MovieTrailer> CREATOR = new MovieTrailer.Creator<MovieTrailer>() {

        @Override
        public MovieTrailer[] newArray(int size) {
            return new MovieTrailer[size];
        }

        @Override
        public MovieTrailer createFromParcel(Parcel source) {
            return new MovieTrailer(source);
        }
    };

    private MovieTrailer(Parcel parcel) {
        id = parcel.readLong();
        name = parcel.readString();
        type = parcel.readString();
        videoUrl = parcel.readString();
        imageUrl = parcel.readString();
    }
}
