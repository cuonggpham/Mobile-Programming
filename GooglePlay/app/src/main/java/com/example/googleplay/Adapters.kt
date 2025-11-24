package com.example.googleplay

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// ============== App Adapter (for horizontal scroll) ==============
class AppAdapter(private val apps: List<AppModel>) : RecyclerView.Adapter<AppAdapter.AppViewHolder>() {

    class AppViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val appIcon: TextView = view.findViewById(R.id.appIcon)
        val appName: TextView = view.findViewById(R.id.appName)
        val appRating: TextView = view.findViewById(R.id.appRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = apps[position]

        val firstLetter = app.name.firstOrNull()?.toString()?.uppercase() ?: "A"
        holder.appIcon.text = firstLetter

        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.RECTANGLE
        drawable.cornerRadius = 20f
        drawable.setColor(app.color)
        holder.appIcon.background = drawable

        holder.appName.text = app.name
        holder.appRating.text = "${app.rating} ★"
    }

    override fun getItemCount() = apps.size
}

// ============== Suggested App Adapter (for vertical list in columns) ==============
class SuggestedAppAdapter(private val apps: List<SuggestedAppModel>) : RecyclerView.Adapter<SuggestedAppAdapter.SuggestedAppViewHolder>() {

    class SuggestedAppViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val appIcon: TextView = view.findViewById(R.id.appIcon)
        val appName: TextView = view.findViewById(R.id.appName)
        val appCategory: TextView = view.findViewById(R.id.appCategory)
        val appRating: TextView = view.findViewById(R.id.appRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestedAppViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_suggested_app, parent, false)
        return SuggestedAppViewHolder(view)
    }

    override fun onBindViewHolder(holder: SuggestedAppViewHolder, position: Int) {
        val app = apps[position]

        val firstLetter = app.name.firstOrNull()?.toString()?.uppercase() ?: "A"
        holder.appIcon.text = firstLetter

        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.RECTANGLE
        drawable.cornerRadius = 12f
        drawable.setColor(app.color)
        holder.appIcon.background = drawable

        holder.appName.text = app.name
        holder.appCategory.text = "${app.category} • ${app.subcategory}"
        holder.appRating.text = "${app.rating} ★"
    }

    override fun getItemCount() = apps.size
}

// ============== Column Adapter (contains 3 apps vertically) ==============
class SuggestedColumnAdapter(private val columns: List<List<SuggestedAppModel>>) : RecyclerView.Adapter<SuggestedColumnAdapter.ColumnViewHolder>() {

    class ColumnViewHolder(val recyclerView: RecyclerView) : RecyclerView.ViewHolder(recyclerView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColumnViewHolder {
        // Create RecyclerView programmatically instead of using XML
        val recyclerView = RecyclerView(parent.context).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                (320 * parent.context.resources.displayMetrics.density).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                marginEnd = (8 * parent.context.resources.displayMetrics.density).toInt()
            }
        }
        return ColumnViewHolder(recyclerView)
    }

    override fun onBindViewHolder(holder: ColumnViewHolder, position: Int) {
        val columnApps = columns[position]

        holder.recyclerView.layoutManager = LinearLayoutManager(
            holder.itemView.context,
            LinearLayoutManager.VERTICAL,
            false
        )
        holder.recyclerView.adapter = SuggestedAppAdapter(columnApps)
    }

    override fun getItemCount() = columns.size
}

class MixedCategoryAdapter(private val items: List<CategoryItem>) : RecyclerView.Adapter<MixedCategoryAdapter.CategoryViewHolder>() {

    companion object {
        private const val TYPE_NORMAL = 0
        private const val TYPE_SUGGESTED = 1
    }

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryTitle: TextView = view.findViewById(R.id.categoryTitle)
        val sponsoredLabel: TextView = view.findViewById(R.id.sponsoredLabel)
        val categoryIcon: TextView = view.findViewById(R.id.categoryIcon)
        val appsRecyclerView: RecyclerView = view.findViewById(R.id.appsRecyclerView)
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is CategoryItem.NormalCategory -> TYPE_NORMAL
            is CategoryItem.SuggestedCategory -> TYPE_SUGGESTED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        // Use same layout for both types
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        when (val item = items[position]) {
            is CategoryItem.NormalCategory -> {
                // Normal category
                holder.sponsoredLabel.visibility = View.GONE
                holder.categoryTitle.text = item.categoryModel.title
                holder.categoryIcon.text = "→"

                holder.appsRecyclerView.layoutManager = LinearLayoutManager(
                    holder.itemView.context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                holder.appsRecyclerView.adapter = AppAdapter(item.categoryModel.apps)
            }
            is CategoryItem.SuggestedCategory -> {
                // Suggested category
                holder.sponsoredLabel.visibility = View.VISIBLE
                holder.categoryTitle.text = "Suggested for you"
                holder.categoryIcon.text = "⋮"

                holder.appsRecyclerView.layoutManager = LinearLayoutManager(
                    holder.itemView.context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                holder.appsRecyclerView.adapter = SuggestedColumnAdapter(item.columns)
            }
        }
    }

    override fun getItemCount() = items.size
}

