package com.CMSC447.nurseroster.domain;

import java.util.List;

public class ScheduleSequence extends Sequence{
    List<Integer> employeePattern;
    List<Employee> employeeRequirements;
    List<Role> rolePattern;

    public ScheduleSequence(List<Integer> empPattern, List<Employee> reqs, List<Role> rolePattern){
    	this.employeePattern = empPattern; 
    	this.employeeRequirements = reqs;
    	this.rolePattern  = rolePattern;
    }

    public List<List<ShiftAssignment>> matches(List<ShiftAssignment> shift){
        System.out.println("Implement ScheduleSequence.matches");
        return null;
    }
}
