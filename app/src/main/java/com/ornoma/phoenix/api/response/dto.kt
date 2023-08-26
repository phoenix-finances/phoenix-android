package com.ornoma.phoenix.api.response

data class LoginRequest (
    var email:String,
    var password:String
)

data class LoginResponse(
    var jwtToken:String,
    var username:String
)

data class UserResponse(
    var id: Long,
    var email: String,
    var name: String
)

data class PersonaQuestionRequest(
    var title: LocalizedText,
    var text: LocalizedText,
    var hint: LocalizedText,
    var commentIncluded: Boolean,
    var entityOrder: Int,
    var type: String
)

data class LocalizedText(
    var translatedLanguageArray: List<TranslatedLanguage>,
    var type: String,
    var code: String
)

data class TranslatedLanguage(
    var language: String,
    var text: String
)