package com.github.mjra007.sponge.listeners;

import com.github.mjra007.sponge.Storage;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.scheduler.Task;

import java.util.UUID;

public class PlayerLogin {

    @Listener
    public void loadAccount(ClientConnectionEvent.Join playerJoinEvent, @First Player player) {
        Task.builder()
                .async()
                .execute(() -> onPlayerLogin(player.getUniqueId()))
                .submit(Storage.getInstance());
    }

    public void onPlayerLogin(UUID uuid) {
        //Create user if file doesnt exist
        if (!Storage.getInstance().getUsersDataBank().getUserData(uuid).isPresent()) {
            Storage.getInstance().getUsersDataBank().createNewUser(uuid);
        }

        Storage.getInstance().getUsersDataBank().getUserData(uuid);
    }
}

