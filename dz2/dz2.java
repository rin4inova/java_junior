package dz2;

import java.lang.reflect.Method;

// Используя Reflection API, напишите программу, которая выводит на экран
// все методы класса String.
public class dz2 {
    public static void main(String[] args) {
        String str = "Hello world!";
        Class clazz = str.getClass();
        Method[]methods = clazz.getMethods();
        for (Method method: methods){
            System.out.println(method.getName());
        }

    }
}