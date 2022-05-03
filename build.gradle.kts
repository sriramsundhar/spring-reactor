plugins {
    java
    jacoco
    `maven-publish`
    id("org.springframework.boot") version "2.6.7"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("net.researchgate.release") version "2.8.1"
    id("io.freefair.lombok") version "6.4.3"
    id("com.palantir.docker") version "0.33.0"
    id("com.google.cloud.tools.jib") version "3.2.1"
    id("org.owasp.dependencycheck") version "7.1.0.1"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("io.projectreactor:reactor-core")
    implementation("org.springframework.data:spring-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-devtools")
    implementation("mysql:mysql-connector-java:8.0.28")

    runtimeOnly("dev.miku:r2dbc-mysql:0.8.2.RELEASE")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("io.projectreactor:reactor-test:3.4.17")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")


    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.cucumber:cucumber-java:7.3.2")
    testImplementation("io.cucumber:cucumber-junit:7.3.2")
    testImplementation("io.cucumber:cucumber-spring:7.3.2")
    testImplementation("org.junit.vintage:junit-vintage-engine:5.8.2")
    testImplementation("org.springframework:spring-webflux")

}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

configurations {
    implementation {
        resolutionStrategy.failOnVersionConflict()
    }
}

val cucumberRuntime: Configuration by configurations.creating {
    extendsFrom(configurations["testImplementation"])
}

tasks.jacocoTestReport {
    dependsOn("test")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks {
    test {
        testLogging.showExceptions = true
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
release {
    failOnSnapshotDependencies = true
    with(propertyMissing("git") as net.researchgate.release.GitAdapter.GitConfig) {
        requireBranch = "main"
    }
    preTagCommitMessage = "Gradle Release Plugin - pre tag commit: "
    tagCommitMessage = "Gradle Release Plugin - creating tag: "
    newVersionCommitMessage = "Gradle Release Plugin - new version commit: "
}

tasks {
    "afterReleaseBuild" {
        dependsOn(getTasksByName("publish", true))
        dependsOn(getTasksByName("dockerPush", true))
        dependsOn(getTasksByName("dockerPushLatest", true))
    }
}

tasks.named("check") {
    dependsOn("dependencyCheckAnalyze")
}

publishing {
    publications {
        repositories {
            maven {
                isAllowInsecureProtocol = true
                url =
                        System.getenv("GITHUB_REPOSITORY")?.toString()?.let { uri("https://maven.pkg.github.com/".plus(it)) }
                                ?: uri(findProperty("artifactory").toString())
                credentials {
                    username = System.getenv("ARTIFACTORY_USERNAME")?.toString()
                            ?: findProperty("artifactoryUsername").toString()
                    password = System.getenv("ARTIFACTORY_PASSCODE")?.toString()
                            ?: findProperty("artifactoryPassword").toString()
                }
            }
        }
    }
}

docker {
    name = "sriramsundhar/${project.name}:${project.version}"
    tag("latest", "sriramsundhar/${project.name}:latest")
    files("$buildDir/libs/${project.name}-${project.version}.jar")
    buildArgs(mapOf(Pair("JAR_FILE", "${project.name}-${project.version}.jar")))
}
jib.from.image = "adoptopenjdk/openjdk11:slim"
