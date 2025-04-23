package dataman.erp.pagging.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class InMemoryFilterService {

    @Autowired
    @Qualifier("tirangaCompanyJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> getFilteredRecords(Map<String, Object> filters, int pageNo, int pageSize) {
        // Step 1: Run your base query (you can customize this)
        String baseQuery = "SELECT * FROM AbhayEmployees"; // You can pass this as a parameter if needed
        List<Map<String, Object>> records = jdbcTemplate.queryForList(baseQuery);

        // Step 2: Apply filters in-memory
        Set<String> controlKeys = Set.of("sortBy", "sortOrder", "pageNo", "pageSize");

        List<Map<String, Object>> filtered = records.stream()
                .filter(record -> matchesAllFilters(record, filters, controlKeys))
                .collect(Collectors.toList());

        // Step 3: Sorting
        String sortBy = filters.getOrDefault("sortBy", "id").toString();
        String sortOrder = filters.getOrDefault("sortOrder", "ASC").toString();

        Comparator<Map<String, Object>> comparator = Comparator.comparing(record ->
                (Comparable) record.getOrDefault(sortBy, "")
        );

        if ("DESC".equalsIgnoreCase(sortOrder)) {
            comparator = comparator.reversed();
        }

        filtered.sort(comparator);

        // Step 4: Pagination
        int total = filtered.size();
        int totalPages = (int) Math.ceil((double) total / pageSize);
        pageNo = Math.max(1, Math.min(pageNo, totalPages));
        int fromIndex = Math.max(0, (pageNo - 1) * pageSize);
        int toIndex = Math.min(fromIndex + pageSize, total);

        List<Map<String, Object>> pagedData = filtered.subList(fromIndex, toIndex);

        // Step 5: Response
        Map<String, Object> response = new HashMap<>();
        response.put("data", pagedData);
        response.put("pageNo", pageNo);
        response.put("totalPages", totalPages);
        response.put("BOP", pageNo == 1);
        response.put("EOP", pageNo >= totalPages);

        return response;
    }

    private boolean matchesAllFilters(Map<String, Object> record, Map<String, Object> filters, Set<String> controlKeys) {
        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String key = entry.getKey();
            Object filterValue = entry.getValue();

            if (controlKeys.contains(key)) continue;

            String column;
            Object columnValue;
            Predicate<Object> predicate = null;

            if (key.endsWith("Prefix")) {
                column = key.replace("Prefix", "");
                columnValue = record.get(column);
                predicate = val -> val != null && val.toString().startsWith(filterValue.toString());

            } else if (key.endsWith("Suffix")) {
                column = key.replace("Suffix", "");
                columnValue = record.get(column);
                predicate = val -> val != null && val.toString().endsWith(filterValue.toString());

            } else if (key.endsWith("Contains")) {
                column = key.replace("Contains", "");
                columnValue = record.get(column);
                predicate = val -> val != null && val.toString().contains(filterValue.toString());

            } else if (key.endsWith("Gt")) {
                column = key.replace("Gt", "");
                columnValue = record.get(column);
                predicate = val -> compare(val, filterValue) > 0;

            } else if (key.endsWith("Gte")) {
                column = key.replace("Gte", "");
                columnValue = record.get(column);
                predicate = val -> compare(val, filterValue) >= 0;

            } else if (key.endsWith("Lt")) {
                column = key.replace("Lt", "");
                columnValue = record.get(column);
                predicate = val -> compare(val, filterValue) < 0;

            } else if (key.endsWith("Lte")) {
                column = key.replace("Lte", "");
                columnValue = record.get(column);
                predicate = val -> compare(val, filterValue) <= 0;

            } else if (key.endsWith("Between")) {
                column = key.replace("Between", "");
                columnValue = record.get(column);
                if (filterValue instanceof List<?> list && list.size() == 2) {
                    Object lower = list.get(0);
                    Object upper = list.get(1);
                    predicate = val -> compare(val, lower) >= 0 && compare(val, upper) <= 0;
                }

            } else if (key.endsWith("In")) {
                column = key.replace("In", "");
                columnValue = record.get(column);
                if (filterValue instanceof List<?> list) {
                    predicate = list::contains;
                }

            } else {
                column = key;
                columnValue = record.get(column);
                predicate = val -> val != null && val.equals(filterValue);
            }

            if (predicate == null || !predicate.test(columnValue)) {
                return false;
            }
        }

        return true;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private int compare(Object o1, Object o2) {
        if (o1 == null || o2 == null) return -1;
        try {
            return ((Comparable) o1).compareTo(o2);
        } catch (Exception e) {
            return -1;
        }
    }
}
