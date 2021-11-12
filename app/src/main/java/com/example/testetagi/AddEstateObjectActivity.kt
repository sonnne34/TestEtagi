package com.example.testetagi

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.testetagi.databinding.ActivityAddEstateObjectBinding
import com.example.testetagi.singletons.EstateSingleton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException
import java.util.*
import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AlertDialog

class AddEstateObjectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEstateObjectBinding

    private lateinit var inputAdress: TextInputLayout
    private lateinit var inputArea: TextInputLayout
    private lateinit var inputCost: TextInputLayout
    private lateinit var inputFloor: TextInputLayout
    private lateinit var inputRooms: TextInputLayout

    private lateinit var editAdress: EditText
    private lateinit var editArea: EditText
    private lateinit var editCost: EditText
    private lateinit var editFloor: EditText
    private lateinit var editRooms: EditText

    private lateinit var image: ImageView

    private lateinit var btnAddBD: Button
    private lateinit var btnCancelAdd: Button

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private lateinit var mDataBase: DatabaseReference

    private lateinit var id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddEstateObjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputAdress = binding.inputAddAdress
        inputArea = binding.inputAddArea
        inputCost = binding.inputAddCost
        inputFloor = binding.inputAddFloor
        inputRooms = binding.inputAddRooms

        editArea = binding.editAddArea
        editCost = binding.editAddCost
        editFloor = binding.editAddFloor
        editRooms = binding.editAddRooms
        editAdress = binding.editAddAdress
        editAdress.setText("ул. ")

        image = binding.addImg

        btnAddBD = binding.btnAddFragment
        btnCancelAdd = binding.btnCancelAdd

        //инициализируем бд
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        mDataBase = FirebaseDatabase.getInstance().getReference("Etagi/Items")

        //создаём id для нового объекта
        val idRand: UUID = UUID.randomUUID()
        id = idRand.toString()

        //onClick выбора фото
        chooseImage()
        //onClick отмены
        btnCancelAdd()
        ////onClick отправки объета
        btnAddBD()

    }

    //onClick отмены
    private fun btnCancelAdd(){
        btnCancelAdd.setOnClickListener {
            onBackPressed()
        }
    }

    ////onClick отправки объета
    private fun  btnAddBD(){
        btnAddBD.setOnClickListener {
            checkingEdit()
        }
    }

    private fun checkingEdit(){

        //проверка на полноту заполнения данных
            if (editAdress.text.isEmpty() || editArea.text.isEmpty() ||
                editCost.text.isEmpty() || editFloor.text.isEmpty() ||
                editRooms.text.isEmpty()) {
                if (editAdress.text.isEmpty()) {
                    inputAdress.error = "Обязательное поле"
                } else {
                    inputAdress.error = null
                }
                if (editArea.text.isEmpty()) {
                    inputArea.error = "Обязательное поле"
                } else {
                    inputArea.error = null
                }
                if (editRooms.text.isEmpty()) {
                    inputRooms.error = "Обязательное поле"
                } else {
                    inputRooms.error = null
                }
                if (editCost.text.isEmpty()) {
                    inputCost.error = "Обязательное поле"
                } else {
                    inputCost.error = null
                }
                if (editFloor.text.isEmpty()) {
                    inputFloor.error = "Обязательное поле"
                } else {
                    inputFloor.error = null
                }
            } else {
                //отправить фото
                uploadImage()

                //Диалоговое окно об успешной отправки
                openSuccessDialog(this)

                //обновить список объектов
                EstateSingleton.notifyTwo()


            }
    }

    //выбор картинки из галереи
        private fun chooseImage() {
        image.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            filePath = data.data!!
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                image.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    //отправка фото в бд
    private fun uploadImage() {
        //проверка наличия картинки для отправки
        if (filePath != null) {
            //диалоговое окно с состоянием загрузки
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Загрузка...")
            progressDialog.show()
            //соединение с бд
            val ref = storageReference!!.child("Etagi/$id")
            ref.putFile(filePath!!)
                .addOnSuccessListener {
                    //диалоговое окно с состоянием загрузки
                    progressDialog.dismiss()
                    Toast.makeText(this, "Загрузка", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    //диалоговое окно на случай ошибки
                    progressDialog.dismiss()
                    Toast.makeText(this, "Ошибка загрузки изображения" + e.message, Toast.LENGTH_SHORT)
                        .show()
                }
                .addOnProgressListener { taskSnapshot ->
                    //оповещение об успешной отправки
                    val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                        .totalByteCount
                    progressDialog.setMessage("Загружено " + progress.toInt() + "%")
                    //создать и отправить объект в бд
                    loadAddFireBase()
                }
        }
    }

    //сохранить объект
    private fun loadAddFireBase(){

        //ссылка на картинку для объекта
        val image = "gs://skazki-99ce4.appspot.com/Etagi/$id"

        mDataBase = FirebaseDatabase.getInstance().getReference("Etagi/Items/$id/AdressId")
        mDataBase.ref.setValue(id)

        mDataBase = FirebaseDatabase.getInstance().getReference("Etagi/Items/$id/Image")
        mDataBase.ref.setValue(image)

        mDataBase = FirebaseDatabase.getInstance().getReference("Etagi/Items/$id/Adress")
        mDataBase.ref.setValue(editAdress.text.toString())

        mDataBase = FirebaseDatabase.getInstance().getReference("Etagi/Items/$id/Area")
        mDataBase.ref.setValue(editArea.text.toString())

        mDataBase = FirebaseDatabase.getInstance().getReference("Etagi/Items/$id/Cost")
        mDataBase.ref.setValue(editCost.text.toString())

        mDataBase = FirebaseDatabase.getInstance().getReference("Etagi/Items/$id/Floor")
        mDataBase.ref.setValue(editFloor.text.toString())

        mDataBase = FirebaseDatabase.getInstance().getReference("Etagi/Items/$id/Rooms")
        mDataBase.ref.setValue(editRooms.text.toString())

        //обновить список
        EstateSingleton.notifyTwo()

    }

    //диалог об успешной отправки данных
    fun openSuccessDialog(context: Context) {
        val quitDialog = AlertDialog.Builder(
            context
        )
        quitDialog.setTitle("Отлично!")
        quitDialog.setTitle("Объект создан")
        quitDialog.setPositiveButton(
            "Огонь)"
        ) { _, _ ->
            //вернуться к списку объектов
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}