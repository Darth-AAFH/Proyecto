package com.example.wildtracker

import java.util.regex.Matcher
import java.util.regex.Pattern

class ValidateEmail {
    //crea un patro y math para hacer busqueda
    companion object{
        var pat: Pattern?= null
        var mat: Matcher?= null

        fun isEmail(email:String): Boolean{
            //estruct
            //valida caracteres antes y despues del arroba y necesita de 2 a 4 caracteres despues del punto
                                          //caract val antes del @       caract antes del .    2 a 4 caracteres al final
            pat = Pattern.compile("^[\\w\\-\\_\\+]+(\\.[\\w\\-\\_]+)*@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}$")
            mat = pat!!.matcher(email)
            return mat!!.find() //valida si el patron encaja
        }
    }
}