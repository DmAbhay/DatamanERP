package dataman.erp.pagging.controller;

import dataman.erp.pagging.service.DynamicQueryService;
import dataman.erp.pagging.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DynamicQueryService dynamicQueryService;

    @PostMapping("/filter")
    public Map<String, Object> getFilteredEmployees(
            @RequestBody Map<String, Object> filters,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        return employeeService.getEmployees(filters, pageNo, pageSize);
    }

    @PostMapping("/filter-records")
    public Map<String, Object> getFiltered(
            @RequestBody Map<String, Object> filters,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        return dynamicQueryService.getRecords(filters, pageNo, pageSize);
    }

    @PostMapping("/filter-recordss")
    public Map<String, Object> filterOnBaseQuery(
            @RequestBody Map<String, Object> filters,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        return dynamicQueryService.getRecords(filters, pageNo, pageSize);
    }
}

