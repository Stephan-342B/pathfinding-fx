package com.mahefa.pathfindingfx;

/**
 * Entry point that does <strong>not</strong> extend {@link javafx.application.Application}.
 * <p>
 * When the JVM's main class extends {@code Application} and the JavaFX modules are on the
 * classpath (unnamed module) rather than the module path, the launcher aborts with
 * "JavaFX runtime components are missing, and are required to run this application".
 * Delegating from a plain class sidesteps that guard, so the app launches identically
 * whether run via the Spring Boot plugin (forked) or as a packaged fat jar.
 */
public class Launcher {

    public static void main(String[] args) {
        PathFindingFx.main(args);
    }
}
