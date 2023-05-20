package com.nativenerds.develapp.todo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "task_table")
class Task12(
    @ColumnInfo(name = "title") var title: String?,
    @ColumnInfo(name = "submit_date") var submitdate: String?,
    @ColumnInfo(name = "progress") var progress: Int?,
    @ColumnInfo(name = "description") var description: String?,
)
{
    @PrimaryKey(autoGenerate = true) var id = 0
}