package com.example.testetagi.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testetagi.R
import com.example.testetagi.loaders.LoadImage
import com.example.testetagi.models.CategoryEstateObjectModel
import com.example.testetagi.models.EstateObjectCatModel
import com.example.testetagi.singletons.EstateSingleton

class EstateObjectAdapter(context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mCategoryEstateObjectModel: ArrayList<CategoryEstateObjectModel>? = null
    private var mEstateObjectCatModel: ArrayList<EstateObjectCatModel> = ArrayList()
    private var mContext = context


    @SuppressLint("NotifyDataSetChanged")
    fun setupAdapter(list: ArrayList<CategoryEstateObjectModel>) {
        mEstateObjectCatModel.clear()

        if (mCategoryEstateObjectModel == null) {
            mCategoryEstateObjectModel = list
        }

        for (categoryModel in list) {
            for (i in categoryModel.Items) {
                mEstateObjectCatModel.add(EstateObjectCatModel(i.value))
            }
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.item_estate_object_main, parent, false)
        return HolderItem(itemView = itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HolderItem){
            holder.bind(estateObjectModel = mEstateObjectCatModel[position], mContext)
        }
    }

    override fun getItemCount(): Int {
        return mEstateObjectCatModel.count()
    }

    class HolderItem(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

    var adress = itemView?.findViewById(R.id.txt_adress_estate_item_menu) as TextView
    var cost = itemView?.findViewById<TextView>(R.id.txt_cost_estate_item_menu)
    var area: TextView = itemView?.findViewById(R.id.txt_area_estate_item_menu)!!
    var image = itemView?.findViewById<ImageView>(R.id.img_estate_item_menu)

        fun bind(estateObjectModel: EstateObjectCatModel, context: Context) {

            adress.text = estateObjectModel.Items?.Adress
            cost!!.text = estateObjectModel.Items?.Cost
            area.text = estateObjectModel.Items?.Area

            LoadImage().loadImageApartments(context, estateObjectModel, image!!)
            EstateSingleton.addEstate(estateObjectModel)

        }
    }
}
