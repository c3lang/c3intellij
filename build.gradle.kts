import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.jetbrains.grammarkit.tasks.GenerateLexerTask
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import java.net.URI
import java.util.zip.ZipInputStream

abstract class FetchC3StdTask : DefaultTask() {
    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun fetch() {
        val outputDir = outputDirectory.get().asFile
        val archive = temporaryDir.resolve("c3c-master.zip")

        logger.lifecycle("Downloading C3 std library from $archiveUrl")
        URI(archiveUrl).toURL().openStream().use { input ->
            archive.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        outputDir.deleteRecursively()
        outputDir.mkdirs()

        val prefix = "c3c-master/lib/std/"
        val outputPath = outputDir.canonicalFile.toPath()
        ZipInputStream(archive.inputStream().buffered()).use { zip ->
            while (true) {
                val entry = zip.nextEntry ?: break
                if (!entry.name.startsWith(prefix)) continue

                val relativePath = entry.name.removePrefix(prefix)
                if (relativePath.isBlank()) continue

                val target = outputDir.resolve(relativePath).canonicalFile
                require(target.toPath().startsWith(outputPath)) {
                    "Refusing to extract path outside std output directory: ${entry.name}"
                }

                if (entry.isDirectory) {
                    target.mkdirs()
                } else {
                    target.parentFile.mkdirs()
                    target.outputStream().use { output ->
                        zip.copyTo(output)
                    }
                }
            }
        }
    }

    private companion object {
        const val archiveUrl = "https://github.com/c3lang/c3c/archive/refs/heads/master.zip"
    }
}

plugins {
    id("org.jetbrains.intellij.platform")
    id("org.jetbrains.changelog")
    id("org.jetbrains.grammarkit") version "2023.3.0.3"
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
    implementation("com.fasterxml.jackson.core:jackson-databind:2.22.3")
    implementation("org.eclipse.lsp4j:org.eclipse.lsp4j:1.0.0")

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
        targetOutputDir.set(layout.projectDirectory.dir("src/main/gen/org/c3lang/intellij/lexer"))
        purgeOldFiles.set(true)
    }

    val fetchedStdDirectory = layout.buildDirectory.dir("c3c-std")
    val fetchC3Std = register("fetchC3Std", FetchC3StdTask::class) {
        group = "verification"
        description = "Downloads c3lang/c3c lib/std for parser coverage tests."
        outputDirectory.set(fetchedStdDirectory)
    }

    compileJava {

        
        dependsOn(generateC3Lexer)
    }

    test {
        val configuredStdRoot = providers.systemProperty("c3.std.root").orNull
        val localStdDirectory = layout.projectDirectory.dir("std").asFile
        if (!configuredStdRoot.isNullOrBlank()) {
            systemProperty("c3.std.root", configuredStdRoot)
        } else if (localStdDirectory.isDirectory) {
            systemProperty("c3.std.root", localStdDirectory.absolutePath)
        } else {
            dependsOn(fetchC3Std)
            systemProperty("c3.std.root", fetchedStdDirectory.get().asFile.absolutePath)
        }
    }
}
