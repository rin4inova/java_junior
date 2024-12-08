package dz3;

import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class dz3 {
     /**
     * Задание 1:
     * Разработайте класс Student с полями String name, int age, transient double GPA (средний балл).
     * Обеспечьте поддержку сериализации для этого класса.
     * Создайте объект класса Student и инициализируйте его данными.
     * Сериализируйте этот объект в файл.
     * Десериализируйте объект обратно в программу из файла.
     * Выведите все поля объекта, включая GPA, и обсудите почему значение GPA не было сохранено/восстановлено.
     *
     * Задание 2 (не обязательно):
     * ** Выполнить задачу 1 используя другие типы сериализаторов (в xml и json документов).
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Student student1 = new Student("Ivan", 17, 4.3);
        try(FileOutputStream fileOutputStream = new FileOutputStream("student.bin");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)){
            objectOutputStream.writeObject(student1);
            System.out.println("Объект Student сериализован");
        }

        try(FileInputStream fileInputStream = new FileInputStream("student.bin");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)){
            student1 = (Student)objectInputStream.readObject();
            System.out.println("Объект Student десериализован");
        }
        System.out.println("Объект Student десериализован.");
        System.out.println("Имя: "+ student1.getName());
        System.out.println("Возраст: "+ student1.getAge());
        System.out.println("Средний балл: "+ student1.getGPA());
        System.out.println("Средний балл GPA не сохранен/не восстановлен, т.к. transient");

    }
}
