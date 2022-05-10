package com.rrat.datastoredemo

import android.app.DownloadManager
import android.content.*
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rrat.datastoredemo.databinding.ActivityResourceBinding
import com.rrat.datastoredemo.network.ResourceApiService
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.net.MalformedURLException
import java.net.URL

class ResourceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResourceBinding
    private val resourceApiService = ResourceApiService()

    private lateinit var downloadManager: DownloadManager
    private val broadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if(DownloadManager.ACTION_DOWNLOAD_COMPLETE == action)
            {
                Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResourceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerReceiver(broadcastReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        //READ https://agroecology.cl/Recursos/
        downloadMultipleImages()


    }

    private fun downloadMultipleImages(){
        resourceApiService.getResponse(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.i("RESOURCE", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                CoroutineScope(Dispatchers.IO).launch {
                    val responseData =  kotlin.runCatching {
                        response.body?.string()
                    }
                    if(responseData.isSuccess)
                    {
                        //GET LIST STRING WITH URL's Images
                        resourceApiService.getStringHref(responseData.toString())
                        //download(resourceApiService.listHref[0])
                        runOnUiThread {
                            resourceApiService.downloadMultipleImages(this@ResourceActivity)
                            binding.tvResponse.text = resourceApiService.listHref.toString()
                        }
                    }else{
                        Log.i("RESOURCE", "ERROR URL OR SERVER")
                    }

                }
            }

        })
    }


    private fun mSaveMediaToStorage(bitmap: Bitmap?) {
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(this , "Saved to Gallery" , Toast.LENGTH_SHORT).show()
        }
    }
    private fun mStringToURL(string: String): URL? {
        try {
            return URL(string)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return null
    }
    private fun download(url: String) {
        val mainDir = filesDir
        val folder =  mainDir.absolutePath + "/images"
        Log.i("RESOURCE", folder)

        val dir = File(folder)
        if(!dir.exists())
            dir.mkdirs()

        downloadManager.enqueue(DownloadManager.Request(Uri.parse(url))
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
            .setTitle("Download images")
            .setDescription("Images Simulator")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalFilesDir(this, folder, "imagesCultivos.jpg"))

        //.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "imagesCultivos.jpg"))
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }
}