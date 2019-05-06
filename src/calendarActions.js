const fileSystem = require("fs");
const moment = require("moment");
const ics = require("ics");
const childProcess = require("child_process");

/**
 * inputs: {schedule, employees}
 * schedule is an object in the form provided as output by the Java Optaplanner process
 * employees is an array of objects in the form stored in the Vuex store
 * **/
async function generateICal(
	{ schedule, employees },
	directory = __dirname,
	fs = fileSystem
) {
	// create events from schedule object
	const events = schedule.assignments.map(assignment => {
		// get the corresponding employee
		const employee = employees.filter(
			employee => employee.employeeID === assignment.employee
		)[0];

		// Google will automatically center it on our timezone, so we need to assume a neutral timezone.
		const offset = 0;
		const startTime = moment(
			assignment.shift.startTime,
			"MM/DD/YYYY HH:mm"
		).utcOffset(offset);

		const endTime = moment(
			assignment.shift.endTime,
			"MM/DD/YYYY HH:mm"
		).utcOffset(offset);

		return {
			// title of the form "John Doe @ICU"
			title: `${employee.name} @${assignment.shift.location}`,
			start: [
				startTime.year(),
				startTime.month() + 1,
				startTime.date(),
				startTime.hour(),
				startTime.minute()
			],
			end: [
				endTime.year(),
				endTime.month() + 1,
				endTime.date(),
				endTime.hour(),
				endTime.minute()
			],
			// description of the form "John Doe (Doctor, Full Time) working Morning Shift, Weekdays at ICU"
			description: `${employee.name} (${employee.roles
				.map(role => role.name)
				.join(", ")}) working ${assignment.shift.shiftTypes.join(", ")} at ${
				assignment.shift.location
			}${employee.email ? `\nemployee's email: ${employee.email}` : ""}`,
			categories: assignment.shift.shiftTypes

			/*
            // Attendees currently don't show up on Google Calendar so this has been commented out so it won't cause any trouble
			attendees: employee.email
				? [
						{
							name: employee.name,
							email: employee.email,
							rsvp: true,
							role: "REQ-PARTICIPANT"
						}
				  ]
                : undefined
            */
		};
	});

	try {
		const { err, value } = await ics.createEvents(events);
		if (err) {
			throw err;
		}

		const wd = `${directory}/calendars`;

		if (!fs.existsSync(wd)) {
			fs.mkdirSync(wd);
		}

		// write it to the ics file
		const filename = `${wd}/calendar${new Date().getTime()}.ics`;
		await fs.writeFileSync(filename, value);
		return filename;
	} catch (err) {
		console.error(err);
		return err;
	}
}

/**
 * Transforms state data for general shift structure into individual shifts for Optaplanner input
 **/
function createSchedulingShifts(shiftData, startDate, endDate) {
	let current = moment(startDate);
	const end = moment(endDate);
	const days = [];

	while (current.isBefore(end)) {
		days.push(current);
		current = moment(current.toDate()).add(1, "days");
	}

	const daysOfWeek = [
		"Sunday",
		"Monday",
		"Tuesday",
		"Wednesday",
		"Thursday",
		"Friday",
		"Saturday"
	];

	const shifts = [];
	days
		.filter(day => shiftData.startDays.includes(daysOfWeek[day.day()]))
		.forEach(day => {
			const [hours, minutes] = shiftData.startTime.split(":");
			const startTime = day.hours(Number(hours)).minutes(Number(minutes));
			shiftData.roles.forEach(roleRestriction => {
				const shiftObj = {
					shiftTypes: [
						shiftData.name
						// other tags would go in this array
					],
					permittedRoles: roleRestriction.permittedRoles,
					location: shiftData.location,
					startTime: startTime.format("MM/DD/YYYY HH:mm"),
					endTime: startTime
						.add(shiftData.duration, "minutes")
						.format("MM/DD/YYYY HH:mm")
				};
				for (let i = 0; i < roleRestriction.amount; i++) {
					shifts.push({ ...shiftObj });
				}
			});
		});

	return shifts;
}

/**
 * Transforms state data for employees into Optaplanner input for employees
 **/
function createSchedulingEmployee(employeeData) {
	const id = employeeData.employeeID;

	const roles = employeeData.roles.map(role => role.roleID);

	const constraints = [];

	employeeData.shifts.forEach(shift => {
		shift.startDays.forEach(day => {
			constraints.push({
				day,
				// positive if it's checked, negative if not
				// we can work on a neutral option at a later point in time
				isPositivePreference: shift.selected.includes(day),
				startTime: shift.startTime,
				endTime: moment(shift.startTime, "HH:mm")
					.add(shift.duration, "minutes")
					.format("HH:mm"),
				location: shift.location
			});
		});
	});

	employeeData.preferredDays.forEach(day => {
		// don't include neutral dates
		if (
			day.isPositivePreference !== undefined &&
			day.isPositivePreference !== null
		) {
			constraints.push({
				id: day.dayID,
				isHard: false,
				isSchedule: false,
				type: "DatePreference",
				parameters: {
					isPositivePreference: day.isPositivePreference,
					priority: day.priority,
					startDate: day.date,
					endDate: day.date,
					location: day.location // location is probably undefined
				}
			});
		}
	});

	return { id, constraints, roles };
}

/**
 * Transforms state data for roles into Optaplanner input for roles
 **/
function createSchedulingRole(roleData) {
	const constraints = [];

	roleData.requirements.forEach(requirement => {
		switch (requirement.type) {
			case "hours worked":
				constraints.push({
					id: requirement.requirementID,
					type: "HoursRequired",
					parameters: {
						amount: requirement.amount,
						per: requirement.per
					}
				});
				break;

			case "attending weeks":
				constraints.push({
					id: requirement.requirementID,
					type: "AttendWeeksRequired",
					parameters: {
						amount: requirement.amount,
						per: requirement.per
					}
				});
				break;

			case "nursing weeks":
				constraints.push({
					id: requirement.requirementID,
					type: "NursingWeeksRequired",
					parameters: {
						amount: requirement.amount,
						per: requirement.per
					}
				});
				break;
		}
	});

	roleData.shifts.forEach(shift => {
		shift.startDays.forEach(day => {
			constraints.push({
				day,
				// positive if it's checked, negative if not
				// we can work on a neutral option at a later point in time
				isPositivePreference: shift.selected.includes(day),
				startTime: shift.startTime,
				endTime: moment(shift.startTime, "HH:mm")
					.add(shift.duration, "minutes")
					.format("HH:mm"),
				location: shift.location
			});
		});
	});

	return { id: roleData.roleID, constraints };
}

/**
 * Transforms state data for roles into Optaplanner input for roles
 **/
function createSchedulingGlobalConstraint(constraintData) {
	return constraintData;
}

async function generateSchedule(
	state,
	{ forCurrentFiscalYear, forCurrentScheduleBlock },
	directory = __dirname,
	fs = fileSystem,
	cp = childProcess
) {
	let periodOfInterest;
	if (forCurrentFiscalYear) {
		periodOfInterest = state.data.fiscalYears.map(
			fy => state.data.currentFiscalYear === fy.fyID
		);
	} else if (forCurrentScheduleBlock) {
		periodOfInterest = state.data.scheduleBlocks.map(
			sb => state.data.currentScheduleBlock.sbID === sb.sbID
		);
	} else {
		throw new Error(
			"forCurrentFiscalYear or forCurrentScheduleBlock are required arguments"
		);
	}

	const shifts = state.data.shifts.reduce((total, shift) => {
		const newShifts = createSchedulingShifts(
			shift,
			periodOfInterest.startTime,
			periodOfInterest.endTime
		);
		return total.concat(newShifts);
	}, []);

	const employees = state.data.employees.map(employee =>
		createSchedulingEmployee(employee)
	);

	const roles = state.data.role.map(role => createSchedulingRole(role));

	// there is currently no scheduleConstraints property for UI state
	const scheduleConstraints = state.data.scheduleConstraints.map(constraint =>
		createSchedulingGlobalConstraint(constraint)
	);

	// provide input data for the scheduling program
	const fileToWrite = `input_constraints${new Date().getTime()}.json`;
	let wd = `${directory}/inputs`;

	if (!fs.existsSync(wd)) {
		fs.mkdirSync(wd);
	}

	fs.writeFileSync(`${wd}/${fileToWrite}`, {
		shifts,
		roles,
		employees,
		scheduleConstraints
	});

	// Run the scheduling program and wait for results
	// It may be necessary to use a .bat batch file instead of directly running the .jar
	// in the case of process.platform === "win32"
	const executable = "create_schedule.jar";
	// command line execution of the form "c:downloads/create_schedule.jar input_constraints1255556850.json"
	cp.execSync(`${directory}/${executable} ${fileToWrite}`);

	// get all files in the schedules folder
	wd = `${directory}/schedules/`;

	if (!fs.existsSync(wd)) {
		fs.mkdirSync(wd);
	}

	const files = fs.readdirSync(wd);

	// get the last file edited
	let correctFile = files.sort((f1, f2) => {
		return (
			new Date(fs.statSync(`${wd}/${f2}`).mtime).getTime() -
			new Date(fs.statSync(`${wd}/${f1}`).mtime).getTime()
		);
	})[0];
	// return evaluated JSON
	return require(`${wd}/${correctFile}`);
}

export {
	generateICal,
	generateSchedule,
	createSchedulingRole,
	createSchedulingShifts,
	createSchedulingEmployee,
	createSchedulingGlobalConstraint
};
