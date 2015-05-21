package com.ebay.cloud.iaas.imagemanagement.jbehave.steps;

import com.ebay.cloud.iaas.imagemanagement.service.OSProfileState;
import com.ebay.cloud.iaas.imagemanagement.service.TaskState;
import org.apache.cxf.jaxrs.impl.MetadataMap;
import org.jbehave.core.annotations.AsParameterConverter;
import org.jbehave.core.model.ExamplesTable;

import javax.ws.rs.core.MultivaluedMap;
import java.util.Map;

public class Converters {

    // This method must be before the convertPropertiesToMap one.
    @AsParameterConverter
    public MultivaluedMap<String, String> convertPropertiesToMultivaluedMap(String data) {

        ExamplesTable table = new ExamplesTable(data);

        MultivaluedMap<String, String> result = new MetadataMap<String, String>();

        for (Map<String, String> row : table.getRows()) {
            result.add(row.get("name"), row.get("value"));
        }

        return result;
    }

    // This method must be after the convertPropertiesToMultivaluedMap one.
    @AsParameterConverter
    public Map<String, String> convertPropertiesToMap(String data) {

        ExamplesTable table = new ExamplesTable(data);
        return Utils.readPropertiesFromTable(table);
    }

    @AsParameterConverter
    public OSProfileState convertOSProfileState(String name) {

        return OSProfileState.valueOf(name);
    }

    @AsParameterConverter
    public TaskState convertTaskState(String name) {

        return TaskState.valueOf(name);
    }
}
