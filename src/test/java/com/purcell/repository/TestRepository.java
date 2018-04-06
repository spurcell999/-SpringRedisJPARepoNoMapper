package com.purcell.repository;

import com.purcell.domain.Address;
import com.purcell.domain.Gender;
import com.purcell.domain.Person;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.springframework.data.redis.core.PartialUpdate.newPartialUpdate;


@RunWith(SpringRunner.class)
@SpringBootTest
public class TestRepository {

    @Autowired
    @Qualifier("cloud")
    RedisConnectionFactory connectionFactory;

    @Autowired
    RedisKeyValueTemplate template;

    @Autowired
    PersonRepository repo;

    Person scott = new Person("scott", "purcell", Gender.MALE);
    Person diane = new Person("diane", "purcell", Gender.FEMALE);
    Person matt = new Person("matt", "purcell", Gender.MALE);
    Person ben = new Person("ben", "purcell", Gender.MALE);
    Person brady = new Person("brady", "purcell", Gender.MALE);
    Person doug = new Person("doug", "storms", Gender.MALE);

    @Before
    public void setUp() {

        RedisConnection connection = connectionFactory.getConnection();
        connection.flushAll();
        connection.close();

    }

    @Test
    public void reveiveExpirationEvent() throws InterruptedException {

        scott.setTtl(1L);

        flushTestUsers();

        Thread.sleep(2500);

        assertThat(repo.findByFirstnameAndLastname("scott", "purcell"), is(empty()));
    }


    @Test
    public void findBySingleProperty() {

        flushTestUsers();
        Iterable<Person> allEntries = repo.findAll();
        List<Person> lastNames = repo.findByLastname(scott.getLastname());

        assertThat(lastNames, containsInAnyOrder(scott, diane, matt, ben, brady));
        assertThat(lastNames, not(hasItem(doug)));
    }

    @Test
    public void findByMultiplePropertiesUsingAnd() {

        flushTestUsers();

        List<Person> list = repo.findByFirstnameAndLastname(scott.getFirstname(), scott.getLastname());

        assertThat(list, hasItem(scott));
        assertThat(list, not(hasItems(diane, matt, ben, brady, doug)));
    }


    @Test
    public void findByMultiplePropertiesUsingOr() {

        flushTestUsers();

        List<Person> lastNames = repo.findByFirstnameOrLastname(scott.getFirstname(), diane.getLastname());

        assertThat(lastNames, containsInAnyOrder(scott, diane, matt, ben, brady));
        assertThat(lastNames, not(hasItems(doug)));
    }

    @Test
    public void findByEmbeddedProperty() {

        Address addr = new Address();
        addr.setCountry("USA");
        addr.setCity("O'Fallon");

        scott.setAddress(addr);

        flushTestUsers();

        List<Person> list = repo.findByAddress_City(addr.getCity());

        assertThat(list, hasItem(scott));
        assertThat(list, not(hasItems(diane, matt, ben, brady, doug)));
    }

    @Test
    public void updateEntity() {

        scott.setFirstname("steve");

        flushTestUsers();

        Person foo = repo.findOne(scott.getId());
        assertThat(repo.findOne(scott.getId()), is(scott));

        template.update(newPartialUpdate(scott.getId(), Person.class).set("firstname", "scott"));

        assertThat(repo.findByFirstnameAndLastname("scott", "purcell"), hasSize(1));
        assertThat(repo.findByFirstnameAndLastname("steve", "purcell"), is(empty()));
    }

    @Test
    public void useReferencesToStoreDataToOtherObjects() {

        flushTestUsers();

        scott.setChildren(Arrays.asList( matt, ben, brady));

        repo.save(scott);

        Person laoded = repo.findOne(scott.getId());
        assertThat(laoded.getChildren(), hasItems(matt, ben, brady));


        repo.delete(Arrays.asList(matt, ben));

        laoded = repo.findOne(scott.getId());
        assertThat(laoded.getChildren(), hasItems(brady));
        assertThat(laoded.getChildren(), not(hasItems(matt, ben)));
    }




    private void flushTestUsers() {
        repo.save(Arrays.asList(scott, diane, matt, ben, brady, doug));
    }

}
