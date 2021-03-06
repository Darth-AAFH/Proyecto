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
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import com.example.wildtracker.LoginActivity
import com.example.wildtracker.R
import com.example.wildtracker.musica.mPlayerActivity
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
import com.google.firebase.firestore.FieldValue
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
     * @throws LocationServices accede a la ubicaci??n
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
     * Revisa si la ubicaci??n est?? habilitada, si no lo esta, una
     * funci??n la manda habilitar
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

    /** Solicita el acceso a la ubicaci??n */
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
     * En el resultado de pedir el permiso de ubicaci??n
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
                    "Para activar la localizaci??n ve a ajustes y acepta los permisos",
                    Toast.LENGTH_SHORT
                ).show()

            }
            else -> {}
        }
    }

    /**
     * Funci??n que se manda a llamar cuando se pudo acceder a la
     * ubicaci??n y el mapa se ha cargado
     *
     * @param map Instancia de un mapa de Google
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMapLongClickListener { latLng ->
            showAlertAddDialog(latLng)
        }
        map.setOnInfoWindowLongClickListener { markerToDelete ->

            showAlertDeleteDialog(markerToDelete,markerToDelete.snippet )
        }
       /* map.setOnMarkerClickListener {
            askForAddOrDelete(markers.snippet)
        }*/
        createMarker()
        enableMyLocation()

        /*
        * Cuando se carga el mapa, accede a Firebase Cloud Firestore y trae todos los datos
        * que se encuentran en "locations" asignando los respectivos valores de cada marcador
        * para asi poder mostrarlos en el mapa       * */
        db.collection("locations").get().addOnSuccessListener {
            try {
            var ultimo = it.last().get("ID")
            for (document in it) { // Entra a las propiedades de cada "locations"
                val lat = document.get("latitud") as Double
                val lng = document.get("longitud") as Double
                val latLng: LatLng1 = LatLng1(lat, lng)
                val placeType = document.get("tipo") as String
                val contadorA??adido = document.get("contador a??adir") as Long
                val contadorEliminar = document.get("contador eliminar") as Long

                //Verifica que tipo de marcador es: Parque || Gimnasio para asi poder a??adir cada marcador de cada "location"


                if (placeType.equals("Parque") && contadorA??adido.toInt() >= 5 && contadorEliminar>-4) {
                    val marker = map.addMarker(
                        MarkerOptions().position(latLng)
                            .title("${document.get("tipo") as String}")
                            .snippet("${document.get("descripcion") as String}")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.park))
                    )
                    markers.add(marker!!)
                } else if (placeType.equals("Gimnasio") && contadorA??adido.toInt() >= 5 && contadorEliminar>-4) {
                    val marker = map.addMarker(
                        MarkerOptions().position(latLng)
                            .title("${document.get("tipo") as String}")
                            .snippet("${document.get("descripcion") as String}")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.gym))
                    )
                    markers.add(marker!!)
                } else {
                    val marker = map.addMarker(
                        MarkerOptions().position(latLng)
                            .title("${document.get("tipo") as String}")
                            .snippet("${document.get("descripcion") as String}")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.fantasma))
                    )
                    markers.add(marker!!)
                    if (contadorEliminar.toInt() < -4) {
                        deleteMarker(marker)
                        db.collection("locations").document("${marker.snippet}").delete()
                        markers.remove(marker)
                    }

                }
            }
            }
            catch (ex:Exception){

            }
        }


    }

    /**
     * Muestra 2 tipos de alert Dialog:
     * 1. Para preguntar si que quiere a??adir un nuevo marcador: Si ||
     *    No || ?
     * 2. Para verificar que se desea a??adir
     */
    private fun showAlertAddDialog(latLng: LatLng1) {
        //Inicializa el primer AlertDialog
        builder = AlertDialog.Builder(this)
        val dialogBuilder = AlertDialog.Builder(this)
        builder.setTitle("A??adir un nuevo lugar en mapa")
            .setMessage("Quieres Agregar un marcador en este punto?")
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
                 * Edit text para la descripci??n del lugar en el mapa
                 */
                val etMarkerDescription = dialogView.findViewById<View>(R.id.PlacesName) as EditText

                /**
                 * Segundo AlertDialog para verificar el punto a a??adir
                 */
                val alertDialog: AlertDialog = dialogBuilder.create()
                alertDialog.show()
                /** Boton para confirmar el a??adir lugar */
                val btAddMarkerAlertDialog = dialogView.findViewById<Button>(R.id.buttonAddAlert)
                btAddMarkerAlertDialog.isVisible = false
                /** Boton para cancelar el a??adir lugar */
                val btCancell = dialogView.findViewById<Button>(R.id.buttonCancelAlert)
                val radioGroup = dialogView.findViewById<RadioGroup>(R.id.RadioGroupDialog)

                /**
                 * Variable que almacena el tipo de lugar del marcador
                 * a??adido
                 */
                var selectedPlace = ""
                /**RadioGrup elige el tipo de marcador a a??adir: Parque || Gimnasio
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
                    /**Obtiene la descripci??n del marcador a colocar en el mapa*/
                    var edDescripction = etMarkerDescription.text.toString()
                    if (edDescripction.isEmpty())
                        etMarkerDescription.error = "A??ade una descripci??n al lugar"
                    else {
                        addMarker(latLng, edDescripction, selectedPlace)
                        alertDialog.dismiss()
                    }
                }
                btCancell.setOnClickListener { alertDialog.dismiss() }

            }
            .setNegativeButton("No") { dialogInterface, it -> dialogInterface.cancel() }
            .setNeutralButton("Ayuda") { dialogInterface, it ->
                Toast.makeText(
                    this@MapsActivity,
                    "Agregar un punto en el marcador", Toast.LENGTH_SHORT
                ).show()
            }
            .show()
    }

    /**
     * Funcion para a??adir marcadores en el mapa
     * @param latLng  Latlng -> Recibe latitud y longitud
     * @param descripcion  String -> Descripci??n del lugar a a??adir
     * @param selectedPlace String -> Tipo de lugar "Parque" o "Gimnasio"
     */
    private fun addMarker(latLng: LatLng1, descripcion: String, selectedPlace: String) {
        //Separar lat y lng para despues almacenarla en la base de datos como datos de tipo Double
        val latitud: Double = latLng.latitude
        val longitud: Double = latLng.longitude
        var contadorA??adir: Int = 0
        var contadorEliminar: Int = 0
        val ghostmarker = map.addMarker(
            MarkerOptions().position(latLng).title("${selectedPlace}").snippet("${descripcion}")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.fantasma))
        )
        markers.add(ghostmarker!!)
        /*
        // A??adir un marcador dependiendo del tipo de lugar "Gimasio" o "Parque"
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


        }*/
        /**A??ade a la base de datos marcadores*/
        db.collection("locations").document("$descripcion").set(
            hashMapOf(
                "latitud" to latitud,
                "longitud" to longitud,
                "descripcion" to descripcion,
                "tipo" to selectedPlace,
                "contador a??adir" to contadorA??adir,
                "contador eliminar" to contadorEliminar

            )

        )
        val markerRef = db.collection("locations").document("$descripcion")

        markerRef.update("contador a??adir", FieldValue.increment(1))
    }

    /**AlertDialog para preguntar si se desea eliminar un marcador en el mapa o sumar puntos a a??adirlo*/
    private fun showAlertDeleteDialog(markerToDelete: Marker, snippet: String?) {
        Toast.makeText(this,snippet,Toast.LENGTH_SHORT).show()
        val markerRef = db.collection("locations").document("$snippet")
        builder = AlertDialog.Builder(this)
        builder.setTitle("Punto en el mapa")
            .setMessage("Este es un marcador a a??adir, puedes ayudar para a??adirlo o eliminarlo!")
            .setCancelable(true)
            .setPositiveButton("A??adir") { dialogInterface, it ->
                //deleteMarker(markerToDelete)
                markerRef.update("contador a??adir", FieldValue.increment(1))
                finish();
                startActivity(getIntent());
            }
            .setNegativeButton("Eliminar") { dialogInterface, it -> //dialogInterface.cancel()
                markerRef.update("contador eliminar", FieldValue.increment(-1))
                finish();
                startActivity(getIntent());
            }
            .show()

    }

    /**Funcion para eliminar el marcador seleccionado en el mapa*/
    private fun deleteMarker(markerToDelete: Marker) {
        Log.i(TAG, "OnWindowClickDelete")
        markers.remove(markerToDelete)
        markerToDelete.remove()
    }

    /**
     * Obtiene la ultima Ubicaci??n del usuario
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
     * Navegaci??n por el menu desplegable
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_perfil -> callPerfilActivity()
            R.id.nav_inicio -> callInicioActivity()
            R.id.nav_plantillas -> callPlantillasActivity()
            R.id.nav_ejercicio -> callEjercicioActivity()
            R.id.nav_maps -> callMapsActivity()
            
            R.id.nav_ranking -> callRankingActivity()
            R.id.nav_chat -> callChatActivity()
            R.id.logOut -> signOut()
            
            R.id.nav_musica ->callMusica()
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
        val intent = Intent(this, callMetasActivity()::class.java)
        startActivity(intent)
    }
    private fun callMusica() {
        val intent = Intent(this, mPlayerActivity::class.java)
        startActivity(intent)
    }

    /**
     * Cierre de sesi??n del usuario
     */
    fun signOut() {

        LoginActivity.useremail = ""
        FirebaseAuth.getInstance().signOut()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("727481893022-adct709pnvj5tlihh532i6gjgm26thh6.apps.googleusercontent.com")
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()
        //Cierra sesion y manda devuelta al login
        deleteAppData()
    }
    private fun deleteAppData() {
        try {
            // clearing app data
            val packageName = applicationContext.packageName
            val runtime = Runtime.getRuntime()
            runtime.exec("pm clear $packageName")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}

























