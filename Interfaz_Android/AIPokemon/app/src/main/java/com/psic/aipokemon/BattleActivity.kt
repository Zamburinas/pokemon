package com.psic.aipokemon


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import com.psic.aipokemon.core.src.*
import com.psic.aipokemon.core.src.Battle.RandomSpeed
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.Random
import java.util.Scanner


class BattleActivity : ComponentActivity() {
    private val alertDialog: AlertDialog? = null
    private var isMuteClicked = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_battle)

        val mediaPlayer = MediaPlayer.create(this, R.raw.battle);
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()

        val button2 = findViewById<ImageView>(R.id.buttonCross)
        button2.setOnClickListener {
            showExitConfirmationDialog()
        }
        val muteButton = findViewById<ImageView>(R.id.muteButton)


        muteButton.setOnClickListener {
            // Obtener el servicio de audio
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

            if (isMuteClicked) {
                // Si está silenciado, restaurar el sonido
                audioManager.adjustVolume(AudioManager.ADJUST_UNMUTE, AudioManager.FLAG_PLAY_SOUND)

            } else {
                // Si no está silenciado, silenciar el sonido
                audioManager.adjustVolume(AudioManager.ADJUST_MUTE, AudioManager.FLAG_PLAY_SOUND)

                // Cambia la imagen del botón "Mute"
                muteButton.setImageResource(R.drawable.mute_icon_icons_com_69340)
            }

            // Actualiza el estado del botón "Mute"
            isMuteClicked = !isMuteClicked
        }


        val jsonString: String = obtenerJsonString("pokemons")
        val jsonStringTable: String = obtenerJsonString("typestable")
        val jsonStringMove: String = obtenerJsonString("movements")

        val availablePokemons: List<Pokemon> = PokemonDataReader.createAvailablePokemons(jsonString,jsonStringMove)
        val typeTable: Map<String, Map<String, Double>> = PokemonDataReader.createTypeTable(jsonStringTable)

        var player = Player("Ash")
        var IA = Player("IA")
        IA.team.clear()
        while (IA.team.size < 3) {
            val randomIndex = Random().nextInt(availablePokemons.size)
            val randomPokemon = availablePokemons[randomIndex]
            IA.addPokemonToTeam(randomPokemon)
        }

        player.team.clear()
        while (player.team.size < 3) {
            val randomIndex = Random().nextInt(availablePokemons.size)
            val randomPokemon = availablePokemons[randomIndex]
            player.addPokemonToTeam(randomPokemon)
        }

        println("Initiating combat...")
        val battle = Battle(player, IA, typeTable)
        val pokemonsField: Array<String> =battle.start()

        val equipo: List<Pokemon> = player.getTeam()
        cambiarFondosDeViews(this, equipo[0].getName().lowercase(), equipo[1].getName().lowercase(), equipo[2].getName().lowercase())

        val playerPokemon= player.currentPokemon.name.lowercase()
        val pokemon1Player = this.findViewById<View>(R.id.izquierda)
        val resourceIdPlayer = this.resources.getIdentifier(playerPokemon, "drawable", this.packageName)
        pokemon1Player.setBackgroundResource(resourceIdPlayer)
        pokemon1Player.visibility = View.VISIBLE
        val iaPokemon= IA.currentPokemon.name.lowercase()
        val pokemon1Ia = this.findViewById<View>(R.id.derecha)
        val resourceIdIA = this.resources.getIdentifier(iaPokemon, "drawable", this.packageName)
        pokemon1Ia.setBackgroundResource(resourceIdIA)
        pokemon1Ia.visibility = View.VISIBLE



        pokemon1Ia.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View): Boolean {
                val pokemonData: JSONObject? = obtenerDatosPokemon(iaPokemon)
                val primaryType = pokemonData?.optString("primaryType", "")
                val secondaryType = pokemonData?.optString("secondaryType", "")
                val type = if (secondaryType?.isEmpty() == true) primaryType else "$primaryType/$secondaryType"
                val range: IntArray = RandomSpeed(IA.getCurrentPokemon().getSpeed(), 30)
                val alertDialogBuilder = AlertDialog.Builder( this@BattleActivity)
                alertDialogBuilder.setTitle("Pokemon Stats")
                alertDialogBuilder.setMessage("Name:  $iaPokemon\nType: $type\nSpeed: ${range[0]} - ${range[1]}")

                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()
                return true
            }
        })

        val pokemon1IA = this.findViewById<View>(R.id.iapokemon1)
        pokemon1IA.setBackgroundResource(resourceIdIA)

        val movesArray: Array<Move> = player.currentPokemon.moves

        val pokemonAttack1 = this.findViewById<Button>(R.id.attack1Button)
        pokemonAttack1.text=movesArray[0].name
        pokemonAttack1.setOnClickListener {
            playTurn("Attack",0, battle, player, IA)
        }
        pokemonAttack1.setOnLongClickListener {
            // Acción cuando se realiza un long click en el botón
            mostrarInformacion(player, 0)
            true  // Indica que el evento fue manejado
        }
        val pokemonAttack2 = this.findViewById<Button>(R.id.attack2Button)
        pokemonAttack2.text=movesArray[1].name
        pokemonAttack2.setOnClickListener {
            playTurn("Attack",1,battle, player, IA)
        }
        pokemonAttack2.setOnLongClickListener {
            // Acción cuando se realiza un long click en el botón
            mostrarInformacion(player, 1)
            true  // Indica que el evento fue manejado
        }
        val pokemonAttack3 = this.findViewById<Button>(R.id.attack3Button)
        pokemonAttack3.text=movesArray[2].name
        pokemonAttack3.setOnClickListener {
            playTurn("Attack",2,battle, player, IA)
        }
        pokemonAttack3.setOnLongClickListener {
            // Acción cuando se realiza un long click en el botón
            mostrarInformacion(player, 2)
            true  // Indica que el evento fue manejado
        }
        val pokemonAttack4 = this.findViewById<Button>(R.id.attack4Button)
        pokemonAttack4.text=movesArray[3].name
        pokemonAttack4.setOnClickListener {
            playTurn("Attack",3,battle, player, IA)
        }
        pokemonAttack4.setOnLongClickListener {
            // Acción cuando se realiza un long click en el botón
            mostrarInformacion(player, 3)
            true  // Indica que el evento fue manejado
        }

        val pokemon1= this.findViewById<Button>(R.id.button1)
        val pokemon2= this.findViewById<Button>(R.id.button2)
        val pokemon3= this.findViewById<Button>(R.id.button3)
        pokemon1.setOnClickListener {
            playTurn("Change",0,battle, player, IA)
        }
        pokemon2.setOnClickListener {
            playTurn("Change",1,battle, player, IA)
        }
        pokemon3.setOnClickListener {
            playTurn("Change",2,battle, player, IA)
        }



    }

    fun mostrarInformacion(player : Player, move: Int) {
        val movesArray: Array<Move> = player.currentPokemon.moves
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Movement Stat")
            .setMessage(
                """
            Type: ${movesArray[move].type}
            Power: ${movesArray[move].power}
            Acuraccy: ${movesArray[move].accuracy}
            """.trimIndent()
            )
            .setPositiveButton(
                "OK"
            ) { dialog, which -> dialog.dismiss() }
            .show()


    }

    fun ocultarInformacion() {
        // Cerrar el AlertDialog si está abierto
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss()
        }
    }

    fun playTurn(action: String, movement: Int,battle : Battle,player: Player,IA:Player){
        val HealthIA= this.findViewById<TextView>(R.id.HealthIA)
        val BarIA= this.findViewById<ProgressBar>(R.id.barraVidaDerecha)
        val HealthPlayer= this.findViewById<TextView>(R.id.HealthPlayer)
        val BarPlayer= this.findViewById<ProgressBar>(R.id.barraVidaIzquierda)
        val pokemon1Player = this.findViewById<View>(R.id.izquierda)
        val pokemon1Ia = this.findViewById<View>(R.id.derecha)

        val selectedMove2 = Random().nextInt(IA.getCurrentPokemon().getMoves().size)
        if(action=="Attack"){
            //No voy a implmentar lo de los PP/MoveIndex para no complicarme queda para hacer
            //movimiento de la IA random

            battle.resolveTurn(
                player.getCurrentPokemon(),
                IA.getCurrentPokemon(),
                movement,
                selectedMove2
            )
            //Aquí se ha resuelto el turno


        }else{
            //Cambiar
            val availablePokemon: MutableList<Pokemon> = ArrayList()

            // Filter and display only Pok\u00E9mon with remaining health points

            // Filter and display only Pok\u00E9mon with remaining health points
            for (pokemon in player.team) {
                if (!pokemon.isDead ) {
                    availablePokemon.add(pokemon)
                }
            }
            val chosenPokemon: Pokemon = availablePokemon.get(movement)
            if(!(chosenPokemon==player.currentPokemon)) {
                player.currentPokemon = chosenPokemon
                val resourceIdPlayer = this.resources.getIdentifier(player.currentPokemon.name.lowercase(), "drawable", this.packageName)
                pokemon1Player.setBackgroundResource(resourceIdPlayer)
                UpdateAttack(player)

                battle.resolveTurn(
                    player.getCurrentPokemon(),
                    IA.getCurrentPokemon(),
                    -1,
                    selectedMove2
                )

            }

        }
        if(IA.getCurrentPokemon().isDead()) {
            if(battle.isBattleOver==0) {
                while (IA.getCurrentPokemon()
                        .isDead()
                ) IA.setPokemonFromTeam(Random().nextInt(3))

                val resourceIdIA = this.resources.getIdentifier(
                    IA.getCurrentPokemon().name.lowercase(),
                    "drawable",
                    this.packageName
                )
                pokemon1Ia.setBackgroundResource(resourceIdIA)
                val pokemonAI2= this.findViewById<View>(R.id.iapokemon2)
                val resourceIdFondo = resources.getIdentifier(IA.currentPokemon.name.lowercase(), "drawable", packageName)
                if (pokemonAI2.background.constantState?.equals(ContextCompat.getDrawable(this, R.drawable.pokeball)?.constantState) == true) {

                    pokemonAI2.setBackgroundResource(resourceIdFondo)
                } else {
                    val pokemonAI3= this.findViewById<View>(R.id.iapokemon3)
                    pokemonAI3.setBackgroundResource(resourceIdFondo)
                }

            }
        }
        val healthPercentage2: Double = IA.getCurrentPokemon().getStats().getHealthPoints()
            .toDouble() / IA.getCurrentPokemon().getMaxHealthPoints() * 100
        HealthIA.text= (healthPercentage2.toInt()).toString()+ "%"
        BarIA.progress= healthPercentage2.toInt()

        if(player.getCurrentPokemon().isDead()) {
            if(battle.isBattleOver==0) {
                while (player.getCurrentPokemon()
                        .isDead()
                ) player.setPokemonFromTeam(Random().nextInt(3))

                val resourceIdPlayer = this.resources.getIdentifier(
                    player.currentPokemon.name.lowercase(),
                    "drawable",
                    this.packageName
                )
                pokemon1Player.setBackgroundResource(resourceIdPlayer)
                UpdateAttack(player)


            }
        }
        val healthPercentage1: Double = player.getCurrentPokemon().getStats().getHealthPoints()
            .toDouble() / player.getCurrentPokemon().getMaxHealthPoints() * 100
        HealthPlayer.text= (healthPercentage1.toInt()).toString()+ "%"
        BarPlayer.progress= healthPercentage1.toInt()
        //Ver si se acabó
        if(battle.isBattleOver==1){
            //Ganas tu

            val intent = Intent(this@BattleActivity, FinalActivity::class.java)
            intent.putExtra("string","You WIN")
            startActivity(intent)

        }else if (battle.isBattleOver==2){
            //Gana la Ia

            val intent = Intent(this@BattleActivity, FinalActivity::class.java)
            intent.putExtra("string","You LOOSE")
            startActivity(intent)
        }
        System.out.println(battle.isBattleOver.toString())
        return
    }

    private fun UpdateAttack(player : Player){
        val movesArray: Array<Move> = player.currentPokemon.moves
        val pokemonAttack1 = this.findViewById<Button>(R.id.attack1Button)
        pokemonAttack1.text=movesArray[0].name
        val pokemonAttack2 = this.findViewById<Button>(R.id.attack2Button)
        pokemonAttack2.text=movesArray[1].name
        val pokemonAttack3 = this.findViewById<Button>(R.id.attack3Button)
        pokemonAttack3.text=movesArray[2].name
        val pokemonAttack4 = this.findViewById<Button>(R.id.attack4Button)
        pokemonAttack4.text=movesArray[3].name

    }

    private fun cambiarFondoDeButton(context: Context, button: Button, fondo: String) {
        val resourceId = context.resources.getIdentifier(fondo, "drawable", context.packageName)
        if (resourceId != 0) {
            button.setBackgroundResource(resourceId)
        }
    }
    fun cambiarFondosDeViews(activity: ComponentActivity, archivo1: String, archivo2: String, archivo3: String) {
        val pokemon1Player = activity.findViewById<View>(R.id.pokemon1player)
        val pokemon2Player = activity.findViewById<View>(R.id.pokemon2player)
        val pokemon3Player = activity.findViewById<View>(R.id.pokemon3player)
        val button1 = activity.findViewById<Button>(R.id.button1)
        val button2 = activity.findViewById<Button>(R.id.button2)
        val button3 = activity.findViewById<Button>(R.id.button3)

        cambiarFondoDeView(activity, pokemon1Player, archivo1)
        cambiarFondoDeView(activity, pokemon2Player, archivo2)
        cambiarFondoDeView(activity, pokemon3Player, archivo3)
        cambiarFondoDeButton(activity, button1, archivo1)
        cambiarFondoDeButton(activity, button2, archivo2)
        cambiarFondoDeButton(activity, button3, archivo3)

        button1.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View): Boolean {
                mostrarPantallita(archivo1)
                return true
            }
        })

        button2.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View): Boolean {
                mostrarPantallita(archivo2)
                return true
            }
        })

        button3.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View): Boolean {
                mostrarPantallita(archivo3)
                return true
            }
        })

// Agregar OnTouchListener para manejar el evento de liberación
        button1.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                ocultarPantallita()
            }
            false
        }

        button2.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                ocultarPantallita()
            }
            false
        }

        button3.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                ocultarPantallita()
            }
            false
        }
    }

    private fun cambiarFondoDeView(context: Context, view: View, archivo: String) {
        val resourceId = context.resources.getIdentifier(archivo, "drawable", context.packageName)
        if (resourceId != 0) {
            view.setBackgroundResource(resourceId)
        }
    }


    fun showExitConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Salir de la partida")
        builder.setMessage("¿Seguro que quieres salir de la partida?")
        builder.setPositiveButton("Confirmar") { _, _ ->
            // Acción al confirmar
            val intent = Intent(this, OptionsActivity::class.java)
            startActivity(intent)
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            // Acción al cancelar
            dialog.dismiss()
        }
        builder.show()
    }

    private fun obtenerJsonString(nombreRecurso: String): String {
        val res: Resources = this.resources
        val resourceId = res.getIdentifier(nombreRecurso, "raw", this.packageName)

        if (resourceId == 0) {
            // El recurso no fue encontrado
            return ""
        }

        val inputStream: InputStream = res.openRawResource(resourceId)
        val scanner = Scanner(inputStream, StandardCharsets.UTF_8.name()).useDelimiter("\\A")
        return if (scanner.hasNext()) scanner.next() else ""
    }
    private fun mostrarPantallita(pokemonNombre: String) {
        ocultarPantallita() // Oculta la pantallita actual antes de mostrar una nueva

        // Obtiene los datos del JSON basado en el nombre del Pokémon
        val pokemonData: JSONObject? = obtenerDatosPokemon(pokemonNombre)

        // Verifica si se encontraron datos para el Pokémon
        if (pokemonData != null) {

            // Accede a los campos específicos del JSON
            val level = pokemonData.optInt("level", 0)
            val healthPoints = pokemonData.optInt("healtPoints", 0)
            val attack = pokemonData.optInt("attack", 0)
            val defense = pokemonData.optInt("defense", 0)
            val speed = pokemonData.optInt("speed", 0)
            val specialAttack = pokemonData.optInt("specialAttack", 0)
            val specialDefense = pokemonData.optInt("specialdefense", 0)
            val primaryType = pokemonData.optString("primaryType", "")
            val secondaryType = pokemonData.optString("secondaryType", "")
            val movementsArray = pokemonData.optJSONArray("movements")

            // Ahora puedes usar estos datos como desees, por ejemplo, mostrarlos en un cuadro de diálogo
            mostrarDatosPokemonEnDialogo(
                level,
                healthPoints,
                attack,
                defense,
                speed,
                specialAttack,
                specialDefense,
                primaryType,
                secondaryType,
                movementsArray
            )
        }
    }
    private fun obtenerDatosPokemon(pokemonNombre: String): JSONObject? {
        try {
            // Obtiene el JSON del recurso
            val jsonString =
                obtenerJsonString() // Reemplaza esto con tu método para obtener el JSON
            val jsonObject = JSONObject(jsonString)

            // Obtiene los datos específicos del Pokémon
            return jsonObject.optJSONObject(capitalizarPrimeraLetra(pokemonNombre))
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }


    private fun obtenerJsonString(): String? {
        val res = resources
        val inputStream = res.openRawResource(R.raw.pokemons)
        val scanner = Scanner(inputStream, StandardCharsets.UTF_8.name()).useDelimiter("\\A")
        return if (scanner.hasNext()) scanner.next() else ""
    }

    fun capitalizarPrimeraLetra(input: String?): String? {
        return if (input == null || input.isEmpty()) {
            input // Devuelve la cadena original si es nula o vacía
        } else input[0].uppercaseChar().toString() + input.substring(1)

        // Capitaliza la primera letra y concatena el resto de la cadena
    }
    private fun mostrarDatosPokemonEnDialogo(
        level: Int,
        healthPoints: Int,
        attack: Int,
        defense: Int,
        speed: Int,
        specialAttack: Int,
        specialDefense: Int,
        primaryType: String,
        secondaryType: String,
        movementsArray: JSONArray
    ) {
        val type = if (secondaryType.isEmpty()) primaryType else "$primaryType/$secondaryType"
        val movements = formatearMovimientos(movementsArray)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Datos de Pokémon")
            .setMessage(
                """
            Level: $level
            HP: $healthPoints
            Attack: $attack
            Defense: $defense
            Speed: $speed
            Special Attack: $specialAttack
            Special Defense: $specialDefense
            Type: $type
            Movements: 
            $movements
            """.trimIndent()
            )
            .setPositiveButton(
                "OK"
            ) { dialog, which -> dialog.dismiss() }
            .show()
    }

    private fun formatearMovimientos(movementsArray: JSONArray): String {
        val formattedMovements = StringBuilder()
        for (i in 0 until movementsArray.length()) {
            formattedMovements.append("\t\t\t\t" + movementsArray.optString(i))
            formattedMovements.append("\n") // Añade una tabulación después del salto de línea
        }
        return formattedMovements.toString() // Elimina cualquier espacio en blanco al final
    }


    private fun ocultarPantallita() {
        // Cerrar el AlertDialog si está abierto
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss()
        }
    }

    }



