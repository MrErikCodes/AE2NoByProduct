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
}

dependencies {
    // Architectury API: loader-agnostic networking, registries and creative tabs. The artifact suffix
    // (forge/fabric/neoforge) matches mod.loader exactly, so the same shared code compiles on each.
    "modImplementation"("dev.architectury:architectury-${mod.loader}:${mod.prop("architectury_version")}")

    // Applied Energistics 2: full mod at compile + dev runtime so the shared appeng.*-targeting mixin
    // resolves. The artifact name is per-version: 1.20.1 uses appliedenergistics2-<loader>; AE2 19.x
    // (1.21.1) dropped the loader suffix (NeoForge only). Defaults to the 1.20.1 form.
    "modImplementation"("appeng:${mod.prop("ae2_artifact", "appliedenergistics2-${mod.loader}")}:${mod.prop("ae2_version")}")
    // GuideME is a hard runtime dependency of AE2; not needed to compile our code, so it is optional
    // (omit the property when the dev runtime resolves GuideME another way, e.g. bundled inside AE2).
    mod.prop("guideme_version", "").takeIf { it.isNotEmpty() }?.let {
        "modRuntimeOnly"("org.appliedenergistics:guideme:$it")
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

modSettings {
    clientOptions {
        narrator = false
    }
}
