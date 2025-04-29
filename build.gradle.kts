import org.gradle.testing.jacoco.tasks.JacocoReport
import java.io.File
import java.util.regex.Pattern

plugins {
    kotlin("jvm") version "2.1.10"
    id("jacoco")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("io.insert-koin:koin-core:4.0.3")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.12.0-M1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
    testImplementation("io.mockk:mockk:1.13.16")
    testImplementation("com.google.truth:truth:1.4.2")
    testImplementation("io.insert-koin:koin-test:4.0.3")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.named("jacocoTestReport"))
    doFirst {
        println("Running tests in: ${testClassesDirs.files}")
    }
}

tasks.named<JacocoReport>("jacocoTestReport") {
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/test/html"))
    }
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude("org/example/domain/model/**")
                exclude("org/example/di/**")
                exclude("org/example/MainKt.class")
            }
        })
    )
}


tasks.register("verifyTestCoverage") {
    group = "verification"
    description = "Verifies that test coverage is 100%"
    dependsOn("test", "jacocoTestReport")
    inputs.files(tasks.named<JacocoReport>("jacocoTestReport").map { it.outputs.files })

    doLast {
        val buildDir = layout.buildDirectory.get().asFile
        val htmlReportFile = File(buildDir, "reports/jacoco/test/html/index.html")

        val content = htmlReportFile.readText()
        val pattern = Pattern.compile("Total.*?([0-9]{1,3})%")
        val matcher = pattern.matcher(content)

        if (matcher.find()) {
            val coveragePercent = matcher.group(1).toInt()
            logger.lifecycle("Test coverage: $coveragePercent%")

            if (coveragePercent < 100) {
                throw GradleException("Code coverage is less than 100% (actual: $coveragePercent%)")
            }

            logger.lifecycle("✓ Test coverage is $coveragePercent%")
        } else {
            throw GradleException("Could not parse coverage information from report")
        }
    }
}

kotlin {
    jvmToolchain(23)
}