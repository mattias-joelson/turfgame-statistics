package org.joelson.mattias.turfgame.application.db;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import org.joelson.mattias.turfgame.application.model.RegionDTO;
import org.joelson.mattias.turfgame.util.StringUtils;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "regions")
public class RegionEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(nullable = false)
    private int id;

    @NotNull
    @Column(nullable = false)
    private String name;

    @Nullable
    private String country;
    
    public RegionEntity() {
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    @NotNull
    public String getName() {
        return name;
    }
    
    public void setName(@NotNull String name) {
        this.name = StringUtils.requireNotNullAndNotEmpty(name, "Name can not be null", "Name can not be empty");
    }
    
    @Nullable
    public String getCountry() {
        return country;
    }
    
    public void setCountry(@Nullable String country) {
        this.country = StringUtils.requireNullOrNonEmpty(country, "Country can not be empty");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RegionEntity) {
            RegionEntity that = (RegionEntity) obj;
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
        return String.format("RegionEntity[id %d, name %s, country %s]",
                id, name, country);
    }
    
    public RegionDTO toDTO() {
        return new RegionDTO(id, name, country);
    }
    
    static RegionEntity build(int id, @NotNull String name, @Nullable String country) {
        RegionEntity region = new RegionEntity();
        region.setId(id);
        region.setName(name);
        region.setCountry(country);
        return region;
    }
    
    static RegionEntity build(RegionDTO regionDTO) {
        return build(regionDTO.getId(), regionDTO.getName(), regionDTO.getCountry());
    }
}
