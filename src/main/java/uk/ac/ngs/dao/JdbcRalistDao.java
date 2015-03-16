package uk.ac.ngs.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import uk.ac.ngs.domain.RalistRow;

/**
 * DAO for the CA DB
 * <code>ralist</code> table.
 *
 * @author David Meredith
 * @author Josh Hadley
 */
@Repository
public class JdbcRalistDao {

    private NamedParameterJdbcTemplate jdbcTemplate;
    private static final Log log = LogFactory.getLog(JdbcRalistDao.class);
    private static final String LIMIT_ROWS = "LIMIT :limit ";
    private static final String OFFSET_ROWS = "OFFSET :offset ";
    private static final String ORDER_BY = "order by order_id "; 
    public static final String SQL_SELECT_ALL = "select ra_id, order_id, ou, l, active from ralist ";
    private static final String SQL_INSERT ="insert into ralist (order_id, ou, l) values (:order_id, :ou, :l);";

    /**
     * Set the JDBC dataSource.
     *
     * @param dataSource
     */
    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    private static final class RalistRowMapper implements RowMapper<RalistRow> {

        @Override
        public RalistRow mapRow(ResultSet rs, int rowNum) throws SQLException {
            RalistRow row = new RalistRow();
            row.setRa_id(rs.getInt("ra_id"));
            row.setOrder_id(rs.getInt("order_id"));
            row.setL(rs.getString("l"));
            row.setOu(rs.getString("ou"));
            row.setActive(rs.getBoolean("active"));
            return row;
        }
    }

    /**
     * Return all the rows in the
     * <code>ralist</code> table.
     *
     * @param limit to this many rows (null is for no limit)
     * @param offset results by this number of rows (null for no offset)
     * @return
     */
    /*public List<RalistRow> findAll(Integer limit, Integer offset) {
        Map<String, Object> namedParameters = new HashMap<String, Object>();
        String query = SQL_SELECT_ALL+ORDER_BY;
        if (limit != null) {
            query += LIMIT_ROWS;
            namedParameters.put("limit", limit);
        }
        if (offset != null) {
            query += OFFSET_ROWS;
            namedParameters.put("offset", offset);
        }
        return this.jdbcTemplate.query(query, namedParameters, new JdbcRalistDao.RalistRowMapper());
    }*/

    /**
     * Return all the rows in the <code>ralist</code> table according to 
     * whether the row is active (or not).
     *
     * @param active true or false (null for any active state)  
     * @param limit to this many rows (null is for no limit)
     * @param offset results by this number of rows (null for no offset)
     * @return
     */
    public List<RalistRow> findAllByActive(Boolean active, Integer limit, Integer offset) {
        Map<String, Object> namedParameters = new HashMap<String, Object>();
        String query = SQL_SELECT_ALL;
        if(active != null){
          query += "where active = :active " ;   
          namedParameters.put("active", active); 
        }
        query += ORDER_BY; 
        
        // limit/offset 
        if (limit != null) {
            query += LIMIT_ROWS;
            namedParameters.put("limit", limit);
        }
        if (offset != null) {
            query += OFFSET_ROWS;
            namedParameters.put("offset", offset);
        }
        return this.jdbcTemplate.query(query, namedParameters, new JdbcRalistDao.RalistRowMapper());
    }
    
    /**
     * Returns all the rows in the <code>ralist</code> where the location
     * matches the given location.
     * 
     * @param L location to search by
     * @return
     */
    public List<RalistRow> findAllByLocation(String L){
        Map<String, Object> namedParameters = new HashMap<String,Object>();
        
        String query = SQL_SELECT_ALL;
        
        query += "where l = :L";
        
        query += ORDER_BY;
        
        return this.jdbcTemplate.query(query, namedParameters, new JdbcRalistDao.RalistRowMapper());
    }
    
    /**
     * Insert new RA record into <code>Ralist</code> table with the given
     * parameters
     * 
     * @param order_id sorting order for the RA records
     * @param L location of RA
     * @param ou organisation unit of RA
     * @return 
     */
    public int insertIntoDB(int order_id, String L, String ou){
        Map<String, Object> namedParameters = new HashMap<String,Object>();
        
        namedParameters.put("order_id", order_id);
        namedParameters.put("l", L);
        namedParameters.put("ou", ou);
        
        String query = SQL_INSERT;
       
        return this.jdbcTemplate.update(query, namedParameters);
    }
}
