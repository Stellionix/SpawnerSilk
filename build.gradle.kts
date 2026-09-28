allprojects {
    group = "me.crylonz.spawnersilk"
    version = "5.9.3"
}

tasks.register("printVersion") {
    doLast {
        println(project.version)
    }
}
