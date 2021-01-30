package com.ximuyi.demo.morphia;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.IndexOptions;
import org.mongodb.morphia.annotations.Indexes;
import org.mongodb.morphia.annotations.Property;
import org.mongodb.morphia.annotations.Reference;
import org.mongodb.morphia.utils.IndexType;

@Entity("employees")
@Indexes(
        @Index(fields = {@Field(value = "uniqueId", type = IndexType.ASC), @Field(value = "name", type = IndexType.ASC)}, options = @IndexOptions(unique = true))
)
class Employee {
    @Id                     // The ID can be any type you’d like but is generally something like ObjectId or Long
    private ObjectId id;
    private long uniqueId;
    private String name;
    @Reference
    private Employee manager;
    @Reference
    private List<Employee> directReports;
    @Property("wage")       // f you leave this annotation off, Morphia will use the Java field name as the document field name.
    private Double salary;

    public Employee() {
    }

    public Employee(String name, Double salary) {
        this.name = name;
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public void setManager(Employee manager) {
        this.manager = manager;
    }

    public List<Employee> getDirectReports() {
        if (directReports == null){
            directReports = new ArrayList<>();
        }
        return directReports;
    }

    public void incSalary(Double salary) {
        this.salary += salary;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", manager=" + manager +
                ", directReports=" + directReports +
                ", salary=" + salary +
                '}';
    }
}