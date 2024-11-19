package com.parkwoocheol.firebase.firestore.ktx.extensions

import com.google.firebase.firestore.*
import com.parkwoocheol.firebase.firestore.ktx.model.FirestoreResult
import kotlinx.coroutines.tasks.await

/**
 * Executes batch write operations with receiver-typed lambda for cleaner DSL-style syntax.
 */
suspend fun FirebaseFirestore.batch(operation: WriteBatch.() -> Unit): FirestoreResult<Unit> = try {
    batch().apply(operation).commit().await()
    FirestoreResult.Success(Unit)
} catch (throwable: Throwable) {
    FirestoreResult.Error(throwable)
}

/**
 * Performs batch set operations on multiple documents.
 */
suspend inline fun <reified T : Any> FirebaseFirestore.batchSet(
    documents: Map<DocumentReference, T>,
    merge: Boolean = false
): FirestoreResult<Unit> = batch {
    documents.forEach { (ref, data) ->
        if (merge) {
            set(ref, data, SetOptions.merge())
        } else {
            set(ref, data)
        }
    }
}

/**
 * Deletes multiple documents in a single batch operation.
 */
suspend inline fun FirebaseFirestore.batchDelete(
    documents: List<DocumentReference>
): FirestoreResult<Unit> = batch {
    documents.forEach { ref ->
        delete(ref)
    }
}

/**
 * Performs batch updates on multiple documents.
 */
suspend inline fun FirebaseFirestore.batchUpdate(
    updates: Map<DocumentReference, Map<String, Any?>>
): FirestoreResult<Unit> = batch {
    updates.forEach { (ref, data) ->
        update(ref, data)
    }
}