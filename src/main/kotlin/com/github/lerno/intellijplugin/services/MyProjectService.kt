package com.github.lerno.intellijplugin.services

import com.intellij.openapi.project.Project
import com.github.lerno.intellijplugin.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
