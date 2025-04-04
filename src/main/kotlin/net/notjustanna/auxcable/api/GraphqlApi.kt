package net.notjustanna.auxcable.api

import graphql.GraphQL
import graphql.execution.SubscriptionExecutionStrategy
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeRuntimeWiring
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.FlowableOnSubscribe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent
import net.dv8tion.jda.api.events.session.SessionRecreateEvent
import net.dv8tion.jda.api.events.session.ShutdownEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.notjustanna.auxcable.state.Message
import net.notjustanna.auxcable.state.State


object GraphqlApi {
    fun init(initialState: State): GraphQL {
        val typeDefinitionRegistry = SchemaParser().parse(
            GraphQL::class.java.getResourceAsStream("/schema.graphqls")!!.bufferedReader(Charsets.UTF_8)
        )

        val runtimeWiring: RuntimeWiring = RuntimeWiring.newRuntimeWiring()
            .type(buildQueries(initialState))
            .type(buildMutations(initialState))
            .type(buildSubscriptions(initialState))
            .type(buildState())
            .type(buildMessage())
            .build()

        val schemaGenerator = SchemaGenerator()
        val graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring)

        val build = GraphQL
            .newGraphQL(graphQLSchema)
            .subscriptionExecutionStrategy(SubscriptionExecutionStrategy())
            .build()

        return build
    }

    private fun buildState(): TypeRuntimeWiring {
        return TypeRuntimeWiring.newTypeWiring("State") { wiring ->
            wiring.dataFetcher("type") {
                it.getSource<State>().type
            }

        }
    }

    private fun buildMessage(): TypeRuntimeWiring {
        return TypeRuntimeWiring.newTypeWiring("Message") { wiring ->
            wiring.dataFetcher("content") {
                it.getSource<Message>().content
            }
            wiring.dataFetcher("type") {
                it.getSource<Message>().type
            }
        }
    }

    private fun buildQueries(initialState: State): TypeRuntimeWiring {
        return TypeRuntimeWiring.newTypeWiring("Query") { wiring ->
            wiring.dataFetcher("state") {
                initialState.stateStream.firstOrError().blockingGet()
            }
        }
    }

    private fun buildMutations(initialState: State): TypeRuntimeWiring {
        return TypeRuntimeWiring.newTypeWiring("Mutation") { wiring ->
            wiring.dataFetcher("login") {
                val token = it.getArgument<String>("token")
                val remember = it.getArgument<Boolean>("remember")
                initialState.stateStream.firstOrError().blockingGet().login(token, remember)
            }

            wiring.dataFetcher("logout") {
                initialState.stateStream.firstOrError().blockingGet().logout()
            }

            wiring.dataFetcher("connect") {
                val channelId = it.getArgument<String>("channelId")
                initialState.stateStream.firstOrError().blockingGet().connect(channelId)
            }

            wiring.dataFetcher("disconnect") {
                initialState.stateStream.firstOrError().blockingGet().disconnect()
            }

            wiring.dataFetcher("stream") {
                val enabled = it.getArgument<Boolean>("enabled")
                initialState.stateStream.firstOrError().blockingGet().stream(enabled)
            }
        }
    }

    private fun buildSubscriptions(initialState: State): TypeRuntimeWiring {
        return TypeRuntimeWiring.newTypeWiring("Subscription") { wiring ->
            wiring.dataFetcher("currentState") {
                initialState.stateStream.toFlowable(BackpressureStrategy.BUFFER)
            }
            wiring.dataFetcher("newMessage") {
                initialState.stateStream.firstOrError().blockingGet().messageStream.toFlowable(BackpressureStrategy.BUFFER)
            }
            wiring.dataFetcher("guilds") {
                val jda = initialState.stateStream.firstOrError().blockingGet().jda ?: throw IllegalStateException("Not logged in")
                val source = FlowableOnSubscribe<List<Guild>> { emitter ->
                    val listener = EventListener {
                        when (it) {
                            is ShutdownEvent -> {
                                emitter.onComplete()
                            }
                            is SessionRecreateEvent,
                            is ChannelCreateEvent,
                            is ChannelDeleteEvent,
                            is GuildJoinEvent,
                            is GuildLeaveEvent -> {
                                emitter.onNext(jda.guilds)
                            }
                        }
                    }
                    jda.addEventListener(listener)
                    emitter.setCancellable { jda.removeEventListener(listener) }
                    emitter.onNext(jda.guilds)
                }
                Flowable.create(source, BackpressureStrategy.BUFFER)
            }
        }
    }
}