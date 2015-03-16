package uk.ac.ngs.forms;

import java.io.Serializable;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

//import org.hibernate.validator.constraints.NotEmpty;

/**
 * Search RA-OP form bean. 
 * @author Josh Hadley
 *
 */
public class SearchRaopFormBean implements Serializable {

    @Pattern(message="Invalid chars \" ' ; `", regexp="^[^\"';`]*$") 
    private String ra;
    
    @Pattern(message="Invalid chars \" ' ; `", regexp="^[^\"';`]*$")
    private String name; 
    
    @Pattern(message="Invalid chars \" ' ; `", regexp="^[^\"';`]*$")
    private String emailAddress; 
    
    private Boolean searchNullEmailAddress = false; 
        
    @Min(value=0, message="0 is minimum")
    private Integer showRowCount = 10; 
   
    @Min(value=0, message="0 is minimum")
    private Integer startRow = 0; 
         
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmailAddress() {
        return emailAddress;
    }
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    
    /**
     * @return the showRowCount
     */
    public Integer getShowRowCount() {
        return showRowCount;
    }

    /**
     * @param showRowCount the showRowCount to set
     */
    public void setShowRowCount(Integer showRowCount) {
        this.showRowCount = showRowCount;
    }


    /**
     * @return the searchNullEmailAddress
     */
    public Boolean getSearchNullEmailAddress() {
        return searchNullEmailAddress;
    }

    /**
     * @param searchNullEmailAddress the searchNullEmailAddress to set
     */
    public void setSearchNullEmailAddress(Boolean searchNullEmailAddress) {
        this.searchNullEmailAddress = searchNullEmailAddress;
    }

    /**
     * @return the startRow
     */
    public Integer getStartRow() {
        return startRow;
    }

    /**
     * @param startRow the startRow to set
     */
    public void setStartRow(Integer startRow) {
        this.startRow = startRow;
    }

    /**
     * @return the location
     */
    public String getRa() {
        return ra;
    }

    /**
     * @param location the location to set
     */
    public void setRa(String ra) {
        this.ra = ra;
    }

}
