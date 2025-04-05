package net.notjustanna.auxcable.models

import net.notjustanna.auxcable.state.StateType

data class StateModel(val type: StateType, val account: AccountModel?)
