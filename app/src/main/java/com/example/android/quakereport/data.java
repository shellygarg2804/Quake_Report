package com.example.android.quakereport;

import java.util.Date;

public class data {
    private Double magnitude;
    private String place="";
    private Long TimeInMilliSeconds=null;
    private String url="";

    public data(Double magnitude, String place, Long TimeInMilliSeconds, String url)
    {
        this. magnitude=magnitude;
        this.place=place;
        this.TimeInMilliSeconds=TimeInMilliSeconds;
        this.url =url;
    }

    public Double getMagnitude() {
        return magnitude;
    }

    public String getPlace() {
        return place;
    }

    public Long getTimeInMilliSeconds() {
        return TimeInMilliSeconds;
    }

    public String getUrl() {
        return url;
    }
}
