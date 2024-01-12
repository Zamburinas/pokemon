package com.psic.aipokemon


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.animation.doOnEnd
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

val mapaColores: HashMap<String, String> = hashMapOf(
    "Electric" to "#ffd300",
    "Fire" to "#ff0000",
    "Water" to "#0000ff",
    "Grass" to "#008f39",
    "Steel" to "#a8c0ce",
    "Bug" to "#89ac76",
    "Dragon" to "#613d97",
    "Ghost" to  "#332449",
    "Fairy" to "#ffc2eb",
    "Ice" to  "#a8d6e5",
    "Fighting" to "#8e402a",
    "Normal" to "#c2c2c2",
    "Psychic" to "#f870a0",
    "Rock" to "#c5954e",
    "Dark" to "#3e2529",
    "Ground" to "#d6c185",
    "Poison" to "#5d2260",
    "Flying" to "#4682b4"
)//Para esto cambiar en dos sitios updateButton y antes, donde los inicializas

class BattleActivity : ComponentActivity() {
    private val alertDialog: AlertDialog? = null
    private var isMuteClicked = false
    private lateinit var logScrollView:ScrollView
    private lateinit var logTextView:TextView
    private lateinit var chatScrollView:ScrollView
    private lateinit var chatTextView:TextView
    private lateinit var layoutBattle: View
    private lateinit var layoutChat: View
    private var turno = 0

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dual)

        val intent1= intent

        val text = intent1.getStringExtra("Team")

        layoutBattle = layoutInflater.inflate(R.layout.activity_battle, null)
        layoutChat = layoutInflater.inflate(R.layout.activity_chat, null)

        val container = findViewById<FrameLayout>(R.id.container)
        container.addView(layoutBattle)

        logScrollView = layoutBattle.findViewById<ScrollView>(R.id.logScrollView)
        logTextView = layoutBattle.findViewById<TextView>(R.id.logTextView)
        chatScrollView = layoutChat.findViewById<ScrollView>(R.id.logScrollView)
        chatTextView = layoutChat.findViewById<TextView>(R.id.logTextView)

        val chatButton = layoutBattle.findViewById<Button>(R.id.chatButton)
        val battleButton = layoutChat.findViewById<Button>(R.id.battleButton)

        chatButton.setOnClickListener {
            switchLayouts(container)
        }

        battleButton.setOnClickListener {
            switchLayouts(container)
        }

        val mediaPlayer = MediaPlayer.create(this, R.raw.battle)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()

        val button2 = layoutBattle.findViewById<ImageView>(R.id.buttonCross)
        button2.setOnClickListener {
            showExitConfirmationDialog()
        }
        val muteButton = layoutBattle.findViewById<ImageView>(R.id.muteButton)


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

        val player = Player("Ash")
        val IA = Player("IA")
        IA.team.clear()
        while (IA.team.size < 3) {
            val randomIndex = Random().nextInt(availablePokemons.size)
            val randomPokemon = availablePokemons[randomIndex]
            IA.addPokemonToTeam(randomPokemon)
        }

        player.team.clear()
        if(text!=null){
            val elementos = text.split(",")
            for (elemento in elementos) {

                val indiceEncontrado = availablePokemons.indexOfFirst { it.name == elemento.capitalize() }

                val pokemon = availablePokemons[indiceEncontrado]
                player.addPokemonToTeam(pokemon)
            }
        }else{
            while (player.team.size < 3) {
                val randomIndex = Random().nextInt(availablePokemons.size)
                val randomPokemon = availablePokemons[randomIndex]
                player.addPokemonToTeam(randomPokemon)
            }
        }

        println("Initiating combat...")
        printLogMessage("Initiating combat...")
        val battle = Battle(player, IA, typeTable)

        val pokemonsField: Array<String> =battle.start()

        battle.messages.procesarMensajes(battle)

        val equipo: List<Pokemon> = player.getTeam()
        cambiarFondosDeViews(this, equipo[0].getName().lowercase(), equipo[1].getName().lowercase(), equipo[2].getName().lowercase())

        val playerPokemon= player.currentPokemon.name.lowercase()
        val pokemon1Player = this.findViewById<View>(R.id.izquierda)
        val resourceIdPlayer = this.resources.getIdentifier(playerPokemon, "drawable", this.packageName)
        pokemon1Player.visibility = View.VISIBLE

        val iaPokemon= IA.currentPokemon.name.lowercase()
        val pokemon1Ia = this.findViewById<View>(R.id.derecha)
        val resourceIdIA = this.resources.getIdentifier(iaPokemon, "drawable", this.packageName)
        pokemon1Ia.visibility = View.VISIBLE

        pokemon1Ia.setBackgroundResource(resourceIdIA)
        pokemon1Player.setBackgroundResource(resourceIdPlayer)

        initAnimation(resourceIdPlayer,resourceIdIA)



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
        pokemonAttack1.setBackgroundColor(Color.parseColor(mapaColores.get(movesArray[0].type)))
        pokemonAttack1.text=movesArray[0].name
        pokemonAttack1.setOnClickListener {
            playTurn("Attack",0, battle, player, IA)
        }
        pokemonAttack1.setOnLongClickListener {
            // Acción cuando se realiza un long click en el botón
            mostrarInformacion(player, 0, IA.currentPokemon)
            true  // Indica que el evento fue manejado
        }
        val pokemonAttack2 = this.findViewById<Button>(R.id.attack2Button)
        pokemonAttack2.setBackgroundColor(Color.parseColor(mapaColores.get(movesArray[1].type)))
        pokemonAttack2.text=movesArray[1].name
        pokemonAttack2.setOnClickListener {
            playTurn("Attack",1,battle, player, IA)
        }
        pokemonAttack2.setOnLongClickListener {
            // Acción cuando se realiza un long click en el botón
            mostrarInformacion(player, 1, IA.currentPokemon)
            true  // Indica que el evento fue manejado
        }
        val pokemonAttack3 = this.findViewById<Button>(R.id.attack3Button)
        pokemonAttack3.setBackgroundColor(Color.parseColor(mapaColores.get(movesArray[2].type)))
        pokemonAttack3.text=movesArray[2].name
        pokemonAttack3.setOnClickListener {
            playTurn("Attack",2,battle, player, IA)
        }
        pokemonAttack3.setOnLongClickListener {
            // Acción cuando se realiza un long click en el botón
            mostrarInformacion(player, 2, IA.currentPokemon)
            true  // Indica que el evento fue manejado
        }
        val pokemonAttack4 = this.findViewById<Button>(R.id.attack4Button)
        pokemonAttack4.setBackgroundColor(Color.parseColor(mapaColores.get(movesArray[3].type)))
        pokemonAttack4.text=movesArray[3].name
        pokemonAttack4.setOnClickListener {
            playTurn("Attack",3,battle, player, IA)
        }
        pokemonAttack4.setOnLongClickListener {
            // Acción cuando se realiza un long click en el botón
            mostrarInformacion(player, 3, IA.currentPokemon)
            true  // Indica que el evento fue manejado
        }

        val pokemon1= this.findViewById<Button>(R.id.button1)
        val pokemon2= this.findViewById<Button>(R.id.button2)
        val pokemon3= this.findViewById<Button>(R.id.button3)
        pokemon1.setOnClickListener {
            if (player.currentPokemon.isDead()){
                player.setPokemonFromTeam(1)
                playTurnAfterDead(player,battle,0)
                activarAtaques()
                playTurn("Change",0,battle, player, IA)

            }else playTurn("Change",0,battle, player, IA)
        }
        pokemon2.setOnClickListener {
            if (player.currentPokemon.isDead()){
                player.setPokemonFromTeam(2)
                playTurnAfterDead(player,battle,1)
                activarAtaques()
                playTurn("Change",1,battle, player, IA)

            }else playTurn("Change",1,battle, player, IA)
        }
        pokemon3.setOnClickListener {
            if (player.currentPokemon.isDead()){
                player.setPokemonFromTeam(3)
                playTurnAfterDead(player,battle,2)
                activarAtaques()
                playTurn("Change",2,battle, player, IA)

            }else playTurn("Change",2,battle, player, IA)
        }



    }

    fun activarAtaques(){

        val attackButton1 = findViewById<Button>(R.id.attack1Button)
        val attackButton2 = findViewById<Button>(R.id.attack2Button)
        val attackButton3 = findViewById<Button>(R.id.attack3Button)
        val attackButton4 = findViewById<Button>(R.id.attack4Button)

        attackButton1.isEnabled = true
        attackButton2.isEnabled = true
        attackButton3.isEnabled = true
        attackButton4.isEnabled = true
    }

    fun mostrarInformacion(player : Player, move: Int, pokemon: Pokemon) {
        val movesArray: Array<Move> = player.currentPokemon.moves
        val builder = AlertDialog.Builder(this)
        val jsonStringTable: String = obtenerJsonString("typestable")
        val typeTable: Map<String, Map<String, Double>> = PokemonDataReader.createTypeTable(jsonStringTable)
        val objetoTipoAtacante = typeTable[movesArray[move].type]
        var eficaciaTotal=0.0
        if (pokemon.secondaryType!=null){
            val eficaciaAtaque = objetoTipoAtacante?.get(pokemon.primaryType) ?: 1.0
            val eficaciaAtaque1 = objetoTipoAtacante?.get(pokemon.secondaryType) ?: 1.0
            eficaciaTotal= eficaciaAtaque*eficaciaAtaque1
        }else{
            eficaciaTotal = objetoTipoAtacante?.get(pokemon.primaryType) ?: 1.0
        }

        var mensaje=""
        if (eficaciaTotal==4.0){
            mensaje="This attack is super effective (x4)."
        }else if(eficaciaTotal==2.0){
            mensaje="This attack is super effective (x2)."
        }else if(eficaciaTotal==1.0){
            mensaje="This attack is effective (x1)."
        }else if(eficaciaTotal==0.5){
            mensaje="This attack is not very effective (x0.5)."
        }else if(eficaciaTotal==0.25){
            mensaje="This attack is not very effective (x0.25)."
        }else {
            mensaje="This attack will not cause damage (x0)."
        }

        builder.setTitle("Movement Stat")
            .setMessage(
                """
            Type: ${movesArray[move].type}
            Power: ${movesArray[move].power}
            Acuraccy: ${movesArray[move].accuracy}  
            $mensaje
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
        pokemon1Ia.visibility = View.VISIBLE
        pokemon1Player.visibility = View.VISIBLE

        val selectedMove2 = Random().nextInt(IA.getCurrentPokemon().getMoves().size)
        var aCategory = player.getCurrentPokemon().getMoves()[movement].getCategory();

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
            battle.messages.procesarMensajes(battle)

        }else{
            aCategory = "None"
            //Cambiar
            val availablePokemon: MutableList<Pokemon> = ArrayList()

            for (pokemon in player.team) {
                availablePokemon.add(pokemon)
            }
            val chosenPokemon: Pokemon = availablePokemon.get(movement)

            if(!(chosenPokemon==player.currentPokemon)) {
                val oldPokemon = player.currentPokemon.name
                player.currentPokemon = chosenPokemon
                val resourceIdPlayer = this.resources.getIdentifier(player.currentPokemon.name.lowercase(), "drawable", this.packageName)
                battle.messages.add("Switching from " + oldPokemon + " to " + chosenPokemon.name)
                battle.chat.add("Switching from " + oldPokemon + " to " + chosenPokemon.name)

                battle.resolveTurn(
                    player.getCurrentPokemon(),
                    IA.getCurrentPokemon(),
                    -1,
                    selectedMove2
                )

                battle.messages.procesarMensajes(battle)
                switchAnimation(resourceIdPlayer)
                UpdateAttack(player)

            }

        }

        val speedPlayer = player.getCurrentPokemon().getStats().getSpeed()
        val speedIA = IA.getCurrentPokemon().getStats().getSpeed()

        if (speedIA > speedPlayer) {

            val firstAnimator = executePlayerCode(player, HealthPlayer, BarPlayer, battle, IA, BarIA,HealthIA,0)

            setAnimatorWithListener(firstAnimator)

            firstAnimator.doOnEnd {

                checkPlayerPokDead(player,battle)

                val secondAnimator = executeIACode(IA, HealthIA, BarIA, player, battle, BarPlayer,HealthPlayer,1)

                setAnimatorWithListener(secondAnimator)

                secondAnimator.doOnEnd {

                    checkIAPokDead(IA,battle,pokemon1Ia,player)

                    checkEndGame(battle)
                }

                secondAnimator.start()
                attkIAAnimation(aCategory)
            }

            firstAnimator.start()
            attkPlAnimation(aCategory)
        }
        else {
            val firstAnimator = executeIACode(IA, HealthIA, BarIA, player, battle,BarPlayer,HealthPlayer,0)

            setAnimatorWithListener(firstAnimator)

            firstAnimator.doOnEnd {

                checkIAPokDead(IA,battle,pokemon1Ia,player)

                checkEndGame(battle)

                val secondAnimator = executePlayerCode(player, HealthPlayer, BarPlayer, battle, IA,BarIA,HealthIA,1)

                setAnimatorWithListener(secondAnimator)

                secondAnimator.doOnEnd {
                    checkPlayerPokDead(player,battle)
                }
                secondAnimator.start()
                attkPlAnimation(aCategory)
            }

            firstAnimator.start()
            attkIAAnimation(aCategory)
        }
        return
    }

    fun playTurnAfterDead(player: Player,battle: Battle,selected:Int) {
        //Cambiar despues de morir
        val availablePokemon: MutableList<Pokemon> = ArrayList()

        for (pokemon in player.team) {
            availablePokemon.add(pokemon)
        }
        val chosenPokemon: Pokemon = availablePokemon.get(selected)
        if (!(chosenPokemon == player.currentPokemon)) {
            val oldPokemon = player.currentPokemon.name
            player.currentPokemon = chosenPokemon
            val resourceIdPlayer = this.resources.getIdentifier(
                player.currentPokemon.name.lowercase(),
                "drawable",
                this.packageName
            )
            battle.messages.add("Switching from " + oldPokemon + " to " + chosenPokemon.name)
            battle.chat.add("Switching from " + oldPokemon + " to " + chosenPokemon.name)

            battle.messages.procesarMensajes(battle)
            switchAnimation(resourceIdPlayer)
            UpdateAttack(player)
        }
    }

    fun executeIACode(IA: Player,HealthIA:TextView,BarIA:ProgressBar,player: Player,battle: Battle,BarPlayer: ProgressBar,HealthPlayer: TextView, orden:Int): ObjectAnimator {

        val percFinalIA: Double = ((IA.getCurrentPokemon().getStats().getHealthPoints()
            .toDouble()) / (IA.getCurrentPokemon().getMaxHealthPoints()) * 100)
        val recoveredIA: Double = ((IA.getCurrentPokemon().getStats().getAddedHP()
            .toDouble()) / (IA.getCurrentPokemon().getMaxHealthPoints()) * 100)
        val percInicialIA: Double = ((IA.getCurrentPokemon().getStats().getInitHP()
            .toDouble()) / (IA.getCurrentPokemon().getMaxHealthPoints()) * 100)
        HealthIA.text= (percFinalIA.toInt()).toString()+ "%"
        //BarIA.progress= percFinalIA.toInt()

        var animator = ObjectAnimator.ofInt(BarIA, "progress", BarIA.progress, percFinalIA.toInt())
        animator.duration = 1000
        if (percFinalIA.toInt() < 0) {
            HealthIA.text= (0).toString()+ "%"
            animator = ObjectAnimator.ofInt(BarIA, "progress", BarIA.progress, 0)
        }

        if (orden == 0) {
            val damageToIA = (percInicialIA.toInt() - (percFinalIA.toInt()-recoveredIA.toInt()))
            if ((percInicialIA.toInt() -  damageToIA) < 0) {
                HealthIA.text= (0).toString()+ "%"
                animator = ObjectAnimator.ofInt(BarIA, "progress", BarIA.progress, 0)
            }
            else {
                HealthIA.text= (percInicialIA.toInt() -  damageToIA).toString()+ "%"
                animator = ObjectAnimator.ofInt(BarIA, "progress", BarIA.progress, percInicialIA.toInt() -  damageToIA)
            }
        }

        if (player.getCurrentPokemon().getStats().getAddedHP().toDouble() > 0) {
            val percInicialPl: Double = ((player.getCurrentPokemon().getStats().getInitHP()
                .toDouble()) / (player.getCurrentPokemon().getMaxHealthPoints()) * 100)
            val recoveredPl: Double = ((player.getCurrentPokemon().getStats().getAddedHP()
                .toDouble()) / (player.getCurrentPokemon().getMaxHealthPoints()) * 100)
            val percFinalPl: Double = ((player.getCurrentPokemon().getStats().getHealthPoints()
                .toDouble()) / (player.getCurrentPokemon().getMaxHealthPoints())) * 100

            HealthPlayer.text= (percFinalPl.toInt()).toString()+ "%"
            animator = ObjectAnimator.ofInt(BarPlayer, "progress", BarPlayer.progress, percFinalPl.toInt())
            animator.duration = 1000

            if (orden == 0) {
                HealthPlayer.text= (percInicialPl.toInt() + recoveredPl.toInt()).toString()+ "%"
                animator = ObjectAnimator.ofInt(BarPlayer, "progress", BarPlayer.progress, percInicialPl.toInt() + recoveredPl.toInt())
            }
        }

        return animator
    }

    fun executePlayerCode(player: Player,HealthPlayer:TextView,BarPlayer:ProgressBar,battle: Battle,IA: Player,BarIA: ProgressBar,HealthIA: TextView, order:Int): ObjectAnimator {

        val percInicialPl: Double = ((player.getCurrentPokemon().getStats().getInitHP()
            .toDouble()) / (player.getCurrentPokemon().getMaxHealthPoints()) * 100)
        val recoveredPl: Double = ((player.getCurrentPokemon().getStats().getAddedHP()
            .toDouble()) / (player.getCurrentPokemon().getMaxHealthPoints()) * 100)
        val percFinalPl: Double = ((player.getCurrentPokemon().getStats().getHealthPoints()
            .toDouble()) / (player.getCurrentPokemon().getMaxHealthPoints())) * 100

        HealthPlayer.text= (percFinalPl.toInt()).toString()+ "%"
        //BarPlayer.progress= healthPercentage1.toInt()

        var animator2 = ObjectAnimator.ofInt(BarPlayer, "progress", BarPlayer.progress, percFinalPl.toInt())
        animator2.duration = 1000

        if (percFinalPl.toInt() < 0) {
            HealthPlayer.text= (0).toString()+ "%"
            animator2 = ObjectAnimator.ofInt(BarPlayer, "progress", BarPlayer.progress, 0)
        }

        if (order == 0) {
            val damageToPl = (percInicialPl.toInt() - (percFinalPl.toInt()-recoveredPl.toInt()))
            if((percInicialPl.toInt() -  damageToPl < 0)) {
                HealthPlayer.text= (0).toString()+ "%"
                animator2 = ObjectAnimator.ofInt(BarPlayer, "progress", BarPlayer.progress, 0)
            }
            else {
                HealthPlayer.text= (percInicialPl.toInt() -  damageToPl).toString()+ "%"
                animator2 = ObjectAnimator.ofInt(BarPlayer, "progress", BarPlayer.progress, percInicialPl.toInt() -  damageToPl)
            }
        }

        if (IA.getCurrentPokemon().getStats().getAddedHP().toDouble() > 0) {
            val percFinalIA: Double = ((IA.getCurrentPokemon().getStats().getHealthPoints()
                .toDouble()) / (IA.getCurrentPokemon().getMaxHealthPoints()) * 100)
            val recoveredIA: Double = ((IA.getCurrentPokemon().getStats().getAddedHP()
                .toDouble()) / (IA.getCurrentPokemon().getMaxHealthPoints()) * 100)
            val percInicialIA: Double = ((IA.getCurrentPokemon().getStats().getInitHP()
                .toDouble()) / (IA.getCurrentPokemon().getMaxHealthPoints()) * 100)

            HealthIA.text= (percFinalIA.toInt()).toString()+ "%"
            animator2 = ObjectAnimator.ofInt(BarIA, "progress", BarIA.progress, percFinalPl.toInt())
            animator2.duration = 1000

            if (order == 0) {
                HealthIA.text= (percInicialIA.toInt() + recoveredIA.toInt()).toString()+ "%"
                animator2 = ObjectAnimator.ofInt(BarIA, "progress", BarIA.progress, percInicialPl.toInt() + recoveredPl.toInt())
            }
        }

        return animator2
    }
    fun checkPlayerPokDead(player: Player,battle: Battle) {
        if (player.getCurrentPokemon().isDead()) {
            if (battle.isBattleOver == 0) {
                val attackButton1 = findViewById<Button>(R.id.attack1Button)
                val attackButton2 = findViewById<Button>(R.id.attack2Button)
                val attackButton3 = findViewById<Button>(R.id.attack3Button)
                val attackButton4 = findViewById<Button>(R.id.attack4Button)

                attackButton1.isEnabled = false
                attackButton2.isEnabled = false
                attackButton3.isEnabled = false
                attackButton4.isEnabled = false

                val pokemon= player.getCurrentPokemon();
                val listaPokemon= player.team
                println(player.getCurrentPokemon().name)
                val indice = (listaPokemon.indexOf(pokemon))+1

                val botonPokemon = findViewById<Button>(resources.getIdentifier("button$indice", "id", this.packageName))
                botonPokemon.isEnabled = false
                val resourceIdPlayer = this.resources.getIdentifier(
                    player.currentPokemon.name.lowercase(),
                    "drawable",
                    this.packageName
                )

                val pokemonView = this.findViewById<View>(R.id.izquierda)
                deadAnimation(resourceIdPlayer, pokemonView)
                UpdateAttack(player)
            }
        }
    }

    fun checkIAPokDead(IA: Player,battle: Battle,pokemon1Ia:View, player: Player) {
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
                deadAnimationIA(resourceIdIA,pokemon1Ia,player,IA,battle)
                val pokemonAI2= this.findViewById<View>(R.id.iapokemon2)
                val resourceIdFondo = resources.getIdentifier(IA.currentPokemon.name.lowercase(), "drawable", this.packageName)
                if (pokemonAI2.background.constantState?.equals(ContextCompat.getDrawable(this, R.drawable.pokeball)?.constantState) == true) {
                    deadAnimationIA(resourceIdFondo,pokemonAI2,player,IA,battle)
                } else {
                    val pokemonAI3= this.findViewById<View>(R.id.iapokemon3)
                    deadAnimationIA(resourceIdFondo,pokemonAI3,player,IA,battle)
                }
            }
        }
    }

    fun checkEndGame(battle: Battle) {
        //Ver si se acabó
        if (battle.isBattleOver == 1) {
            //Ganas tu

            val intent = Intent(this@BattleActivity, FinalActivity::class.java)
            intent.putExtra("string", "You WIN")
            startActivity(intent)

        } else if (battle.isBattleOver == 2) {
            //Gana la Ia

            val intent = Intent(this@BattleActivity, FinalActivity::class.java)
            intent.putExtra("string", "You LOST")
            startActivity(intent)
        }
        System.out.println(battle.isBattleOver.toString())
    }

    private fun UpdateAttack(player : Player){
        val movesArray: Array<Move> = player.currentPokemon.moves
        val pokemonAttack1 = this.findViewById<Button>(R.id.attack1Button)
        pokemonAttack1.setBackgroundColor(Color.parseColor(mapaColores.get(movesArray[0].type)))
        pokemonAttack1.text=movesArray[0].name


        val pokemonAttack2 = this.findViewById<Button>(R.id.attack2Button)
        pokemonAttack2.setBackgroundColor(Color.parseColor(mapaColores.get(movesArray[1].type)))
        pokemonAttack2.text=movesArray[1].name

        val pokemonAttack3 = this.findViewById<Button>(R.id.attack3Button)
        pokemonAttack3.setBackgroundColor(Color.parseColor(mapaColores.get(movesArray[2].type)))
        pokemonAttack3.text=movesArray[2].name

        val pokemonAttack4 = this.findViewById<Button>(R.id.attack4Button)
        pokemonAttack4.setBackgroundColor(Color.parseColor(mapaColores.get(movesArray[3].type)))
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

    private fun ArrayList<String> .procesarMensajes(battle: Battle) {
        printChat(battle.messages)
        battle.messages.add("Choose an option...")
        this.forEachIndexed { index, mensaje ->
            Handler().postDelayed({
                printLogMessage(mensaje)

                // Si es el último mensaje, limpia la lista de mensajes
                if (index == this.size - 1) {
                    val blockingView = layoutBattle.findViewById<View>(R.id.blockingView)
                    blockingView.visibility = View.GONE
                    battle.messages.clear()
                    battle.setMessages(battle.messages)
                }
            }, index * 2000L) // Ajusta el retardo para cada mensaje
        }
    }

    fun printLogMessage(message: String) {

        logScrollView.fullScroll(ScrollView.FOCUS_UP)

        logTextView.text = message

        val animator = ObjectAnimator.ofInt(logScrollView, "scrollY", logScrollView.bottom)
        animator.duration = 2000
        animator.start()

    }

    private fun printChat(messages: ArrayList<String>) {

        val currentContent = chatTextView.text.toString()
        val messagesText = messages.joinToString(separator = "<br>")
        var turnoText = "<br><br><big><b>Turn: $turno</b></big>"

        if (turno == 0) {
            turnoText = "<big><b>The Battle Starts!</b></big>"
        }

        val updatedContent = "$turnoText<br>$messagesText"

        chatTextView.append(Html.fromHtml(updatedContent, Html.FROM_HTML_MODE_LEGACY))

        chatScrollView.post {
            chatScrollView.fullScroll(ScrollView.FOCUS_UP)
        }

        turno++
    }


    private fun switchAnimation(resourceIdPlayer:Int) {
        //IDA
        val pokemonView = this.findViewById<View>(R.id.izquierda)

        val anim1 = ObjectAnimator.ofFloat(pokemonView, "translationX", -250f).apply {
            duration = 1000
        }

        setAnimatorWithListener(anim1)

        anim1.doOnEnd {
            pokemonView.setBackgroundResource(resourceIdPlayer)
            val anim2 = ObjectAnimator.ofFloat(pokemonView, "translationX", 0f).apply {
                duration = 1000
            }
            setAnimatorWithListener(anim2)
            anim2.start()
        }

        anim1.start()
    }

    private fun deadAnimation(resourceIdPlayer:Int,pokemonView:View) {

        val fadeOutAnim = ObjectAnimator.ofFloat(pokemonView, "alpha", 1f, 0f).apply {
            duration = 2000 // Duración de la animación en milisegundos
        }

        setAnimatorWithListener(fadeOutAnim)

        fadeOutAnim.doOnEnd {
            pokemonView.setBackgroundResource(resourceIdPlayer)
            pokemonView.visibility = View.INVISIBLE
            val fadeOutAnim2 = ObjectAnimator.ofFloat(pokemonView, "alpha", 0f, 1f).apply {
                duration = 2000 // Duración de la animación en milisegundos
            }
            setAnimatorWithListener(fadeOutAnim2)
            fadeOutAnim2.start()
        }

        fadeOutAnim.start()

    }

    private fun deadAnimationIA(resourceIdPlayer:Int,pokemonView:View,player: Player,IA: Player,battle: Battle) {

        val fadeOutAnim = ObjectAnimator.ofFloat(pokemonView, "alpha", 1f, 0f).apply {
            duration = 2000 // Duración de la animación en milisegundos
        }

        setAnimatorWithListener(fadeOutAnim)

        fadeOutAnim.doOnEnd {
            pokemonView.setBackgroundResource(resourceIdPlayer)

            val availablePokemon: MutableList<Pokemon> = ArrayList()

            for (pokemon in player.team) {
                availablePokemon.add(pokemon)
            }

            val indice = availablePokemon.indexOf(player.currentPokemon)

            playTurn("Change",indice, battle, player, IA)

            val fadeOutAnim2 = ObjectAnimator.ofFloat(pokemonView, "alpha", 0f, 1f).apply {
                duration = 2000 // Duración de la animación en milisegundos
            }
            setAnimatorWithListener(fadeOutAnim2)
            fadeOutAnim2.start()
        }

        fadeOutAnim.start()

    }

    private fun initAnimation(resourceIdPlayer:Int,resourceIdIA:Int) {
        //IDA
        val pokemonView = this.findViewById<View>(R.id.izquierda)
        val iaView = this.findViewById<View>(R.id.derecha)

        pokemonView.translationX = -250f
        iaView.translationX = 250f

        val anim1 = ObjectAnimator.ofFloat(pokemonView, "translationX", 0f).apply {
            duration = 1500
        }

        setAnimatorWithListener(anim1)

        anim1.doOnEnd {
            val anim2 = ObjectAnimator.ofFloat(iaView, "translationX", 0f).apply {
                duration = 1500
            }
            setAnimatorWithListener(anim2)
            anim2.start()
        }

        anim1.start()
    }

    private fun switchLayouts(container:FrameLayout) {
        if (layoutBattle.visibility == View.VISIBLE) {
            container.removeView(layoutBattle)
            container.addView(layoutChat)
            layoutBattle.visibility = View.GONE
            layoutChat.visibility = View.VISIBLE
        } else {
            container.removeView(layoutChat)
            container.addView(layoutBattle)
            layoutBattle.visibility = View.VISIBLE
            layoutChat.visibility = View.GONE
        }
    }

    fun setAnimatorWithListener(animator: ObjectAnimator) {

        val blockingView = layoutBattle.findViewById<View>(R.id.blockingView)

        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                // La animación ha comenzado
                blockingView.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator) {
                // La animación ha finalizado
            }

            override fun onAnimationCancel(animation: Animator) {
                // La animación ha sido cancelada
            }

            override fun onAnimationRepeat(animation: Animator) {
                // La animación se repite
            }
        })
    }

    fun attkIAAnimation(category:String) {

        if (!category.equals("None")) {
            var yourImageView = layoutBattle.findViewById<ImageView>(R.id.phy_attk_IA)
            val targetView = layoutBattle.findViewById<View>(R.id.derecha)

            val targetLocation = IntArray(2)
            targetView.getLocationOnScreen(targetLocation)

            if (category.equals("Special")) {
                yourImageView = layoutBattle.findViewById<ImageView>(R.id.phy_attk_IA)
            }

            yourImageView.x = targetLocation[0].toFloat()
            yourImageView.y = targetLocation[1].toFloat()

            val alphaAnimator = ObjectAnimator.ofFloat(yourImageView, View.ALPHA, 1f, 0f)
            alphaAnimator.duration = 500 // Duración de la animación en milisegundos (ajusta según sea necesario)
            alphaAnimator.repeatMode = ObjectAnimator.REVERSE // Hace que la animación se revierta
            alphaAnimator.repeatCount = 2

            yourImageView.visibility = View.VISIBLE

            alphaAnimator.doOnEnd {
                alphaAnimator.cancel()
                yourImageView.visibility = View.INVISIBLE
            }

            alphaAnimator.start()
        }
    }

    fun attkPlAnimation(category:String) {

        if (!category.equals("None")) {
            var yourImageView = layoutBattle.findViewById<ImageView>(R.id.phy_attk_IA)
            val targetView = layoutBattle.findViewById<View>(R.id.izquierda)

            val targetLocation = IntArray(2)
            targetView.getLocationOnScreen(targetLocation)

            if (category.equals("Special")) {
                yourImageView = layoutBattle.findViewById<ImageView>(R.id.phy_attk_IA)
            }

            yourImageView.x = targetLocation[0].toFloat()
            yourImageView.y = targetLocation[1].toFloat()

            val alphaAnimator = ObjectAnimator.ofFloat(yourImageView, View.ALPHA, 1f, 0f)
            alphaAnimator.duration = 500 // Duración de la animación en milisegundos (ajusta según sea necesario)
            alphaAnimator.repeatMode = ObjectAnimator.REVERSE // Hace que la animación se revierta
            alphaAnimator.repeatCount = 2

            yourImageView.visibility = View.VISIBLE

            alphaAnimator.doOnEnd {
                alphaAnimator.cancel()
                yourImageView.visibility = View.INVISIBLE
            }

            alphaAnimator.start()
        }
    }

}



