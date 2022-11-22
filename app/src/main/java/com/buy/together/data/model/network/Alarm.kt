package com.buy.together.data.model.network


import com.buy.together.data.model.domain.AlarmDto
import com.buy.together.data.model.domain.BoardDto
import com.buy.together.data.model.domain.CommentDto
import com.google.firebase.Timestamp

data class Alarm(
    var category : String = "기타",
    var dateTime : Timestamp = Timestamp.now(),
    var id : String = "",
    var referContent : String = "",
    var referId : String = "",
    var referTitle : String="",
    var type : String = "",
){
    constructor(boardDto: BoardDto): this(
        category = boardDto.category,
        dateTime = Timestamp.now(),
        id = "ALARM_${Timestamp.now().seconds}_${boardDto.writer}",
        referContent = boardDto.content,
        referId = boardDto.id,
        referTitle= boardDto.title,
        type= "WRITE"
    )

    constructor(category: String,commentDto: CommentDto):this(
        category = category,
        dateTime = Timestamp.now(),
        id = "ALARM_${Timestamp.now().seconds}_${commentDto.writer}",
        referContent = commentDto.content,
        referId = commentDto.id,
        referTitle= commentDto.boardTitle,
        type= "COMMENT"
    )
    
    fun makeToAlarmDto() = AlarmDto(
        if (type==AlarmDto.TYPE.COMMENT.name) AlarmDto.TYPE.COMMENT else AlarmDto.TYPE.WRITE,
        id, category, referId, referTitle, referContent, dateTime.seconds)
}