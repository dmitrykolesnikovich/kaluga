package com.splendo.kaluga.example

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.splendo.kaluga.example.alerts.AlertsActivity
import com.splendo.kaluga.example.collectionView.CollectionViewActivity
import com.splendo.kaluga.example.loading.LoadingActivity
import com.splendo.kaluga.example.location.LocationActivity
import com.splendo.kaluga.example.permissions.PermissionsActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        btn_feature_alerts.setOnClickListener {
            startActivity(Intent(this, AlertsActivity::class.java))
        }
        btn_feature_collection_view.setOnClickListener {
            startActivity(Intent(this, CollectionViewActivity::class.java))
        }
        btn_feature_hud.setOnClickListener {
            startActivity(Intent(this, LoadingActivity::class.java))
        }
        btn_feature_location.setOnClickListener {
            startActivity(Intent(this, LocationActivity::class.java))
        }
        btn_feature_permissions.setOnClickListener {
            startActivity(Intent(this, PermissionsActivity::class.java))
        }
    }
}