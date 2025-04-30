plugins {
	java
	jacoco
	id("org.springframework.boot") version "3.4.2"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.liquibase.gradle") version "2.0.4"
}

group = "hse"
version = "0.0.1-SNAPSHOT"

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
	implementation("org.liquibase:liquibase-core")
	liquibaseRuntime("org.liquibase:liquibase-core")
	liquibaseRuntime("org.liquibase.ext:liquibase-hibernate6:5.0.0")
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
	compileOnly("org.apache.tomcat:annotations-api:6.0.53")
	implementation("org.springframework.kafka:spring-kafka")
	implementation("org.apache.kafka:kafka-clients")
	testImplementation("org.springframework.kafka:spring-kafka-test")
}