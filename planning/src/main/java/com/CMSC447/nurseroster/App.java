package com.CMSC447.nurseroster;

import java.io.File;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;

import com.CMSC447.nurseroster.domain.NurseRoster;
import com.CMSC447.nurseroster.fileio.DataLoader;
import com.CMSC447.nurseroster.fileio.Input;
import com.CMSC447.nurseroster.fileio.SolutionWriter;
/**
 * Hello world!
 *
 */
public class App {
	
	public static Solver<NurseRoster> solve(NurseRoster initialSolution, String nurseRosterXML){
		File solverConfig = new File(nurseRosterXML);
		SolverFactory<NurseRoster> solverFactory = SolverFactory.createFromXmlFile(solverConfig);
        Solver<NurseRoster> solver = solverFactory.buildSolver();
		solver.solve(initialSolution);
    	return solver;
	}
		
	
    public static void main( String[] args ) {
        String inFile = args[0];
        String outFile = args[1];
        String nurseRosterXML = args[2];
        if (DataLoader.loadFile(inFile)) {
        	NurseRoster randomSolution = new NurseRoster(Input.employees, Input.shifts, Input.scheduleConstraints);
        	SolutionWriter.write("basicOutput.json", randomSolution);
        	Solver<NurseRoster> solver = solve(randomSolution, nurseRosterXML);
        	SolutionWriter.write(outFile, solver.getBestSolution());
        }
        else {
        	System.out.println(new File(inFile).getAbsolutePath());
        }
    }
}
