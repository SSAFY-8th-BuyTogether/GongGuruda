package com.buy.together.data.model.network

import com.buy.together.data.model.domain.BoardDto
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

}