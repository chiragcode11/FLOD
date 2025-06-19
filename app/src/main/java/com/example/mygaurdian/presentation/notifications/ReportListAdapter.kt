package com.example.mygaurdian.presentation.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mygaurdian.databinding.ItemReportBinding
import com.example.mygaurdian.domain.model.CommunityReport
import java.text.SimpleDateFormat
import java.util.*

class ReportListAdapter(
    private var items: List<CommunityReport>,
    private val onUpvote: (CommunityReport) -> Unit
) : RecyclerView.Adapter<ReportListAdapter.RVH>() {

    inner class RVH(val b: ItemReportBinding)
        : RecyclerView.ViewHolder(b.root) {

        fun bind(r: CommunityReport) {
            b.tvCategory.text    = r.category
            b.tvDescription.text = r.description
            b.tvUpvotes.text     = "👍 ${r.upvotes.size}"
            val df = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
            b.tvTime.text        = df.format(Date(r.timestamp))

            b.btnUpvote.setOnClickListener { onUpvote(r) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RVH(
            ItemReportBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )

    override fun onBindViewHolder(holder: RVH, position: Int) =
        holder.bind(items[position])

    override fun getItemCount() = items.size

    fun update(list: List<CommunityReport>) {
        items = list
        notifyDataSetChanged()
    }
}
