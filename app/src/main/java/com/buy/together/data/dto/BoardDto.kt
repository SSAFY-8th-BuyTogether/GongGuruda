package com.buy.together.data.dto

data class BoardDto (
    var id: String,
    var title : String,
    var category : String,
    var deadLine : Long,
    var price : Int,
    var content : String,
    var writeTime : Long,
    var writer : String,

    var images : List<String> = listOf(),
    var maxPeople : Int? = null,
    var meetPoint : String? = null,
    var meetTime : Long? = null,
    var buyPoint : String? = null,
) : java.io.Serializable