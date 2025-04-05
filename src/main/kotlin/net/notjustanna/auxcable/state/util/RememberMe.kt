package net.notjustanna.auxcable.state.util

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.linecorp.armeria.internal.common.JacksonUtil
import java.io.File

data class Account(
    val id: String,
    val name: String,
    val imageUrl: String,
    val token: String
)

object RememberMe {
    private val mapper = JacksonUtil.newDefaultObjectMapper()
    private val accounts = mutableListOf<Account>()

    init {
        this.read()
    }

    fun all(): List<Account> {
        return accounts.toList()
    }

    fun getById(id: String): Account? {
        return accounts.find { it.id == id }
    }

    fun save(token: String, id: String, name: String, imageUrl: String) {
        val account = Account(id, name, imageUrl, token)
        accounts.removeIf { it.id == id }
        accounts.add(account)
        accounts.sortBy { it.id }
        this.write()
    }

    fun update(id: String, name: String, imageUrl: String) {
        val account = getById(id) ?: return
        if (account.name == name && account.imageUrl == imageUrl) {
            return
        }
        accounts.remove(account)
        accounts.add(account.copy(name = name, imageUrl = imageUrl))
        accounts.sortBy { it.id }
        this.write()
    }

    private fun read() {
        val file = File("remember_me.json")
        if (!file.exists()) {
            return
        }

        accounts.clear()
        accounts.addAll(mapper.readValue(file, object : TypeReference<List<Account>>() {}))
    }

    private fun write() {
        val file = File("./remember_me.json")
        file.absoluteFile.parentFile.mkdirs()
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, accounts)
    }

    fun deleteById(id: String): Boolean {
        val account = getById(id) ?: return false
        accounts.remove(account)
        this.write()
        return true
    }
}