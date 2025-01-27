package studying;

import studying.domains.Customer;
import studying.factories.HandCarFactory;
import studying.factories.PedalCarFactory;
import studying.params.EmptyEngineParams;
import studying.params.PedalEngineParams;
import studying.services.CarService;
import studying.services.CustomerStorage;
import studying.services.HseCarService;

public class Main {
    public static void main(String[] args) {
<<<<<<< HEAD
<<<<<<< HEAD
        System.out.println("HSE");

        var carService = new CarService();

        var customerStorage = new CustomerStorage();

        var hseCarService = new HseCarService(carService, customerStorage);

        var pedalCarFactory = new PedalCarFactory();

        var handCarFactory = new HandCarFactory();

        customerStorage.addCustomer(new Customer("Ivan1",6,4));
        customerStorage.addCustomer(new Customer("Maksim",4,6));
        customerStorage.addCustomer(new Customer("Petya",6,6));
        customerStorage.addCustomer(new Customer("Nikita",4,4));

        carService.addCar(pedalCarFactory, new PedalEngineParams(6));
        carService.addCar(pedalCarFactory, new PedalEngineParams(6));

        carService.addCar(handCarFactory, EmptyEngineParams.DEFAULT);
        carService.addCar(handCarFactory, EmptyEngineParams.DEFAULT);

        customerStorage.getCustomers().stream().map(Customer::toString).forEach(System.out::println);

        hseCarService.sellCars();

        customerStorage.getCustomers().stream().map(Customer::toString).forEach(System.out::println);
=======
=======
>>>>>>> fae1144 (merge)
        // # Тестирование
        // 1. Создать экземпляр класса `CarService`
        CarService carService = new CarService();
        
        // 2. Создать экземпляр класса `CustomerStorage`
        CustomerStorage customerStorage = new CustomerStorage();
        
        // 3. Создать экземпляр класса `HseCarService`
        HseCarService hseCarService = new HseCarService(carService, customerStorage);
        
        // 4. Создать экземпляр класса `PedalCarFactory`
        PedalCarFactory pedalCarFactory = new PedalCarFactory();
        
        // 5. Создать экземпляр класса `HandCarFactory`
        HandCarFactory handCarFactory = new HandCarFactory();

        LevitationCarFactory levitationCarFactory = new LevitationCarFactory();
        
        // 6. Добавить следующих покупателей:
        customerStorage.addCustomer(new Customer("Customer 1", 6, 4, 50));
        customerStorage.addCustomer(new Customer("Customer 2", 4, 6, 200));
        customerStorage.addCustomer(new Customer("Customer 3", 0, 0, 300));
        customerStorage.addCustomer(new Customer("Customer 4", 4, 4, 2));
        
        // 7. Добавить автомобили:
        carService.addCar(pedalCarFactory, new PedalEngineParams(1));
        carService.addCar(pedalCarFactory, new PedalEngineParams(2));
        carService.addCar(handCarFactory, new EmptyEngineParams());
        carService.addCar(handCarFactory, new EmptyEngineParams());
        carService.addCar(levitationCarFactory, new EmptyEngineParams());
        
        // 8. Вывести на экран информацию о покупателях и их автомобилях
        customerStorage.getCustomers().forEach(customer -> System.out.println(customer));
        customerStorage.getCustomers().forEach(car -> System.out.println(car));
        
        // 9. Вызвать метод `SellCars`
        hseCarService.sellCars();
        
        // 10. Вывести на экран информацию о покупателях и их автомобилях. Проверить, что результат соответствует следующему:
        //     - Одному покупателю вручен педальный автомобиль
        //     - Одному покупателю вручен автомобиль с ручным приводом
        //     - Одному покупателю вручен любой автомобиль
        //     - Один покупатель остался без автомобиля
        //     - При этом у всех врученных автомобилей различные номера
        customerStorage.getCustomers().forEach(customer -> System.out.println(customer));
<<<<<<< HEAD
>>>>>>> fae1144 (merge)
=======
>>>>>>> fae1144 (merge)
    }
}
