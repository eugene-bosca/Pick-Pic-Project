package com.bmexcs.pickpic.data.services

sealed class SignInResult {
    data object Success : SignInResult()
    data object NoCredentials : SignInResult()
    data object ConnectionError : SignInResult()
    data object TokenParseError : SignInResult()
    data object UnknownError : SignInResult()
}
