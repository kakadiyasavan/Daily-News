package com.example.dailynews

import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.dailynews.Model.NewsModel
import com.example.dailynews.databinding.ActivityMainBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"
    lateinit var storegeref : StorageReference
    lateinit var refrence : DatabaseReference
    lateinit var binding: ActivityMainBinding
lateinit var uri:Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storegeref = FirebaseStorage.getInstance().reference
        refrence = FirebaseDatabase.getInstance().reference

        binding.btnSelectImg.setOnClickListener {
            var intent = Intent(ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent,25)
        }

        binding.btnSubmit.setOnClickListener {
            var ref = storegeref.child("images/${uri.lastPathSegment}.jpg")
            var uploadTask = ref.putFile(uri)

            var uriTask = uploadTask.continueWithTask { task ->
                if (task.isSuccessful){
                    task.exception.let {
                        throw it!!
                    }
                }
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful){
                    var dowloadUri = task.result
                    var key = refrence.root.push().key
                    var title = binding.edtTitle.text.toString()
                    var description = binding.edtDescription.text.toString()
                    var data = NewsModel(title, description,key.toString(),dowloadUri.toString())
refrence.root.child("User").child(key.toString()).setValue(data)
                    Toast.makeText(this,"Data Add successfully",Toast.LENGTH_SHORT).show()

                    binding.run {
                        edtTitle.setText("")
                        edtDescription.setText("")
                        btnSelectImg.setImageResource(0)
                    }
                }else{
                }
            }
        }
    }
   }