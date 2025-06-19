package com.example.mygaurdian.presentation.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mygaurdian.databinding.ItemSosEventBinding
import com.example.mygaurdian.domain.model.SOSEvent
import java.text.SimpleDateFormat
import java.util.*

class SOSAdapter(
    private var items: List<SOSEvent>
) : RecyclerView.Adapter<SOSAdapter.SOSVH>() {

    inner class SOSVH(val b: ItemSosEventBinding)
        : RecyclerView.ViewHolder(b.root) {
        fun bind(evt: SOSEvent) {
            b.tvLocation.text = "Lat: ${evt.lat}, Lng: ${evt.lng}"
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            b.tvTime.text = df.format(Date(evt.timestamp))
            b.tvTeamId.text = "Team: ${evt.teamId}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SOSVH(
            ItemSosEventBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )

    override fun onBindViewHolder(holder: SOSVH, position: Int) =
        holder.bind(items[position])

    override fun getItemCount() = items.size

    fun update(list: List<SOSEvent>) {
        items = list
        notifyDataSetChanged()
    }
}
