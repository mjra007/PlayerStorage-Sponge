package com.github.mjra007.sponge;

import com.github.mjra007.DynamicDataStorage.DataKey;
import com.github.mjra007.DynamicDataStorage.DynamicDataStorageMap;
import com.google.common.cache.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class UsersDataBank {

    LoadingCache<UUID, DynamicDataStorageMap> cache;

    public UsersDataBank(){
        CacheLoader<UUID, DynamicDataStorageMap>  loader  = new CacheLoader<UUID, DynamicDataStorageMap>() {

            @Override
            public DynamicDataStorageMap load(UUID key) throws IOException {
                Path userFilePath = Paths.get(Storage.getInstance().getDirectoryPath().toString(), key.toString());
                File file = new File(userFilePath.toUri());
                if(file.exists()){
                    Storage.getInstance().Logger.info("Load user profile: "+key);
                    return DynamicDataStorageMap.read(Paths.get(Storage.getInstance().getDirectoryPath().toString(), key.toString()));
                }
                return null;
            }

        };

        RemovalListener<UUID, DynamicDataStorageMap> removalListener = removal -> {
            DynamicDataStorageMap value = removal.getValue();
            try {
                DynamicDataStorageMap.write(value, Paths.get(Storage.getInstance().getDirectoryPath().toString(), removal.getKey().toString()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            }
            Storage.getInstance().Logger.info("Unloading profile UUID: "+removal.getKey().toString());
        };

        cache = CacheBuilder.newBuilder().maximumSize(40)
                 .expireAfterAccess(20, TimeUnit.MINUTES)
                .removalListener(removalListener)
                .build(loader);
    }

    public Optional<DynamicDataStorageMap> getUserData(UUID uuid) {
        try {
            return Optional.of(cache.get(uuid));
        } catch (ExecutionException e) {
            return Optional.empty();
        }
    }

    public <T> Optional<T> getUserDataValue(UUID uuid, DataKey<T> key){
        Optional<DynamicDataStorageMap> userData = getUserData(uuid);
        if(userData.isPresent()){
            return userData.get().get(key);
        }
        return Optional.empty();
    }

    public Optional<DynamicDataStorageMap> createNewUser(UUID uuid) {
        DynamicDataStorageMap dynamicDataStorageMap = new DynamicDataStorageMap();
        try {
            DynamicDataStorageMap.write(dynamicDataStorageMap, Paths.get(Storage.getInstance().getDirectoryPath().toString(), uuid.toString()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return getUserData(uuid);
    }

}
