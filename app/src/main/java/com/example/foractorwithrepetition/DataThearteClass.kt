package com.example.foractorwithrepetition

class DataThearteClass(
    private var dataTitle: String,
    private var dataDesc: String,
    private var dataLang: String,
    private var dataImage: Int,
    private var dataUri: String
    //val imageUrl: String?  // URL-адрес изображения
) {
    fun getDataTitle(): String {
        return dataTitle
    }

    fun getDataDesc(): String {
        return dataDesc
    }

    fun getDataLang(): String {
        return dataLang
    }

    fun getDataImage(): Int {
        return dataImage
    }

    fun getDataUri(): String{
        return dataUri
    }
//    fun getImageUrl(): String?{
//        return imageUrl
//    }

}

