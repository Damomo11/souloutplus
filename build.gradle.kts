plugins {
    // fabric-loom-remap for obfuscated versions (1.21.11 is still remapped)
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
    modImplementation("net.fabricmc:fabric-loader:0.19.3")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.140.2+$mc")

    // MaLiLib at runtime (config GUI, hotkeys) - not bundled
    modImplementation("fi.dy.masa.malilib:malilib-fabric-$mc:0.27.20")

    // Optional integrations: compile-time only, detected at runtime
    modCompileOnly("fi.dy.masa.litematica:litematica-fabric-$mc:0.26.16")
    modCompileOnly("fi.dy.masa.tweakeroo:tweakeroo-fabric-$mc:0.27.15")
    modCompileOnly("com.terraformersmc:modmenu:17.0.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    // 本机只有 JDK 25：不强制 toolchain（它会去找 JDK21 安装而失败），
    // 用当前 JVM 的 javac --release 21 编译（JDK25 完全支持 21 字节码）
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(21)
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
