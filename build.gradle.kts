import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
  java
  application
  id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "org.exemplo"
version = "0.0.1"

java {
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
}

repositories {
  mavenCentral()
}

val vertxVersion = "4.5.4"
val junitJupiterVersion = "5.10.1"

val mainVerticleName = "org.exemplo.rinha2024q1.App"

application {
  mainClass.set(mainVerticleName)
}

dependencies {
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation("io.vertx:vertx-web")
  implementation("io.vertx:vertx-pg-client")
  implementation("com.ongres.scram:client:2.1")
  implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.1")
  testImplementation("io.vertx:vertx-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
}

tasks.withType<ShadowJar> {
  archiveClassifier.set("fat")
  manifest {
    attributes(mapOf("Main-Verticle" to mainVerticleName))
  }
  mergeServiceFiles()
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

