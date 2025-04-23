//package dataman.erp.pagging.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//
//@Service
//public class DynamicQueryService {
//
//    @Autowired
//    @Qualifier("tirangaCompanyJdbcTemplate")
//    private JdbcTemplate jdbcTemplate;
//
//    public Map<String, Object> getRecords(Map<String, Object> filters, int pageNo, int pageSize) {
//        if (!filters.containsKey("tableName")) {
//            throw new IllegalArgumentException("Table name is required.");
//        }
//
//        String tableName = filters.get("tableName").toString();
//        StringBuilder query = new StringBuilder("SELECT * FROM " + tableName + " WHERE 1=1");
//        StringBuilder countQuery = new StringBuilder("SELECT COUNT(*) FROM " + tableName + " WHERE 1=1");
//        List<Object> params = new ArrayList<>();
//
//        // Process Filters Dynamically
//        for (Map.Entry<String, Object> entry : filters.entrySet()) {
//            String column = entry.getKey();
//            Object value = entry.getValue();
//
//            if (column.equals("tableName") || column.equals("sortBy") || column.equals("sortOrder") || column.equals("pageNo") || column.equals("pageSize")) {
//                continue; // Skip non-column fields
//            }
//
//            if (value instanceof List<?>) {
//                // Handle IN Clause
//                List<?> values = (List<?>) value;
//                if (!values.isEmpty()) {
//                    String inClause = String.join(",", Collections.nCopies(values.size(), "?"));
//                    query.append(" AND ").append(column).append(" IN (").append(inClause).append(")");
//                    countQuery.append(" AND ").append(column).append(" IN (").append(inClause).append(")");
//                    params.addAll(values);
//                }
//            } else {
//                // Handle Normal Conditions
//                query.append(" AND ").append(column).append(" = ?");
//                countQuery.append(" AND ").append(column).append(" = ?");
//                params.add(value);
//            }
//        }
//
//        // Handle Sorting (Validate column name)
//        if (filters.containsKey("sortBy")) {
//            query.append(" ORDER BY ").append(filters.get("sortBy"));
//            if ("desc".equalsIgnoreCase(filters.getOrDefault("sortOrder", "asc").toString())) {
//                query.append(" DESC");
//            }
//        }
//
//        // Get Total Records Count
//        int totalRecords = jdbcTemplate.queryForObject(countQuery.toString(), params.toArray(), Integer.class);
//        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
//
//        // Ensure pageNo is within valid range
//        pageNo = Math.max(1, Math.min(pageNo, totalPages));
//        int offset = (pageNo - 1) * pageSize;
//
//        // Apply Pagination
//        query.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
//        params.add(offset);
//        params.add(pageSize);
//
//        List<Map<String, Object>> records = jdbcTemplate.queryForList(query.toString(), params.toArray());
//
//        // Prepare Response
//        Map<String, Object> response = new HashMap<>();
//        response.put("data", records);
//        response.put("pageNo", pageNo);
//        response.put("totalPages", totalPages);
//        response.put("BOP", pageNo == 1);
//        response.put("EOP", pageNo >= totalPages);
//
//        return response;
//    }
//}
//


//package dataman.erp.pagging.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//
//@Service
//public class DynamicQueryService {
//
//    @Autowired
//    @Qualifier("tirangaCompanyJdbcTemplate")
//    private JdbcTemplate jdbcTemplate;
//
//    public Map<String, Object> getRecords(Map<String, Object> filters, int pageNo, int pageSize) {
//        if (!filters.containsKey("tableName")) {
//            throw new IllegalArgumentException("Table name is required.");
//        }
//
//        String tableName = filters.get("tableName").toString();
//        StringBuilder query = new StringBuilder("SELECT * FROM " + tableName + " WHERE 1=1");
//        StringBuilder countQuery = new StringBuilder("SELECT COUNT(*) FROM " + tableName + " WHERE 1=1");
//        List<Object> params = new ArrayList<>();
//
//        // Process Filters Dynamically
//        for (Map.Entry<String, Object> entry : filters.entrySet()) {
//            String column = entry.getKey();
//            Object value = entry.getValue();
//
//            if (column.equalsIgnoreCase("tableName") || column.equalsIgnoreCase("sortBy")
//                    || column.equalsIgnoreCase("sortOrder") || column.equalsIgnoreCase("pageNo")
//                    || column.equalsIgnoreCase("pageSize")) {
//                continue; // Skip non-column fields
//            }
//
//            if (value instanceof List<?>) {
//                // Handle IN Clause
//                List<?> values = (List<?>) value;
//                if (!values.isEmpty()) {
//                    String inClause = String.join(",", Collections.nCopies(values.size(), "?"));
//                    query.append(" AND ").append(column).append(" IN (").append(inClause).append(")");
//                    countQuery.append(" AND ").append(column).append(" IN (").append(inClause).append(")");
//                    params.addAll(values);
//                }
//            } else {
//                // Handle Normal Conditions
//                query.append(" AND ").append(column).append(" = ?");
//                countQuery.append(" AND ").append(column).append(" = ?");
//                params.add(value);
//            }
//        }
//
//        // Get Total Records Count
//        int totalRecords = jdbcTemplate.queryForObject(countQuery.toString(), params.toArray(), Integer.class);
//        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
//
//        // Ensure pageNo is within valid range
//        pageNo = Math.max(1, Math.min(pageNo, totalPages));
//        int offset = (pageNo - 1) * pageSize;
//
//        // Sorting Logic
//        String sortBy = filters.getOrDefault("sortBy", "id").toString(); // Default sort by 'id'
//        String sortOrder = "DESC".equalsIgnoreCase(filters.getOrDefault("sortOrder", "ASC").toString()) ? "DESC" : "ASC";
//
//        // Ensure ORDER BY exists (MS SQL requires it for pagination)
//        query.append(" ORDER BY ").append(sortBy).append(" ").append(sortOrder);
//
//        // Check SQL Server version to determine pagination strategy
//        if (isSqlServer2012OrNewer()) {
//            // Use OFFSET ... FETCH for SQL Server 2012+
//            query.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
//            params.add(offset);
//            params.add(pageSize);
//        } else {
//            // Use ROW_NUMBER() for SQL Server 2008 and older
//            query = new StringBuilder("SELECT * FROM (")
//                    .append("SELECT *, ROW_NUMBER() OVER (ORDER BY ").append(sortBy).append(" ").append(sortOrder)
//                    .append(") AS RowNum FROM ").append(tableName).append(") AS Temp ")
//                    .append("WHERE RowNum BETWEEN ? AND ?");
//            params.add(offset + 1);
//            params.add(offset + pageSize);
//        }
//
//        List<Map<String, Object>> records = jdbcTemplate.queryForList(query.toString(), params.toArray());
//
//        // Prepare Response
//        Map<String, Object> response = new HashMap<>();
//        response.put("data", records);
//        response.put("pageNo", pageNo);
//        response.put("totalPages", totalPages);
//        response.put("BOP", pageNo == 1);
//        response.put("EOP", pageNo >= totalPages);
//
//        return response;
//    }
//
//    private boolean isSqlServer2012OrNewer() {
//        try {
//            String version = jdbcTemplate.queryForObject("SELECT CAST(SERVERPROPERTY('ProductVersion') AS VARCHAR)", String.class);
//            String[] versionParts = version.split("\\.");
//            int majorVersion = Integer.parseInt(versionParts[0]);
//            return majorVersion >= 11; // SQL Server 2012 is version 11+
//        } catch (Exception e) {
//            return false; // Assume older version if there's an issue fetching version
//


package dataman.erp.pagging.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DynamicQueryService {

    @Autowired
    @Qualifier("tirangaCompanyJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> getRecords(Map<String, Object> filters, int pageNo, int pageSize) {
        if (!filters.containsKey("tableName")) {
            throw new IllegalArgumentException("Table name is required.");
        }

        String tableName = filters.get("tableName").toString();
        String sortBy = filters.getOrDefault("sortBy", "id").toString();
        String sortOrder = filters.getOrDefault("sortOrder", "ASC").toString().equalsIgnoreCase("DESC") ? "DESC" : "ASC";

        Set<String> controlKeys = Set.of("tableName", "sortBy", "sortOrder", "pageNo", "pageSize");

        List<Object> params = new ArrayList<>();
        StringBuilder whereClause = new StringBuilder(" WHERE 1=1");

        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (controlKeys.contains(key)) continue;

            String condition = null;
            String column;

            if (key.endsWith("Prefix")) {
                column = key.replace("Prefix", "");
                condition = column + " LIKE ?";
                params.add(value + "%");

            } else if (key.endsWith("Suffix")) {
                column = key.replace("Suffix", "");
                condition = column + " LIKE ?";
                params.add("%" + value);

            } else if (key.endsWith("Contains")) {
                column = key.replace("Contains", "");
                condition = column + " LIKE ?";
                params.add("%" + value + "%");

            } else if (key.endsWith("Gt")) {
                column = key.replace("Gt", "");
                condition = column + " > ?";
                params.add(value);

            } else if (key.endsWith("Gte")) {
                column = key.replace("Gte", "");
                condition = column + " >= ?";
                params.add(value);

            } else if (key.endsWith("Lt")) {
                column = key.replace("Lt", "");
                condition = column + " < ?";
                params.add(value);

            } else if (key.endsWith("Lte")) {
                column = key.replace("Lte", "");
                condition = column + " <= ?";
                params.add(value);

            } else if (key.endsWith("Between")) {
                column = key.replace("Between", "");
                if (value instanceof List<?> valList && valList.size() == 2) {
                    condition = column + " BETWEEN ? AND ?";
                    params.add(valList.get(0));
                    params.add(valList.get(1));
                }

            } else if (key.endsWith("In")) {
                column = key.replace("In", "");
                if (value instanceof List<?> valList && !valList.isEmpty()) {
                    String placeholders = String.join(",", Collections.nCopies(valList.size(), "?"));
                    condition = column + " IN (" + placeholders + ")";
                    params.addAll(valList);
                }

            } else {
                column = key;
                condition = column + " = ?";
                params.add(value);
            }

            if (condition != null) {
                whereClause.append(" AND ").append(condition);
            }
        }

        // Build final query
        StringBuilder query = new StringBuilder("SELECT * FROM ").append(tableName)
                .append(whereClause)
                .append(" ORDER BY ").append(sortBy).append(" ").append(sortOrder);

        StringBuilder countQuery = new StringBuilder("SELECT COUNT(*) FROM ").append(tableName).append(whereClause);

        // Get total count
        int totalRecords = jdbcTemplate.queryForObject(countQuery.toString(), params.toArray(), Integer.class);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
        pageNo = Math.max(1, Math.min(pageNo, totalPages));
        int offset = (pageNo - 1) * pageSize;

        // Add pagination
        if (isSqlServer2012OrNewer()) {
            query.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
            params.add(offset);
            params.add(pageSize);
        } else {
            // Fallback for SQL Server 2008 and older
            StringBuilder legacyQuery = new StringBuilder("SELECT * FROM (SELECT *, ROW_NUMBER() OVER (ORDER BY ")
                    .append(sortBy).append(" ").append(sortOrder)
                    .append(") AS RowNum FROM ").append(tableName)
                    .append(whereClause)
                    .append(") AS Temp WHERE RowNum BETWEEN ? AND ?");
            params.add(offset + 1);
            params.add(offset + pageSize);
            query = legacyQuery;
        }

        List<Map<String, Object>> records = jdbcTemplate.queryForList(query.toString(), params.toArray());

        Map<String, Object> response = new HashMap<>();
        response.put("data", records);
        response.put("pageNo", pageNo);
        response.put("totalPages", totalPages);
        response.put("BOP", pageNo == 1);
        response.put("EOP", pageNo >= totalPages);
        return response;
    }

    private boolean isSqlServer2012OrNewer() {
        try {
            String version = jdbcTemplate.queryForObject("SELECT CAST(SERVERPROPERTY('ProductVersion') AS VARCHAR)", String.class);
            String[] parts = version.split("\\.");
            return Integer.parseInt(parts[0]) >= 11;
        } catch (Exception e) {
            return false;
        }
    }
}
//==============Payload==================
//{
//        "tableName": "AbhayEmployees",
//        "namePrefix": "A",
//        "departmentIn": ["IT", "HR", "Marketing"],
//        "sortBy": "id",
//        "sortOrder": "DESC",
//        "pageNo": 1,
//        "pageSize": 10
//}


