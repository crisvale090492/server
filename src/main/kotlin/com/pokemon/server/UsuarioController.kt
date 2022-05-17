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

    @PostMapping("intercambiarPokemon/{tokenUsuario1}/{tokenUsuario2}/{pokemonId1}/{pokemonId2}")
    fun intercambiarPokemon(
        @PathVariable token1: String,
        @PathVariable token2: String,
        @PathVariable pokemonId1: String,
        @PathVariable pokemonId2: String): Any {
        var pokemon1: Pokemon
        var pokemon2: Pokemon
        usuarioRepository.findAll().forEach { user ->
            if (user.token == token1)
                if (user.token == token2)
            }

                usuarioRepository.findAll().forEach { user ->
                    if (user.token == token2) {
                listaPokemon.listaPokemon.forEach { pokemon ->
                    if (pokemonId1.toLong() == pokemon.id) {
                        pokemon1 = pokemon

                    }
                }
                return "El id del pokemon no existe"
            }
        }
        return "Token1 no encontrado"


    }
}