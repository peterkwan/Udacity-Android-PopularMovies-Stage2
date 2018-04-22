package org.peterkwan.udacity.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class MovieReview implements Parcelable {

    private long id;
    private String author;
    private String content;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(author);
        dest.writeString(content);
    }

    public static final Parcelable.Creator<MovieReview> CREATOR = new Parcelable.Creator<MovieReview>() {

        @Override
        public MovieReview[] newArray(int size) {
            return new MovieReview[size];
        }

        @Override
        public MovieReview createFromParcel(Parcel source) {
            return new MovieReview(source);
        }
    };

    private MovieReview(Parcel parcel) {
        id = parcel.readLong();
        author = parcel.readString();
        content = parcel.readString();
    }
}
