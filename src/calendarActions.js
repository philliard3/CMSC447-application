const { writeFileSync, readdirSync, statSync } = require("fs");
const moment = require("moment");
const ics = require("ics");
const { execFileSync } = require("child_process");

/**
 * inputs: {schedule, employees}
 * schedule is an object in the form provided as output by the Java process
 * employees is an array of objects in the form stored in the Vuex store
 * **/
async function generateICal({ schedule, employees }) {
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
		// write it to the ics file
		const filename = `${__dirname}/calendar${new Date().getTime()}.ics`;
		writeFileSync(filename, value);
		return filename;
	} catch (err) {
		console.error(err);
	}
}

function createSchedulingShifts(shift, startDate, endDate) {
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

	const shifts = days
		.filter(day => shift.startDays.includes(daysOfWeek[day.day()]))
		.map(day => {
			const [hours, minutes] = shift.startTime.split(":");
			const startTime = day.hours(Number(hours)).minutes(Number(minutes));
			return {
				shiftTypes: shift.name,
				location: shift.location,
				startTime: startTime.format("MM/DD/YYYY HH:mm"),
				endTime: startTime
					.add(shift.duration, "minutes")
					.format("MM/DD/YYYY HH:mm")
			};
		});

	return shifts;
}

function createSchedulingEmployee(employeeData) {
	return employeeData;
}

function createSchedulingRole(constraintData) {
	return constraintData;
}

function createSchedulingGlobalConstraint(constraintData) {
	return constraintData;
}

async function generateSchedule(
	state,
	{ forCurrentFiscalYear, forCurrentScheduleBlock }
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
	const scheduleConstraints = state.data.scheduleConstraints.map(constraint =>
		createSchedulingGlobalConstraint(constraint)
	);

	// provide input data for the scheduling program
	const fileToWrite = `input_constraints${new Date().getTime()}.json`;
	writeFileSync(`${__dirname}/inputs/${fileToWrite}.json`, {
		shifts,
		roles,
		employees,
		scheduleConstraints
	});

	// Run the scheduling program and wait for results
	// It may be necessary to use a .bat batch file instead of directly running the .jar
	// in the case of process.platform === "win32"
	execFileSync(`create_schedule.jar`, [fileToWrite]);

	// get all files in the schedules folder
	const files = readdirSync(`${__dirname}/schedules/`);

	// get the last file edited
	let correctFile = files.sort((f1, f2) => {
		return (
			new Date(statSync(`${__dirname}/${f2}`).mtime).getTime() -
			new Date(statSync(`${__dirname}/${f1}`).mtime).getTime()
		);
	})[0];
	// return evaluated JSON
	return require(`${__dirname}/schedules/${correctFile}`);
}

module.exports = { generateICal, generateSchedule };
