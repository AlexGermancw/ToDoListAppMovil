package com.alexc.todoapp.task.model

import android.os.Parcelable
import com.alexc.todoapp.task.helper.FirebaseHelper
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    var id: String = "",
    var description: String = "",
    var status: Int = 0
) : Parcelable {
    init {
        this.id = FirebaseHelper.getDataBase().push().key ?: ""
    }
}
