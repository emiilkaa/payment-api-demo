import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

buildscript {
    dependencies {
        classpath("org.liquibase:liquibase-gradle-plugin:2.1.1")
    }
}

plugins {
    id("org.springframework.boot") version "2.5.1"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    id("org.liquibase.gradle") version "2.1.1"
    kotlin("jvm") version "1.6.20"
    kotlin("plugin.spring") version "1.4.32"
    kotlin("plugin.jpa") version "1.6.20"
    kotlin("kapt") version "1.5.20"
}

group = "com.example"
version = "0.0.2-POSTGRES"

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

sourceSets {
    main {
        java {
            srcDirs("src/main/kotlin")
        }
    }
}

repositories {
    mavenCentral()
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, "seconds")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.liquibase:liquibase-core")
    implementation("org.hibernate:hibernate-core")
    implementation("com.vladmihalcea:hibernate-types-52:2.21.1")
    implementation("org.springframework.retry:spring-retry:1.3.1")
    implementation("org.apache.ignite:ignite-core:2.10.0")
    implementation("org.springframework.cloud:spring-cloud-sleuth-instrumentation:3.0.3")
    compileOnly("org.projectlombok:lombok")
    runtimeOnly("com.oracle.database.jdbc:ojdbc11")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("com.h2database:h2")
    implementation(kotlin("stdlib-jdk8"))

    kapt("org.hibernate:hibernate-jpamodelgen:5.4.30.Final")

    liquibaseRuntime("org.liquibase:liquibase-core:3.6.1")
    liquibaseRuntime("org.liquibase:liquibase-groovy-dsl:2.0.1")
    liquibaseRuntime("ch.qos.logback:logback-classic")
    liquibaseRuntime("org.postgresql:postgresql")
}

tasks.getByName<Jar>("jar") {
    enabled = false
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

liquibase {
    val props = Properties()
    val fileProperties = file("$projectDir/liquibase.properties")

    if (fileProperties.exists()) {
        fileProperties.inputStream().let { props.load(it) }
        activities.register("main") {
            "url" to props.getProperty("url")
            "username" to props.getProperty("username")
            "password" to props.getProperty("password")
            "databaseChangeLogTableName" to props.getProperty("databaseChangeLogTableName")
            "defaultSchemaName" to props.getProperty("defaultSchemaName")
            "changeLogFile" to props.getProperty("changeLogFile")
        }
    } else {
        fileProperties.appendText("jdbc:postgresql://77.232.142.33:5432/payment_api_app\n")
        fileProperties.appendText("username=PAYMENT_API_APP\n")
        fileProperties.appendText("password=change_it\n")
        fileProperties.appendText("changeLogFile=${project.projectDir.path}/src/main/resources/liquibase/changelog.yaml\n")
        fileProperties.appendText("defaultSchemaName=PAYMENT_API\n")
        fileProperties.appendText("databaseChangeLogTableName=DATABASECHANGELOG\n")
        println("Please fill ${projectDir}/liquibase.properties if you want use liquibase plugin")
    }
}