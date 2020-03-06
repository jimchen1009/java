package com.ximuyi.demo.morphia;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.ximuyi.demo.mongodb.MongoDBConfig;
import com.ximuyi.demo.mongodb.MongoDbMain;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MorphiaMain {

    static final Logger logger = LoggerFactory.getLogger(MongoDbMain.class);

    private static final MongoDBLegacyManager dbManager = new MongoDBLegacyManager(MongoDBConfig.REPLICA_ADDRESS);

    public static void main(String[] args) {
        Datastore dataStore = dbManager.createDefaultDataStore(Employee.class.getPackage().getName());
        dataStore.ensureIndexes();
        List<Employee> employees = dataStore.createQuery(Employee.class).asList();
        Map<String, Employee> employeeMap = employees.stream().collect(Collectors.toMap(Employee::getName, value -> value));
        String[] names = new String[]{"Jim", "Okay", "None"};
        String managerName = names[0];
        List<Employee> createdList = new ArrayList<>();
        for (String name : names) {
            if (employeeMap.containsKey(name)) {
                continue;
            }
            Employee employee = new Employee(name, 10000.0);
            createdList.add(employee);
            if (name.equals(managerName)){
                employee.incSalary(30000.0);
            }
            else {
                Employee Jim = Objects.requireNonNull(employeeMap.get(managerName));
                employee.getDirectReports().add(Jim);
                employee.setManager(Jim);
            }
            createdList.add(employee);
            employeeMap.put(name, employee);
        }
        dataStore.save(createdList);
        dataStore.save(employeeMap.values());   // 可以重复保存

        /**
         * Querying
         * dataStore.createQuery(Employee.class) 是有状态的，不能复用
         */
        List<Employee> employees0 = dataStore.createQuery(Employee.class).asList();
        logEmployeeList(employees0, "query.asList");
        List<Employee> employees1 = dataStore.createQuery(Employee.class).field("salary").greaterThanOrEq(40000.0).asList();
        logEmployeeList(employees1, "query.greaterThanOrEq");
        List<Employee>  employees2 = dataStore.createQuery(Employee.class).filter("salary <", 40000.0).asList();
        logEmployeeList(employees2, "query.filter");

        /**
         * Updates
         */
        Query<Employee> underPaidQuery  = dataStore.createQuery(Employee.class).filter("salary <=", Integer.MAX_VALUE);;
        final UpdateOperations<Employee> updateOperations = dataStore.createUpdateOperations(Employee.class)
                .inc("salary", 1); // This corresponds to the $inc operator
        final UpdateResults results = dataStore.update(underPaidQuery, updateOperations);
        logger.info("{}", results);

        /**
         * Removes
         */
//        final Query<Employee> overPaidQuery = dataStore.createQuery(Employee.class).filter("salary >", 0);
//        dataStore.delete(overPaidQuery);
    }

    private static void logEmployeeList(List<Employee> employees, String message){
        logger.info("{}, employees:{}", message, employees);
    }
}
