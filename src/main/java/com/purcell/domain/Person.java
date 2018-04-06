package com.purcell.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.util.List;


@Data
@EqualsAndHashCode(exclude = { "children" })
@RedisHash("hperson")
@NoArgsConstructor
public class Person {
    private @Id
    String id;

    private @Indexed
    String firstname;
    private @Indexed
    String lastname;
    private Gender gender;
    private Address address;
    private Fan fan;


    private @TimeToLive
    Long ttl;

    private @Reference
    List<Person> children;

    public Person(String firstname, String lastname, Gender gender) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.gender = gender;
    }


}
