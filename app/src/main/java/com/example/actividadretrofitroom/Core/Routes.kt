package com.example.actividadretrofitroom.Core

import kotlinx.serialization.Serializable

/**
 * Define todas las rutas de navegación de la aplicación.
 *
 * Cada objeto/clase dentro de [AppRoutes] representa un destino de navegación.
 * Se usa @Serializable para compatibilidad con Navigation Compose 2.8+,
 * lo que permite pasar argumentos tipados de forma segura sin strings hardcodeados.
 *
 * Uso desde un Composable:
 * ```kotlin
 * navController.navigate(AppRoutes.CountryDetail(cca3 = "COL"))
 * ```
 */
sealed class AppRoutes {

    /**
     * Pantalla principal — lista de países con búsqueda y filtros.
     * No requiere argumentos.
     */
    @Serializable
    data object CountryList : AppRoutes()

    /**
     * Pantalla de detalle de un país específico.
     *
     * @param cca3 Código de país de 3 letras según el estándar ISO 3166-1 alpha-3.
     *             Ejemplo: "COL" para Colombia, "USA" para Estados Unidos.
     */
    @Serializable
    data class CountryDetail(val cca3: String) : AppRoutes()
}