import { writeFileSync, readdirSync, statSync } from "fs";
const moment = require("moment");
const ics = require("ics");
const { execFileSync } = require("child_process");

/**
 * inputs: {schedule, employees}
 * schedule is an object in the form
 * **/
async function generateICal({ schedule, employees }) {
	const events = schedule.assignments.map(assignment => {
		// get the corresponding employee
		const employee = employees.filter(
			employee => employee.employeeID === assignment.employee
		)[0];

		const startTime = moment(assignment.startTime);
		return {
			// title of the form "John Doe @ICU"
			title: `${employee.name} @${assignment.location}`,
			start: [startTime.toArray()],
			// description of the form "John Doe (Doctor, Full Time) working Morning Shift, Weekdays at ICU"
			description: `${employee.name} (${employee.roles
				.map(role => role.name)
				.join(", ")}) working ${assignment.shiftTypes.join(", ")} at ${
				assignment.location
			}`,
			duration: {
				minutes: startTime.diff(moment(assignment.endTime), "minutes")
			},
			categories: assignment.shiftTypes,
			attendees: { name: employee.name, email: employee.email }
		};
	});

	try {
		// write it to the ics file
		writeFileSync(
			`${__dirname}/calendar${new Date().getTime()}.ics`,
			await ics.createEvents(events)
		);
	} catch (err) {
		console.error(err);
	}
}

async function generateSchedule(data) {
	const fileToWrite = `input_constraints${new Date().getTime()}.json`;
	// provide
	writeFileSync(`${__dirname}/inputs/${fileToWrite}.json`, data);
	const inputTime = moment(new Date());
	execFileSync(`create_schedule.jar`, [fileToWrite]);
	const files = readdirSync(`${__dirname}/schedules/`);
	let correctFile = files.filter(filename => {
		if (moment(new Date(statSync(filename).mtime)).isAfter(inputTime)) {
			return true;
		}
		return false;
	})[0];

	/*
    execFile(`create_schedule.jar`, [fileToWrite]);
	// loop through until a new calendar has been created
	let correctFile = null;
	let cont = true;
	while (cont) {
		// get the list of files
		const files = readdirSync(`${__dirname}/schedules/`);
		cont = files.some(filename => {
			if (moment(new Date(statSync(filename).mtime)).isAfter(inputTime)) {
				correctFile = filename;
				return true;
			}
			return false;
		});
    }
    */

	return require(`${__dirname}/schedules/${correctFile}`);
}

export default { generateICal, generateSchedule };
