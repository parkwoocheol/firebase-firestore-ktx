# Firebase Firestore KTX

Type-safe Kotlin extensions for Firebase Firestore with enhanced Flow support.

[![](https://jitpack.io/v/parkwoocheol/firebase-firestore-ktx.svg)](https://jitpack.io/#parkwoocheol/firebase-firestore-ktx)
[![Kotlin](https://img.shields.io/badge/kotlin-1.9.22-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

## Features 🚀

- 💫 Enhanced Firebase KTX extensions with FirestoreResult wrapper
- 🔄 Improved Flow support with integrated error handling
- 📦 Consistent Loading, Success, and Error states
- 🎯 Type-safe property references for queries
- 🔁 Automatic retry support for transactions

## Installation 📦

Add the JitPack repository and include the library:

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        maven("https://jitpack.io")
    }
}

// build.gradle.kts (app level)
dependencies {
    implementation("com.github.parkwoocheol:firebase-firestore-ktx:1.0.0")
}
```

Note: Replace `1.0.0` with the latest version from the JitPack badge above.

## Usage Guide 🔥

### 1. Data Models

```kotlin
data class Developer(
    @DocumentId override val id: String = "",
    val name: String,
    val email: String,
    val role: String,
    @ServerTimestamp
    val createdAt: Timestamp? = null
) : FirestoreDocument
```

### 2. Real-time Document Updates

```kotlin
// Using enhanced dataObjects with state handling
developerRef.dataObjectsWithState<Developer>()
    .onEach { result ->
        when (result) {
            is FirestoreResult.Success -> {
                val developer = result.data
                updateUI(developer)
            }
            is FirestoreResult.Error -> {
                // Direct access to Throwable
                handleError(result.throwable)
            }
            FirestoreResult.Loading -> showLoading()
        }
    }
    .collect()

// Using enhanced snapshots
developerRef.snapshotsWithState()
    .onEach { result ->
        when (result) {
            is FirestoreResult.Success -> handleSnapshot(result.data)
            is FirestoreResult.Error -> handleError(result.throwable)
            FirestoreResult.Loading -> showLoading()
        }
    }
    .collect()
```

### 3. Query Operations

```kotlin
// Type-safe queries with real-time updates
developersRef
    .whereEqualTo(Developer::role, "senior")
    .whereGreaterThan(Developer::experience, 5)
    .orderByDesc(Developer::createdAt)
    .dataObjectsWithState<Developer>()
    .collect { result ->
        when (result) {
            is FirestoreResult.Success -> updateList(result.data)
            is FirestoreResult.Error -> handleError(result.throwable)
            FirestoreResult.Loading -> showLoading()
        }
    }
```

### 4. Transactions

```kotlin
firestore.runTransactionWithRetry { transaction ->
    // Get current data
    val developerResult = transaction.getData<User>(userRef)

    developerResult.onSuccess { developer ->
        // Update developer profile
        transaction.setData(userRef, developer.copy(
            role = "senior developer",
            lastPromoted = Timestamp.now()
        ))
    }
}
```

### 5. Batch Operations

```kotlin
// DSL-style batch operations
firestore.batch { 
    set(parkRef, Developer(
        name = "Woocheol Park",
        role = "senior"
    ))
    update(kimRef, mapOf("role" to "lead"))
    delete(leeRef)
}

// Multiple document updates
val updates = mapOf(
    parkRef to Developer(name = "Woocheol Park", role = "senior"),
    kimRef to Developer(name = "Minsoo Kim", role = "junior")
)
firestore.batchSet(updates, merge = true)
    .onSuccess { /* handle success */ }
    .onError { throwable -> handleError(throwable) }
```

### 6. Error Handling

```kotlin
// Using extension functions with Flow error handling
developerRef.dataObjectsWithState<Developer>()
    .onEach { result ->
        when (result) {
            is FirestoreResult.Success -> {
                result.data?.let { developer ->
                    updateUI(developer)
                } ?: showEmpty()
            }
            is FirestoreResult.Error -> {
                when (val throwable = result.throwable) {
                    is FirebaseFirestoreException -> handleFirestoreError(throwable)
                    else -> handleGeneralError(throwable)
                }
            }
            FirestoreResult.Loading -> showLoading()
        }
    }
    .catch { throwable -> 
        // Handle Flow collection errors
        handleFlowError(throwable)
    }
    .collect()
```

### 7. ProGuard Rules

If you're using ProGuard, add these rules to your `proguard-rules.pro`:

```proguard
# Firebase Models
-keepclassmembers class com.your.package.models.** {
    *;
}
```

## License 📄

```
Copyright 2024 Woocheol Park

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```