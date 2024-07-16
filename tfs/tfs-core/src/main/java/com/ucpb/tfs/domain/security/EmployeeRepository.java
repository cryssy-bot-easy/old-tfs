package com.ucpb.tfs.domain.security;

import java.util.List;
import java.util.Map;
import java.sql.Timestamp;
import java.lang.Object;

public interface EmployeeRepository {

    public void save(Employee employee);

    public Employee getEmployee(UserId userId);

    public List<Map<String,Object>> getEmployeesMatching(String userId, String name);

    public Map getEmployeeById(UserId userId);
}