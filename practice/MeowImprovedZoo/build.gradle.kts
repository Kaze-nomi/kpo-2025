plugins {
    java
    jacoco
    id("org.springframework.boot") version "3.4.2"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "hse"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.0")
	implementation("com.fasterxml.jackson.core:jackson-databind")
	implementation("org.springframework.boot:spring-boot-starter")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("org.springframework.boot:spring-boot-starter-aop")
}

tasks.withType<Test> {
    useJUnitPlatform()
}


// Настройка задачи test
tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport) // Отчёт Jacoco будет создан после выполнения тестов
}

// Настройка задачи jacocoTestReport
tasks.jacocoTestReport {
    // Фильтруем классы, исключая Main.class
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                // Шаблон для исключения всех классов с именем Main.class (в любых пакетах)
                exclude("**/Zoo.class")
            }
        })
    )
}