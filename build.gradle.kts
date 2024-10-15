val dockerUsername: String by project
val dockerPassword: String by project

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
    id("com.google.cloud.tools.jib") version "3.4.3"
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
}

jib {
    // 애플리케이션을 빌드할 기본 이미지를 구성
    from {
        image = "eclipse-temurin:21.0.3_9-jre-ubi9-minimal"
    }
    // 애플리케이션을 빌드할 대상 이미지를 구성
    to {
        // 이미지는 dockerhub에 만들어준 repo
        image = "lgodl1598/toy-helloworld"
        tags = setOf("0.0.1")

        auth {
            username = "lgodl1598"
            password = "1q2w3e4r!"
        }
    }
    // 빌드된 이미지에서 실행될 컨테이너를 구성
    container {
        jvmFlags = listOf(
            "-Dspring.profiles.active=dev",
            "-Dfile.encoding=UTF-8",
        )
        ports = listOf("80")
        setAllowInsecureRegistries(true)  // 보안이 적용되지 않은 registry 연결 허용
    }
}

group = "org.service"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    runtimeOnly("com.mysql:mysql-connector-j")
    testImplementation("io.mockk:mockk:1.13.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
