package net.weesli.outPostModule.database;

import net.weesli.outPostModule.utils.Outpost;
import net.weesli.rClaim.RClaim;
import net.weesli.rozsLib.database.mysql.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MySQLDatabase implements Database{

    private MySQLBuilder builder;
    private Connection connection;

    private String host = RClaim.getInstance().getConfig().getString("options.database.host");
    private int port = RClaim.getInstance().getConfig().getInt("options.database.port");
    private String user = RClaim.getInstance().getConfig().getString("options.database.username");
    private String pass = RClaim.getInstance().getConfig().getString("options.database.password");
    private String db = RClaim.getInstance().getConfig().getString("options.database.database");

    public MySQLDatabase() {
        builder = new MySQLBuilder(host,port,db,user,pass);
        connection = builder.build();
        createTable();
    }

    private void createTable() {
        List<Column> columns = new ArrayList<>();
        columns.add(new Column("id", "VARCHAR(255)", 255).setPrimary(true));
        columns.add(new Column("owner", "VARCHAR(255)", 255));
        columns.add(new Column("x", "INT", 9999));
        columns.add(new Column("y", "INT", 9999));
        columns.add(new Column("z", "INT", 9999));
        columns.add(new Column("time", "INT", 99999));
        columns.add(new Column("farmer_efficiency", "INT", 255));
        try {
            builder.createTable("outposts", connection, columns);
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
