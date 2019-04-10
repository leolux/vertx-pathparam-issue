package com.test;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

import java.net.HttpURLConnection;

public class Webserver extends AbstractVerticle {

    private Boolean staticHandlerAdded = false;

    public void start() {
        Router router = Router.router(vertx);
        String pathWithBug = "/sub/:appName/*";
        String pathWithoutBug = "/sub/myApp/*";

        //Here you can enable and disable the bug
        Boolean enableBug = true;

        Route myAppRoute;
        if (enableBug) {
            myAppRoute = router.route(pathWithBug);
        } else {
            myAppRoute = router.route(pathWithoutBug);
        }

        //js files
        myAppRoute.method(HttpMethod.GET).handler(rc -> {
            String requestPath = rc.request().path();
            if (requestPath.contains(".js")) {
                //... send js file
            } else {
                if (!staticHandlerAdded) {
                    //add the static handler
                    StaticHandler staticHandler = StaticHandler.create();
                    staticHandler.setWebRoot("webroot/sub/myApp");
                    myAppRoute.handler(staticHandler);
                    staticHandlerAdded = true;
                    System.out.println("Static handler added");

                    myAppRoute.handler(rc2 -> {
                        System.out.println("Catch all in main route");
                        rc2.response().setStatusCode(HttpURLConnection.HTTP_NOT_FOUND);
                        rc2.response().end();
                    });
                    vertx.setTimer(1000, fire -> {
                        rc.next();
                    });
                } else {
                    System.out.println("Now go to the static handler");
                    rc.next();
                }
            }
        });

        HttpServerOptions httpServerOptions = new HttpServerOptions();
        HttpServer server = vertx.createHttpServer(httpServerOptions);
        server.requestHandler(router::accept).listen(8111);

        System.out.println("Now open http://localhost:8111/sub/myApp/assets/bug-fixed.png");
    }
}
