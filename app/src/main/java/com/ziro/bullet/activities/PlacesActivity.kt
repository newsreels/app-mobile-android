package com.ziro.bullet.activities

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.ziro.bullet.R
import com.ziro.bullet.data.models.location.Location
import com.ziro.bullet.fragments.searchNew.locationnew.PlacesListFragmentNew3
import com.ziro.bullet.interfaces.AudioCallback
import com.ziro.bullet.interfaces.GoHome
import com.ziro.bullet.model.AudioObject

class PlacesActivity : BaseActivity(), GoHome {

    private lateinit var location: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_places)

        if (intent != null) {
            location = (intent.getSerializableExtra("location") as Location?)!!
        }

        if (::location.isInitialized) {

            val manager: FragmentManager = supportFragmentManager
            val transaction: FragmentTransaction = manager.beginTransaction()
            val bundle = Bundle()
            bundle.putString("locationId", location.id)
            bundle.putString("locationName", location.name)

            val oldFragment = manager.findFragmentByTag("navlocation")
            if (oldFragment != null) {
                manager.beginTransaction().remove(oldFragment).commit()
            }
            transaction.add(
                R.id.frag_container,
                PlacesListFragmentNew3.getInstancenew(bundle, this),
                "navlocation"
            )
            transaction.commit()
        }
    }

    override fun home() {

    }

    override fun sendAudioToTempHome(
        audioCallback: AudioCallback?,
        fragTag: String?,
        status: String?,
        audio: AudioObject?
    ) {

    }

    override fun scrollUp() {

    }

    override fun scrollDown() {

    }

    override fun sendAudioEvent(event: String?) {

    }

}