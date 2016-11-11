package com.dmtaiwan.alexander.beirecipes.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alexander on 11/10/2016.
 */

public class Direction implements Parcelable {

    public Direction() {
    }

    public static Direction newDirection(String description) {
        Direction direction = new Direction();
        direction.setDescription(description);
        return direction;
    }

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    protected Direction(Parcel in) {
        description = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Direction> CREATOR = new Parcelable.Creator<Direction>() {
        @Override
        public Direction createFromParcel(Parcel in) {
            return new Direction(in);
        }

        @Override
        public Direction[] newArray(int size) {
            return new Direction[size];
        }
    };
}