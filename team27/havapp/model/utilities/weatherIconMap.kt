package no.uio.ifi.in2000.team27.havapp.model.utilities

/*
En map som oversetter alle vaerbeskrivelser fra engelsk til norsk.
 */
val customIconsMap = mapOf(
    "clearsky_day" to "klarvær_sol",
    "clearsky_night" to "klarvær_måne",
    "clearsky_polartwilight" to "",
    "cloudy" to "skyet",
    "fair_day" to "delvis_skyet_dag",
    "fair_night" to "delvis_skyet_natt",
    "fair_polartwilight" to "lett_skyet_dag",
    "fog" to "tåke",
    "heavyrain" to "kraftig_regn",
    "heavyrainandthunder" to "kraftige_regnbyger_og_torden",
    "heavyrainshowers_day" to "kraftige_regnbyger_og_torden_sol",
    "heavyrainshowers_night" to "kraftige_regnbyger_natt",
    "heavyrainshowers_polartwilight" to "kraftige_regnbyger",
    "heavyrainshowersandthunder_day" to "regnbyger_torden_sol",
    "heavyrainshowersandthunder_night" to "lette_regnbyger_og_torden_natt",
    "heavyrainshowersandthunder_polartwilight" to "kraftige_regnbyger_og_torden_sol",
    "heavysleet" to "kraftig_sludd",
    "heavysleetandthunder" to "kraftige_sluddbyger_torden",
    "heavysleetshowers_day" to "kraftige_sluddbyger_sol",
    "heavysleetshowers_night" to "kraftige_sluddbyger_natt",
    "heavysleetshowers_polartwilight" to "kraftige_sluddbyger_sol",
    "heavysleetshowersandthunder_day" to "kraftige_sluddbyger_torden_sol",
    "heavysleetshowersandthunder_night" to "kraftige_sluddbyger_torden_natt",
    "heavysleetshowersandthunder_polartwilight" to "kraftige_sluddbyger_torden_sol",
    "heavysnow" to "kraftig_snø",
    "heavysnowandthunder" to "kraftige_snøbyger_og_torden",
    "heavysnowshowers_day" to "kraftige_snøbyger_sol",
    "heavysnowshowers_night" to "kraftige_snøbyger_natt",
    "heavysnowshowers_polartwilight" to "kraftige_snøbyger_sol",
    "heavysnowshowersandthunder_day" to "kraftige_snøbyger_og_torden_sol",
    "heavysnowshowersandthunder_night" to "kraftige_snøbyger_og_torden_natt",
    "heavysnowshowersandthunder_polartwilight" to "kraftige_snøbyger_og_torden_sol",
    "lightrain" to "lett_regn",
    "lightrainandthunder" to "lett_regn_og_torden",
    "lightrainshowers_day" to "lette_regnbyger_sol",
    "lightrainshowers_night" to "lette_regnbyger_natt",
    "lightrainshowers_polartwilight" to "lette_regnbyger_sol",
    "lightshowersandthunder_day" to "lette_regnbyger_torden_sol",
    "lightshowersandthunder_night" to "lette_regnbyger_og_torden_natt",
    "lightshowersandthunder_polartwilight" to "lette_regnbyger_torden_sol",
    "lightsleet" to "lett_sludd",
    "lightsleetandthunder" to "lette_sluddbyger_og_torden",
    "lightsleetshowers_day" to "sluddbyger_sol",
    "lightsleetshowers_night" to "sluddbyger_natt",
    "lightleetshowers_polartwilight" to "sluddbyger_sol",
    "lightsnow" to "lett_snø",
    "lightsnowandthunder" to "lette_snøbyger_og_torden",
    "lightsnowshowers_day" to "lette_snøbyger_sol",
    "lightsnowshowers_night" to "lette_snøbyger_natt",
    "lightsnowshowers_polartwilight" to "lette_snøbyger_sol",
    "lightssleetshowersandthunder_day" to "lette_sluddbyger_og_torden_dag",
    "lightssleetshowersandthunder_night" to "lette_sluddbyger_og_torden",
    "lightssleetshowersandthunder_polartwilight" to "lette_sluddbyger_og_torden_dag",
    "lightssnowshowersandthunder_day" to "lette_snøbyger_og_torden",
    "lightssnowshowersandthunder_night" to "lette_snøbyger_og_torden_natt",
    "lightssnowshowersandthunder_polartwilight" to "lette_snøbyger_og_torden",
    "partlycloudy_day" to "lett_skydet_dag",
    "partlycloudy_night" to "lett_skyet_natt",
    "partlycloudy_polartwilight" to "lett_skydet_dag",
    "rain" to "regn",
    "rainandthunder" to "regn_og_torden",
    "rainshowers_day" to "regnbyger_sol",
    "rainshowers_night" to "regnbyger_natt",
    "rainshowers_polartwilight" to "regnbyger_sol",
    "rainshowersandthunder_day" to "regnbyger_torden_sol",
    "rainshowersandthunder_night" to "lette_regnbyger_og_torden_natt",
    "rainshowersandthunder_polartwilight" to "regnbyger_torden_sol",
    "sleet" to "sludd",
    "sleetandthunder" to "sluddbyger_og_torden",
    "sleetshowers_day" to "sluddbyger_sol",
    "sleetshowers_night" to "sluddbyger_natt",
    "sleetshowers_polartwilight" to "sluddbyger_sol",
    "sleetshowersandthunder_day" to "sluddbyger_og_torden_sol",
    "sleetshowersandthunder_night" to "sluddbyger_og_torden_natt",
    "sleetshowersandthunder_polartwilight" to "sluddbyger_og_torden_sol",
    "snow" to "snø",
    "snowandthunder" to "snøbyger_og_torden",
    "snowshowers_day" to "snø_sol",
    "snowshowers_night" to "snø_natt",
    "snowshowers_polartwilight" to "snø_sol",
    "snowshowersandthunder_day" to "snøbyger_og_torden_dag",
    "snowshowersandthunder_night" to "snøbyger_og_torden_natt",
    "snowshowersandthunder_polartwilight" to "snøbyger_og_torden_dag",
)

fun getWeatherIcon(yrIcon: String): String {
    return customIconsMap[yrIcon] ?: "Fant ikke match for YR-Ikon: $yrIcon"
}


fun main() {
    println(getWeatherIcon("cloudy"))

}
