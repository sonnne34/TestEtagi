package com.example.testetagi.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.example.testetagi.R
import com.example.testetagi.databinding.FragmentEditEstateObjectBinding
import com.example.testetagi.models.EstateObjectCatModel
import com.example.testetagi.singletons.EstateSingleton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EditEstateObjectFragment : Fragment() {

    private lateinit var binding: FragmentEditEstateObjectBinding
    private lateinit var model: EstateObjectCatModel

    private lateinit var inputArea: TextInputLayout
    private lateinit var inputCost: TextInputLayout
    private lateinit var inputFloor: TextInputLayout
    private lateinit var inputRooms: TextInputLayout

    private lateinit var editArea: EditText
    private lateinit var editCost: EditText
    private lateinit var editFloor: EditText
    private lateinit var editRooms: EditText

    private lateinit var btnEditBD: Button
    private lateinit var btnCancelEdit: Button

    lateinit var mDataBase: DatabaseReference
    lateinit var mainFragment: MainFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditEstateObjectBinding.inflate(layoutInflater)

        inputArea = binding.inputEditArea
        inputCost = binding.inputEditCost
        inputFloor = binding.inputEditFloor
        inputRooms = binding.inputEditRooms

        editArea = binding.editEditArea
        editCost = binding.editEditCost
        editFloor = binding.editEditFloor
        editRooms = binding.editEditRooms

        btnEditBD = binding.btnEditFragment
        btnCancelEdit = binding.btnCancelEdit

        //достаём позицию объекта списка
        val position = arguments?.getString("pos")?.toInt()
        //открываем список
        val list: ArrayList<EstateObjectCatModel> = EstateSingleton.estateItem
        //кладём в модель нужную позицию из списка
        model = list[position!!]
        binding.txtHeaderEditAdress.text = model.Items?.Adress
        editCost.setText(model.Items?.Cost)
        editArea.setText(model.Items?.Area)
        editFloor.setText(model.Items?.Floor)
        editRooms.setText(model.Items?.Rooms)

        //проверка и отправка отредактированных данных
        btnEditBD()
        //закрыть окно редактирования
        btnCancelEdit()

        return binding.root
    }

    //onClick проверка и отправка отредактированных данных
    private fun btnEditBD(){
        btnEditBD.setOnClickListener {
            checkingEdit()
        }
    }

    //onClick закрыть редактирование
    private fun btnCancelEdit(){
        btnCancelEdit.setOnClickListener {
            val manager = (activity as AppCompatActivity).supportFragmentManager
            manager.popBackStack()
        }
    }

    //отправка отредактированных данных
    private fun loadFireBase(){

        model.Items?.Cost = editCost.text.toString()
        model.Items?.Area = editArea.text.toString()
        model.Items?.Floor = editFloor.text.toString()
        model.Items?.Rooms = editRooms.text.toString()

        val adressId = model.Items?.AdressId
        mDataBase = FirebaseDatabase.getInstance().getReference("Etagi/Items/$adressId/Area")
        mDataBase.ref.setValue(model.Items?.Area)

        mDataBase = FirebaseDatabase.getInstance().getReference("Etagi/Items/$adressId/Cost")
        mDataBase.ref.setValue(model.Items?.Cost)

        mDataBase = FirebaseDatabase.getInstance().getReference("Etagi/Items/$adressId/Floor")
        mDataBase.ref.setValue(model.Items?.Floor)

        mDataBase = FirebaseDatabase.getInstance().getReference("Etagi/Items/$adressId/Rooms")
        mDataBase.ref.setValue(model.Items?.Rooms)

    }

    //проверка полноты введённых данных
    private fun checkingEdit(){

            if (editArea.text.isEmpty() ||
                editCost.text.isEmpty() || editFloor.text.isEmpty() ||
                editRooms.text.isEmpty()) {

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
            } else{

                //отправка отредактированных данных
                loadFireBase()
                Toast.makeText(context, "Изменения сохранены", Toast.LENGTH_LONG).show()
                //закрыть окно
                closeAdd()
            }
    }

    //закрыть окно редактирования
    private fun closeAdd(){
        mainFragment = MainFragment()
        val manager = (activity as AppCompatActivity).supportFragmentManager
        manager.beginTransaction()
            .replace(R.id.container, mainFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

}