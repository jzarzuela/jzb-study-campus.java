package com.aetrion.flickr.places;

/**
 * A Flickr place.
 *
 * @author mago
 * @version $Id: Location.java,v 1.2 2008/07/03 21:37:44 x-mago Exp $
 * @see com.aetrion.flickr.photos.SearchParameters#setPlaceId(String)
 * @see com.aetrion.flickr.photos.Photo#getPlaceId()
 */
public class Location {
    private String woeId = "";
    private String placeId = "";
    private String placeUrl = "";
    private Place locality = null;
    private Place county = null;
    private Place region = null;
    private Place country = null;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private int placeType = 0;

    public Location() {
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceUrl() {
        return placeUrl;
    }

    public void setPlaceUrl(String placeUrl) {
        this.placeUrl = placeUrl;
    }

    public Place getLocality() {
        return locality;
    }

    public void setLocality(Place locality) {
        this.locality = locality;
    }

    public Place getCounty() {
        return county;
    }

    public void setCounty(Place county) {
        this.county = county;
    }

    public Place getRegion() {
        return region;
    }

    public void setRegion(Place region) {
        this.region = region;
    }

    public Place getCountry() {
        return country;
    }

    public void setCountry(Place country) {
        this.country = country;
    }

    /**
     *
     * @return woeid
     * @see com.aetrion.flickr.photos.SearchParameters#setWoeId(String)
     */
    public String getWoeId() {
        return woeId;
    }

    public void setWoeId(String woeId) {
        this.woeId = woeId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        try {
            setLatitude(Double.parseDouble(latitude));
        } catch (NumberFormatException e) {}
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        try {
            setLongitude(Double.parseDouble(longitude));
        } catch (NumberFormatException e) {}
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

	public int getPlaceType() {
		return placeType;
	}

	public void setPlaceType(int placeType) {
		this.placeType = placeType;
	}

}
