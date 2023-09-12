package com.ornoma.phoenix.api.response

import com.google.gson.annotations.SerializedName

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

data class RegistrationRequest(
    var name: String,
    var email:String,
    var password: String
)

data class RegistrationResponse(
    @SerializedName("id")
    val id: Long,
    val name: String,
    val email: String,
    val password: String
)

/*data class PersonaQuestionRequest(
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
)*/

//Create ledgers
data class CreateLedgersRequest(
    var name: String
)

data class CreateLedgersResponse(
    var id: Long,
    var name: String,
    var balance:Long?,
    var transactionCount:Int,
    var parent: Long?
)



data class CreateTransactionGroupRequest(
    val description: String, //Notes
    val transactions: List<TransactionDetail>
)

data class TransactionDetail(
    val ledgerId: Int,
    val debit: Double, //tv->debit
    val credit: Double //tv->credit
)

data class CreateTransactionGroupResponse(
    val id: Int,
    val description: String,
    val transactions: List<TransactionDetailResponse>,
    val transactionTimelineId: Int
)

data class TransactionDetailResponse(
    val id: Int,
    val transactionGroupId: Int,
    val ledgerId: Int,
    val debit: Double,
    val credit: Double
)
