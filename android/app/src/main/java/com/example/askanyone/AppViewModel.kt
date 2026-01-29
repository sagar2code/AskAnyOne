package com.example.askanyone

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.askanyone.models.ErrorResponse
import com.example.askanyone.datastore.TokenManager
import androidx.lifecycle.viewModelScope
import com.example.askanyone.models.Answer
import com.example.askanyone.models.CreateAnswerRequest
import com.example.askanyone.models.CreateQuestionRequest
import com.example.askanyone.models.LoginRequest
import com.example.askanyone.models.Question
import com.example.askanyone.models.RegisterRequest
import com.example.askanyone.network.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.launch


class AppViewModel(application : Application) : AndroidViewModel(application) { // constructor +class declaration

    private val tokenManager = TokenManager(application)
    var loginError: String? by mutableStateOf(null)
    var registerError: String? by mutableStateOf(null)
    var token: String? by mutableStateOf(null)

    var getQuestionError: String? by mutableStateOf(null)

    var addQuestionError : String? by mutableStateOf(null)

    var addAnswerError: String? by mutableStateOf(null)

    var myQuestionsError: String? by mutableStateOf(null)

    var detailError by mutableStateOf<String?>(null)

    var deleteQuestionError: String? by mutableStateOf(null)
    var deleteAnswerError: String? by mutableStateOf(null)



    var questions by mutableStateOf<List<Question>>(emptyList())

    var selectedQuestion by mutableStateOf<Question?>(null)

    // for question details
    var answers by mutableStateOf<List<Answer>>(emptyList())
    var myQuestions by mutableStateOf<List<Question>>(emptyList())

    private fun parseErrorMessage(errorBody: String?): String {
        return try {
            val body = errorBody ?: "{}"
            val response = Gson().fromJson(body, ErrorResponse::class.java)
            response.error
        } catch (e: Exception) {
            "Something went wrong"
        }
    }

    //converting json string to error response object by accessing the error field
    // Exception is a class to represent runtime error and e is obv a variable
    fun login(email: String, password: String, onSuccess: () -> Unit) {
        loginError = null

        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.login(
                    LoginRequest(email = email, password = password)//creates kotlin dataclass object
                )
                if (response.isSuccessful) {
                    val tokenValue = response.body()?.token// we are doing this cause response.body can be null
                    if (tokenValue != null) {              //most of the time it wont be null but we are doing it for safety
                        token = tokenValue
                        tokenManager.saveToken(tokenValue)
                        onSuccess()
                    } else {
                        loginError = "Token missing"
                    }
                } else {
                    loginError = parseErrorMessage(response.errorBody()?.string())
                }
            } catch (e: Exception) {
                loginError = e.message ?: "Login failed"
            }
        }
    }

    fun register(email: String, password: String, onSuccess: () -> Unit) {
        registerError = null

        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.register(RegisterRequest(email, password))

                if (response.isSuccessful) {
                    val tokenValue = response.body()?.token
                    if (tokenValue != null) {
                        token = tokenValue
                        tokenManager.saveToken(tokenValue)
                        onSuccess()
                    } else {
                        registerError = "Token missing"
                    }
                } else {
                    registerError = parseErrorMessage(response.errorBody()?.string())// coverts json object to string
                }

            } catch (e: Exception) {
                registerError = e.message ?: "Registration failed"
            }
        }
    }


    fun loadToken() {
        viewModelScope.launch {
            token=tokenManager.getToken()
        }
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            tokenManager.clearToken()
            token = null
            onDone()
        }
    }

    fun fetchQuestions() {
        getQuestionError = null

        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.getAllQuestions()

                if (response.isSuccessful) {
                    questions = response.body() ?: emptyList()
                } else {
                    getQuestionError = "Failed: ${response.code()}"
                }

            } catch (e: Exception) {
                getQuestionError = e.message ?: "Failed to load questions"
            }
        }
    }

    fun addQuestion(title: String, body: String, onSuccess: () -> Unit) {
        addQuestionError = null

        viewModelScope.launch {
            try {
                val savedToken = tokenManager.getToken()

                if (savedToken == null) {
                    addQuestionError = "Not logged in"
                    return@launch
                }

                val response = RetrofitClient.api.addQuestion(
                    request = CreateQuestionRequest(title = title, body = body),
                    token = "Bearer $savedToken"
                )

                if (response.isSuccessful) {
                    fetchQuestions()
                    onSuccess()
                } else {
                    addQuestionError = parseErrorMessage(response.errorBody()?.string())
                    if (response.code() == 403) {
                        kotlinx.coroutines.delay(5000)
                        logout { }

                        return@launch
                    }
                }

            } catch (e: Exception) {
                addQuestionError = e.message ?: "Failed to add question"
            }
        }
    }

    fun loadQuestionDetail(id: Int) {
        detailError = null

        viewModelScope.launch {
            try {
                val qRes = RetrofitClient.api.getQuestionById(id)
                val aRes = RetrofitClient.api.getAnswersByQuestion(id)

                if (qRes.isSuccessful) selectedQuestion = qRes.body()
                else{
                    detailError = parseErrorMessage(qRes.errorBody()?.string())
                }
                if (aRes.isSuccessful) answers = aRes.body() ?: emptyList()
                else{
                    detailError = parseErrorMessage(qRes.errorBody()?.string())
                }

            } catch (e: Exception) {
                detailError = e.message ?: "Failed to load details"
            }
        }
    }

    fun addAnswer(body: String, questionId: Int, onSuccess: () -> Unit) {
        addAnswerError = null

        viewModelScope.launch {
            try {
                val savedToken = tokenManager.getToken()

                if (savedToken == null) {
                    addAnswerError = "Not logged in"
                    return@launch
                }

                val response = RetrofitClient.api.addAnswer(
                    id = questionId,
                    request = CreateAnswerRequest(body = body),
                    token = "Bearer $savedToken"
                )

                if (response.isSuccessful) {
                    loadQuestionDetail(questionId) // refresh answers
                    onSuccess()
                } else {
                    addAnswerError = parseErrorMessage(response.errorBody()?.string())
                    if (response.code() == 403) {
                            kotlinx.coroutines.delay(5000)
                            logout { }

                        return@launch
                    }
                }

            } catch (e: Exception) {
                addAnswerError = e.message ?: "Failed to add answer"
            }
        }
    }

    fun deleteQuestion(questionId: Int, onSuccess: () -> Unit) {
        deleteQuestionError = null

        viewModelScope.launch {
            try {
                val savedToken = tokenManager.getToken()
                if (savedToken == null) {
                    deleteQuestionError = "Not logged in"
                    return@launch
                }

                val response = RetrofitClient.api.deleteQuestion(
                    id = questionId,
                    token = "Bearer $savedToken"
                )

                if (response.isSuccessful) {
                    // refresh list
                    fetchQuestions()// we refresh list from here because updating backend wont change the ui
                    onSuccess()
                } else {
                    val msg = parseErrorMessage(response.errorBody()?.string())
                    deleteQuestionError = msg

                    // optional: logout on expiry
                    if (response.code() == 403 ) {
                        kotlinx.coroutines.delay(5000)
                        logout { }
                    }
                }
            } catch (e: Exception) {
                deleteQuestionError = e.message ?: "Failed to delete question"
            }
        }
    }

    fun deleteAnswer(answerId: Int, questionId: Int, onSuccess: () -> Unit) {
        deleteAnswerError = null

        viewModelScope.launch {
            try {
                val savedToken = tokenManager.getToken()
                if (savedToken == null) {
                    deleteAnswerError = "Not logged in"
                    return@launch
                }

                val response = RetrofitClient.api.deleteAnswer(
                    id = answerId,
                    token = "Bearer $savedToken"
                )

                if (response.isSuccessful) {
                    // refresh details page
                    loadQuestionDetail(questionId)
                    onSuccess()
                } else {
                    val msg = parseErrorMessage(response.errorBody()?.string())
                    deleteAnswerError = msg

                    if (response.code() == 403 ) {
                        kotlinx.coroutines.delay(5000)
                        logout { }
                        return@launch
                    }
                }
            } catch (e: Exception) {
                deleteAnswerError = e.message ?: "Failed to delete answer"
            }
        }
    }

    fun fetchMyQuestions() {
        myQuestionsError = null

        viewModelScope.launch {
            try {
                val savedToken = tokenManager.getToken()
                if (savedToken == null) {
                    myQuestionsError = "Not logged in"
                    return@launch
                }

                val response = RetrofitClient.api.getMyQuestions("Bearer $savedToken")

                if (response.isSuccessful) {
                    myQuestions = response.body() ?: emptyList()
                } else {
                    myQuestionsError = parseErrorMessage(response.errorBody()?.string())

                    if (response.code() == 403) {
                        kotlinx.coroutines.delay(5000)
                        logout { }
                    }
                }
            } catch (e: Exception) {
                myQuestionsError = e.message ?: "Failed to load my doubts"
            }
        }
    }

}
