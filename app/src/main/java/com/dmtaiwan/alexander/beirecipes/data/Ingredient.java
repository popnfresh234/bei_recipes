package com.dmtaiwan.alexander.beirecipes.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alexander on 10/29/2016.
 */

public class Ingredient implements Parcelable {

    public Ingredient() {

    }
    private String name;
    private double count;
    private String unit;
    private String fraction;
    private double proportionalCount;

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getFraction() {
        return fraction;
    }

    public void setFraction(String fraction) {
        this.fraction = fraction;
    }

    public double getProportionalCount() {
        return proportionalCount;
    }

    public void setProportionalCount(double proportionalCount) {
        this.proportionalCount = proportionalCount;
    }



    protected Ingredient(Parcel in) {
        name = in.readString();
        count = in.readDouble();
        unit = in.readString();
        fraction = in.readString();
        proportionalCount = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeDouble(count);
        dest.writeString(unit);
        dest.writeString(fraction);
        dest.writeDouble(proportionalCount);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Ingredient> CREATOR = new Parcelable.Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };
}