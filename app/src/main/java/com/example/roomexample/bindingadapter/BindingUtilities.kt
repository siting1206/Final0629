package com.example.roomexample.bindingadapter

import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.roomexample.database.Scene

//for resolve app:setImage in the item_layout.xml
@BindingAdapter("setImage", "asIcon")
fun ImageView.setSceneImage(scene: Scene?, asIcon: Boolean) {
    scene?.let {
        val option = if (asIcon)   //resize into a small icon
            RequestOptions().centerCrop().override(60, 60)
        else
            RequestOptions().centerCrop()

        if (scene.photoFile.isNotEmpty()) {  //show an external photo
            Glide.with(this.context)
                .load(Uri.parse(scene.photoFile))
                .apply(option)
                .into(this)
        } else {   //show a photo from the resource folder
            //setImageResource(scene.photoId)
            Glide.with(this.context)
                .load(scene.photoId)
                .apply(option)
                .into(this)
        }
    }
}

//for stopping the progressbar
@BindingAdapter("Data")
fun ProgressBar.setViewVisibility(dataChecked: Any?) {
    visibility = if (dataChecked != null) View.GONE else View.VISIBLE
}
