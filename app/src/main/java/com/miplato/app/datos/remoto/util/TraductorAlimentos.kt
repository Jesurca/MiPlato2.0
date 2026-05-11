package com.miplato.app.datos.remoto.util

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TraductorAlimentos @Inject constructor() {

    private val diccionario = mapOf(
        "Apple" to "Manzana",
        "Banana" to "Plátano",
        "Orange" to "Naranja",
        "Strawberry" to "Fresa",
        "Grape" to "Uva",
        "Pineapple" to "Piña",
        "Mango" to "Mango",
        "Watermelon" to "Sandía",
        "Broccoli" to "Brócoli",
        "Carrot" to "Zanahoria",
        "Tomato" to "Tomate",
        "Cucumber" to "Pepino",
        "Potato" to "Patata",
        "Onion" to "Cebolla",
        "Garlic" to "Ajo",
        "Spinach" to "Espinacas",
        "Lettuce" to "Lechuga",
        "Chicken" to "Pollo",
        "Beef" to "Ternera",
        "Pork" to "Cerdo",
        "Fish" to "Pescado",
        "Egg" to "Huevo",
        "Milk" to "Leche",
        "Cheese" to "Queso",
        "Bread" to "Pan",
        "Rice" to "Arroz",
        "Pasta" to "Pasta",
        "Pizza" to "Pizza",
        "Hamburger" to "Hamburguesa",
        "Salad" to "Ensalada",
        "Soup" to "Sopa",
        "Cake" to "Pastel",
        "Cookie" to "Galleta",
        "Ice cream" to "Helado",
        "Coffee" to "Café",
        "Tea" to "Té",
        "Juice" to "Zumo",
        "Water" to "Agua",
        "Yogurt" to "Yogur",
        "Nut" to "Nuez",
        "Almond" to "Almendra",
        "Peanut" to "Cacahuete",
        "Honey" to "Miel",
        "Sugar" to "Azúcar",
        "Salt" to "Sal",
        "Pepper" to "Pimienta",
        "Oil" to "Aceite",
        "Butter" to "Mantequilla",
        "Fruit" to "Fruta",
        "Vegetable" to "Verdura",
        "Food" to "Comida",
        "Cuisine" to "Cocina",
        "Dish" to "Plato",
        "Sweet" to "Dulce",
        "Savory" to "Salado"
    )

    fun traducir(termino: String): String {
        // Normalizar el término (quitar espacios, primera letra mayúscula)
        val normalizado = termino.trim().lowercase().replaceFirstChar { it.uppercase() }
        return diccionario[normalizado] ?: termino
    }

    fun traducirLista(terminos: List<String>): List<String> {
        return terminos.map { traducir(it) }.distinct()
    }
}
