package com.parkwoocheol.firebase.firestore.ktx.model

import com.google.firebase.firestore.DocumentId

/**
 * Base interface for Firestore documents.
 */
interface FirestoreDocument {
    @get:DocumentId
    val id: String
}
