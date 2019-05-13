

package com.CMSC447.nurseroster.solver.move;


import com.CMSC447.nurseroster.domain.Employee;
import com.CMSC447.nurseroster.domain.NurseRoster;
import com.CMSC447.nurseroster.domain.ShiftAssignment;
import org.optaplanner.core.impl.score.director.ScoreDirector;

public class NurseRosterMoveHelper {

    public static void moveEmployee(ScoreDirector<NurseRoster> scoreDirector, ShiftAssignment shiftAssignment, Employee toEmployee) {
        scoreDirector.beforeVariableChanged(shiftAssignment, "employee");
        shiftAssignment.setEmployee(toEmployee);
        scoreDirector.afterVariableChanged(shiftAssignment, "employee");
    }

    private NurseRosterMoveHelper() {
    }

}
