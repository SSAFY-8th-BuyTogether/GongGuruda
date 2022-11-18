package com.buy.together.data.model.network.firestore

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


inline fun <reified  T>observeDoc(docRef: DocumentReference): Flow<T?> = callbackFlow {
    val subscription = docRef.addSnapshotListener { snapshot, error ->
        if(error != null) {
            trySend( null)
            return@addSnapshotListener
        }
        snapshot?.exists()?.let {
            val obj = snapshot.toObject(T::class.java)
            trySend(obj)
        }
    }
    awaitClose { subscription.remove() }
}

inline fun <reified T>observeCollection(colRef: Query): Flow<List<T?>?> = callbackFlow {
    val subscription = colRef.addSnapshotListener { query, error ->
        if (error != null) {
            trySend(null)
            return@addSnapshotListener
        }
        val docs = query?.documents?.map { it.toObject(T::class.java) }
        trySend(docs)
    }
    awaitClose { subscription.remove() }
}

