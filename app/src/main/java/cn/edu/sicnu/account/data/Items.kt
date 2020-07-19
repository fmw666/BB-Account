package cn.edu.sicnu.account.data


data class Items (
    var id: Int ?= 0,
    var in_or_out: Int ?= null,
    var money: Double ?= null,
    var type: String ?= null,
    var note: String ?= null,
    var time: String ?= "${Time.year}.${Time.month}.${Time.day}"
)