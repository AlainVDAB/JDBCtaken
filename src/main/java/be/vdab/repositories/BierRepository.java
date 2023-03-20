package be.vdab.repositories;

import be.vdab.domain.Bier;
import be.vdab.dto.AantalBierPerBrouwer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BierRepository extends AbstractRepository {
    public int verwijderBierenMetOnbekendeAlcohol() throws SQLException {
        var sql = """
                delete from bieren
                where alcohol is null
                """;
        try (var connection = super.getConnection();
             var statement = connection.prepareStatement(sql)) {
            return statement.executeUpdate();
        }
    }

    public void failliet() throws SQLException {
        var sqlOvernameZwareBieren = """
                update bieren
                set brouwerId = 2
                where brouwerId=1 and alcohol >=8.5
                """;
        var sqlOvernameAndereBieren = """
                update bieren
                set brouwerId = 3
                where brouwerId=1
                """;
        var sqlVerwijderBrouwer = """
                delete from brouwers
                where  id = 1
                """;

        try (var connection = super.getConnection();
             var statement1 = connection.prepareStatement(sqlOvernameZwareBieren);
             var statement2 = connection.prepareStatement(sqlOvernameAndereBieren);
             var statement3 = connection.prepareStatement(sqlVerwijderBrouwer)) {
            connection.setAutoCommit(false);
            statement1.executeUpdate();
            statement2.executeUpdate();
            statement3.executeUpdate();
            connection.commit();

        }

    }


    public List<String> bierenVanEenmaand(int maand) throws SQLException {
        var namen = new ArrayList<String>();
        var sql = """
                select naam
                from bieren
                where {fn month(sinds)} = ?
                order by naam
                """;
        try (var connection = super.getConnection();
             var statement = connection.prepareStatement(sql)) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);
            statement.setInt(1, maand);
            for (var result = statement.executeQuery(); result.next(); ) {
                namen.add(result.getString("naam"));
            }
            connection.commit();
            return namen;
        }
    }
public List<AantalBierPerBrouwer> bierenPerBrouwer() throws SQLException {
        var brouwers = new ArrayList<AantalBierPerBrouwer>();
        var sql = """
                select brouwers.naam as naamBrouwer, count(bieren.id) as aantalBieren from bieren
                inner join brouwers on bieren.brouwerId = brouwers.id
                group by naamBrouwer
                order by naamBrouwer
                                """;
        try (var connection = super.getConnection();
             var statement = connection.prepareStatement(sql)) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);
            //statement.setInt(1, maand);
            for (var result = statement.executeQuery(); result.next(); ) {
                brouwers.add(new AantalBierPerBrouwer(result.getString("naamBrouwer"),result.getInt("aantalbieren")));
            }
            //connection.commit();
            return brouwers;
        }
    }
}
