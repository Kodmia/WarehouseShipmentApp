package ru.dikoresearch.warehouse.presentation.orderdetails

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.dikoresearch.warehouse.databinding.OrderGoodsItemBinding

class OrderGoodsAdapter(
    val onItemClicked: (String) -> Unit,
    val onCheckedChanged: (Int, Boolean) -> Unit
): RecyclerView.Adapter<OrderGoodsAdapter.GoodsViewHolder>()  {

    var listOfGoods = emptyList<OrderGoodsAdapterModel>()
    @SuppressLint("NotifyDataSetChanged")
    set(value){
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoodsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = OrderGoodsItemBinding.inflate(layoutInflater, parent, false)
        return GoodsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GoodsViewHolder, position: Int) {
        val goods = listOfGoods[position]

        with(holder.binding){
            this.root.setOnClickListener {
                onItemClicked(goods.goods.art)
            }
            goodsArtTextView.text = goods.goods.art
            goodsNameTextView.text = goods.goods.name
            goodsQuantityTextView.text = goods.goods.count
            goodsCheckView.isChecked = goods.isChecked
            goodsCheckView.isEnabled = !goods.isLoaded

            goodsCheckView.setOnClickListener {
                onCheckedChanged(position, goodsCheckView.isChecked)
            }

        }
    }

    override fun getItemCount(): Int = listOfGoods.size

    class GoodsViewHolder(
        val binding: OrderGoodsItemBinding
    ): RecyclerView.ViewHolder(binding.root)
}