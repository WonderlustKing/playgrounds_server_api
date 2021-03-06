package com.playgrounds.api.user.repository;

import com.mongodb.WriteResult;
import com.playgrounds.api.user.model.Favorite;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;


public interface UserOperations {
    @CacheEvict("playgroundsCache")
    WriteResult addFavorite(String user_id, Favorite favorite);

    @CacheEvict("playgroundsCache")
    WriteResult removeFromFavorites(String user_id, Favorite favorite);
}
