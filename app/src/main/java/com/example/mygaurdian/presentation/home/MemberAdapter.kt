package com.example.mygaurdian.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mygaurdian.databinding.ItemMemberBinding
import com.example.mygaurdian.domain.model.User

class MemberAdapter(
    private var members: List<User>,
    private val onRemove: (User) -> Unit,
    private val onInvite: (User) -> Unit
) : RecyclerView.Adapter<MemberAdapter.MemberVH>() {

    inner class MemberVH(val b: ItemMemberBinding)
        : RecyclerView.ViewHolder(b.root) {
        fun bind(u: User) {
            b.tvEmail.text = u.email
            b.btnRemove.setOnClickListener { onRemove(u) }
            b.btnInvite.setOnClickListener { onInvite(u) }
        }
    }

    override fun onCreateViewHolder(p: ViewGroup, v: Int) = MemberVH(
        ItemMemberBinding.inflate(LayoutInflater.from(p.context), p, false)
    )

    override fun onBindViewHolder(h: MemberVH, pos: Int) = h.bind(members[pos])
    override fun getItemCount() = members.size

    fun update(list: List<User>) {
        members = list; notifyDataSetChanged()
    }
}
