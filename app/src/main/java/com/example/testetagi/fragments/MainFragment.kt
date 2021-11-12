package com.example.testetagi.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testetagi.AddEstateObjectActivity
import com.example.testetagi.R
import com.example.testetagi.loaders.LoadFireBase
import com.example.testetagi.adapters.EstateObjectAdapter
import com.example.testetagi.AuthenticationActivity
import com.example.testetagi.databinding.MainFragmentBinding
import com.example.testetagi.interfaces.NetworkMonitorUtil
import com.example.testetagi.listeners.EventListeners
import com.example.testetagi.listeners.RecyclerItemClickListenr
import com.example.testetagi.models.CategoryEstateObjectModel
import com.example.testetagi.singletons.EstateSingleton

class MainFragment : Fragment(), EventListeners {

    private lateinit var binding: MainFragmentBinding
    private var estateObjectAdapter: EstateObjectAdapter? = null
    private var estateObjectList: ArrayList<CategoryEstateObjectModel> = ArrayList()
    private lateinit var viewEstateFragment: ViewEstateFragment
    private lateinit var btnQuitSession: ImageButton
    private lateinit var btnAddEstate: ImageButton
    private lateinit var loadingPB: ProgressBar

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(layoutInflater)

        EstateSingleton.subscribe(this)

        loadingPB = binding.loading

        if (estateObjectAdapter == null){
            estateObjectAdapter = EstateObjectAdapter(inflater.context)
            LoadFireBase(inflater.context).loadApartments(estateObjectList, estateObjectAdapter!!)
        }

        btnAddEstate = binding.imgBtnAddMain
        btnQuitSession = binding.imgBtnExitMain

        val rv = binding.rvEstateObjectMain
        rv.adapter = estateObjectAdapter
        rv.layoutManager = LinearLayoutManager(binding.root.context,
            RecyclerView.VERTICAL,false)

        onClickItem(rv, binding.root.context)
        btnQuitSession(binding.root.context)
        btnAddEstate()

        return binding.root
    }

    fun onClickItem(rv: RecyclerView, context: Context){
        rv.addOnItemTouchListener(
            RecyclerItemClickListenr(context, rv,
                object : RecyclerItemClickListenr.OnItemClickListener {

                    override fun onItemClick(view: View, position: Int) {

                        val args = Bundle()
                        args.putString("pos", position.toString())

                            viewEstateFragment = ViewEstateFragment()
                            viewEstateFragment.arguments = args
                            val manager = (activity as AppCompatActivity).supportFragmentManager
                            manager.beginTransaction()
                                .replace(R.id.container, viewEstateFragment, args.toString())
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .addToBackStack(null)
                                .commit()
                    }

                    override fun onItemLongClick(view: View?, position: Int) {

                    }
                })
        )
    }

    private fun btnQuitSession(context: Context) {
        btnQuitSession.setOnClickListener {

            val quitDialog = AlertDialog.Builder(
                context
            )
            quitDialog.setTitle("Выход")
            quitDialog.setTitle("Вы уверенны, что хотите закончить сессию?")
            quitDialog.setPositiveButton(
                "Да"
            ) { _, _ ->

                val intent = Intent(context, AuthenticationActivity::class.java)
                AuthenticationActivity().closeSession()
                startActivity(intent)

            }
            quitDialog.setNegativeButton(
                "Ой, нет!"
            ) { _, _ ->
            }
            quitDialog.show()

        }
    }

    private fun btnAddEstate(){
        btnAddEstate.setOnClickListener {

            val intent = Intent(context, AddEstateObjectActivity::class.java)
            startActivity(intent)

        }
    }

    override fun updateRR() {
        context?.let { EstateObjectAdapter(it).setupAdapter(estateObjectList) }
    }

}