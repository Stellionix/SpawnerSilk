allprojects {
    group = "me.crylonz.spawnersilk"
    version = "5.9.2"
}

tasks.register("printVersion") {
    doLast {
        println(project.version)
    }
}
