package no.uio.ifi.in2000.team27.havapp.model.impact

/*
Denne filen inneholder en liste med FunFactCards objekter som brukes i ImpactScreen og HomeScreen.
 */
data class FunFactCard(
    val title: String,
    val text: String,
    val img: String
)

val funfacts = mutableListOf(
    FunFactCard(
        title = "Havet er stort",
        text = "Havet dekker 71% av jordens overflate.",
        img = "https://d3i6fh83elv35t.cloudfront.net/static/2022/03/GettyImages-659285427-1024x680.jpg"
    ),
    FunFactCard(
        title = "Havet er dypt",
        text = "Det dypeste punktet i havet er Marianergropen, som er 11 034 meter dyp.",
        img = "https://static.vecteezy.com/system/resources/thumbnails/022/249/457/small_2x/deep-sea-or-under-the-deep-water-horizon-sun-rays-generative-ai-photo.jpg"
    ),
    FunFactCard(
        title = "Havet er varmt",
        text = "Havet absorberer mye av solenergien, og er derfor en viktig faktor i reguleringen av jordens klima.",
        img = "https://news.stthomas.edu/wp-content/uploads/2020/01/Ocean-1.jpg"
    ),

    FunFactCard(
        title = "Havet er hjemmet til nesten 95 prosent av alt liv",
        text = "Faktisk er 94 prosent av livet akvatisk. Det betyr at de av oss som bor på land, er en veldig liten minoritet.",
        img = "https://cff2.earth.com/uploads/2020/08/16145355/shutterstock_1314085790-scaled.jpg"
    ),

    FunFactCard(
        title = "Gull i havet",
        text = "Det er nok gull i havet til at hver av oss kan ha ni pund av det",
        img = "https://www.thoughtco.com/thmb/H5htpRqz0nzmEa_DaI95-N9dg4A=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/Treasure-chest-underwater-fergregory-5b0db2ffa474be003765cad1.jpg"
    ),

    FunFactCard(
        title = "Jordens lengste fjellkjede er under vann",
        text = "Den faktiske lengste fjellkjeden på jorden er den underjordiske Mid-Oceanic Ridge.",
        img = "https://www.greenpeace.org/static/planet4-international-stateless/2019/10/d21ffac5-gp0sttd9m-1024x683.jpg"
    ),

    FunFactCard(
        title = "Mennesker bruker bare 1% av all tilgjengelig vann",
        text = "Bare 1% av jordens vann er trygt for menneskelig forbruk.",
        img = "https://climate.mit.edu/sites/default/files/2022-04/dan-meyers-DNvv48VzZiA-unsplash.jpg"
    ),

    FunFactCard(
        title = "Vi har bedre kart over Mars enn over havet",
        text = " Til tross for at det er nesten 80 millioner kilometer unna.",
        img = "https://gfx.nrk.no/a9j0_sWi2CxRLdVtSaI2jgczaGrl59DrgupYeAnDqZYw.jpg"
    ),

    FunFactCard(
        title = "78% av marine pattedyr er i fare for å kveles av plast",
        text = "Plastposer og annet plastavfall som havner i havet dreper over 1 000 000 sjødyr hvert år.",
        img = "https://i.natgeofe.com/n/ef3f1df1-e010-4eba-81eb-658609d02d52/plastic-waste-single-use-worldwide-consumption-animals-2.jpg"
    ),

    FunFactCard(
        title = "Det er internettforbindelse i havet",
        text = "En kabel som er begravd dypt i havet fører mer enn 99 prosent av interkontinentale datatrafikk.",
        img = "https://media.wired.com/photos/6361b2a71341ae5cc2594c4b/master/pass/The-Most-Vulnerable-Place-on-the-Internet-Security-GettyImages-1362710800.jpg"
    ),

    FunFactCard(
        title = "Husk å resirkulere glassflasker",
        text = "Resirkulering av en glassflaske sparer nok energi til å drive en vanlig lyspære i omtrent fire timer.",
        img = "https://www.aquatechwatersystems.com/blog/wp-content/uploads/2021/10/microplastics-768x490.jpg"
    ),

    FunFactCard(
        title = "En blåhval sin tunge er tyngre enn en elefant",
        text = "Det betyr at den kan være tyngre enn 7 000 kilo.",
        img = "https://irp-cdn.multiscreensite.com/a67897e8/dms3rep/multi/blue-whale-412255e2.jpg"
    ),

    FunFactCard(
        title = "Krabber har smaksløker på føttene sine",
        text = "Krabber kaster bort ingen tid på formaliteter og bruker bare føttene sine til å smake på mat",
        img = "https://divemagazine.com/wp-content/uploads/Spider-Crabs_0249.jpg"
    ),

    FunFactCard(
        title = "Plast i havet",
        text = "Innen 2050 vil plasten i havet veie mer enn fiskene.",
        img = "https://media.audubon.org/2023-10/h_21.00248575.jpg"
    ),

    FunFactCard(
        title = "Vi kan få energi fra havet",
        text = "Mange land har begynt å eksperimentere med regulering av havets termiske energi som en kilde til fornybar energi.",
        img = "https://publicinterestnetwork.org/wp-content/uploads/2022/09/NJE_offshore-wind_Tom-Buysse_shutterstock.jpg"
    ),

    FunFactCard(
        title = "Aluminium kan gjenvinnes uendelig",
        text = " Å resirkulere bare en aluminiumsboks sparer nok energi til å drive en TV i 3 timer.",
        img = "https://astrup.no/var/astrup/storage/images/media/bilder/aluminium/aluminium-standardprofiler_1276x850/13019-1-nor-NO/Aluminium-standardprofiler_1276x850_slideshow.jpg"
    ),

    /*
    FunFactCard(
        title = "Osonlaget vil være fullstendig innen 2069",
        text = "Gjenopprettingen ble muliggjort på grunn av utrullingen av ozonlagsøyende stoffer, noe som viser at hvis mennesker jobber sammen, kan vi virkelig redde planeten!",
        img = "https://cdn.downtoearth.org.in/library/large/2023-01-09/0.28108700_1673247254_istock-861582894.jpg"
    ),*/


    )

