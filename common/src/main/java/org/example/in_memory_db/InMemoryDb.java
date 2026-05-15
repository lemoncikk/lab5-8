package org.example.in_memory_db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDb {
    private Map<String, Table> tables = new ConcurrentHashMap<>();

    public void createTable(String name, List<String> schema) {
        tables.put(name, new Table(name, schema));
    }

    public List<Map<String, Object>> select(String tableName, List<String> columns) {
        Table t = tables.get(tableName);
        if (columns == null || columns.isEmpty() || columns.contains("*")) {
            t.getRows();
        }
        return t.getRows().stream().map(row -> {
            Map<String, Object> filtred = new HashMap<>();
            columns.forEach(f -> filtred.put(f, row.get(f)));
            return filtred;
        }).toList();
    }

    public void deleteTable(String name) {
        tables.remove(name);
    }

    public void insertIntoTable(String name, List<String> fields, List<Object> values) {
        tables.get(name).insertRow(fields, values);
    }

}
