package ImportData;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class SinglePage {
    private List<LinkedHashMap> validations;
    private List<LinkedHashMap> customers;
    private Pagination pagination;
    private HashMap<String, Validation> itemValidation;

    public List<LinkedHashMap> getValidations() {
        return validations;
    }

    public void setValidations(List<LinkedHashMap> validations) {
        this.validations = validations;
    }

    public List<LinkedHashMap> getCustomers() {
        return customers;
    }

    public void setCustomers(List<LinkedHashMap> customers) {
        this.customers = customers;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public HashMap<String, Validation> getItemValidation() {
        return itemValidation;
    }

    public void setItemValidation(HashMap<String, Validation> itemValidation) {
        this.itemValidation = itemValidation;
    }
}
