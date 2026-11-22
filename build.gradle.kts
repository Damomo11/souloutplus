plugins {
    // 26.x 不混淆时代：loom-back-compat 选 unobf 变体 → 标准 fabric-loom（loomx.loom_version）
    id("dev.kikugie.loom-back-compat")
}

fun prop(name: String): String = project.property(name).toString()

val mc = prop("minecraft_version")

version = "${prop("mod.version")}+$mc"
group = prop("mod.group")
base { archivesName.set(prop("mod.id")) }

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net") { name = "FabricMC" }
    maven("https://api.modrinth.com/maven") { name = "Modrinth" }
    maven("https://maven.terraformersmc.com/releases") { name = "TerraformersMC" }
    maven("https://masa.dy.fi/maven") { name = "Masa" }
    maven("https://masa.dy.fi/maven/sakura-ryoko") { name = "SakuraRyoko" }
    // malilib 1.21.x 的传递依赖 conditional-mixin 在 FallenBreath 自己的 maven 上
    maven("https://maven.fallenbreath.me/releases") { name = "FallenBreath" }
}

dependencies {
    minecraft("com.mojang:minecraft:$mc")
    loomx.applyMojangMappings()
    modImplementation("net.fabricmc:fabric-loader:0.20.1")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.161.0+$mc")

    // MaLiLib at runtime (config GUI, hotkeys) - not bundled
    modImplementation("fi.dy.masa.malilib:malilib-fabric-$mc:0.29.6")

    // Optional integrations: compile-time only, detected at runtime
    modCompileOnly("fi.dy.masa.litematica:litematica-fabric-$mc:0.28.8")
    modCompileOnly("fi.dy.masa.tweakeroo:tweakeroo-fabric-$mc:0.29.5")
    modCompileOnly("com.terraformersmc:modmenu:20.0.3")
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
    // 26.x 运行时即 Java 25（game class major 69），本机 JDK 25 直接编 --release 25
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(25)
}

tasks.processResources {
    val props = mapOf(
        "mod_id" to prop("mod.id"),
        "mod_name" to prop("mod.name"),
        "mod_version" to prop("mod.version"),
        "minecraft_version" to prop("minecraft_version"),
    )
    inputs.properties(props)
    filesMatching("fabric.mod.json") { expand(props) }
}

loom {
    runs {
        named("client") {
            programArguments.addAll(listOf("--width", "1280", "--height", "720", "--username", "SoulTest"))
            runDir("run")
        }
    }
}
