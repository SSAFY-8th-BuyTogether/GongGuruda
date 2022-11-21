package com.buy.together.data.model.domain

data class BoardDto (
    var id: String = "",
    var title : String = "",
    var category : String = "",
    var deadLine : Long = 0L,
    var price : Int = 0,
    var content : String = "",
    var writeTime : Long= 0L,
    var writer : String = "",

    var writerProfile : String? = null,
    var participator : List<String> = listOf(),
    var images : List<String> = listOf(),
    var maxPeople : Int? = null,
    var meetPoint : String? = null,
    var meetTime : Long? = null,
    var buyPoint : String? = null,
) : java.io.Serializable