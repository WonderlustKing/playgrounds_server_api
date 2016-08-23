package com.playgrounds.api.Repository;

import com.mongodb.WriteResult;
import com.playgrounds.api.Domain.Favorite;
import org.springframework.cache.annotation.CachePut;

/**
 * Created by christos on 20/8/2016.
 */
public interface UserOperations {
    @CachePut("playgroundsCache")
    WriteResult addFavorite(String user_id, Favorite favorite);
}
