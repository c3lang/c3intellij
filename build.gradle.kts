import org.jetbrains.grammarkit.tasks.GenerateLexerTask
import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("org.jetbrains.intellij.platform")
    id("org.jetbrains.changelog")
    id("org.jetbrains.grammarkit") version "2022.3.2"
}

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

idea {
    module {
        generatedSourceDirs.add(file("src/main/gen"))
    }
}

sourceSets {
    main {
        java {
            srcDirs("src/main/gen", "src/main/java")
        }
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.20.2")
    implementation("org.eclipse.lsp4j:org.eclipse.lsp4j:0.24.0")

    // IntelliJ Platform Gradle Plugin Dependencies Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-dependencies-extension.html
    intellijPlatform {
        intellijIdea("2025.2.6.2")
        bundledPlugin("com.intellij.modules.json")
        testFramework(TestFrameworkType.Platform)
    }
}

tasks {
    val generateC3Lexer = register("generateC3Lexer", GenerateLexerTask::class) {
        sourceFile.set(file("src/main/java/org/c3lang/intellij/C3.flex"))
        targetDir.set("src/main/gen/org/c3lang/intellij/lexer")
        targetClass.set("C3Lexer")
        purgeOldFiles.set(true)
    }

    compileJava {

        
        dependsOn(generateC3Lexer)
    }
}
