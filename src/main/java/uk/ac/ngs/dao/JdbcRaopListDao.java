/**
 * 
 */
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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import uk.ac.ngs.common.Pair;
import uk.ac.ngs.domain.RaopListRow;

/**
 * DAO for <code>raoplist</code> table actions.  
 * 
 * @author David Meredith
 */
@Repository
public class JdbcRaopListDao {

    private NamedParameterJdbcTemplate jdbcTemplate;
    //private final static DateFormat utcTimeStampFormatter = new SimpleDateFormat("yyyyMMddHHmmss"); 
    private static final Log log = LogFactory.getLog(JdbcRaopListDao.class);

    public static final String SELECT_COUNT = "select count(*) from raoplist ";
    public final String SELECT_PROJECT = "select ou, l, name, email, phone, "
                + "street, city, postcode, cn, manager, operator, trainingdate, "
                + "title, conemail, location, ra_id, department_hp, "
                + "institute_hp, active, ra_id2 from raoplist"; 
    public final String SELECT_ALL ="select * from raoplist";
    
    /**
     * Set the JDBC dataSource. 
     * @param dataSource
     */
    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }
    
    /**
     * Keys for defining 'where' parameters and values for building queries.  
     * <p>
     * If the key ends with '_LIKE' then the where clause is appended with a SQL
     * 'like' clause (e.g. <code>key like 'value'</code>). 
     * If the key ends with '_EQ' then the where clause is appended  with a SQL 
     * '=' clause (e.g. <code>key = 'value'</code>).
     * <p>
     * _EQ takes precedence over _LIKE so if both EMAIL_EQ and EMAIL_LIKE are given, then 
     * only EMAIL_EQ is used to create the query. 
     */
    public static enum WHERE_PARAMS {

        CN_LIKE, EMAIL_LIKE, EMAIL_EQ, DN_HAS_RA_LIKE
    };
    
    private final static class RaopRowMapper implements RowMapper<RaopListRow> {

        @Override
        public RaopListRow mapRow(ResultSet rs, int rowNum) throws SQLException {
            RaopListRow row = new RaopListRow(); 
            row.setOu(rs.getString("ou")); 
            row.setL(rs.getString("l"));
            row.setName(rs.getString("name"));
            row.setEmail(rs.getString("email"));
            row.setPhone(rs.getString("phone"));
            row.setStreet(rs.getString("street")); 
            row.setCity(rs.getString("city")); 
            row.setPostcode(rs.getString("postcode")); 
            row.setCn(rs.getString("cn")); 
            row.setManager(rs.getBoolean("manager"));
            row.setOperator(rs.getBoolean("operator"));
            row.setTrainingDate(rs.getDate("trainingdate")); 
            row.setTitle(rs.getString("title")); 
            row.setConeemail(rs.getString("conemail")); 
            row.setLocation(rs.getString("location"));  
            row.setRa_id(rs.getInt("ra_id"));
            row.setDepartment_hp(rs.getString("department_hp")); 
            row.setInstitute_hp(rs.getString("institute_hp"));  
            row.setActive(rs.getBoolean("active"));  
            row.setRa_id2(rs.getInt("ra_id2"));
            return row;
        }
        
    }
    
    public List<RaopListRow> findAll(){
        return this.jdbcTemplate.query(SELECT_PROJECT, 
                new RaopRowMapper());   
    }
    
    /**
     * Search the <pre>raoplist</pre> table by the specified search parameters. 
     * All parameters are nullable. 
     * @param ou
     * @param l
     * @param cn
     * @param active
     * @return list of records that satisfy the specified search parameters. 
     */
    public List<RaopListRow> findBy(String ou, String l, String cn, Boolean active){
        Map<String, Object> namedParameters = new HashMap<String, Object>();
        StringBuilder whereBuilder = new StringBuilder(); 
        if(ou != null){
            whereBuilder.append("and ou = :ou "); 
            namedParameters.put("ou", ou); 
        }
        if(l != null){
            whereBuilder.append("and l = :l "); 
            namedParameters.put("l", l); 
        }
        if(cn != null){
            whereBuilder.append("and cn like :cn "); 
            namedParameters.put("cn", cn); 
        }
        if(active != null){
            whereBuilder.append("and active = :active "); 
            namedParameters.put("active", active); 
        }
        // if one of the params was set above, then insert the where clause 
        String sqlWhere = "";
        if(whereBuilder.toString().startsWith("and ")){
            sqlWhere = " where "+whereBuilder.toString().substring(4, whereBuilder.toString().length()); 
        } 
        return this.jdbcTemplate.query(SELECT_PROJECT+sqlWhere, 
                namedParameters, new RaopRowMapper());   
    }

    /**
     * Find the <tt>raoplist</tt> row with the specified ra_id. 
     * @param ra_id
     * @return RaopListRow or null if not found 
     */
    public RaopListRow findBy(long ra_id){
        String query = SELECT_PROJECT + " where ra_id = :ra_id"; 
        Map<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("ra_id", ra_id);
        try { 
            return this.jdbcTemplate.queryForObject(query, namedParameters, new RaopRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            log.warn("No raoplist row found with [" + ra_id + "]");
            return null;
        } 
    }
    
    /**
     * Search for RA-OPs using the search criteria specified in the given 
     * where-by parameter map. 
     * Multiple whereByParams are appended together using 'and' statements. 
     * 
     * @param whereByParams Search-by parameters used in where clause of SQL query. 
     * @param limit Limit the returned row count to this many rows or null not to specify a limit.   
     * @param offset Return rows from this row number or null not to specify an offset.  
     * @return 
     */
    public List<RaopListRow> findBy(Map<WHERE_PARAMS, String> whereByParams, Integer limit, Integer offset) {
        Pair<String, Map<String, Object>> p = this.buildQuery(SELECT_ALL, whereByParams, limit, offset, true);
        return this.jdbcTemplate.query(p.first, p.second, new RaopRowMapper());
    }
    /**
     * Count the total number of rows that are selected by the given where-by search criteria. 
     * @see #findBy(java.util.Map, java.lang.Integer, java.lang.Integer)  
     * @param whereByParams
     * @return number of matching rows.  
     */
    public int countBy(Map<WHERE_PARAMS, String> whereByParams) {
        Pair<String, Map<String, Object>> p = this.buildQuery(SELECT_COUNT, whereByParams, null, null, false);
        return this.jdbcTemplate.queryForInt(p.first, p.second);
    }

    /**
     * Build up the query using the given where by parameters in the map
     * and return the query and the named parameter map for subsequent parameter-binding/execution.  
     * @param selectStatement
     * @param whereByParams
     * @param limit
     * @param offset
     * @param orderby
     * @return 
     */
    protected Pair<String, Map<String, Object>> buildQuery(String selectStatement,
            Map<WHERE_PARAMS, String> whereByParams, Integer limit, Integer offset, boolean orderby) {

        String whereClause = "";
        Map<String, Object> namedParameters = new HashMap<String, Object>();
        if (whereByParams != null && !whereByParams.isEmpty()) {
            StringBuilder whereBuilder = new StringBuilder("where ");
            
            if (whereByParams.containsKey(WHERE_PARAMS.CN_LIKE)) {
                whereBuilder.append("cn like :cn and ");
                namedParameters.put("cn", whereByParams.get(WHERE_PARAMS.CN_LIKE));
            }
            // EQ takes precidence over LIKE 
            if (whereByParams.containsKey(WHERE_PARAMS.EMAIL_EQ)) {
                log.debug("Searching for null email - check val ["+whereByParams.get(WHERE_PARAMS.EMAIL_EQ)+"]"); 
                if(whereByParams.get(WHERE_PARAMS.EMAIL_EQ) == null){
                    whereBuilder.append("email is null and ");
                } else {
                    whereBuilder.append("email = :email and ");
                    namedParameters.put("email", whereByParams.get(WHERE_PARAMS.EMAIL_EQ));
                }
            } else {
                if (whereByParams.containsKey(WHERE_PARAMS.EMAIL_LIKE)) {
                    whereBuilder.append("email like :email and ");
                    namedParameters.put("email", whereByParams.get(WHERE_PARAMS.EMAIL_LIKE));
                }
            }
            if(whereByParams.containsKey(WHERE_PARAMS.DN_HAS_RA_LIKE)){
                String[] location = whereByParams.get(WHERE_PARAMS.DN_HAS_RA_LIKE).split(",");
                String l = location[0].replace("L=", "").trim();
                String ou = location[1].replace("OU=", "").trim();
                log.info(ou);
                log.info(l);
                whereBuilder.append("ou like :ou and l like :l and"); 
                namedParameters.put("ou", ou);
                namedParameters.put("l", l);
            }
            // Always trim leading/trailing whitespace and remove trailling and (if any) 
            whereClause = whereBuilder.toString().trim();
            if (whereClause.endsWith("and")) {
                whereClause = whereClause.substring(0, whereClause.length() - 3);
            }
            whereClause = whereClause.trim();
        }
        // Build up the sql statement. 
        String sql = selectStatement + ' ' + whereClause;        
        if (limit != null) {
            sql = sql + " LIMIT :limit";
            namedParameters.put("limit", limit);
        }
        if (offset != null) {
            sql = sql + " OFFSET :offset";
            namedParameters.put("offset", offset);
        }
        log.debug(sql);
        return Pair.create(sql.trim(), namedParameters);
    }
    
    /**
     * Insert a new row into the 'raop' table using the values from the given RaopListRow. 
     * Important: the crr_key (PK) must be set to a value that is not already in use 
     * (the row PK is set by the calling client and not a db sequence -  
     * this is an inherited legacy issue).
     * 
     * @param raop 
     * @return the number of rows affected (should always be 1)
     */
    public int insertRaopListRow(RaopListRow raop) {
        if (raop.getCn() == null) {
            throw new IllegalArgumentException("Invalid crr, crr.crr_key is zero or negative");
        }
        Map<String, Object> namedParameters = this.buildParameterMap(raop);
        String INSERT_RAOP = "insert into raoplist (ou, l, name, email, phone, street, city, postcode, cn, manager, operator, trainingdate, title, conemail, location, ra_id, department_hp, institute_hp, active, ra_id2) "
                + "values(:ou, :l, :name, :email, :phone, :street, :city, :postcode, :cn, :manager, :operator, :trainingdate, :title, :conemail, :location, :ra_id, :department_hp, :institute_hp, :active, :ra_id2)";

        return this.jdbcTemplate.update(INSERT_RAOP, namedParameters);
    }

    /**
     * Update a specific row in the 'raop' table using the values from the given RaopListRow. 
     * Important: the crr_key (PK) must be set to a value that is not already in use 
     * (the row PK is set by the calling client and not a db sequence -  
     * this is an inherited legacy issue).
     * 
     * @param raop 
     * @return the number of rows affected (should always be 1)
     */
    public int updateRaopRow(RaopListRow raop) {
        if (raop.getCn() == null) {
            throw new IllegalArgumentException("Invalid crr, crr.crr_key is zero or negative");
        }
        Map<String, Object> namedParameters = this.buildParameterMap(raop);
        String UPDATE_RAOP = "update raoplist SET 'name' = 'Test' WHERE ou = :ou AND l = :l AND name = :name AND email = :email AND phone = :phone AND street = :street AND city = :city AND postcode = :postcode AND cn = :cn AND manager = :manager AND operator = :operator "
                + "AND trainingdate = :trainingdate AND title = :title AND conemail = :coneemail AND location = :location AND ra_id = :ra_id AND department_hp = :department_hp AND institute_hp = :institute_hp AND active = :active AND ra_id2 = :ra_id2;";
             
        return this.jdbcTemplate.update(UPDATE_RAOP, namedParameters);
    }
    
    private Map<String, Object> buildParameterMap(RaopListRow raop) {
        Map<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("ou", raop.getOu());
        namedParameters.put("l", raop.getL());
        namedParameters.put("name", raop.getName());
        namedParameters.put("email", raop.getEmail());
        namedParameters.put("phone", raop.getPhone());
        namedParameters.put("city", raop.getCity());
        namedParameters.put("postcode", raop.getPostcode());
        namedParameters.put("cn", raop.getCn());
        namedParameters.put("manager", raop.getManager());
        namedParameters.put("operator", raop.getOperator());
        namedParameters.put("trainingdate", raop.getTrainingDate());
        namedParameters.put("title", raop.getTitle());
        namedParameters.put("coneemail", raop.getConeemail());
        namedParameters.put("location", raop.getLocation());
        namedParameters.put("ra_id", raop.getRa_id());
        namedParameters.put("department_hp", raop.getDepartment_hp());
        namedParameters.put("institute_hp", raop.getInstitute_hp());
        namedParameters.put("active", raop.getActive());
        namedParameters.put("ra_id2", raop.getRa_id2());
        return namedParameters;
    }
}
