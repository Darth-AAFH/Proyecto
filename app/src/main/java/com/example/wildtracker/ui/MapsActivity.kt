package com.example.wildtracker.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.wildtracker.LoginActivity
import com.example.wildtracker.R
import com.example.wildtracker.R.layout.dialog_interface
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.maps.model.LatLng as LatLng1


class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawer: DrawerLayout
    private lateinit var map: GoogleMap
    private lateinit var builder: AlertDialog.Builder
    private var markers: MutableList<Marker> = mutableListOf<Marker>()

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    private fun isPermissionsGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED


    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (!::map.isInitialized) return
        if (isPermissionsGranted()) {
            map.isMyLocationEnabled = true

        } else {
            requestLocationPermission()
        }

    }


    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()

        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION
            )
        }
    }


    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                map.isMyLocationEnabled = true
            } else {
                Toast.makeText(
                    this,
                    "Para activar la localización ve a ajustes y acepta los permisos",
                    Toast.LENGTH_SHORT
                ).show()

            }
            else -> {}
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        /* map.setOnInfoWindowClickListener { markerToDelete ->
             Log.i(TAG, "OnWindowClickDelete")
             markers.remove(markerToDelete)
             markerToDelete.remove()
         }*/
        map.setOnMapLongClickListener { latLng ->
            showAlertAddDialog(latLng)
        }
        map.setOnInfoWindowLongClickListener { markerToDelete ->

            showAlertDeleteDialog(markerToDelete)
        }


        createMarker()
        enableMyLocation()
    }


    private fun showAlertAddDialog(latLng: LatLng1) {

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_interface, null)
        //Alert Dialog Builder
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("Formulario")
        //show dialogInterface
        mBuilder.show()
            .dismiss()

        //OnAddDialogListener
        /*  builder = AlertDialog.Builder(this)

          builder.setTitle("Alert")
              .setMessage("Quieres Agregar el marcador?")
              .setCancelable(true)
              .setPositiveButton("Si") { dialogInterface, it -> addMarker(latLng) }
              .setNegativeButton("No") { dialogInterface, it -> dialogInterface.cancel() }
              .setNeutralButton("?") { dialogInterface, it ->
                  Toast.makeText(
                      this@MapsActivity,
                      "Agregar un punto en el marcador", Toast.LENGTH_SHORT
                  ).show()
              }*/


    }


    private fun addMarker(latLng: LatLng1) {
        val marker = map.addMarker(
            MarkerOptions().position(latLng).title("NewMarcador").snippet("A cool place")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location))
        )

        markers.add(marker!!)
    }


    private fun showAlertDeleteDialog(markerToDelete: Marker) {
        builder = AlertDialog.Builder(this)
        builder.setTitle("Alert")
            .setMessage("Quieres Eliminar el marcador?")
            .setCancelable(true)
            .setPositiveButton("Si") { dialogInterface, it -> deleteMarker(markerToDelete) }
            .setNegativeButton("No") { dialogInterface, it -> dialogInterface.cancel() }
            .show()

    }

    private fun deleteMarker(markerToDelete: Marker) {
        Log.i(TAG, "OnWindowClickDelete")
        markers.remove(markerToDelete)
        markerToDelete.remove()
    }


    @SuppressLint("MissingPermission")
    private fun createMarker() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                val la = location?.latitude
                val lo = location?.longitude
                val aqui = LatLng1(la!!, lo!!)
                map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(aqui, 18f),
                    1000,
                    null
                )
            }
        val favoritePlace = LatLng1(20.702609, -103.389246)
        val home = LatLng1(20.669607, -103.388950)
        map.addMarker(MarkerOptions().position(favoritePlace).title("Here we are"))
        /*  map.animateCamera(
              CameraUpdateFactory.newLatLngZoom(home , 18f),
              1000,
              null
          )
  */
        map.addMarker(
            MarkerOptions()
                .position(home)
                .title("Marcador en mi casa")
                .snippet("Population: 776733")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location))
        )


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        LocationServices.getFusedLocationProviderClient(this)
        createMapFragment()
        initToolbar()
        initNavigationView()
    }


    private fun initToolbar() {
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        toolbar.title = "Maps"
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawerlayout)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.bar_title,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
    }


    private fun initNavigationView() {

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val headerView: View = LayoutInflater.from(this)
            .inflate(R.layout.nav_header_main, navigationView, false)
        //Header para datos del usuario
        navigationView.removeHeaderView(headerView)
        //para actualizar los datos del header
        navigationView.addHeaderView(headerView)

        val tvUser: TextView = headerView.findViewById(R.id.tvUser)
        tvUser.text = LoginActivity.useremail

    }


    private fun createMapFragment() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.fragggment_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_perfil -> callPerfilActivity()
            R.id.nav_inicio -> callInicioActivity()
            R.id.nav_plantillas -> callPlantillasActivity()
            R.id.nav_ejercicio -> callEjercicioActivity()
            R.id.nav_maps -> callMapsActivity()
            R.id.nav_seguimiento -> callSeguimientoActivity()
            R.id.nav_ranking -> callRankingActivity()
            R.id.nav_chat -> callChatActivity()
            R.id.logOut -> signOut()
            R.id.nav_metas -> callMetasActivity()
        }

        drawer.closeDrawer(GravityCompat.START) // cerrar menu

        return true
    }

    private fun callPerfilActivity() {
        val intent = Intent(this, PerfilActivity::class.java)
        startActivity(intent)
    }

    private fun callInicioActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun callPlantillasActivity() {
        val intent = Intent(this, PlantillasActivity::class.java)
        startActivity(intent)
    }

    private fun callEjercicioActivity() {
        val intent = Intent(this, EjecicioActivity::class.java)
        startActivity(intent)
    }

    private fun callMapsActivity() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }

    private fun callSeguimientoActivity() {
        val intent = Intent(this, SeguimientoActivity::class.java)
        startActivity(intent)
    }

    private fun callRankingActivity() {
        val intent = Intent(this, RankingActivity::class.java)
        startActivity(intent)
    }

    private fun callChatActivity() {
        val intent = Intent(this, ChatActivity::class.java)
        startActivity(intent)
    }

    private fun callMetasActivity() {
        val intent = Intent(this, RecordActivity::class.java)
        startActivity(intent)
    }

    private fun signOut() {
        LoginActivity.useremail = ""
        FirebaseAuth.getInstance().signOut()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("727481893022-adct709pnvj5tlihh532i6gjgm26thh6.apps.googleusercontent.com")
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()
        //Cierra sesion y manda devuelta al login
        startActivity(Intent(this, LoginActivity::class.java))
    }


}

























