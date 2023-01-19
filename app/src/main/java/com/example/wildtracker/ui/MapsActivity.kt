package com.example.wildtracker.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Context
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
import com.google.maps.android.SphericalUtil
import kotlinx.android.synthetic.main.map_coment.*
import java.text.SimpleDateFormat
import java.util.*
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
    var date = " "

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

    @SuppressLint("SuspiciousIndentation")
    fun alertScrollView(markerToDelete: Marker, snippet: String?) {
        val progresDialog = ProgressDialog(this)
        progresDialog.setMessage("Cargando Imagen")
        progresDialog.setCancelable(false)
        progresDialog.show()


        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val myScrollView: View = inflater.inflate(R.layout.maps_place_info, null, false)

        val tv = myScrollView
            .findViewById<View>(R.id.textViewWithScroll) as TextView

        // Initializing a blank textview so that we can just append a text later
        tv.text = ""
        var descripcion = " "


        if (snippet != null) {

            db.collection("locations").document(snippet).collection("Comentarios").get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        try {
                            descripcion = document.get("Descripcion").toString()
                            date = document.get("Fecha").toString()
                            if ((document.get("Descripcion")
                                    .toString() != "null") && descripcion.length > 4
                            ) {
                                myScrollView.setVisibility(View.GONE);
                                myScrollView.setVisibility(View.VISIBLE);
                                tv.append("Comentario : $date\n")
                                tv.append("${descripcion}\n")
                            }
                        } catch (e: Exception) {

                        }
                    }
                    if (progresDialog.isShowing) {
                        progresDialog.dismiss()
                    }
                }
        }


        AlertDialog.Builder(this).setView(myScrollView)
            .setTitle("Informacion del lugar")
            .setNeutralButton("Agregar Comentario") { _, _ ->
                Toast.makeText(this, "Intentas agregar un comentario", Toast.LENGTH_LONG).show()
                alertAddComent(snippet)
            }
            .setPositiveButton(
                "OK"
            ) { dialog, id -> dialog.cancel() }.show()

        Toast.makeText(this, snippet, Toast.LENGTH_LONG).show()
        if (snippet != null) {


        }


    }

    fun alertAddComent(snippet: String?) {
        val progresDialog = ProgressDialog(this)
        progresDialog.setMessage("Cargando Imagen")
        progresDialog.setCancelable(false)
        progresDialog.show()

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val myScrollView: View = inflater.inflate(R.layout.map_coment, null, false)

        val etMapComment = myScrollView.findViewById<View>(R.id.MapsComent) as EditText
        // Initializing a blank textview so that we can just append a text later

        val dialogBuilder = AlertDialog.Builder(this)

        val alertDialog: AlertDialog = dialogBuilder.create()
        dialogBuilder.setView(myScrollView)
            .setTitle("Comentario del lugar")
            .show()
        val btAddMarkerAlertDialog = myScrollView.findViewById<Button>(R.id.buttonAddCommentAlert)
        val btCancelMarkerAlertDialog = myScrollView.findViewById<Button>(R.id.buttonCancelAlert)
        var RatingPlace = findViewById<RatingBar>(R.id.rBar)

        btAddMarkerAlertDialog.setOnClickListener {

            // Toast.makeText(this,"ESTRELLAS ${   RatingPlace.rating}",Toast.LENGTH_LONG).show()

            var rating: String? = RatingPlace?.rating.toString()
            // Toast.makeText(this,"Añadiendo comentario con ${ etMapComment.text.toString() + RatingPlace?.rating}",Toast.LENGTH_LONG).show()

            //Funcion para agregar el comentario en firebase
            etMapComment.text
            val pattern = "yyyy-MM-dd HH:mm"
            val simpleDateFormat = SimpleDateFormat(pattern)
            date = simpleDateFormat.format(Date().time)

            // db.collection("locations").document(snippet).collection("Comentarios").get().addOnSuccessListener{ result->
            if (snippet != null) {
                db.collection("locations").document(snippet).collection("Comentarios").document()
                    .set(
                        hashMapOf(
                            "Descripcion" to etMapComment.text.toString(),
                            "Fecha" to date,
                            "Calificacion" to rating

                        )

                    )
            }
            callMapsActivity()
        }

        btCancelMarkerAlertDialog.setOnClickListener {
            Toast.makeText(this, "Intentando cerrar", Toast.LENGTH_LONG).show()
            callMapsActivity()
        }


    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMapLongClickListener { latLng ->
            //Validar ubicacion con los demas puntos de firebase
            //validarPuntoenMapa(latLng)
            AlertaMap(latLng)
        }
        map.setOnInfoWindowLongClickListener { markerToDelete ->
            AlertaModificarMapa(markerToDelete, markerToDelete.snippet)
        }
        map.setOnInfoWindowClickListener {

                markerToDelete ->
            alertScrollView(markerToDelete, markerToDelete.snippet)

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
                    val contadorAñadido = document.get("contador añadir") as Long
                    val contadorEliminar = document.get("contador eliminar") as Long

                    //Verifica que tipo de marcador es: Parque || Gimnasio para asi poder añadir cada marcador de cada "location"


                    if (placeType.equals("Parque") && contadorAñadido.toInt() >= 5 && contadorEliminar > -4) {
                        val marker = map.addMarker(
                            MarkerOptions().position(latLng)
                                .title("${document.get("tipo") as String}")
                                .snippet("${document.get("descripcion") as String}")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.park))
                        )
                        markers.add(marker!!)
                    } else if (placeType.equals("Gimnasio") && contadorAñadido.toInt() >= 5 && contadorEliminar > -4) {
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
            } catch (ex: Exception) {

            }
        }


    }

    /**
     * Muestra 2 tipos de alert Dialog:
     * 1. Para preguntar si que quiere añadir un nuevo marcador: Si ||
     *    No || ?
     * 2. Para verificar que se desea añadir
     */
    private fun AlertaMap(latLng: LatLng1) {


        //Inicializa el primer AlertDialog
        builder = AlertDialog.Builder(this)
        val dialogBuilder = AlertDialog.Builder(this)
        // Toast.makeText(this,"",Toast.LENGTH_LONG).show()


        builder.setTitle("Añadir un nuevo lugar en mapa")
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
                        validarPuntoenMapa(latLng, selectedPlace, edDescripction)
                        alertDialog.dismiss()

                    }
                }
                btCancell.setOnClickListener { alertDialog.dismiss() }

            }
            .setNegativeButton("No") { dialogInterface, it ->
                dialogInterface.cancel() //Se cancela el agregar un lugar
            }
            .setNeutralButton("Ayuda") { dialogInterface, it ->
                Toast.makeText(
                    this@MapsActivity,
                    "Agregar un punto en el marcador", Toast.LENGTH_SHORT
                ).show()
            }
            .show()
    }

    private fun validarPuntoenMapa(
        latLng: com.google.android.gms.maps.model.LatLng,
        selectedPlace: String,
        edDescripction: String
    ) {

        var distance = 10.0
        db.collection("locations").get().addOnSuccessListener { result ->
            //Consulta en la base de datos los usuarios que coicidan con el nombre de usuario a dejar de seguir, cuando lo encuentra lo elimina
            var latitudActual = latLng.latitude;
            var longitudActual = latLng.longitude
            var latitud = "";
            var longitud = ""
            var EstaCerca = false
            var Valido = false
            for (document in result) {
                var tipo = document.get("tipo").toString()
                var descripcion = document.get("descripcion").toString()
                latitud = document.get("latitud").toString(); latitud.toDouble()
                longitud = document.get("longitud").toString();longitud.toDouble()

                //Obtener ubicaciones
                val miPosicion = LatLng1(latitudActual, longitudActual)
                var posicionCercana = LatLng1(latitud.toDouble(), longitud.toDouble())
                Valido = descripcion.uppercase() != edDescripction.uppercase()

                //Operacion para determinar la distancia entre 2 puntos
                distance = SphericalUtil.computeDistanceBetween(miPosicion, posicionCercana)
                if (distance < 101 && tipo.equals(selectedPlace)) {
                    EstaCerca = true

                }

            }
            if (!EstaCerca)
                addMarker(latLng, edDescripction, selectedPlace)
            else if (EstaCerca) {
                Toast.makeText(
                    this,
                    "No puedes añadir un marcador aqui, ya hay otro en 100m o menos",
                    Toast.LENGTH_SHORT
                ).show()
            }


        }
        // Log.d("Estado:", EstaCerca.toString())

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
        var contadorAñadir: Int = 0
        var contadorEliminar: Int = 0
        val ghostmarker = map.addMarker(
            MarkerOptions().position(latLng).title("${selectedPlace}").snippet("${descripcion}")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.fantasma))
        )
        markers.add(ghostmarker!!)
        /*
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


        }*/
        /**Añade a la base de datos marcadores*/
        db.collection("locations").document("$descripcion").set(
            hashMapOf(
                "latitud" to latitud,
                "longitud" to longitud,
                "descripcion" to descripcion,
                "tipo" to selectedPlace,
                "contador añadir" to contadorAñadir,
                "contador eliminar" to contadorEliminar

            )

        )
        val markerRef = db.collection("locations").document("$descripcion")

        // markerRef.update("contador añadir", FieldValue.increment(1))
        db.collection("locations").document(descripcion).collection("eliminan").document().set(
            hashMapOf(
                "userID" to ("Empty")
            )

        )


        markerRef.update("contador añadir", FieldValue.increment(1))
        db.collection("locations").document(descripcion).collection("añaden").document().set(
            hashMapOf(
                "userID" to (FirebaseAuth.getInstance().currentUser?.uid ?: LoginActivity)
            )

        )


    }

    /**AlertDialog para preguntar si se desea eliminar un marcador en el mapa o sumar puntos a añadirlo*/
    private fun AlertaModificarMapa(markerToDelete: Marker, snippet: String?) {
        var userID = ""
        var Votado = false
        Toast.makeText(this, snippet, Toast.LENGTH_SHORT).show()
        var contador = 1
        val markerRef = db.collection("locations").document("$snippet")
        builder = AlertDialog.Builder(this)
        builder.setTitle("Punto en el mapa")
            .setMessage("Este es un marcador a añadir, puedes ayudar para añadirlo o eliminarlo!")
            .setCancelable(true)
            .setPositiveButton("Añadir") { dialogInterface, it ->


                //Quien lo manda

                if (snippet != null) {
                    db.collection("locations").document(snippet).collection("añaden").get()
                        .addOnSuccessListener { result ->
                            for (document in result) {
                                if ((document.get("userID")).toString() == (FirebaseAuth.getInstance().currentUser?.uid
                                        ?: LoginActivity)
                                ) {
                                    ++contador
                                    // Toast.makeText(this,"Contador validado $contador",Toast.LENGTH_SHORT).show()
                                }
                                if ((document.get("userID")).toString() != (FirebaseAuth.getInstance().currentUser?.uid
                                        ?: LoginActivity) && contador < 1
                                ) {
                                    // Toast.makeText(this,"Contador validado $contador",Toast.LENGTH_SHORT).show()
                                    db.collection("locations").document(snippet)
                                        .collection("añaden").document().set(
                                        hashMapOf(
                                            "userID" to (FirebaseAuth.getInstance().currentUser?.uid
                                                ?: LoginActivity)
                                        )
                                    )
                                    markerRef.update("contador añadir", FieldValue.increment(1))

                                }
                            }
                        }
                    if (contador >= 1) {
                        Toast.makeText(this, "Ya has votado", Toast.LENGTH_SHORT).show()
                    } else {
                        //  Toast.makeText(this,"Contador $contador",Toast.LENGTH_SHORT).show()
                    }
                }

                finish();
                startActivity(getIntent());
            }
            .setNegativeButton("Eliminar") { dialogInterface, it -> //dialogInterface.cancel()

                //Quien lo manda
                if (snippet != null) {
                    db.collection("locations").document(snippet).collection("eliminan").get()
                        .addOnSuccessListener { result ->
                            for (document in result) {
                                if ((document.get("userID")).toString() == (FirebaseAuth.getInstance().currentUser?.uid
                                        ?: LoginActivity)
                                ) {
                                    ++contador
                                    Votado = true
                                    // Toast.makeText(this,"Contador validado $contador",Toast.LENGTH_SHORT).show()
                                }
                                if ((document.get("userID")).toString() != (FirebaseAuth.getInstance().currentUser?.uid
                                        ?: LoginActivity) && contador < 1 && !Votado
                                ) {
                                    // Toast.makeText(this,"Contador validado $contador",Toast.LENGTH_SHORT).show()
                                    db.collection("locations").document(snippet)
                                        .collection("eliminan").document().set(
                                        hashMapOf(
                                            "userID" to (FirebaseAuth.getInstance().currentUser?.uid
                                                ?: LoginActivity)
                                        )
                                    )
                                    markerRef.update("contador eliminar", FieldValue.increment(-1))
                                }
                            }
                        }
                    if (contador >= 1) {
                        Toast.makeText(this, "Ya has votado", Toast.LENGTH_SHORT).show()
                    } else {
                        //   Toast.makeText(this,"Contador $contador",Toast.LENGTH_SHORT).show()
                    }
                }

                finish();
                startActivity(getIntent());
            }
            .show()
        //alertScrollView(markerToDelete, snippet)
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
            R.id.nav_metas -> callMetasActivity()
            R.id.nav_ranking -> callRankingActivity()
            R.id.nav_chat -> callChatActivity()
            R.id.logOut -> signOut()

            R.id.nav_musica -> callMusica()
            R.id.nav_amigos -> callAmigosActivity()
            R.id.Settings -> callAjustesActivity()
            R.id.nav_seguimiento -> callSeguimientoActivity()
        }

        drawer.closeDrawer(GravityCompat.START) // cerrar menu

        return true
    }

    /**
     * Llamada de actividades
     */
    private fun callAjustesActivity() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun callAmigosActivity() {
        val intent = Intent(this, Activity_Amigos::class.java)
        startActivity(intent)
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
        val intent = Intent(this, MetasActivity::class.java)
        startActivity(intent)
    }

    private fun callMusica() {
        val intent = Intent(this, mPlayerActivity::class.java)
        startActivity(intent)
    }

    /**
     * Cierre de sesión del usuario
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























