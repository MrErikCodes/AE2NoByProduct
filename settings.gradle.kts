pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.kikugie.dev/releases")
        maven("https://maven.kikugie.dev/snapshots")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev")
        maven("https://maven.minecraftforge.net")
        maven("https://maven.neoforged.net/releases/")
    }
}

plugins {
    id("gg.meza.stonecraft") version "1.10.+"
    id("dev.kikugie.stonecutter") version "0.9.+"
}

stonecutter {
    centralScript = "build.gradle.kts"
    kotlinController = true
    shared {
        // Each entry creates one version node per loader, named "<mc>-<loader>" (e.g. "1.20.1-forge"),
        // all mapping to the same real Minecraft version. The loader is read back from the node name.
        fun mc(version: String, vararg loaders: String) {
            for (it in loaders) version("$version-$it", version)
        }

        // MC 1.20.1 ships AE2 on Forge and Fabric; MC 1.21.1 ships AE2 on NeoForge only (no Fabric).
        mc("1.20.1", "fabric", "forge")
        mc("1.21.1", "neoforge")

        vcsVersion = "1.20.1-fabric"
    }
    create(rootProject)
}

rootProject.name = "ae2nobyproduct"
