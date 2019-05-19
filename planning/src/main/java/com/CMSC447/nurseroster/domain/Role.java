package com.CMSC447.nurseroster.domain;

import java.util.ArrayList;
import java.util.List;

import com.CMSC447.nurseroster.domain.constraint.PersonalConstraint;

public class Role {
    public ArrayList<PersonalConstraint> constraints;
    public int id;
    
    
    //constructor
    public Role(int id, ArrayList<PersonalConstraint> constraints){
    	this.id = id;
    	this.constraints = constraints;
    }
    
    public boolean equals(Role other) {
    	return this.id == other.id;
    }
    
    public int hashCode() {
    	return id;
    }
}
