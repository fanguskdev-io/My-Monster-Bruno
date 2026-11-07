import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(project(":shared"))

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)
}

compose.desktop {
    application {
        mainClass = "com.fanguskdev.mymonsterbruno.DesktopMainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.fanguskdev.mymonsterbruno"
            packageVersion = "1.0.0"
        }
    }
}

// The recognizer of the desktop version on the Mac (ADR 0027): Apple's, on the Mac itself, built from tools/reconocer
// with the Info.plist that macOS asks for inside it, and named to the game when it is run. Elsewhere, or if it cannot be
// built, there is none, and Bruno does not check the word.
val recognizer = tasks.register<Exec>("reconocedor") {
    description = "Compila el reconocedor de voz de la versión del Mac (ADR 0027)."
    val folder = rootProject.layout.projectDirectory.dir("tools/reconocer")
    val source = folder.file("reconocer.swift").asFile
    val plist = folder.file("Info.plist").asFile
    val output = layout.buildDirectory.file("reconocer/reconocer").get().asFile
    inputs.files(source, plist)
    outputs.file(output)
    onlyIf { System.getProperty("os.name").startsWith("Mac") }
    isIgnoreExitValue = true
    doFirst { output.parentFile.mkdirs() }
    commandLine(
        "xcrun", "swiftc", "-O", source.path, "-o", output.path,
        "-Xlinker", "-sectcreate", "-Xlinker", "__TEXT", "-Xlinker", "__info_plist", "-Xlinker", plist.path,
    )
}

// Compose makes the task that runs the desktop version once the build is read, so it is set up when it appears.
tasks.withType<JavaExec>().matching { it.name == "run" }.configureEach {
    dependsOn(recognizer)
    systemProperty("bruno.reconocedor", layout.buildDirectory.file("reconocer/reconocer").get().asFile.path)
}
