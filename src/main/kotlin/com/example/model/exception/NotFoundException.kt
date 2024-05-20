package com.example.model.exception

class NotFoundException(
    details: String
) : RuntimeException(details)