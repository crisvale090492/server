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
        usuarioRepository.findAll().forEach { user1 ->
            if (user1.token == token1){
                usuarioRepository.findAll().forEach { user2 ->
                    if (user2.token == token2){
                        listaPokemon.listaPokemon.forEach { pokemon1 ->
                            if (pokemonId1.toLong() == pokemon1.id) {
                                user1.pokemonsCapturados.forEach { pokeCapturado1 ->
                                    if (pokemonId1.toInt() == pokeCapturado1){
                                        listaPokemon.listaPokemon.forEach { pokemon2 ->
                                            if (pokemonId2.toLong() == pokemon2.id) {
                                                user2.pokemonsCapturados.forEach { pokeCapturado2 ->
                                                    if (pokemonId2.toInt() == pokeCapturado2) {
                                                        user2.pokemonsCapturados.add(pokeCapturado1)
                                                        user1.pokemonsCapturados.add(pokeCapturado2)
                                                        user2.pokemonsCapturados.remove(pokeCapturado2)
                                                        user1.pokemonsCapturados.remove(pokeCapturado1)
                                                }
                                        }
                                                return "El usuario 2 no es dueño de este pokemon"
                                }
                            }
                                        return "El pokemon 2 no existe"
                        }

                                }
                                return "El usuario 1 no es dueño de este pokemon"

                                }
                            else
                                return "El pokemon 1 no existe"
                        }
                        listaPokemon.listaPokemon.forEach { pokemon2 ->
                            if (pokemonId2.toLong() == pokemon2.id) {
                            }
                        }
                        return "Los pokemon no existen"



                        }
                }
                return "El token del usuario 2 no existe"

                }
            else
                return "El token del usuario 1 no existe"
            }
        usuarioRepository.findAll().forEach { user2 ->
            if (user2.token == token2) {
            }
        }
        return "Los token no existen"
    }
}