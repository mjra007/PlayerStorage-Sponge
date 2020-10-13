package com.github.mjra007.sponge;

import com.github.mjra007.sponge.listeners.PlayerLogin;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import java.nio.file.Path;

@Plugin(
        id = "storage",
        name = "storage",
        url = "http://enchantedoasis.uk/",
        authors = {
                "mjra007"
        }
)

public class Storage {

    private static Storage instance;

    private UsersDataBank usersDataBank;

    @Inject
    public org.slf4j.Logger Logger;

    public Gson GSONInstance;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    @Listener
    @SuppressWarnings("unchecked")
    public void onServerStart(GameInitializationEvent event) {
        Logger.info("Enabling Storage plugin...");
        GSONInstance = new GsonBuilder().create();

        instance = this;
        Sponge.getEventManager().registerListeners(this, new PlayerLogin());

        Logger.info("Storage initialised successfully!");
    }


    public UsersDataBank getUsersDataBank() {
        return usersDataBank;
    }

    public Path getDirectoryPath(){
        return this.configDir;
    }

    public static Storage getInstance(){
        return instance;
    }

}
