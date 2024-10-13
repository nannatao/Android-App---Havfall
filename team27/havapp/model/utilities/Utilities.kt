package no.uio.ifi.in2000.team27.havapp.model.utilities

import no.uio.ifi.in2000.team27.havapp.R

class Utilities {
    /**
     * Denne klassen holder på en del hjelpefunksjoner vi bruker i appen.
     * Disse hjelpefunksjonene brukes på tvers av alle deler av appen, så det ga mer
     * mening å samle de her enn i ulike repositories, viewmodels og lignende.
     */

    companion object {
        fun getTrashImageFromCount(count: Int): Int {
            val trashCountToImageMap: Map<Int, Int> = mapOf(
                1 to R.drawable.trash_one,
                2 to R.drawable.trash_two,
                3 to R.drawable.trash_three,
                4 to R.drawable.trash_four,
                5 to R.drawable.trash_five,
                6 to R.drawable.trash_six,
                7 to R.drawable.trash_seven,
                8 to R.drawable.trash_eight,
                9 to R.drawable.trash_nine,
                10 to R.drawable.trash_ten
            )

            return trashCountToImageMap[count] ?: 1
        }
    }

}