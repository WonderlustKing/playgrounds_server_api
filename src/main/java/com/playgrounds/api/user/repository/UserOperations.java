package com.playgrounds.api.user.repository;

import com.mongodb.WriteResult;
import com.playgrounds.api.user.model.Favorite;
import org.springframework.cache.annotation.CachePut;

/**
 * Created by christos on 20/8/2016.
 */
public interface UserOperations {
    @CachePut("playgroundsCache")
    WriteResult addFavorite(String user_id, Favorite favorite);
}
