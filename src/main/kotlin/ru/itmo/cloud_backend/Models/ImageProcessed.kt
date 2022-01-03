package ru.itmo.cloud_backend.Models

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.ImageWriter

class ImageProcessed(
    val name:String,
    val image:ImmutableImage,
    val writer:ImageWriter,
    val contentType:String
) {

}

enum class ImageSizeType{
    IMAGE_POSTER,
    IMAGE_SMALL_POSTER
}