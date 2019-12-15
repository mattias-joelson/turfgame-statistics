package org.joelson.mattias.turfgame.application.model;

import org.joelson.mattias.turfgame.util.StringUtils;

import java.util.Objects;

public class RegionDTO {
    
    private final int id;
    private final String name;
    private final String country;
    
    public RegionDTO(int id, String name, String country) {
        this.id = id;
        this.name = StringUtils.requireNotNullAndNotEmpty(name, "Name can not be null", "Name can not be empty");
        this.country = StringUtils.requireNullOrNonEmpty(country, "Country can not be empty");
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getCountry() {
        return country;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RegionDTO) {
            RegionDTO that = (RegionDTO) obj;
            return id == that.id && Objects.equals(name, that.name) && Objects.equals(country, that.country);
        }
        return super.equals(obj);
    }
    
    @Override
    public int hashCode() {
        return id;
    }
    
    @Override
    public String toString() {
        return String.format("RegionDTO{id %d, name %s, country %s", id, name, country);
    }
}