plugins {
    kotlin("jvm") version "1.9.23"
}

group = "org.bitcoin.kit"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // https://mvnrepository.com/artifact/fr.acinq.secp256k1/secp256k1-kmp-jni-jvm
    implementation("fr.acinq.secp256k1:secp256k1-kmp-jni-jvm:0.15.0")

    // https://mvnrepository.com/artifact/fr.acinq.bitcoin/bitcoin-kmp-jvm
    implementation("fr.acinq.bitcoin:bitcoin-kmp-jvm:0.19.0")

    // https://mvnrepository.com/artifact/com.squareup.retrofit2/retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")

}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}