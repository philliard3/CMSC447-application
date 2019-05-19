/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.CMSC447.nurseroster.domain;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Shift implements Comparable<Shift>{

    public final LocalDateTime startTime;
    public final LocalDateTime endTime;
    public final ArrayList<String> types;
    public final String location;
    public final boolean mandatory;
    public final ArrayList<Role> roles;

    public Shift(LocalDateTime startTime, LocalDateTime endTime, ArrayList<String> shiftTypes, String location, boolean mandatory, ArrayList<Role> roles) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.types = shiftTypes;
        this.location = location;
        this.mandatory = mandatory; 
        this.roles = roles;
    }
    
    public double hours() {
    	Duration duration = Duration.between(startTime, endTime);
    	long seconds = duration.getSeconds();
    	double minutes = seconds / 60.0;
    	double hours = minutes / 60.0;
    	
    	return hours;
    }
    
    public int compareTo(Shift other) {
    	return startTime.getNano() - other.startTime.getNano();
    }
    
    public boolean equals(Shift other) {
    	boolean sameTime = this.startTime.equals(other.startTime) && this.endTime.equals(other.endTime);
    	boolean sameTypes = this.types.containsAll(other.types) && other.types.containsAll(this.types);
    	boolean sameLocation = this.location == other.location;
    	boolean sameMandatory = this.mandatory == other.mandatory;
    	boolean sameRoles = this.roles.containsAll(other.roles) && other.roles.containsAll(this.roles);
    	return sameTime && sameTypes && sameLocation && sameMandatory && sameRoles;
    }
}
