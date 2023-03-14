package be.vdab.repositories;

import be.vdab.domain.Brouwer;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class BrouwerRepository extends AbstractRepository {

    public BigDecimal berekenGemiddelde() throws SQLException {
        var sql = """
                select avg(omzet) as gemiddelde
                from brouwers
                """;
        try (var connection = super.getConnection();
             var statement = connection.prepareStatement(sql)) {
            var result = statement.executeQuery();
            result.next();
            return result.getBigDecimal("gemiddelde");
        }
    }

    public List<Brouwer> allenHogerDanGemiddelde() throws SQLException {
        var brouwers = new ArrayList<Brouwer>();
        var sql = """
                select id, naam, adres, postcode,gemeente, omzet from brouwers
                where omzet > (select avg(omzet) from brouwers)
                """;
        try (var connection = super.getConnection();
             var statement = connection.prepareStatement(sql)) {
            for (var result = statement.executeQuery(); result.next(); ) {
                brouwers.add(naarBrouwer(result));
            }
        }
        return brouwers;
    }

    private Brouwer naarBrouwer(ResultSet result) throws SQLException {
        return new Brouwer(result.getLong("id"), result.getString("naam"), result.getString("adres"), result.getInt("postcode"),
                result.getString("gemeente"), result.getBigDecimal("omzet"));
    }

    public List<Brouwer> toonOmzetTussen(BigDecimal van, BigDecimal tot) throws SQLException {
        var brouwers = new ArrayList<Brouwer>();
        var sql = """
                select id, naam, adres, postcode, gemeente, omzet
                from brouwers
                where omzet > ? and omzet < ?
                order by omzet, id 
                """;
        try (var connection = super.getConnection();
             var statement = connection.prepareStatement(sql)) {
            statement.setBigDecimal(1, van);
            statement.setBigDecimal(2, tot);
            for (var result = statement.executeQuery(); result.next(); ) {
                brouwers.add(naarBrouwer(result));
            }

        }
        return brouwers;
    }

    public Optional<Brouwer> findById(long id) throws SQLException {
        var sql = """
                select id, naam, adres, postcode, gemeente, omzet
                from brouwers
                where id = ?
                                
                """;
        try (var connection = super.getConnection();
             var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            var result = statement.executeQuery();
            return result.next() ? Optional.of(naarBrouwer(result))
                    : Optional.empty();

        }
    }

    public List<Brouwer> findByOmzetTussen(int van, int tot) throws SQLException {
        var brouwers = new ArrayList<Brouwer>();
        try (var connection = super.getConnection();
             var statement = connection.prepareCall("{call BrouwersMetOmzetTussen(?,?)}")) {
            statement.setInt(1, van);
            statement.setInt(2, tot);
            for (var result = statement.executeQuery(); result.next(); ) {
                brouwers.add(naarBrouwer(result));
            }
            return brouwers;
        }
    }


}
