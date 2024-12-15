package ru.gb.Person;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("personPU");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            entityManager.getTransaction().begin();

            // Соз дание объектов Person
            Person alice = new Person("Alice", 30);
            Person bob = new Person("Bob", 25);
            Person charlie = new Person("Charlie", 35);

            // Сохранение объектов в базу данных
            entityManager.persist(alice);
            entityManager.persist(bob);
            entityManager.persist(charlie);

            entityManager.getTransaction().commit();

            // Запрос всех объектов Person из базы данных
            List<Person> persons = entityManager.createQuery("SELECT p FROM Person p", Person.class).getResultList();
            System.out.println("All persons:");
            for (Person person : persons) {
                System.out.println(person);
            }

        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            entityManager.close();
            entityManagerFactory.close();
        }
    }
}