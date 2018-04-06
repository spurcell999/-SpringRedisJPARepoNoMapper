package com.purcell.repository;

import com.purcell.domain.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends CrudRepository<Person, String> {

    List<Person> findByLastname(String lastname);

    List<Person> findByFirstnameAndLastname(String fistname, String lastname);

    List<Person> findByFirstnameOrLastname(String firstname, String lastname);

    List<Person> findByAddress_City(String city);

}
