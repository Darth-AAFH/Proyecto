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
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import com.example.wildtracker.LoginActivity
import com.example.wildtracker.R
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.maps.model.LatLng as LatLng1


class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    NavigationView.OnNavigationItemSelectedListener {


    /** Instancia a la base de datos */
    private val db = FirebaseFirestore.getInstance()
    private lateinit var drawer: DrawerLayout

    /**
     * Variable para hacer referencia a la clase de Google Maps y ser la
     * entrada a los metodos relacionados con los mapas.
     */
    private lateinit var map: GoogleMap
    private lateinit var builder: AlertDialog.Builder

    /**
     * Lista de marcadores en donde se almacenan los datos de los puntos
     * del mapa
     *
     * @param Position Longitud y latitud : Latlng
     * @param Tittle Titulo del marcador : String
     * @param spippet Descripcion del marcador : String
     */
    private var markers: MutableList<Marker> = mutableListOf<Marker>()

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    /**
     * Funcion al iniciar la actividad
     *
     * @throws LocationServices accede a la ubicación
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        LocationServices.getFusedLocationProviderClient(this)
        createMapFragment()
        initToolbar()
        initNavigationView()
    }

    /**
     * @throws isPermissionsGranted Determina que se haya concedido un
     *     permiso en especifico
     * @param Manifest.permission.ACCESS_FINE_LOCATION Permiso a la
     *     ubicacion actual del usuario
     */
    private fun isPermissionsGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED


    /**
     * Revisa si la ubicación está habilitada, si no lo esta, una
     * función la manda habilitar
     */
    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (!::map.isInitialized) return
        if (isPermissionsGranted()) {
            map.isMyLocationEnabled = true

        } else {
            requestLocationPermission()
        }

    }

    /** Solicita el acceso a la ubicación */
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

    /**
     * En el resultado de pedir el permiso de ubicación
     *
     * @return Bool de map.IsLocationEnabled
     */
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

    /**
     * Función que se manda a llamar cuando se pudo acceder a la
     * ubicación y el mapa se ha cargado
     *
     * @param map Instancia de un mapa de Google
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMapLongClickListener { latLng ->
            showAlertAddDialog(latLng)
        }
        map.setOnInfoWindowLongClickListener { markerToDelete ->
            showAlertDeleteDialog(markerToDelete)
        }
        createMarker()
        enableMyLocation()

        /*
        * Cuando se carga el mapa, accede a Firebase Cloud Firestore y trae todos los datos
        * que se encuentran en "locations" asignando los respectivos valores de cada marcador
        * para asi poder mostrarlos en el mapa       * */
        db.collection("locations").get().addOnSuccessListener {

            for (document in it) { // Entra a las propiedades de cada "locations"
                val lat = document.get("latitud") as Double
                val lng = document.get("longitud") as Double
                val latLng: LatLng1 = LatLng1(lat, lng)
                val placeType = document.get("tipo") as String

                //Verifica que tipo de marcador es: Parque || Gimnasio para asi poder añadir cada marcador de cada "location"
                if (placeType.equals("Parque")) {
                    val marker = map.addMarker(
                        MarkerOptions().position(latLng)
                            .title("${document.get("tipo") as String}")
                            .snippet("${document.get("descripcion") as String}")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.park))
                    )
                    markers.add(marker!!)
                } else {
                    val marker = map.addMarker(
                        MarkerOptions().position(latLng)
                            .title("${document.get("tipo") as String}")
                            .snippet("${document.get("descripcion") as String}")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.gym))
                    )
                    markers.add(marker!!)
                }

            }
        }

    }

    /**
     * Muestra 2 tipos de alert Dialog:
     * 1. Para preguntar si que quiere añadir un nuevo marcador: Si ||
     *    No || ?
     * 2. Para verificar que se desea añadir
     */
    private fun showAlertAddDialog(latLng: LatLng1) {
        //Inicializa el primer AlertDialog
        builder = AlertDialog.Builder(this)
        val dialogBuilder = AlertDialog.Builder(this)
        builder.setTitle("Marcador nuevo")
            .setMessage("Quieres Agregar un nuevo marcador?")
            .setCancelable(true)
            .setPositiveButton("Si")
            { dialogInterface, it ->
                val inflater = this.layoutInflater
                //Infla la vista del mapa con el nuevo dialog para pedir los datos del lugar
                /**
                 * Vista del DialogAlert para llenar datos de un
                 * marcador en el mapa
                 */
                val dialogView: View = inflater.inflate(R.layout.dialog_interface, null)
                dialogBuilder.setView(dialogView)
                /**
                 * Edit text para la descripción del lugar en el mapa
                 */
                val etMarkerDescription = dialogView.findViewById<View>(R.id.PlacesName) as EditText

                /**
                 * Segundo AlertDialog para verificar el punto a añadir
                 */
                val alertDialog: AlertDialog = dialogBuilder.create()
                alertDialog.show()
                /** Boton para confirmar el añadir lugar */
                val btAddMarkerAlertDialog = dialogView.findViewById<Button>(R.id.buttonAddAlert)
                btAddMarkerAlertDialog.isVisible = false
                /** Boton para cancelar el añadir lugar */
                val btCancell = dialogView.findViewById<Button>(R.id.buttonCancelAlert)
                val radioGroup = dialogView.findViewById<RadioGroup>(R.id.RadioGroupDialog)

                /**
                 * Variable que almacena el tipo de lugar del marcador
                 * añadido
                 */
                var selectedPlace = ""

                /**RadioGrup elige el tipo de marcador a añadir: Parque || Gimnasio
                 * y despues pasarlo a String deselectedPlace */
                radioGroup.setOnCheckedChangeListener { radioGroup, selectedId ->
                    when (selectedId) {

                        R.id.rdBtnPark -> {
                            selectedPlace = "Parque"
                            btAddMarkerAlertDialog.isVisible = true
                        }
                        R.id.rdBtnGym -> {
                            selectedPlace = "Gimnasio"
                            btAddMarkerAlertDialog.isVisible = true
                        }
                        else -> btAddMarkerAlertDialog.isVisible = false

                    }
                }
                btAddMarkerAlertDialog.setOnClickListener {
                    /**Obtiene la descripción del marcador a colocar en el mapa*/
                    var edDescripction = etMarkerDescription.text.toString()
                    if (edDescripction.isEmpty())
                        etMarkerDescription.error = "Añade una descripción al lugar"
                    else {
                        addMarker(latLng, edDescripction, selectedPlace)
                        alertDialog.dismiss()
                    }
                    btCancell.setOnClickListener { alertDialog.dismiss() }
                }

            }
            .setNegativeButton("No") { dialogInterface, it -> dialogInterface.cancel() }
            .setNeutralButton("?") { dialogInterface, it ->
                Toast.makeText(
                    this@MapsActivity,
                    "Agregar un punto en el marcador", Toast.LENGTH_SHORT
                ).show()
            }
            .show()
    }

    /**
     * Funcion para añadir marcadores en el mapa
     * @param latLng  Latlng -> Recibe latitud y longitud
     * @param descripcion  String -> Descripción del lugar a añadir
     * @param selectedPlace String -> Tipo de lugar "Parque" o "Gimnasio"
     */
    private fun addMarker(latLng: LatLng1, descripcion: String, selectedPlace: String) {
        //Separar lat y lng para despues almacenarla en la base de datos como datos de tipo Double
        val latitud: Double = latLng.latitude
        val longitud: Double = latLng.longitude

        // Añadir un marcador dependiendo del tipo de lugar "Gimasio" o "Parque"
        if (selectedPlace.equals("Parque")) {
            val marker = map.addMarker(
                MarkerOptions().position(latLng).title("${selectedPlace}").snippet("${descripcion}")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.park))
            )
            markers.add(marker!!)


        } else if (selectedPlace.equals("Gimnasio")) {
            val marker = map.addMarker(
                MarkerOptions().position(latLng).title("${selectedPlace}").snippet("${descripcion}")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.gym))
            )
            markers.add(marker!!)


        }
        /**Añade a la base de datos marcadores*/
        db.collection("locations").add(
            hashMapOf(
                "latitud" to latitud,
                "longitud" to longitud,
                "descripcion" to descripcion,
                "tipo" to selectedPlace
            )
        )
    }

    /**AlertDialog para preguntar si se desea eliminar un marcador en el mapa*/
    private fun showAlertDeleteDialog(markerToDelete: Marker) {

        builder = AlertDialog.Builder(this)
        builder.setTitle("Alert")
            .setMessage("Quieres Eliminar el marcador?")
            .setCancelable(true)
            .setPositiveButton("Si") { dialogInterface, it -> deleteMarker(markerToDelete) }
            .setNegativeButton("No") { dialogInterface, it -> dialogInterface.cancel() }
            .show()
    }

    /**Funcion para eliminar el marcador seleccionado en el mapa*/
    private fun deleteMarker(markerToDelete: Marker) {
        Log.i(TAG, "OnWindowClickDelete")
        markers.remove(markerToDelete)
        markerToDelete.remove()

    }

    /**
     * Obtiene la ultima Ubicación del usuario
     */
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


    }

    /**
     * Iniciar interfaces graficas
     */
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

    /**
     * Iniciar el menu de nav desplegable
     */
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
        tvUser.text = MainActivity.user

    }

    /**
     * Crear el mapa en el activity
     */
    private fun createMapFragment() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.fragggment_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Navegación por el menu desplegable
     */
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

    /**
     * Llamada de actividades
     */
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
        val intent = Intent(this, EjercicioActivity::class.java)
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

    /**
     * Cierre de sesión del usuario
     */
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

























