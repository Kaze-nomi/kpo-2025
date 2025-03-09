package hse.zoo;

import hse.zoo.animals.*;
import hse.zoo.things.*;
import hse.zoo.zoo.Zoo;

import hse.config.ZooApplicationConfig;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {

        ApplicationContext context = new AnnotationConfigApplicationContext(ZooApplicationConfig.class);
        
        Zoo zoo = context.getBean(Zoo.class);

        Animal monkey = new Monkey(5, 1, true, 7);
        Animal rabbit = new Rabbit(3, 2, true, 6);
        Animal tiger = new Tiger(10, 3, true);
        Animal wolf = new Wolf(8, 4, true);

        zoo.addAnimal(monkey);
        zoo.addAnimal(rabbit);
        zoo.addAnimal(tiger);
        zoo.addAnimal(wolf);

        zoo.addThing(new Table(101));
        zoo.addThing(new Computer(102));

        System.out.println("Total food needed: " + zoo.calculateTotalFood() + " kg");

        List<Animal> contactZooAnimals = zoo.getAnimalsForContactZoo();
        System.out.println("Animals for contact zoo:");
        for (Animal animal : contactZooAnimals) {
            System.out.println(animal.getClass().getSimpleName());
        }

        System.out.println("Inventory:");
        zoo.printInventory();

        ((AnnotationConfigApplicationContext)context).close();
    }
}