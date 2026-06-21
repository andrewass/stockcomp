package com.stockcomp.user.internal

class UsernameAlreadyExistsException(
    username: String,
) : RuntimeException("Username $username is already in use")
