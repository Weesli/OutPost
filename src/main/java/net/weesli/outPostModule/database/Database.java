package net.weesli.outPostModule.database;

import net.weesli.outPostModule.utils.Outpost;

import java.util.List;

public interface Database {

    void insert(Outpost outpost);
    void update(Outpost outpost);
    void updateTime(Outpost outpost);
    void delete(String id);
    boolean isValid(String id);
    Outpost getOutpost(String id);
    List<Outpost> getAllOutposts();

}
