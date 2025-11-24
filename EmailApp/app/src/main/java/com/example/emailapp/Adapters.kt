package com.example.emailapp

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EmailAdapter(private val emails: List<Email>) : RecyclerView.Adapter<EmailAdapter.EmailViewHolder>() {

    class EmailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatarText: TextView = view.findViewById(R.id.avatarText)
        val senderName: TextView = view.findViewById(R.id.senderName)
        val timeText: TextView = view.findViewById(R.id.timeText)
        val subjectText: TextView = view.findViewById(R.id.subjectText)
        val previewText: TextView = view.findViewById(R.id.previewText)
        val starIcon: ImageView = view.findViewById(R.id.starIcon)
        val importantIndicator: TextView = view.findViewById(R.id.importantIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_email, parent, false)
        return EmailViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmailViewHolder, position: Int) {
        val email = emails[position]


        val firstLetter = email.senderName.firstOrNull()?.uppercase() ?: "?"
        holder.avatarText.text = firstLetter

        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.OVAL
        drawable.setColor(email.avatarColor)
        holder.avatarText.background = drawable

        holder.senderName.text = email.senderName
        holder.timeText.text = email.time
        holder.subjectText.text = email.subject
        holder.previewText.text = email.preview

        if (email.isStarred) {
            holder.starIcon.setImageResource(R.drawable.ic_star_filled)
        } else {
            holder.starIcon.setImageResource(R.drawable.ic_star_border)
        }

        holder.importantIndicator.visibility = if (email.isImportant) View.VISIBLE else View.GONE
    }

    override fun getItemCount() = emails.size
}

