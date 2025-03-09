package hse.zoo.ZooTest;

import hse.zoo.animals.Animal;
import hse.zoo.animals.Monkey;
import hse.zoo.animals.Rabbit;
import hse.zoo.animals.Tiger;
import hse.zoo.animals.Wolf;
import hse.zoo.things.Table;
import hse.zoo.things.Thing;
import hse.zoo.things.Computer;
import hse.zoo.zoo.Zoo;

import hse.config.ZooApplicationConfig;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


@SpringBootTest
@ContextConfiguration(classes = ZooApplicationConfig.class)
class ZooTest {

	@Autowired
	private Zoo zoo;

	@Test
	@DisplayName("Проверка всех методов Zoo")
	void ZooCheck() {

		Animal monkey = new Monkey(1, 1, true, 1);
		Animal rabbit = new Rabbit(2, 2, true, 2);
		Animal tiger = new Tiger(3, 3, true);
		Animal wolf = new Wolf(4, 4, true);

		Animal monkey2 = new Monkey(5, 5, true, 8);
		Animal rabbit2 = new Rabbit(6, 6, true, 6);
		Animal tiger2 = new Tiger(7, 7, true);
		Animal wolf2 = new Wolf(8, 8, true);
		Animal wolf3 = new Wolf(9, 9, false);

		Thing table = new Table(1);
		Thing table2 = new Table(2);
		Thing table3 = new Table(3);
		Thing computer = new Computer(4);
		Thing computer2 = new Computer(5);

		zoo.addAnimal(monkey);
		zoo.addAnimal(rabbit);
		zoo.addAnimal(tiger);
		zoo.addAnimal(wolf);
		zoo.addAnimal(monkey2);
		zoo.addAnimal(rabbit2);
		zoo.addAnimal(tiger2);
		zoo.addAnimal(wolf2);
		zoo.addAnimal(wolf3);

		zoo.addThing(table);
		zoo.addThing(table2);
		zoo.addThing(table3);
		zoo.addThing(computer);
		zoo.addThing(computer2);

		assertEquals(36, zoo.calculateTotalFood());
		assertEquals(List.of(monkey2, rabbit2), zoo.getAnimalsForContactZoo());

		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));

		zoo.printInventory();

		String expected = "Animal: Monkey, Number: 1\n" +
				"Animal: Rabbit, Number: 2\n" +
				"Animal: Tiger, Number: 3\n" +
				"Animal: Wolf, Number: 4\n" +
				"Animal: Monkey, Number: 5\n" +
				"Animal: Rabbit, Number: 6\n" +
				"Animal: Tiger, Number: 7\n" +
				"Animal: Wolf, Number: 8\n" +
				"Thing: Table, Number: 1\n" +
				"Thing: Table, Number: 2\n" +
				"Thing: Table, Number: 3\n" +
				"Thing: Computer, Number: 4\n" +
				"Thing: Computer, Number: 5\n";

		assertEquals(expected, outContent.toString());
	}
}
