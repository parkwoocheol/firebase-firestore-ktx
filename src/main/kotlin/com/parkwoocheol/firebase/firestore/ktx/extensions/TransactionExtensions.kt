package com.parkwoocheol.firebase.firestore.ktx.extensions

import com.google.firebase.firestore.*
import com.parkwoocheol.firebase.firestore.ktx.model.FirestoreResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

/**
 * Executes a transaction with retry support.
 */
suspend fun <T> FirebaseFirestore.runTransactionWithRetry(
    maxAttempts: Int = 5,
    block: Transaction.() -> T
): FirestoreResult<T> {
    var attempts = 0
    var lastThrowable: Throwable? = null

    while (attempts < maxAttempts) {
        try {
            val result = runTransaction { transaction ->
                transaction.block()
            }.await()
            return FirestoreResult.Success(result)
        } catch (throwable: Throwable) {
            lastThrowable = throwable
            attempts++
            if (attempts >= maxAttempts) break
            delay(attempts * 500L)
        }
    }

    return FirestoreResult.Error(
        lastThrowable ?: FirebaseFirestoreException(
            "Transaction failed after $maxAttempts attempts",
            FirebaseFirestoreException.Code.ABORTED
        )
    )
}

/**
 * Gets and deserializes a document within a transaction.
 */
inline fun <reified T : Any> Transaction.getData(documentRef: DocumentReference): FirestoreResult<T> = try {
    get(documentRef).toObject<T>()?.let {
        FirestoreResult.Success(it)
    } ?: FirestoreResult.Error(NoSuchElementException("Document not found"))
} catch (throwable: Throwable) {
    FirestoreResult.Error(throwable)
}

/**
 * Sets document data within a transaction.
 */
inline fun <reified T : Any> Transaction.setData(
    documentRef: DocumentReference,
    data: T,
    merge: Boolean = false
): FirestoreResult<Unit> = try {
    if (merge) {
        set(documentRef, data, SetOptions.merge())
    } else {
        set(documentRef, data)
    }
    FirestoreResult.Success(Unit)
} catch (throwable: Throwable) {
    FirestoreResult.Error(throwable)
}

/**
 * Deletes a document within a transaction.
 */
fun Transaction.deleteDocument(documentRef: DocumentReference): FirestoreResult<Unit> = try {
    delete(documentRef)
    FirestoreResult.Success(Unit)
} catch (throwable: Throwable) {
    FirestoreResult.Error(throwable)
}