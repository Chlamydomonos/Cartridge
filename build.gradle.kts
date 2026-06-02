
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `custom-build`
    `java-library`
    idea
    `maven-publish`
    id("net.neoforged.moddev") version "2.0.141"
    id("org.jetbrains.kotlin.jvm") version "2.3.0"
}

val modVersion: String by project
val modGroupId: String by project
val modId: String by project
val neoVersion: String by project
val neoVersionRange: String by project
val parchmentMappingsVersion: String by project
val parchmentMinecraftVersion: String by project
val minecraftVersion: String by project
val minecraftVersionRange: String by project
val loaderVersionRange: String by project
val modName: String by project
val modLicense: String by project
val modAuthors: String by project
val modDescription: String by project
val curiosVersion: String by project
val geckolibVersion: String by project
val kffVersion: String by project
val carryOnVersion: String by project

tasks.named<Wrapper>("wrapper") {
    distributionType = Wrapper.DistributionType.BIN
}

version = modVersion
group = modGroupId
base.archivesName.set(modId)

java.toolchain.languageVersion.set(JavaLanguageVersion.of(25))
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_25)
}

neoForge {
    version = neoVersion

    parchment {
        mappingsVersion = parchmentMappingsVersion
        minecraftVersion = parchmentMinecraftVersion
    }

    runs {
        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            logLevel = org.slf4j.event.Level.DEBUG
        }

        create("client") {
            client()
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }

        create("server") {
            server()
            gameDirectory.set(project.file("run/server"))
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
            programArgument("--nogui")
        }

        create("gameTestServer") {
            type.set("gameTestServer")
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }

        create("data") {
            clientData()
            programArguments.addAll(
                "--mod",
                modId,
                "--all",
                "--output",
                file("src/generated/resources/").absolutePath,
                "--existing",
                file("src/main/resources/").absolutePath
            )
        }
    }

    mods {
        create(modId) {
            sourceSet(sourceSets.main.get())
        }
    }
}

sourceSets.main.get().resources { srcDir("src/generated/resources") }

val localRuntime = configurations.maybeCreate("localRuntime")
configurations {
    named("runtimeClasspath") {
        extendsFrom(localRuntime)
    }
}

repositories {
    mavenLocal()
    maven {
        name = "Kotlin for Forge"
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
        content { includeGroup("thedarkcolour") }
    }
    maven {
        name = "Curios"
        url = uri("https://maven.theillusivec4.top/")
    }
    exclusiveContent {
        forRepository {
            maven {
                name = "GeckoLib"
                url = uri("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
            }
        }
        filter {
            @Suppress("UnstableApiUsage")
            includeGroupAndSubgroups("com.geckolib")
        }
    }
    maven {
        url = uri("https://nexus.bot.leisuretimedock.top/repository/maven-releases/")
    }
}

dependencies {
    implementation("thedarkcolour:kotlinforforge-neoforge:${kffVersion}")
    implementation("top.theillusivec4.curios:curios-neoforge:${curiosVersion}")
    implementation("com.geckolib:geckolib-neoforge-${minecraftVersion}:${geckolibVersion}")
    implementation("tschipp.carryon:carryon-neoforge-${minecraftVersion}:${carryOnVersion}")
}

val generateModMetadata by tasks.registering(ProcessResources::class) {
    val replaceProperties = mapOf(
        "minecraft_version" to minecraftVersion,
        "minecraft_version_range" to minecraftVersionRange,
        "neo_version" to neoVersion,
        "neo_version_range" to neoVersionRange,
        "loader_version_range" to loaderVersionRange,
        "mod_id" to modId,
        "mod_name" to modName,
        "mod_license" to modLicense,
        "mod_version" to modVersion,
        "mod_authors" to modAuthors,
        "mod_description" to modDescription,
        "curios_version" to curiosVersion,
        "geckolib_version" to geckolibVersion,
        "carryon_version" to carryOnVersion
    )
    inputs.properties(replaceProperties)
    expand(replaceProperties)
    from("src/main/templates")
    into("build/generated/sources/modMetadata")
}

sourceSets.main.get().resources.srcDir(generateModMetadata)
neoForge.ideSyncTask(generateModMetadata)
neoForge.ideSyncTask(tasks["genGitignore"])

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

