package esprims.gi2.tp4x

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_WEB_SEARCH
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.util.*
import java.util.jar.Manifest.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {



    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        fusedLocationClient = getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        this.getLocation()


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val monastir = LatLng(35.76, 10.81)
        val a:Float = 7F
        mMap.addMarker(MarkerOptions().position(monastir).title("Monastir"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(monastir))

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(monastir,a))
        val cp = CameraPosition.builder().target(monastir)
                               .zoom(10F).bearing(45F).tilt(90f).build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp))

        val sousse = LatLng(35.82, 10.64)
        mMap.addMarker(MarkerOptions().position(sousse).title("Sousse"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sousse))
        mMap.addPolyline(PolylineOptions().add(monastir,sousse))

        val tunis = LatLng(36.8,10.17)
        mMap.addCircle(CircleOptions().center(tunis).radius(10000.0).strokeColor(Color.RED).fillColor(Color.BLUE))

        mMap.setOnMapClickListener {

            Toast.makeText(applicationContext," latitude: "+it.latitude.toString()+" longitude: "+it.longitude.toString(),Toast.LENGTH_LONG).show()

        }
        mMap.setOnMarkerClickListener{

            intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://en.wikipedia.org/wiki/"+it.title.toString()))
            startActivity(intent)
            true
        }

    }

    private fun getLocation() {

        if( (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION )!= PackageManager.PERMISSION_GRANTED) &&
            (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION )!= PackageManager.PERMISSION_GRANTED))
        {

            Toast.makeText(applicationContext,"activer localisation",Toast.LENGTH_LONG).show()
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),1)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)

        }
        else{
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if(it!=null){
                    Toast.makeText(applicationContext,
                        "Local latitude: "+it.latitude.toString()+" | Local longtitude: "+it.longitude.toString(),Toast.LENGTH_LONG).show()

                    mMap.addMarker(MarkerOptions().position(LatLng(it.latitude,it.longitude)).title("last_position"))

                    val geocoder = Geocoder(applicationContext, Locale.getDefault())
                    val adr = geocoder.getFromLocation(it.latitude,it.longitude,1)
                    Toast.makeText(this, adr.get(0).getAddressLine(0).toString(), Toast.LENGTH_LONG).show()
                }
                else
                    Toast.makeText(this,"Unknown Location",Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1){

                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                getLocation()
                else
                Toast.makeText(applicationContext, "Accès non autorisé !", Toast.LENGTH_LONG).show()

            /*2->{
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    getLocation()
                else
                    Toast.makeText(applicationContext, "Accès non autorisé !", Toast.LENGTH_LONG).show()
            }*/
        }



    }
}