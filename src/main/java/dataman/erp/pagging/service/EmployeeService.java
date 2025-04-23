//package dataman.erp.pagging.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Service;
//
//import java.sql.Date;
//import java.util.*;
//
//@Service
//public class EmployeeService {
//
//    @Autowired
//    @Qualifier("tirangaCompanyJdbcTemplate")
//    private JdbcTemplate jdbcTemplate;
//
//    public Map<String, Object> getEmployees(Map<String, String> filters, int pageNo, int pageSize) {
//        StringBuilder query = new StringBuilder("SELECT * FROM AbhayEmployees WHERE 1=1");
//        StringBuilder countQuery = new StringBuilder("SELECT COUNT(*) FROM AbhayEmployees WHERE 1=1");
//        List<Object> params = new ArrayList<>();
//
//        // Apply Dynamic Filters
//        if (filters.containsKey("namePrefix")) {
//            query.append(" AND name LIKE ?");
//            countQuery.append(" AND name LIKE ?");
//            params.add(filters.get("namePrefix") + "%");
//        }
//
//        if (filters.containsKey("department")) {
//            query.append(" AND department = ?");
//            countQuery.append(" AND department = ?");
//            params.add(filters.get("department"));
//        }
//
//        if (filters.containsKey("minSalary")) {
//            query.append(" AND salary >= ?");
//            countQuery.append(" AND salary >= ?");
//            params.add(Double.parseDouble(filters.get("minSalary")));
//        }
//
//        if (filters.containsKey("maxSalary")) {
//            query.append(" AND salary <= ?");
//            countQuery.append(" AND salary <= ?");
//            params.add(Double.parseDouble(filters.get("maxSalary")));
//        }
//
//        if (filters.containsKey("joiningAfter")) {
//            query.append(" AND joining_date >= ?");
//            countQuery.append(" AND joining_date >= ?");
//            params.add(Date.valueOf(filters.get("joiningAfter")));
//        }
//
//        if (filters.containsKey("joiningBefore")) {
//            query.append(" AND joining_date <= ?");
//            countQuery.append(" AND joining_date <= ?");
//            params.add(Date.valueOf(filters.get("joiningBefore")));
//        }
//
//        // Handling Group By Condition
//        if ("true".equalsIgnoreCase(filters.get("groupByName"))) {
//            query.append(" GROUP BY name");
//            countQuery.append(" GROUP BY name");
//        }
//
//        // Handling Sorting (Validate column name)
//        List<String> allowedSortColumns = Arrays.asList("name", "department", "salary", "joining_date");
//        if (filters.containsKey("sortBy") && allowedSortColumns.contains(filters.get("sortBy"))) {
//            query.append(" ORDER BY ").append(filters.get("sortBy"));
//            if ("desc".equalsIgnoreCase(filters.getOrDefault("sortOrder", "asc"))) {
//                query.append(" DESC");
//            }
//        } else {
//            query.append(" ORDER BY id"); // Default sorting by id
//        }
//
//        // Count total records
//        int totalRecords = jdbcTemplate.queryForObject(countQuery.toString(), params.toArray(), Integer.class);
//        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
//
//        // Ensure pageNo is within valid range
//        pageNo = Math.max(1, Math.min(pageNo, totalPages));
//        int offset = (pageNo - 1) * pageSize;
//
//        // Apply Pagination - **MS SQL Server uses OFFSET FETCH**
//        query.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
//        params.add(offset); // Offset
//        params.add(pageSize); // Fetch Next
//
//        List<Map<String, Object>> employees = jdbcTemplate.queryForList(query.toString(), params.toArray());
//
//        // Prepare response
//        Map<String, Object> response = new HashMap<>();
//        response.put("data", employees);
//        response.put("pageNo", pageNo);
//        response.put("totalPages", totalPages);
//        response.put("BOP", pageNo == 1);
//        response.put("EOP", pageNo >= totalPages);
//
//        return response;
//    }
//}



package dataman.erp.pagging.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.*;

@Service
public class EmployeeService {

    @Autowired
    @Qualifier("tirangaCompanyJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> getEmployees(Map<String, Object> filters, int pageNo, int pageSize) {
        StringBuilder query = new StringBuilder("SELECT * FROM AbhayEmployees WHERE 1=1");
        StringBuilder countQuery = new StringBuilder("SELECT COUNT(*) FROM AbhayEmployees WHERE 1=1");
        List<Object> params = new ArrayList<>();

        // Apply Dynamic Filters
        if (filters.containsKey("namePrefix")) {
            query.append(" AND name LIKE ?");
            countQuery.append(" AND name LIKE ?");
            params.add(filters.get("namePrefix") + "%");
        }

        if (filters.containsKey("departments")) {
            List<String> departments = (List<String>) filters.get("departments");
            if (!departments.isEmpty()) {
                String inClause = String.join(",", Collections.nCopies(departments.size(), "?"));
                query.append(" AND department IN (" + inClause + ")");
                countQuery.append(" AND department IN (" + inClause + ")");
                params.addAll(departments);
            }
        }

        if (filters.containsKey("minSalary")) {
            query.append(" AND salary >= ?");
            countQuery.append(" AND salary >= ?");
            params.add(Double.parseDouble(filters.get("minSalary").toString()));
        }

        if (filters.containsKey("maxSalary")) {
            query.append(" AND salary <= ?");
            countQuery.append(" AND salary <= ?");
            params.add(Double.parseDouble(filters.get("maxSalary").toString()));
        }

        if (filters.containsKey("joiningAfter")) {
            query.append(" AND joining_date >= ?");
            countQuery.append(" AND joining_date >= ?");
            params.add(Date.valueOf(filters.get("joiningAfter").toString()));
        }

        if (filters.containsKey("joiningBefore")) {
            query.append(" AND joining_date <= ?");
            countQuery.append(" AND joining_date <= ?");
            params.add(Date.valueOf(filters.get("joiningBefore").toString()));
        }

        // Handling Sorting (Validate column name)
        List<String> allowedSortColumns = Arrays.asList("name", "department", "salary", "joining_date");
        if (filters.containsKey("sortBy") && allowedSortColumns.contains(filters.get("sortBy").toString())) {
            query.append(" ORDER BY ").append(filters.get("sortBy"));
            if ("desc".equalsIgnoreCase(filters.getOrDefault("sortOrder", "asc").toString())) {
                query.append(" DESC");
            }
        } else {
            query.append(" ORDER BY id"); // Default sorting by id
        }

        // Count total records
        int totalRecords = jdbcTemplate.queryForObject(countQuery.toString(), params.toArray(), Integer.class);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

        // Ensure pageNo is within valid range
        pageNo = Math.max(1, Math.min(pageNo, totalPages));
        int offset = (pageNo - 1) * pageSize;

        // Apply Pagination - **MS SQL Server uses OFFSET FETCH**
        query.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add(offset); // Offset
        params.add(pageSize); // Fetch Next

        List<Map<String, Object>> employees = jdbcTemplate.queryForList(query.toString(), params.toArray());

        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("data", employees);
        response.put("pageNo", pageNo);
        response.put("totalPages", totalPages);
        response.put("BOP", pageNo == 1); // Beginning of Pagination
        response.put("EOP", pageNo >= totalPages); // End of Pagination

        return response;
    }
}
