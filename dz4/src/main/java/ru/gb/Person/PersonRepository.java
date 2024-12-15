package ru.gb.Person;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;

public class PersonRepository {
    private EntityManagerFactory entityManagerFactory;

    public PersonRepository() {
        entityManagerFactory = Persistence.createEntityManagerFactory("personPU");
    }

    public void addPerson(Person person) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(person);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public void updatePerson(Person person) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.merge(person);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public void deletePerson(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        Person person = entityManager.find(Person.class, id);
        if (person != null) {
            entityManager.remove(person);
        }
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public Person getPerson(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Person person = entityManager.find(Person.class, id);
        entityManager.close();
        return person;
    }

    public List<Person> getAllPersons() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        TypedQuery<Person> query = entityManager.createQuery("SELECT p FROM Person p", Person.class);
        List<Person> persons = query.getResultList();
        entityManager.close();
        return persons;
    }
}

