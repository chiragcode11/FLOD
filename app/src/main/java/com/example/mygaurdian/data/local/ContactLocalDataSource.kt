package com.example.mygaurdian.data.local

import android.content.ContentResolver
import android.provider.ContactsContract
import com.example.mygaurdian.domain.model.Contact

class ContactLocalDataSource(private val resolver: ContentResolver) {

    fun fetchContacts(): List<Contact> {
        val list = mutableListOf<Contact>()
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone._ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        val cursor = resolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection, null, null,
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
        ) ?: return list

        while (cursor.moveToNext()) {
            val id = cursor.getString(0)
            val name = cursor.getString(1)
            val phone = cursor.getString(2)
            list.add(Contact(id, name, phone))
        }
        cursor.close()
        return list
    }
}
