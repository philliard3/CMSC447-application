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

package com.CMSC447.nurseroster.Solver.move.factory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.CMSC447.nurseroster.Solver.move.ShiftAssignmentSwapMove;
import com.CMSC447.nurseroster.domain.NurseRoster;
import com.CMSC447.nurseroster.domain.ShiftAssignment;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory;


public class ShiftAssignmentSwapMoveFactory implements MoveListFactory<NurseRoster> {


    @Override
    public List<ShiftAssignmentSwapMove> createMoveList(NurseRoster nurseRoster) {
        // Filter out every immovable ShiftAssignment
        List<ShiftAssignment> shiftAssignmentList = new ArrayList<>(
                nurseRoster.getShiftAssignments());
        for (Iterator<ShiftAssignment> it = shiftAssignmentList.iterator(); it.hasNext(); ) {
            ShiftAssignment shiftAssignment = it.next();
        }
        List<ShiftAssignmentSwapMove> moveList = new ArrayList<>();
        for (ListIterator<ShiftAssignment> leftIt = shiftAssignmentList.listIterator(); leftIt.hasNext();) {
            ShiftAssignment leftShiftAssignment = leftIt.next();
            for (ListIterator<ShiftAssignment> rightIt = shiftAssignmentList.listIterator(leftIt.nextIndex()); rightIt.hasNext();) {
                ShiftAssignment rightShiftAssignment = rightIt.next();
                moveList.add(new ShiftAssignmentSwapMove(leftShiftAssignment, rightShiftAssignment));
            }
        }
        return moveList;
    }

}
