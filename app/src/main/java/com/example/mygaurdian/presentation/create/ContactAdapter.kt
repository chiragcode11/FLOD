package com.example.mygaurdian.presentation.create

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mygaurdian.databinding.ItemContactBinding
import com.example.mygaurdian.domain.model.Contact

class ContactAdapter(
    private var contacts: List<Contact>,
    private val onSelectionChanged: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactVH>() {

    inner class ContactVH(val binding: ItemContactBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(c: Contact) {
            binding.tvName.text = c.name
            binding.tvPhone.text = c.phone
            binding.checkbox.isChecked = c.isSelected
            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                c.isSelected = isChecked
                onSelectionChanged(c)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ContactVH(ItemContactBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        ))

    override fun onBindViewHolder(holder: ContactVH, position: Int) =
        holder.bind(contacts[position])

    override fun getItemCount() = contacts.size

    fun update(items: List<Contact>) {
        contacts = items
        notifyDataSetChanged()
    }
}
