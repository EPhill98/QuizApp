package com.example.cwquizapp

class MyModel {
    private var modelName: String? = null
    private var modelImage: Int = 0
    var modelCatTag: Int = 0

     fun getNames(): String {
         return modelName.toString()
     }

    fun setNames(name: String){
        this.modelName = name
    }

    fun setCatTag(catTag: Int){
        this.modelCatTag = catTag
    }

    fun getCatTag(): Int {
        return this.modelCatTag
    }

    fun getImages(): Int {
        return modelImage
    }

    fun setImage(imageDrawable: Int){
        this.modelImage = imageDrawable
    }

}