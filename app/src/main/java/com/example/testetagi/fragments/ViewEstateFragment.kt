package com.example.testetagi.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.example.testetagi.R
import com.example.testetagi.databinding.FragmentViewEstateBinding
import com.example.testetagi.loaders.LoadImage
import com.example.testetagi.models.EstateObjectCatModel
import com.example.testetagi.singletons.EstateSingleton

class ViewEstateFragment : Fragment() {

    private lateinit var binding: FragmentViewEstateBinding
    private lateinit var model: EstateObjectCatModel
    private lateinit var btnEdit: ImageButton
    private lateinit var editEstateObjectFragment: EditEstateObjectFragment


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentViewEstateBinding.inflate(layoutInflater)

        btnEdit = binding.imgBtnEdit

        val position = arguments?.getString("pos")?.toInt()

        val list: ArrayList<EstateObjectCatModel> = EstateSingleton.estateItem

            Log.d("POS", "position= $position")

        model = list[position!!]

        val cost = model.Items?.Cost
        val area = model.Items?.Area
        val prise = EstateSingleton.prise().toInt()
        val img = binding.imgEstateView

        LoadImage().loadImageApartments(inflater.context, model, img)

        binding.txtCostEstateView.text = cost
        binding.txtM2View.text = "| $area м² | "
        binding.txtPriseEstateView.text = "$prise"
        binding.txtRoomsEstateView.text = model.Items?.Rooms + " - комнатная кв. "
        binding.txtFloorEstateView.text = model.Items?.Floor + " эт."
        binding.txtAdressEstateView.text = model.Items?.Adress

        btnEdit(position)

        return binding.root
    }

    private fun btnEdit(position: Int){
        btnEdit.setOnClickListener {
            val args = Bundle()
            args.putString("pos", position.toString())
            editEstateObjectFragment = EditEstateObjectFragment()
            editEstateObjectFragment.arguments = args
            val manager = (activity as AppCompatActivity).supportFragmentManager
            manager.beginTransaction()
                .replace(R.id.container, editEstateObjectFragment, args.toString())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(editEstateObjectFragment.toString())
                .commit()
        }

    }
}