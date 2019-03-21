package com.example.foodhygieneratings;

import java.util.HashMap;

public class Establishment {
    private int FHRSID;
    private String LocalAuthorityBusinessID;
    private String BusinessName;
    private String BusinessType;
    private String RatingValue;
    private String RatingDate;
    private HashMap<String, String> links;

    public Establishment(int FHRSID, String localAuthorityBusinessID, String businessName, String businessType, String ratingValue, String ratingDate, HashMap<String, String> links) {
        this.FHRSID = FHRSID;
        LocalAuthorityBusinessID = localAuthorityBusinessID;
        BusinessName = businessName;
        BusinessType = businessType;
        RatingValue = ratingValue;
        RatingDate = ratingDate;
        this.links = links;
    }

    public int getFHRSID() {
        return FHRSID;
    }

    public void setFHRSID(int FHRSID) {
        this.FHRSID = FHRSID;
    }

    public String getLocalAuthorityBusinessID() {
        return LocalAuthorityBusinessID;
    }

    public void setLocalAuthorityBusinessID(String localAuthorityBusinessID) {
        LocalAuthorityBusinessID = localAuthorityBusinessID;
    }

    public String getBusinessName() {
        return BusinessName;
    }

    public void setBusinessName(String businessName) {
        BusinessName = businessName;
    }

    public String getBusinessType() {
        return BusinessType;
    }

    public void setBusinessType(String businessType) {
        BusinessType = businessType;
    }

    public String getRatingValue() {
        return RatingValue;
    }

    public void setRatingValue(String ratingValue) {
        RatingValue = ratingValue;
    }

    public String getRatingDate() {
        return RatingDate;
    }

    public void setRatingDate(String ratingDate) {
        RatingDate = ratingDate;
    }

    public HashMap<String, String> getLinks() {
        return links;
    }

    public void setLinks(HashMap<String, String> links) {
        this.links = links;
    }

    public String toString(){
        return BusinessName;
    }
}
