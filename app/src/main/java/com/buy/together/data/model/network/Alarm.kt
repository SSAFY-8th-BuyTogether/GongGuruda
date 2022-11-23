package com.buy.together.data.model.network


import com.buy.together.data.model.domain.AlarmDto
import com.buy.together.data.model.domain.CommentDto
import com.google.firebase.Timestamp

data class Alarm(
    var type : String = "",
    var id : String = "",
    var category : String = "기타",
    var referId : String = "",
    var referTitle : String="",
    var referContent : String = "",
    var dateTime : Timestamp = Timestamp.now(),
){
    constructor(category: String, commentDto: CommentDto): this(
        category = category,
        dateTime = Timestamp.now(),
        id = "ALARM_${Timestamp.now().seconds}_${commentDto.writer}",
        referContent = commentDto.boardTitle,
        referId = commentDto.boardId,
        referTitle= commentDto.boardTitle,
        type= "WRITE"
    )

    constructor(category: String, mentionComent : String,commentDto: CommentDto):this(
        category = category,
        dateTime = Timestamp.now(),
        id = "ALARM_${Timestamp.now().seconds}_${commentDto.writer}",
        referContent = mentionComent,
        referId = commentDto.boardId,
        referTitle= commentDto.boardTitle,
        type= "COMMENT"
    )
    
    fun makeToAlarmDto() = AlarmDto(
        if (type==AlarmDto.TYPE.COMMENT.name) AlarmDto.TYPE.COMMENT else AlarmDto.TYPE.WRITE,
        id, category, referId, referTitle, referContent, dateTime.seconds)
}