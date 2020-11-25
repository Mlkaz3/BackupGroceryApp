package com.example.groceryapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.groceryapp.Model.CartItem
import com.example.groceryapp.Model.Product
import com.example.groceryapp.R
import com.squareup.picasso.Picasso
import java.text.DecimalFormat

class CartItemAdapter(contexts: Context, private val cartItemList: ArrayList<CartItem>): RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder>() {

    //declare a context
    var context: Context = contexts

    //round off function
    val df: DecimalFormat = DecimalFormat("0.00")

    //to hold one single view only
    inner class CartItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val img: ImageView = itemView.findViewById(R.id.image_product)
        val title: TextView = itemView.findViewById(R.id.product_title)
        val price: TextView = itemView.findViewById(R.id.price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.cartproduct,parent,false)
        return CartItemViewHolder(itemView)
    }

    override fun getItemCount() = cartItemList.size

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val currentItem = cartItemList[position]
        Picasso.with(context).load(currentItem.productInfo.productImage).into(holder.img)
        Picasso.with(context).isLoggingEnabled = true
        holder.title.text = currentItem.productInfo.productName
        holder.price.text = "RM" + df.format(currentItem.productInfo.productPrice).toString()

    }


}