package com.pdr.veigris;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Trip {
    private static String CoordinateSetSeparator = ";";
    private static String CoordinateLatLngSeparator = ",";

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "datetime")
    public String date;

    @ColumnInfo(name = "coordinates")
    public String coordinates;

    public List<LatLng> GetCoordinateList(){
        String[] splitCoordinates = coordinates.split(CoordinateSetSeparator);
        List<LatLng> ret = Arrays.stream(splitCoordinates).filter(s -> s.length() > 1).map(s ->
        {
            String[] coordinateSet = s.split(CoordinateLatLngSeparator);
            return new LatLng(Double.parseDouble(coordinateSet[0]),Double.parseDouble(coordinateSet[1]));
        }
        ).collect(Collectors.toList());
        return ret;
    }
    public void SetCoordinateList(List<LatLng> cList){
        String ret = "";
        for (LatLng coordinate: cList) {
            ret+=String.format("%s%s%s;", coordinate.latitude, CoordinateLatLngSeparator, coordinate.longitude);
        }
        coordinates = ret;
    }
}