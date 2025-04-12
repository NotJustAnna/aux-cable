package net.notjustanna.handlers;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;
import net.notjustanna.exceptions.ApplicationException;
import net.notjustanna.models.http.exceptions.ApplicationErrorModel;

@Produces
@Singleton
@Requires(classes = {ApplicationException.class, ExceptionHandler.class})
public class ApplicationExceptionHandler implements ExceptionHandler<ApplicationException, HttpResponse<?>> {

    @Override
    public HttpResponse<?> handle(HttpRequest request, ApplicationException exception) {
        ApplicationErrorModel error = new ApplicationErrorModel(exception.getType().name());
        return HttpResponse.status(exception.getType().getStatus()).body(error);
    }
}
