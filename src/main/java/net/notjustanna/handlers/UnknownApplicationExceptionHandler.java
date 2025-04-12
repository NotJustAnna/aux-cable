package net.notjustanna.handlers;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;
import net.notjustanna.exceptions.UnknownApplicationException;
import net.notjustanna.models.http.exceptions.UnknownApplicationErrorModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Produces
@Singleton
@Requires(classes = {UnknownApplicationException.class, ExceptionHandler.class})
public class UnknownApplicationExceptionHandler implements ExceptionHandler<UnknownApplicationException, HttpResponse<?>> {
    Logger log = LoggerFactory.getLogger(UnknownApplicationExceptionHandler.class);

    @Override
    public HttpResponse<?> handle(HttpRequest request, UnknownApplicationException exception) {
        log.error(exception.getMessage(), exception);

        UnknownApplicationErrorModel error = new UnknownApplicationErrorModel(exception.getType().name(), exception.getAt());
        return HttpResponse.status(exception.getType().getStatus()).body(error);
    }
}