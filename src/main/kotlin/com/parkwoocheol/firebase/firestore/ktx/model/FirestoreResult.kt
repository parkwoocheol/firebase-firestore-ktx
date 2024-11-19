package com.parkwoocheol.firebase.firestore.ktx.model

sealed class FirestoreResult<out T> {
    data class Success<T>(val data: T) : FirestoreResult<T>()
    data class Error(val throwable: Throwable) : FirestoreResult<Nothing>()
    data object Loading : FirestoreResult<Nothing>()
}

inline fun <T> FirestoreResult<T>.onSuccess(action: (T) -> Unit): FirestoreResult<T> {
    if (this is FirestoreResult.Success) action(data)
    return this
}

inline fun <T> FirestoreResult<T>.onError(action: (Throwable) -> Unit): FirestoreResult<T> {
    if (this is FirestoreResult.Error) action(throwable)
    return this
}

inline fun <T> FirestoreResult<T>.onLoading(action: () -> Unit): FirestoreResult<T> {
    if (this is FirestoreResult.Loading) action()
    return this
}
