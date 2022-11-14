package com.inca.task

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

//We do not expect any improvement in this class, but feel free to refactor it if you want
@SpringBootApplication
@ConfigurationPropertiesScan
class IncaTestTasksApplication

fun main(args: Array<String>) {
    runApplication<IncaTestTasksApplication>(*args).stop()
}
