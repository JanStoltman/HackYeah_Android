package pl.asi.asilotto.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.bet_row.view.*
import pl.asi.asilotto.R
import pl.asi.asilotto.model.Bet

class BetsAdapter : RecyclerView.Adapter<BetsAdapter.BetsViewHolder>() {
    private var bets: ArrayList<Bet> = arrayListOf()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BetsViewHolder =
        BetsViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.bet_row, p0, false))

    override fun getItemCount(): Int = bets.size

    override fun onBindViewHolder(p0: BetsViewHolder, p1: Int) {
        p0.setBet(bets[p1])
    }


    fun setBets(bets: ArrayList<Bet>) {
        this.bets = bets
        notifyDataSetChanged()
    }

    class BetsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setBet(bet: Bet) {
            itemView.betName.text = bet.price.size.toString()
            itemView.betValue.text = String.format("%.2f zł", bet.price.price)
        }
    }
}