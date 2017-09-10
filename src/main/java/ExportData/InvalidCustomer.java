package ExportData;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class InvalidCustomer {
    private int id;
    private List<String> invalidFields;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty(value = "invalid_fields")
    public List<String> getInvalidFields() {
        return invalidFields;
    }

    public void setInvalidFields(List<String> invalidFields) {
        this.invalidFields = invalidFields;
    }
}
