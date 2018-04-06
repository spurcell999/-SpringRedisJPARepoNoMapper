package com.purcell.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.redis.core.index.Indexed;

@Data
@EqualsAndHashCode
public class Address {
    private @Indexed
    String city;
    private String country;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
