package org.example.in_memory_db;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table {
    @Getter
    private String name;
    @Getter
    private List<String> columns;
    @Getter
    private List<Map<String, Object>> rows;

    public Table(String name, List<String> colums) {
        this.name = name;
        this.columns = colums;
        rows = new ArrayList<>();
    }

    public void insertRow(List<String> columns, List<Object> values) {
        Map<String, Object> newRow = new HashMap<>();
        if (columns != null && !columns.isEmpty()) {
            for (int i = 0; i < columns.size(); i++) {
                newRow.put(columns.get(i), values.get(i));
            }
        } else {
            for (int i = 0; i < columns.size(); i++) {
                newRow.put(columns.get(i), values.get(i));
            }
        }
        rows.add(newRow);
    }
}
