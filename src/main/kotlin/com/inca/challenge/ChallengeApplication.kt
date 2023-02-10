package com.inca.challenge

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class ChallengeApplication

fun main(args: Array<String>) {
    runApplication<ChallengeApplication>(*args).close()
}
