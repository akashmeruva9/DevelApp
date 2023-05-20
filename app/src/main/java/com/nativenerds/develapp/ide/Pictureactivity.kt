package com.nativenerds.develapp.ide

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector
import com.nativenerds.develapp.R


class Pictureactivity : AppCompatActivity() {


    private var img: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.nativenerds.develapp.R.layout.activity_picture)

        // on below line we are initializing our variables.
        img = findViewById<View>(R.id.image_view) as ImageView
        ImagePicker.with(this)
            .crop()
            .cameraOnly()
            .compress(1024)			//Final image size will be less than 1 MB(Optional)
            .maxResultSize(1080, 1920)	//Final image resolution will be less than 1080 x 1080(Optional)
            .start()

    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            if (data != null) {
                img?.setImageURI(data.dataString?.toUri())
                detectTxt()
            }
        }
    }
    private fun detectTxt() {
        val imageBitmap = img?.drawable?.toBitmap()
        // this is a method to detect a text from image.
        // below line is to create variable for firebase
        // vision image and we are getting image bitmap.
        val image: FirebaseVisionImage? = imageBitmap?.let { FirebaseVisionImage.fromBitmap(it) }

        // below line is to create a variable for detector and we
        // are getting vision text detector from our firebase vision.
        val detector: FirebaseVisionTextDetector =
            FirebaseVision.getInstance().getVisionTextDetector()

        // adding on success listener method to detect the text from image.
        if (image != null) {
            detector.detectInImage(image)
                .addOnSuccessListener(OnSuccessListener<Any> { firebaseVisionText -> // calling a method to process
                    // our text after extracting.
                    processTxt(firebaseVisionText as FirebaseVisionText)
                }).addOnFailureListener(OnFailureListener { // handling an error listener.
                    Toast.makeText(
                        this,
                        "Fail to detect the text from image..",
                        Toast.LENGTH_SHORT
                    ).show()
                })
        }
    }

    private fun processTxt(text: FirebaseVisionText) {
        // below line is to create a list of vision blocks which
        // we will get from our firebase vision text.
        val blocks: List<FirebaseVisionText.Block> = text.getBlocks()

        // checking if the size of the
        // block is not equal to zero.
        if (blocks.size == 0) {
            // if the size of blocks is zero then we are displaying
            // a toast message as no text detected.
            Toast.makeText(this, "No Text ", Toast.LENGTH_LONG).show()
            return
        }
        // extracting data from each block using a for loop.
        for (block in text.getBlocks()) {
            // below line is to get text
            // from each block.
            val txt: String = block.getText()

            // below line is to set our
            // string to our text view.
            val intent = Intent(this  , destress()::class.java)
            intent.putExtra("code" , txt)
            startActivity(intent)
        }
    }

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
    }
}