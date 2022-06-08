package com.pokemon.server

import Pokemon
import org.springframework.web.bind.annotation.*

@RestController
class UsuarioController(private val usuarioRepository: UsuarioRepository) {

    // Podemos hacer la request desde el navegador.
    @GetMapping("crearUsuario/{nombre}/{pass}")
    @Synchronized
    fun requestCrearUsuario(@PathVariable nombre: String, @PathVariable pass: String): Any {
        val userOptinal = usuarioRepository.findById(nombre)

        return if (userOptinal.isPresent) {
            val user = userOptinal.get()
            if (user.pass == pass) {
                user
            } else {
                "Contraseña incorrecta"
            }
        } else {
            val user = Usuario(nombre, pass)
            usuarioRepository.save(user)
            user
        }
    }

    /*
    curl --request POST  --header "Content-type:application/json" --data "{\"nombre\":\"u2\", \"pass\":\"p2\"}" localhost:8084/crearUsuario {"nombre":"u2","pass":"p2","token":"u2p2"}
     */
    @PostMapping("crearUsuario")
    @Synchronized
    fun requestCrearUsuarioJson(@RequestBody usuario: Usuario): Any {
        val userOptinal = usuarioRepository.findById(usuario.nombre)

        return if (userOptinal.isPresent) {
            val user = userOptinal.get()
            if (user.pass == usuario.pass) {
                user
            } else {
                "Contraseña incorrecta"
            }
        } else {
            usuarioRepository.save(usuario)
            usuario
        }
    }


    @PostMapping("pokemonFavorito/{token}/{pokemonId}")
    fun guardarPokemonFavorito(@PathVariable token: String, @PathVariable pokemonId: Int): String {
        println(token)
        usuarioRepository.findAll().forEach { user ->
            if (user.token == token) {
                user.pokemonFavoritoId = pokemonId
                usuarioRepository.save(user)
                return "El usuario ${user.nombre} tiene un nuevo Pokémon favorito"
            }
        }
        return "Token no encontrado"
    }

    @GetMapping("pokemonFavorito/{token}")
    fun obtenerPokemonFavorito(@PathVariable token: String): Any {
        usuarioRepository.findAll().forEach { user ->
            if (user.token == token) {
                user.pokemonFavoritoId?.let { pokemonFavoritoIdNotNull ->
                    listaPokemon.listaPokemon.forEach { pokemon ->
                        if (pokemonFavoritoIdNotNull.toLong() == pokemon.id) {
                            return pokemon
                        }
                    }
                } ?: run {
                    return "El usuario no tiene pokemon favorito"
                }
            }
        }
        return "Token no encontrado"
    }

    @PostMapping("pokemonCapturado/{token}/{pokemonId}")
    fun guardarPokemonCapturado(@PathVariable token: String, @PathVariable pokemonId: Int): String {
        usuarioRepository.findAll().forEach { user ->
            if (user.token == token) {
                listaPokemon.listaPokemon.forEach { pokemon ->
                    if (pokemonId.toLong() == pokemon.id) {
                        user.pokemonsCapturados.add(pokemonId)
                        usuarioRepository.save(user)
                        return "Pokemon guardado"
                    }
                }
                return "El id del pokemon no existe"
            }
        }
        return "Token no encontrado"
    }

    @GetMapping("visualizarCapturado/{token}")
    fun visualizarPokemonCapturado(@PathVariable token: String): Any {
        var pokemonsCapturados = mutableListOf<Pokemon>()
        usuarioRepository.findAll().forEach { user ->
            if (user.token == token) {
                user.pokemonsCapturados.forEach { idPokemon ->
                    listaPokemon.listaPokemon.forEach { pokemon ->
                        if (idPokemon.toLong() == (pokemon.id)) {
                            pokemonsCapturados.add(pokemon)
                        }
                    }
                }
                return pokemonsCapturados
            }
        }
        return "Token no encontrado"
    }

    @PostMapping("intercambiarPokemon/{token1}/{token2}/{pokemonId1}/{pokemonId2}")
    fun intercambiarPokemon(
        @PathVariable token1: String,
        @PathVariable token2: String,
        @PathVariable pokemonId1: String,
        @PathVariable pokemonId2: String): Any {
        var pokemon1 : Pokemon? = null
        var pokemon2 : Pokemon? = null
        var user1 : Usuario? = null
        var user2 : Usuario? = null
        var captura1 = false
        var captura2 = false

        usuarioRepository.findAll().forEach {currentUser ->
            if (currentUser.token == token1) {
                user1 = currentUser
                return@forEach
            }
        }
        usuarioRepository.findAll().forEach {currentUser2 ->
            if (currentUser2.token == token2) {
                user2 = currentUser2
                return@forEach
            }
        }
        if (user1 == null && user2 == null ) {
            return "Los token no existen"
        }
        if (user1 == null)
            return "El token del usuario 1 no existe"
        if (user2 == null)
            return "El token del usuario 2 no existe"

        listaPokemon.listaPokemon.forEach {poke1 ->
            if (pokemonId1.toLong() == poke1.id) {
                pokemon1 = poke1
                return@forEach
            }
        }
        listaPokemon.listaPokemon.forEach {poke2 ->
            if (pokemonId2.toLong() == poke2.id) {
                pokemon2 = poke2
                return@forEach
            }
        }
        if (pokemon1 == null && pokemon2 == null ) {
            return "Los pokemons no existen"
        }
        if (pokemon1 == null)
            return "El pokemon1 no existe"
        if (pokemon2 == null)
            return "El pokemon2 no existe"

        user1?.pokemonsCapturados?.forEach {pokeCapturado1 ->
            if (pokemon1?.id == pokeCapturado1.toLong()){
                captura1 = true
                return@forEach
            }
        }
        user2?.pokemonsCapturados?.forEach {pokeCapturado2 ->
            if (pokemon2?.id == pokeCapturado2.toLong()){
                captura2 = true
                return@forEach
            }
        }
        if (!captura1 && !captura2) {
            return "Los usuarios no tienen a los pokemons"
        }
        if (!captura1)
            return "El usuario1 no tiene al pokemon"
        if (!captura2)
            return "El usuario2 no tiene al pokemon"
        pokemon2?.id?.let {
            user1?.pokemonsCapturados?.add(it.toInt())
            user2?.pokemonsCapturados?.remove(it.toInt())

        }
        pokemon1?.id?.let {
            user2?.pokemonsCapturados?.add(it.toInt())
            user1?.pokemonsCapturados?.remove(it.toInt())
        }
        user1?.let {
            usuarioRepository.save(it)
        }
        user2?.let {
            usuarioRepository.save(it)
        }
        return "Cambio realizado correctamente"

    }
}