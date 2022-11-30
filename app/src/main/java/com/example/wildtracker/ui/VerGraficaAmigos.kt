package com.example.wildtracker.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import com.example.wildtracker.R
import kotlinx.android.synthetic.main.activity_ver_grafica_amigos.*
import org.achartengine.ChartFactory
import org.achartengine.GraphicalView
import org.achartengine.model.XYMultipleSeriesDataset
import org.achartengine.model.XYSeries
import org.achartengine.renderer.XYMultipleSeriesRenderer
import org.achartengine.renderer.XYSeriesRenderer

class VerGraficaAmigos : AppCompatActivity() {

    private var grafica: GraphicalView? = null
    private var datosGrafica: XYSeries? = null
    private val informacionGrafica = XYMultipleSeriesDataset()
    private val renderGrafica = XYMultipleSeriesRenderer()
    private var seriesRendererGrafica: XYSeriesRenderer? = null

    var dia1: Double = 0.0; var dia2: Double = 0.0; var dia3: Double = 0.0; var dia4: Double = 0.0;
    var dia5: Double = 0.0; var dia6: Double = 0.0; var dia7: Double = 0.0

    var nombre = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_grafica_amigos)

        val b = intent.extras //b toma el id de la rutina a trabajar
        if (b != null) {
            nombre = b.getString("Nombre").toString()
            dia1 = b.getDouble("Dia1")
            dia2 = b.getDouble("Dia2")
            dia3 = b.getDouble("Dia3")
            dia4 = b.getDouble("Dia4")
            dia5 = b.getDouble("Dia5")
            dia6 = b.getDouble("Dia6")
            dia7 = b.getDouble("Dia7")
        }

        iniciarGrafica()

        if(MainActivity.diaSemanaHoy == 7){
            textViewDia1!!.setText("L"); textViewDia2!!.setText("M"); textViewDia3!!.setText("M"); textViewDia4!!.setText("J")
            textViewDia5!!.setText("V"); textViewDia6!!.setText("S"); textViewDia7!!.setText("D")
        }
        if(MainActivity.diaSemanaHoy == 6){
            textViewDia2!!.setText("L"); textViewDia3!!.setText("M"); textViewDia4!!.setText("M"); textViewDia5!!.setText("J")
            textViewDia6!!.setText("V"); textViewDia7!!.setText("S"); textViewDia1!!.setText("D")
        }
        if(MainActivity.diaSemanaHoy == 5){
            textViewDia3!!.setText("L"); textViewDia4!!.setText("M"); textViewDia5!!.setText("M"); textViewDia6!!.setText("J")
            textViewDia7!!.setText("V"); textViewDia1!!.setText("S"); textViewDia2!!.setText("D")
        }
        if(MainActivity.diaSemanaHoy == 4){
            textViewDia4!!.setText("L"); textViewDia5!!.setText("M"); textViewDia6!!.setText("M"); textViewDia7!!.setText("J")
            textViewDia1!!.setText("V"); textViewDia2!!.setText("S"); textViewDia3!!.setText("D")
        }
        if(MainActivity.diaSemanaHoy == 3){
            textViewDia5!!.setText("L"); textViewDia6!!.setText("M"); textViewDia7!!.setText("M"); textViewDia1!!.setText("J")
            textViewDia2!!.setText("V"); textViewDia3!!.setText("S"); textViewDia4!!.setText("D")
        }
        if(MainActivity.diaSemanaHoy == 2){
            textViewDia6!!.setText("L"); textViewDia7!!.setText("M"); textViewDia1!!.setText("M"); textViewDia2!!.setText("J")
            textViewDia3!!.setText("V"); textViewDia4!!.setText("S"); textViewDia5!!.setText("D")
        }
        if(MainActivity.diaSemanaHoy == 1){
            textViewDia7!!.setText("L"); textViewDia1!!.setText("M"); textViewDia2!!.setText("M"); textViewDia3!!.setText("J")
            textViewDia4!!.setText("V"); textViewDia5!!.setText("S"); textViewDia6!!.setText("D")
        }
    }

    private fun iniciarGrafica() {
        val layout = findViewById<LinearLayout>(R.id.chart2)
        if (grafica == null) {
            cargarGrafica()
            grafica = ChartFactory.getCubeLineChartView(this, informacionGrafica, renderGrafica, 0f)
            layout.addView(grafica)
        } else {
            grafica!!.repaint()
        }
    }

    private fun cargarGrafica() {
        datosGrafica = XYSeries("Simple Data")
        informacionGrafica.addSeries(datosGrafica)

        datosGrafica!!.add(0.0, dia1)
        datosGrafica!!.add(1.0, dia2)
        datosGrafica!!.add(2.0, dia3)
        datosGrafica!!.add(3.0, dia4)
        datosGrafica!!.add(4.0, dia5)
        datosGrafica!!.add(5.0, dia6)
        datosGrafica!!.add(6.0, dia7)

        seriesRendererGrafica = XYSeriesRenderer()
        renderGrafica.addSeriesRenderer(seriesRendererGrafica)
    }
}