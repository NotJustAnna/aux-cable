package net.notjustanna.auxcable.api

import com.linecorp.armeria.server.annotation.Delete
import com.linecorp.armeria.server.annotation.Get
import com.linecorp.armeria.server.annotation.Param
import com.linecorp.armeria.server.annotation.ProducesJson
import net.notjustanna.auxcable.models.AccountModel
import net.notjustanna.auxcable.models.Model
import net.notjustanna.auxcable.state.util.RememberMe

class AccountService {
    @ProducesJson
    @Get("/")
    fun all(): List<AccountModel> {
        return RememberMe.all().map(Model::convert)
    }

    @ProducesJson
    @Get("/:id")
    fun get(@Param id: String): AccountModel {
        return RememberMe.getById(id)?.let(Model::convert) ?: throw IllegalArgumentException("Account not found")
    }

    @ProducesJson
    @Delete("/:id")
    fun delete(@Param id: String): Boolean {
        return RememberMe.deleteById(id)
    }
}