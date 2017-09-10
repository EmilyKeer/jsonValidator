import ExportData.InvalidCustomer;
import ExportData.InvalidCustomersForExport;
import ImportData.Length;
import ImportData.SinglePage;
import ImportData.Validation;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class InfoValidator {

    public static void main(String[] args) throws Exception {

        InfoValidator http = new InfoValidator();

        SinglePage firstSinglePage = http.mapJSONStringToObject(1);
        int pageNumTotal = (int) Math.ceil((double)(firstSinglePage.getPagination().getTotal()) / firstSinglePage.getPagination().getPer_page());
        List<InvalidCustomer> invalidCustomers = http.validateItems(firstSinglePage);

        for (int num = 2; num <= pageNumTotal; num++) {
            SinglePage singlePage = http.mapJSONStringToObject(num);
            List<InvalidCustomer> invalidCustomersAppend = http.validateItems(singlePage);
            invalidCustomers.addAll(invalidCustomersAppend);
        }

        InvalidCustomersForExport invalidCustomersForExport = new InvalidCustomersForExport();
        invalidCustomersForExport.setInvalidCustomers(invalidCustomers);

        ObjectMapper mapper = new ObjectMapper();
        String jsonForExport = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(invalidCustomersForExport);

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("output.json"));
        bufferedWriter.write(jsonForExport);
        bufferedWriter.close();
    }

    private String sendGet(int pageNum) throws Exception {

        String url = "https://backend-challenge-winter-2017.herokuapp.com/customers.json?page=" + pageNum;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = bufferedReader.readLine()) != null) {
            response.append(inputLine);
        }
        bufferedReader.close();

        return response.toString();


    }

    private SinglePage mapJSONStringToObject(int pageNum) throws Exception {
        String stringFromJSON = sendGet(pageNum);
        ObjectMapper mapper = new ObjectMapper();
        SinglePage singlePage = mapper.readValue(stringFromJSON, SinglePage.class);

        //parse validations
        List<LinkedHashMap> validations = singlePage.getValidations();
        HashMap<String, Validation> itemValidation = new HashMap<String, Validation>();
        for (LinkedHashMap<String, LinkedHashMap> item : validations) {
            Map.Entry<String, LinkedHashMap> entry = item.entrySet().iterator().next();
            String key = entry.getKey();
            LinkedHashMap value = entry.getValue();
            Validation validation = new Validation();
            if (value.containsKey("required")) {
                validation.setRequired((Boolean) value.get("required"));
            }
            if (value.containsKey("type")) {
                validation.setType((String) value.get("type"));
            }
            if (value.containsKey("length")) {
                LinkedHashMap<String, Integer> lengthValue = (LinkedHashMap<String, Integer>) value.get("length");
                Length length = new Length();
                if (lengthValue.containsKey("min")) {
                    length.setMin(lengthValue.get("min"));
                }
                if (lengthValue.containsKey("max")) {
                    length.setMin(lengthValue.get("max"));
                }
                validation.setLength(length);
            }
            itemValidation.put(key, validation);
        }
        singlePage.setItemValidation(itemValidation);

        return singlePage;
    }

    private List<InvalidCustomer> validateItems(SinglePage singlePage) throws Exception {
        List<InvalidCustomer> invalidCustomers = new ArrayList<InvalidCustomer>();
        HashMap<String, Validation> itemValidation = singlePage.getItemValidation();
        List<LinkedHashMap> customers = singlePage.getCustomers();
        for (LinkedHashMap<String, Object> customer : customers) {
            List<String> invalidFields = new ArrayList<String>();
            for (String itemForCheck : itemValidation.keySet()) {
                Validation validationCase = itemValidation.get(itemForCheck);
                if (validationCase.isRequired()) { //default to false if not provided
                    if (!customer.containsKey(itemForCheck) || customer.get(itemForCheck) == null) {
                        invalidFields.add(itemForCheck);
                        continue;
                    }
                }

                if (validationCase.getType() != null && customer.containsKey(itemForCheck) && customer.get(itemForCheck) != null) {
                    boolean isRightType = false;
                    String rightType = validationCase.getType();
                    if (rightType.equals("boolean")) {
                        if (customer.get(itemForCheck) instanceof Boolean) {
                            isRightType = true;
                        }
                    }
                    if (rightType.equals("number")) {
                        if (customer.get(itemForCheck) instanceof Integer) {
                            isRightType = true;
                        }
                    }
                    if (rightType.equals("string")) {
                        if (customer.get(itemForCheck) instanceof String) {
                            isRightType = true;
                        }
                    }
                    if (!isRightType) {
                        invalidFields.add(itemForCheck);
                        continue;
                    }
                }

                Length length = validationCase.getLength();
                if (length != null) {
                    int minLength = length.getMin();
                    int maxLength = length.getMax();
                    if (customer.containsKey(itemForCheck) && customer.get(itemForCheck) != null) {
                        String itemString = (String) customer.get(itemForCheck);
                        if ((minLength > 0 && itemString.length() < minLength) || (maxLength > 0 && itemString.length() > maxLength)) {
                            invalidFields.add(itemForCheck);
                        }
                    }
                }
            }
            if (!invalidFields.isEmpty()) {
                InvalidCustomer invalidCustomer = new InvalidCustomer();
                invalidCustomer.setId((Integer)customer.get("id"));
                invalidCustomer.setInvalidFields(invalidFields);
                invalidCustomers.add(invalidCustomer);
            }
        }
        return invalidCustomers;
    }
}