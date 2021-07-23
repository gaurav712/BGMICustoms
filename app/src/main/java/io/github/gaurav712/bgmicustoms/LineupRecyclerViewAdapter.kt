package io.github.gaurav712.bgmicustoms

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject

class LineupRecyclerViewAdapter(private val context: Context) :
    RecyclerView.Adapter<LineupRecyclerViewAdapter.LineupRecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineupRecyclerViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.lineup_row_layout, parent, false)
        return LineupRecyclerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return jsonObject.length()
    }

    override fun onBindViewHolder(holder: LineupRecyclerViewHolder, position: Int) {
        val jsonObject: JSONObject = jsonObject[jsonArray[position].toString()] as JSONObject

        holder.slotNumberTextView.text =
            String.format(context.resources.getString(R.string.slot_number_text), jsonObject.getString("slot"))
        holder.playerOneTextView.text = jsonObject.getString("playerOne")
        holder.playerTwoTextView.text = jsonObject.getString("playerTwo")
        holder.playerThreeTextView.text = jsonObject.getString("playerThree")
        holder.playerFourTextView.text = jsonObject.getString("playerFour")
    }

    class LineupRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val slotNumberTextView: TextView = itemView.findViewById(R.id.slotNumberTextView)
        val playerOneTextView: TextView = itemView.findViewById(R.id.playerOneTextView)
        val playerTwoTextView: TextView = itemView.findViewById(R.id.playerTwoTextView)
        val playerThreeTextView: TextView = itemView.findViewById(R.id.playerThreeTextView)
        val playerFourTextView: TextView = itemView.findViewById(R.id.playerFourTextView)
    }
}