package org.peterkwan.udacity.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class Movie implements Parcelable {

    private long id;
    private String title;
    private String originalTitle;
    private String releaseDate;
    private String posterImagePath;
    private String backdropImagePath;
    private String plotSynopsis;
    private double userRating;
    private double popularity;
    private boolean isFavorite;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(id);
        parcel.writeString(title);
        parcel.writeString(originalTitle);
        parcel.writeString(releaseDate);
        parcel.writeString(posterImagePath);
        parcel.writeString(backdropImagePath);
        parcel.writeString(plotSynopsis);
        parcel.writeDouble(userRating);
        parcel.writeDouble(popularity);
        parcel.writeByte((byte)(isFavorite ? 1 : 0));
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }

        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }
    };

    private Movie(Parcel parcel) {
      id = parcel.readLong();
      title = parcel.readString();
      originalTitle = parcel.readString();
      releaseDate = parcel.readString();
      posterImagePath = parcel.readString();
      backdropImagePath = parcel.readString();
      plotSynopsis = parcel.readString();
      userRating = parcel.readDouble();
      popularity = parcel.readDouble();
      isFavorite = parcel.readByte() != 0;
    }
}
