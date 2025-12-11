plugins {
    id("java")
    id("io.quarkus") version "3.29.3"
    id("io.freefair.lombok") version "9.1.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val quarkusVersion = "3.29.3"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    implementation(enforcedPlatform("io.quarkus.platform:quarkus-bom:${quarkusVersion}"))

    //CDI
    implementation("io.quarkus:quarkus-arc")

    //REST
    implementation("io.quarkus:quarkus-rest")
    implementation("io.quarkus:quarkus-rest-jsonb")

    //DB
    implementation("io.quarkus:quarkus-hibernate-orm")
    implementation("io.quarkus:quarkus-hibernate-orm-panache")
    implementation("io.quarkus:quarkus-jdbc-postgresql")

    //Service Discovery

    implementation("io.quarkus:quarkus-smallrye-stork")
    // implementation("io.smallrye.stork:stork-service-discovery-static-list:2.6.3")

// Stork con Consul (service discovery)
    implementation("io.smallrye.stork:stork-service-discovery-consul")

// Cliente Vert.x para Consul + Mutiny
    //implementation("io.smallrye.reactive:smallrye-mutiny-vertx-consul-client")
    implementation("io.smallrye.reactive:smallrye-mutiny-vertx-consul-client")




}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}