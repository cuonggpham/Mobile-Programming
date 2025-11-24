package com.example.googleplay

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val categoriesRecyclerView = findViewById<RecyclerView>(R.id.categoriesRecyclerView)

        categoriesRecyclerView.layoutManager = LinearLayoutManager(this)
        categoriesRecyclerView.adapter = MixedCategoryAdapter(getSampleData())
    }

    private fun getSuggestedData(): List<List<SuggestedAppModel>> {
        val allApps = listOf(
            SuggestedAppModel("SHEIN-Shopping Online", "Shopping", "Retailer", 4.1f, Color.parseColor("#000000")),
            SuggestedAppModel("EA SPORTS App", "Sports", "Sports", 3.5f, Color.parseColor("#0066CC")),
            SuggestedAppModel("Temu: Shop Like a Billionaire", "Shopping", "Online marketplace", 4.6f, Color.parseColor("#FF6700")),
            SuggestedAppModel("Amazon Shopping", "Shopping", "Online Store", 4.4f, Color.parseColor("#FF9900")),
            SuggestedAppModel("Zalando", "Shopping", "Fashion", 4.2f, Color.parseColor("#FF6900")),
            SuggestedAppModel("AliExpress", "Shopping", "Marketplace", 4.3f, Color.parseColor("#E62E04")),
            SuggestedAppModel("Wish", "Shopping", "Online Shopping", 3.8f, Color.parseColor("#2FB7EC")),
            SuggestedAppModel("H&M", "Shopping", "Fashion", 4.1f, Color.parseColor("#E50010")),
            SuggestedAppModel("ZARA", "Shopping", "Fashion", 4.0f, Color.parseColor("#000000"))
        )

        return allApps.chunked(3)
    }

    private fun getSampleData(): List<CategoryItem> {
        return listOf(
            CategoryItem.NormalCategory(
                CategoryModel(
                    title = "Popular apps",
                    apps = listOf(
                        AppModel("WhatsApp Business", "Communication", 4.4f, Color.parseColor("#25D366")),
                        AppModel("Lidl Plus", "Shopping", 4.1f, Color.parseColor("#0050AA")),
                        AppModel("Glovo: Food & Grocery Delivery", "Food", 4.6f, Color.parseColor("#FFC244")),
                        AppModel("McDonald's", "Food", 3.9f, Color.parseColor("#FFC72C")),
                        AppModel("Netflix", "Entertainment", 4.5f, Color.parseColor("#E50914")),
                        AppModel("Spotify", "Music", 4.4f, Color.parseColor("#1DB954"))
                    )
                )
            ),
            CategoryItem.SuggestedCategory(getSuggestedData()),
            CategoryItem.NormalCategory(
                CategoryModel(
                    title = "Dating apps",
                    apps = listOf(
                        AppModel("Tinder", "Dating", 4.2f, Color.parseColor("#FE3C72")),
                        AppModel("Bumble", "Dating", 4.1f, Color.parseColor("#FFC629")),
                        AppModel("Badoo", "Dating", 4.0f, Color.parseColor("#7B51D3")),
                        AppModel("OkCupid", "Dating", 4.3f, Color.parseColor("#FF6F00")),
                        AppModel("Hinge", "Dating", 4.4f, Color.parseColor("#FC4458"))
                    )
                )
            )

        )
    }
}

