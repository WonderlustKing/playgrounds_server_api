package com.playgrounds.api.user.repository;

import com.mongodb.WriteResult;
import com.playgrounds.api.user.model.Favorite;
import com.playgrounds.api.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import static org.springframework.data.mongodb.core.query.Criteria.where;


public class UserRepositoryImpl implements UserOperations {

    @Autowired
    private MongoOperations mongo;

    @Override
    public WriteResult addFavorite(String user_id, Favorite favorite) {
        Criteria where = where("id").is(user_id);
        Query query = Query.query(where);
        Update update = new Update();
        update.push("favorites", favorite);
        return mongo.updateFirst(query, update, User.class);
    }

    @Override
    public WriteResult removeFromFavorites(String user_id, Favorite favorite) {
        Criteria where = where("id").is(user_id);
        Query query = Query.query(where);
        Update update = new Update();
        update.pull("favorites", favorite);
        return mongo.updateFirst(query, update, User.class);
    }
}
