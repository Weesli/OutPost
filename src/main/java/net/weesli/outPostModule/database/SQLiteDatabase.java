package net.weesli.outPostModule.database;

import net.weesli.outPostModule.utils.Outpost;
import net.weesli.rClaim.RClaim;
import net.weesli.rozsLib.database.mysql.Column;
import net.weesli.rozsLib.database.mysql.Delete;
import net.weesli.rozsLib.database.mysql.Insert;
import net.weesli.rozsLib.database.mysql.Update;
import net.weesli.rozsLib.database.sqlite.SQLiteBuilder;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SQLiteDatabase implements Database {

    private SQLiteBuilder builder;
    private Connection connection;

    public SQLiteDatabase() {
        builder = new SQLiteBuilder("RClaim").setPath(new File(RClaim.getInstance().getDataFolder(), "data").getPath());
        try {
            connection = builder.build();
            createTable();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createTable() {
        List<Column> columns = new ArrayList<>();
        columns.add(new Column("id", "VARCHAR(255)", 255).setPrimary(true));
        columns.add(new Column("owner", "VARCHAR(255)", 255));
        columns.add(new Column("x", "INT", 9999));
        columns.add(new Column("z", "INT", 9999));
        columns.add(new Column("time", "INT", 99999));
        columns.add(new Column("farmer_efficiency", "INT", 255));
        try {
            builder.createTable("rclaims_outpost", connection, columns);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insert(Outpost outpost) {
        Insert insert = new Insert("rclaims_outpost", List.of("id", "owner", "x","z", "time", "farmer_efficiency"), List.of(outpost.getId(),outpost.getOwner(),outpost.getX(),outpost.getZ(), outpost.getTime(), outpost.getFarmer_efficiency()));
        try {
            builder.insert(connection, insert);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Outpost outpost) {
        Update update = new Update("rclaims_outpost", List.of("id", "owner", "x","z"), List.of(outpost.getId(),outpost.getOwner(),outpost.getX(),outpost.getZ()), Map.of("id", outpost.getId()));
        try {
            builder.update(connection, update);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateTime(Outpost outpost) {
        Update update = new Update("rclaims_outpost", List.of("time"), List.of(outpost.getTime()), Map.of("id", outpost.getId()));
        try {
            builder.update(connection, update);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String id) {
        Delete delete = new Delete(connection,"rclaims_outpost",Map.of("id", id));
        try {
            builder.delete(delete);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isValid(String id) {
        String sql = "SELECT * FROM rclaims_outpost WHERE id=?";
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, id);
            ResultSet rs = statement.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Outpost getOutpost(String id) {
        String sql = "SELECT * FROM rclaims_outpost WHERE id=?";
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, id);
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                return new Outpost(rs.getString("id"), UUID.fromString(rs.getString("owner")), rs.getInt("x"), rs.getInt("z"), rs.getInt("time"), rs.getInt("farmer_efficiency"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public List<Outpost> getAllOutposts() {
        List<Outpost> outposts = new ArrayList<>();
        String sql = "SELECT * FROM rclaims_outpost";
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                outposts.add(new Outpost(rs.getString("id"), UUID.fromString(rs.getString("owner")), rs.getInt("x"), rs.getInt("z"), rs.getInt("time"),rs.getInt("farmer_efficiency")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return outposts;
    }
}
