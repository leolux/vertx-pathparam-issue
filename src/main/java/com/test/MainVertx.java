package com.test;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

/**
 *
 */
public class MainVertx {

    public static void main(String[] args) throws InterruptedException {
        Vertx vertx = Vertx.vertx();
        startup(vertx);
    }

    public static void startup(Vertx vertx) throws InterruptedException {
        DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setInstances(1);
        vertx.deployVerticle(Webserver.class.getName(), deploymentOptions, res -> {
            if (res.succeeded()) {
                System.out.println("Verticle deployed");
            } else {
                System.out.println("Verticle deployment failed");
            }
        });

    }


}
