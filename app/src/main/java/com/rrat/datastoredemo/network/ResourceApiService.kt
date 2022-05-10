package com.rrat.datastoredemo.network

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ResourceApiService {

    private val client: OkHttpClient = OkHttpClient()
    private var shareTargetsArray: MutableList<Target?> = ArrayList()
    val listHref = mutableListOf<String>()
    val listName = mutableListOf<String>()

    companion object{
        private const val URL_BASE = "https://agroecology.cl/Recursos/"
    }

    fun getResponse(callback: Callback): Call {
        val request: Request = Request.Builder()
            .url(URL_BASE)
            .build()

        val call = client.newCall(request)
        call.enqueue(callback)
        return call
    }

    fun getStringHref(rawResponse: String) {
        var htmlText = rawResponse
        var indexStartHyperLink = htmlText.indexOf("<a")
        var indexEndHyperLink = htmlText.indexOf("</a>")
        while(indexStartHyperLink != -1){
            val subText: String = htmlText.subSequence(indexStartHyperLink, indexEndHyperLink) as String
            val indexEnd = subText.indexOf(">")
            val href = subText.subSequence(9, indexEnd-1) as String

            if(href.contains("jpg") or href.contains("png") or href.contains("jpeg") or href.contains("bmp"))
            {
                listHref.add(URL_BASE+href)
                listName.add(href)
            }

            htmlText = htmlText.drop(indexEndHyperLink + 3)
            indexStartHyperLink = htmlText.indexOf("<a")
            indexEndHyperLink = htmlText.indexOf("</a>")
        }
        Log.i("RESOURCE", listHref.toString())

    }

    fun downloadMultipleImages(context: Context){
        for(i in listHref.indices)
        {
            //DOWNLOAD IMAGES IN STRING
            downloadImage(context, listHref[i], listName[i])
        }
    }

    private fun downloadImage(context: Context, url: String?, slug: String) {

        val shareTarget = object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) {

                val contextWrapper = ContextWrapper(context)
                val directory: File =
                    contextWrapper.getDir("customDirectory", Context.MODE_PRIVATE)
                val file = File(directory, slug )

                var outputStream: FileOutputStream? = null

                try {
                    outputStream = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.flush()
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    try {
                        outputStream!!.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {}

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
        }

        shareTargetsArray.add(shareTarget)
        Picasso.get().load(url).into(shareTarget)
    }

}