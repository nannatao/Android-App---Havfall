package no.uio.ifi.in2000.team27.havapp.model.alerts

/**
 * En Map fra fylkeId til fylkenavn
 * Brukes i AlertRepository
 **/

/*
    Eksempler på bruk:

    Finne fylkesnummeret til Oslo:
        val fylkeNr = Fylker.filterValues { it == "Oslo" }.keys.first()

    Finne fylkesnavnet til fylkesnummer 3:
        val fylke = Fylker.filterKeys { it == 3 }.values.first()
 */

val Fylker: Map<Int, String> = mapOf(
    3 to "Oslo",
    11 to "Rogaland",
    15 to "Møre og Romsdal",
    18 to "Nordland",
    31 to "Østfold",
    32 to "Akershus",
    33 to "Buskerud",
    34 to "Innlandet",
    39 to "Vestfold",
    40 to "Telemark",
    42 to "Agder",
    46 to "Vestland",
    50 to "Trøndelag",
    55 to "Troms",
    56 to "Finnmark",
    99 to "Uoppgitt"
)

// Funksjonen henter fylkets id fra map-et Fylker ved navn
// @param fylkenavn String
// @return fylkeId String
fun fylkeIdFraNavn(fylkeNavn: String): String {
    val fylkeId = Fylker.filterValues { it == fylkeNavn }.keys.firstOrNull().toString()
    return if (fylkeId == "3") {
        "03"
    } else fylkeId
}