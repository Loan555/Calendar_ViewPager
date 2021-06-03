package com.loan555.myviewpager.model

import java.io.Serializable

class DataNoteItem(
    var noteId: Long,
    var date: CalendarDateModel,
    var titleHead: String,
    var titleBody: String
) : Serializable