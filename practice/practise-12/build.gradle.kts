plugins {
	java
	checkstyle
	jacoco
	id("org.springframework.boot") version "3.4.2"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "hse"
version = "0.0.1-SNAPSHOT"

checkstyle {
	toolVersion = "10.13.0"
	isIgnoreFailures = false
	maxWarnings = 0
	maxErrors = 0
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

tasks.test { 
	finalizedBy(tasks.jacocoTestReport) 
}

tasks.jacocoTestReport { 
	dependsOn(tasks.test)
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
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	runtimeOnly("org.postgresql:postgresql")
	implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")
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

tasks.test {
	finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
	dependsOn(tasks.test) // tests are required to run before generating the report
}