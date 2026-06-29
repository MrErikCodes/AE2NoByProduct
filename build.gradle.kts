import gg.meza.stonecraft.mod

plugins {
    id("gg.meza.stonecraft")
}

repositories {
    // Applied Energistics 2 + GuideME, for add-on developers.
    maven("https://modmaven.dev/") {
        name = "Modmaven"
        content {
            includeGroup("appeng")
            includeGroup("org.appliedenergistics")
        }
    }
    // Modrinth's maven serves current mod jars by slug, used for the 1.21.1 GuideME dev runtime
    // (modmaven lags behind the GuideME version AE2 19.2.x requires).
    maven("https://api.modrinth.com/maven") {
        name = "Modrinth"
        content { includeGroup("maven.modrinth") }
    }
}

dependencies {
    // Architectury API: loader-agnostic networking, registries and creative tabs. The artifact suffix
    // (forge/fabric/neoforge) matches mod.loader exactly, so the same shared code compiles on each.
    "modImplementation"("dev.architectury:architectury-${mod.loader}:${mod.prop("architectury_version")}")

    // Applied Energistics 2: full mod at compile + dev runtime so the shared appeng.*-targeting mixin
    // resolves. The artifact name is per-version: 1.20.1 uses appliedenergistics2-<loader>; AE2 19.x
    // (1.21.1) dropped the loader suffix (NeoForge only). Defaults to the 1.20.1 form.
    "modImplementation"("appeng:${mod.prop("ae2_artifact", "appliedenergistics2-${mod.loader}")}:${mod.prop("ae2_version")}")
    // GuideME is a hard runtime dependency of AE2, needed only for the dev client (not to compile).
    // The full coordinate is per-version: 1.20.1 uses org.appliedenergistics:guideme from modmaven;
    // 1.21.1 uses maven.modrinth:guideme (modmaven lags the version AE2 19.2.x requires). Optional.
    mod.prop("guideme", "").takeIf { it.isNotEmpty() }?.let {
        "modRuntimeOnly"(it)
    }

    if (mod.isFabric) {
        // Team Reborn Energy API: AE2-fabric ships it as a nested jar that loom does not surface on the
        // dev classpath, so AE2 crashes at startup without it. Dev runtime only.
        "modRuntimeOnly"("teamreborn:energy:3.0.0")
    }

    // Plain JUnit unit tests for the loader-agnostic decision logic (EffectiveStateTest).
    "testImplementation"(platform("org.junit:junit-bom:5.10.2"))
    "testImplementation"("org.junit.jupiter:junit-jupiter")
    "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

// Gradle 9 fails on implicit task dependencies. On Forge, Stonecraft's generatePackMCMetaJson writes
// into build/resources/main, which compileTestJava consumes via the main source-set output, so make
// that ordering explicit. The matcher is empty (no-op) on loaders without the task, e.g. Fabric.
tasks.matching { it.name == "compileTestJava" }.configureEach {
    dependsOn(tasks.matching { it.name == "generatePackMCMetaJson" })
}

// Forge's dev launch loads the mod as a split classes/resources sourceset and does not reliably pick
// up the mixin configs from mods.toml [[mixins]], so register them with loom explicitly (the shipped
// jar still uses mods.toml). NeoForge and Fabric read their configs from their own metadata.
if (mod.isForge) {
    loom {
        forge.mixinConfigs("ae2nobyproduct.mixins.json", "ae2nobyproduct.client.mixins.json")
    }
}

modSettings {
    clientOptions {
        narrator = false
    }
}

// CurseForge requires every file to declare its environment (client/server); Stonecraft's auto-publish
// config does not set it, so publishCurseforge fails with "At least one of client or server must be set
// to true". This is a server AND client mod, so mark both required. Additive to Stonecraft's curseforge
// config, and only matters when publishing (no effect on a normal build).
extensions.configure<me.modmuss50.mpp.ModPublishExtension> {
    curseforge {
        clientRequired.set(true)
        serverRequired.set(true)
    }
}
