package com.parkwoocheol.firebase.firestore.ktx.extensions

import com.google.firebase.firestore.*
import com.parkwoocheol.firebase.firestore.ktx.model.FirestoreResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

/**
 * Enhances snapshots() with FirestoreResult wrapper.
 */
fun DocumentReference.snapshotsWithState(
    metadataChanges: MetadataChanges = MetadataChanges.EXCLUDE
): Flow<FirestoreResult<DocumentSnapshot>> = flow {
    emit(FirestoreResult.Loading)
    snapshots(metadataChanges)
        .map { FirestoreResult.Success(it) }
        .catch { throwable -> emit(FirestoreResult.Error(throwable)) }
        .collect { emit(it) }
}

/**
 * Enhances dataObjects() with FirestoreResult wrapper.
 */
inline fun <reified T : Any> DocumentReference.dataObjectsWithState(
    metadataChanges: MetadataChanges = MetadataChanges.EXCLUDE
): Flow<FirestoreResult<T?>> = flow {
    emit(FirestoreResult.Loading)
    dataObjects<T>(metadataChanges)
        .map { FirestoreResult.Success(it) }
        .catch { throwable -> emit(FirestoreResult.Error(throwable)) }
        .collect { emit(it) }
}

/**
 * Updates specific fields in a document.
 */
suspend fun DocumentReference.updateData(
    updates: Map<String, Any?>
): FirestoreResult<Unit> = try {
    update(updates).await()
    FirestoreResult.Success(Unit)
} catch (throwable: Throwable) {
    FirestoreResult.Error(throwable)
}

/**
 * Enhances snapshots() with FirestoreResult wrapper for collections.
 */
fun CollectionReference.snapshotsWithState(
    metadataChanges: MetadataChanges = MetadataChanges.EXCLUDE
): Flow<FirestoreResult<QuerySnapshot>> = flow {
    emit(FirestoreResult.Loading)
    snapshots(metadataChanges)
        .map { FirestoreResult.Success(it) }
        .catch { throwable -> emit(FirestoreResult.Error(throwable)) }
        .collect { emit(it) }
}

/**
 * Enhances dataObjects() with FirestoreResult wrapper for collections.
 */
inline fun <reified T : Any> CollectionReference.dataObjectsWithState(
    metadataChanges: MetadataChanges = MetadataChanges.EXCLUDE
): Flow<FirestoreResult<List<T>>> = flow {
    emit(FirestoreResult.Loading)
    dataObjects<T>(metadataChanges)
        .map { FirestoreResult.Success(it) }
        .catch { throwable -> emit(FirestoreResult.Error(throwable)) }
        .collect { emit(it) }
}