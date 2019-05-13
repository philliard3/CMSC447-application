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
	
	public static Solver<NurseRoster> solve(NurseRoster initialSolution){
		File solverConfig = new File("./src/main/resources\nurseRosterSolverConfig.xml");
		SolverFactory<NurseRoster> solverFactory = SolverFactory.createFromXmlFile(solverConfig);
        Solver<NurseRoster> solver = solverFactory.buildSolver();
		solver.solve(initialSolution);
    	return solver;
	}
		
	
    public static void main( String[] args ) {
        String inFile = "input.json";//args[0];
        String outFile = "output.json";//args[1];
        if (DataLoader.loadFile(inFile)) {
        	NurseRoster randomSolution = new NurseRoster(Input.employees, Input.shifts);
        	Solver<NurseRoster> solver = solve(randomSolution);
        	SolutionWriter.write(outFile, solver.getBestSolution());
        }
        else {
        	System.out.println(new File(inFile).getAbsolutePath());
        }
    }
}
