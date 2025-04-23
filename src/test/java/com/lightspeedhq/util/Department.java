package com.lightspeedhq.util;

/**
 * Class representing a department with a circular reference.
 */
public class Department {
    private String name;
    private Department relatedDepartment;

    public Department() {
    }

    public Department(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Department getRelatedDepartment() {
        return relatedDepartment;
    }

    public void setRelatedDepartment(Department relatedDepartment) {
        this.relatedDepartment = relatedDepartment;
    }
}
