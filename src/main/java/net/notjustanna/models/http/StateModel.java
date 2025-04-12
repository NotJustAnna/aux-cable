package net.notjustanna.models.http;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.annotation.ReflectiveAccess;
import io.micronaut.serde.annotation.Serdeable;
import net.dv8tion.jda.api.JDA;
import net.notjustanna.models.common.StateType;
import net.notjustanna.state.ApplicationState;

@Serdeable
@ReflectiveAccess
public record StateModel(@NonNull StateType type, @Nullable AccountModel account) {
    public static StateModel of(ApplicationState state) {
        JDA jda = state.getJDA();

        if (jda == null) {
            return new StateModel(state.getType(), null);
        }

        return new StateModel(state.getType(), AccountModel.ofSelfUser(state.getJDA().getSelfUser()));
    }
}
