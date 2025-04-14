# Реализованный функционал

## Use Cases:

- **a. Добавить/удалить животное**  

  Контроллер: `AnimalController` (POST `/add`, DELETE `/delete/{id}`)  

  Сервис: `AnimalTransferService`  

  Репозиторий: `AnimalRepository`

- **b. Добавить/удалить вольер**  

  Контроллер: `EnclosureController` (POST `/add`, DELETE `/delete/{id}`)  

  Сервис: `AnimalTransferService`  

  Репозиторий: `EnclosureRepository`

- **c. Переместить животное между вольерами**  

  Контроллер: `AnimalController` (POST `/transfer/{animalId}/{enclosureId}`)  

  Сервис: `AnimalTransferService`  

  Событие: `AnimalMovedEvent`


- **d. Просмотреть расписание кормления**  

  Контроллер: `FeedingScheduleController` (GET `/get/{id}`, GET `/all`)  

  Сервис: `FeedingOrganizationService`  

  Репозиторий: `FeedingScheduleRepository`

- **e. Добавить новое кормление в расписание**  

  Контроллер: `FeedingScheduleController` (POST `/add`)  

  Сервис: `FeedingOrganizationService`  

  Событие: `FeedingTimeEvent`

- **f. Просмотреть статистику зоопарка**  

  Контроллеры: 

  - `AnimalController` (GET `/get/all`, `/get/ill`, `/get/freeAnimals`)  

  - `EnclosureController` (GET `/get/all`, `/get/empty`)

  - `FeedingScheduleController` (GET `/check/{id}`)
  
  Сервис: `ZooStatisticsService`

## Дополнительно:

- In-memory хранилище: `AnimalRepository`, `EnclosureRepository`, `FeedingScheduleRepository`

- Тестирование через Swagger: Конфигурация `SwaggerConfig`, аннотации `@Operation` в контроллерах.

---

# Применённые концепции DDD и Clean Architecture

## Domain-Driven Design:

1. **Entities**:

   - `Animal`: методы `feed()`, `heal()`, `moveTo()`.

   - `Enclosure`: методы `addAnimal()`, `removeAnimal()`, `clean()`.

   - `FeedingSchedule`: методы `changeSchedule()`, `checkIfFed()`.

2. **Value Objects**:

   - `FavouriteFood`: валидация названия еды.

   - `AnimalSpecies`: проверка вида животного.

   - `EnclosureSize`: контроль размеров вольера.

3. **Доменные события**:

   - `AnimalMovedEvent` (публикуется при перемещении).

   - `FeedingTimeEvent` (публикуется при кормлении).

## Clean Architecture:

1. **Слои**:

   - **Domain**: 

     - Сущности: `Animal`, `Enclosure`, `FeedingSchedule`.

     - События: `AnimalMovedEvent`, `FeedingTimeEvent`.

     - Value Objects: `AnimalSpecies`, `FavouriteFood`, `EnclosureSize`.

   - **Application**: 

     - Сервисы: `AnimalTransferService`, `FeedingOrganizationService`, `ZooStatisticsService`.

     - Фасад: `ZooFacade`.

   - **Infrastructure**: 

     - Репозитории: `AnimalRepository`, `EnclosureRepository`, `FeedingScheduleRepository`.

   - **Presentation**: 

     - Контроллеры: `AnimalController`, `EnclosureController`, `FeedingScheduleController`.

2. **Зависимости**:

   - Контроллеры зависят от `ZooFacade` (через интерфейсы сервисов).

   - Сервисы используют интерфейсы репозиториев (`IAnimalRepository`, `IEnclosureRepository`).

   - Domain-слой полностью изолирован.

---

# Тестирование

- **Инструмент**: Swagger UI (доступен по `/swagger-ui.html`).

- **Покрытие кода**: 

  - Тесты контроллеров: `AnimalControllerTest`, `EnclosureControllerTest`, `FeedingScheduleControllerTest`.

  - Проверка сценариев: CRUD-операции, валидация, бизнес-логика (например, запрет на перемещение в переполненный вольер).

---

# Структура проекта
src/
├── main/
│ ├── java/hse/zoo/
│ │ ├── Application/ # Слой приложения
│ │ │ ├── facade/ZooFacade # Фасад для API
│ │ │ ├── services/ # Сервисы
│ │ │ └── eventHandlers/ # Обработчики событий
│ │ │
│ │ ├── Domain/ # Ядро системы
│ │ │ ├── entities/ # Сущности
│ │ │ ├── events/ # Доменные события
│ │ │ ├── valueobjects/ # Value Objects
│ │ │ └── interfaces/ # Интерфейсы репозиториев и сервисов
│ │ │
│ │ ├── Infrastructure/ # Внешние реализации
│ │ │ └── repositories/ # In-memory репозитории
│ │ │
│ │ └── Presentation/ # API и конфигурация
│ │   ├── controllers/ # Контроллеры
│ │   └── config/SwaggerConfig # Документация API
│ │
│ └── resources/ # Конфигурации Spring
│
└── test/ # Тесты
  └── java/hse/zoo/controllerTests/ # Интеграционные тесты

---

# Инструкция по запуску

1. Запустить ./gradlew bootRun

2. Открыть http://localhost:8080/swagger-ui.html

## Также, можно запустить тесты и проверить покрытие через плагин JaCoCo.

1. Запустить ./gradlew test (результаты в build/reports/tests/test/index.html)

2. Посмотреть покрытие тестов JaCoCo (build/reports/jacoco/test/html/index.html)

## Примечание

ID сущностей присваиваются автоматически (начинаются с 0).

---

# Итог

- Соблюдены принципы Clean Architecture (изоляция слоёв, зависимости через интерфейсы).

- Применены ключевые концепции DDD: богатые модели, Value Objects, доменные события.

- Реализовано тестирование API через Swagger и интеграционные тесты.