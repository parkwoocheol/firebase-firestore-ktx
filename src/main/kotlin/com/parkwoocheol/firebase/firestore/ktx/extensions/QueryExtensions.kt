package com.parkwoocheol.firebase.firestore.ktx.extensions

import com.google.firebase.firestore.*
import com.parkwoocheol.firebase.firestore.ktx.model.FirestoreResult
import kotlinx.coroutines.flow.*
import kotlin.reflect.KProperty1

/**
 * Enhances snapshots() with FirestoreResult wrapper for queries.
 */
fun Query.snapshotsWithState(
    metadataChanges: MetadataChanges = MetadataChanges.EXCLUDE
): Flow<FirestoreResult<QuerySnapshot>> = flow {
    emit(FirestoreResult.Loading)
    snapshots(metadataChanges)
        .map { FirestoreResult.Success(it) }
        .catch { throwable -> emit(FirestoreResult.Error(throwable)) }
        .collect { emit(it) }
}

/**
 * Enhances dataObjects() with FirestoreResult wrapper for queries.
 */
inline fun <reified T : Any> Query.dataObjectsWithState(
    metadataChanges: MetadataChanges = MetadataChanges.EXCLUDE
): Flow<FirestoreResult<List<T>>> = flow {
    emit(FirestoreResult.Loading)
    dataObjects<T>(metadataChanges)
        .map { FirestoreResult.Success(it) }
        .catch { throwable -> emit(FirestoreResult.Error(throwable)) }
        .collect { emit(it) }
}

// Type-safe query builders
fun <T, V> Query.whereEqualTo(field: KProperty1<T, V>, value: V): Query =
    whereEqualTo(field.name, value)

fun <T, V : Comparable<V>> Query.whereGreaterThan(field: KProperty1<T, V>, value: V): Query =
    whereGreaterThan(field.name, value)

fun <T, V : Comparable<V>> Query.whereGreaterThanOrEqualTo(field: KProperty1<T, V>, value: V): Query =
    whereGreaterThanOrEqualTo(field.name, value)

fun <T, V : Comparable<V>> Query.whereLessThan(field: KProperty1<T, V>, value: V): Query =
    whereLessThan(field.name, value)

fun <T, V : Comparable<V>> Query.whereLessThanOrEqualTo(field: KProperty1<T, V>, value: V): Query =
    whereLessThanOrEqualTo(field.name, value)

fun <T> Query.whereArrayContains(field: KProperty1<T, *>, value: Any): Query =
    whereArrayContains(field.name, value)

fun <T> Query.whereArrayContainsAny(field: KProperty1<T, *>, values: List<Any>): Query =
    whereArrayContainsAny(field.name, values)

fun <T, V> Query.whereIn(field: KProperty1<T, V>, values: Collection<V>): Query =
    whereIn(field.name, values.toList())

fun <T, V> Query.whereNotIn(field: KProperty1<T, V>, values: Collection<V>): Query =
    whereNotIn(field.name, values.toList())

fun <T> Query.orderByAsc(field: KProperty1<T, *>): Query =
    orderBy(field.name, Query.Direction.ASCENDING)

fun <T> Query.orderByDesc(field: KProperty1<T, *>): Query =
    orderBy(field.name, Query.Direction.DESCENDING)
