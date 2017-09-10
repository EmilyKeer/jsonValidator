package ExportData;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class InvalidCustomersForExport {
    private List<InvalidCustomer> invalidCustomers;

    @JsonProperty(value = "invalid_customers")
    public List<InvalidCustomer> getInvalidCustomers() {
        return invalidCustomers;
    }

    public void setInvalidCustomers(List<InvalidCustomer> invalidCustomers) {
        this.invalidCustomers = invalidCustomers;
    }
}
