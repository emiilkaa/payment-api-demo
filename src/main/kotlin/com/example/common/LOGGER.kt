package com.example.common

import org.slf4j.Logger
import org.slf4j.LoggerFactory

object LOGGER {

    val LOCK: Logger = LoggerFactory.getLogger("LOCK")
    val MONITORING: Logger = LoggerFactory.getLogger("MONITORING")
    val OPERATION: Logger = LoggerFactory.getLogger("OPERATION")
    val REPOSITORY_UPDATE: Logger = LoggerFactory.getLogger("REPOSITORY_UPDATE")
    val REQUEST: Logger = LoggerFactory.getLogger("REQUEST_SERVICE")

}