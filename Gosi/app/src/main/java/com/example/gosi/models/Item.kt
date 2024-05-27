package com.example.gosi.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class Item(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "state") val state: String,
    @ColumnInfo(name = "client") val client: String,
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
)

enum class CarState{
    None,
    InUse,
    IsNew,
    IsDeleted,
}