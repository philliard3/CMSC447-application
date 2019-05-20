// The Moment and ICS packages are universal in their Node and web implementations.
const moment = require("moment");
const ics = require("ics");

const DEFAULT_CONSTRAINT_PRIORITY = 1;
const moonlighterAliases = ["moonlighter", "moon", "moonlight", "ml"];
const ICU_ALIASES = ["ER", "ICU", "Emergency Room", "JHU ICU", "Urgent Care"];
const NURSERY_ALIASES = ["Nursery", "Nursing Room"];

/**
 * inputs: {schedule, employees}
 * schedule is an object in the form provided as output by the Java Optaplanner process
 * employees is an array of objects in the form stored in the Vuex store
 * **/
async function generateICal(
	{ schedule, employees },
	directory = __dirname,
	fs = require("fs"),
	path = require("path")
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

		const wd = path.join(directory, "calendars");

		if (!fs.existsSync(wd)) {
			fs.mkdirSync(wd);
		}

		// write it to the ics file
		const filename = path.join(wd, "calendar", `${new Date().getTime()}.ics`);
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

	while (current.isSameOrBefore(end)) {
		days.push(current);
		current = moment(current).add(1, "days");
	}

	const daysOfWeek = [
		"Sunday",
		"Monday",
		"Tuesday",
		"Wednesday",
		"Thursday",
		"Friday",
		"Saturday",
		"Sunday"
	];

	const shifts = [];
	let shiftTypes = [shiftData.name];
	// add other tags
	if (shiftData.tags && shiftData.tags.length > 0) {
		shiftTypes = shiftTypes.concat(shiftData.tags);
	}

	days
		.filter(day => shiftData.startDays.includes(daysOfWeek[day.day()]))
		.forEach(day => {
			const [hours, minutes] = shiftData.startTime.split(":");
			const startTime = day.hours(Number(hours)).minutes(Number(minutes));
			const shiftObj = {
				shiftTypes,
				location: shiftData.location,
				startTime: startTime.format("MM/DD/YYYY HH:mm"),
				endTime: startTime
					.add(shiftData.duration, "minutes")
					.format("MM/DD/YYYY HH:mm")
			};
			shiftData.roles.forEach(roleRestriction => {
				for (let i = 0; i < roleRestriction.max; i++) {
					shifts.push({
						mandatory: i < roleRestriction.min,
						permittedRoles: roleRestriction.permittedRoles,
						...shiftObj
					});
				}
			});
		});

	return shifts;
}

/**
 * Transforms state data for employees into Optaplanner input for employees
 **/
function createSchedulingEmployee(employeeData, locations, startDate, endDate) {
	const id = employeeData.employeeID;

	const roles = employeeData.roles.map(role => role.roleID);

	const daysOfWeek = [
		"Monday",
		"Tuesday",
		"Wednesday",
		"Thursday",
		"Friday",
		"Saturday",
		"Sunday"
	];

	const constraints = [];
	/** all employees should have a 24 hour break between shifts  **/
	const dayLongShiftGapRequired = true;
	if (dayLongShiftGapRequired) {
		constraints.push({
			id: (employeeData.employeeID + new Date().getTime()) % Math.pow(2, 31),
			priority: DEFAULT_CONSTRAINT_PRIORITY,
			isHard: false,
			type: "PersonalSequenceConstraint",
			parameters: {
				sequence: {
					patterns: [
						{
							daysOfWeek: daysOfWeek.map((_d, idx) => idx + 1),
							requiredTypes: [],
							bannedTypes: [],
							startTimeRanges: [["00:00", "23:59"]],
							endTimeRanges: [["00:00", "23:59"]],
							dateTimeRanges: [
								[
									moment(startDate)
										.startOf("day")
										.format("MM/DD/YY HH:mm"),
									// 23:59 on the last day
									moment(endDate)
										.endOf("day")
										.format("MM/DD/YY HH:mm")
								]
							],
							allowedLocations: locations
						}
					],
					timeDiffs: [[1440, Math.pow(2, 31)]],
					timeDiffMode: "start"
				},
				minMatches: 0,
				maxMatches: Math.pow(2, 31),
				repeatStart: moment(startDate).format("MM/DD/YYYY"),
				repeatEnd: moment(endDate).format("MM/DD/YYYY"),
				repeatPeriod: {
					days: 0,
					months: moment(endDate).diff(moment(startDate), "months") + 1
				}
			}
		});
	}

	if (employeeData.requirements) {
		employeeData.requirements.forEach(requirement => {
			const period =
				requirement.per === "2 weeks"
					? { days: 14, months: 0 }
					: requirement.per === "month"
					? { days: 0, months: 1 }
					: requirement.per === "quarter"
					? { days: 0, months: 3 }
					: requirement.per === "year"
					? { days: 0, months: 12 }
					: { days: 7, months: 0 }; // requirement.per === "week" or anything else

			switch (requirement.type) {
				case "Shifts":
					constraints.push({
						id: requirement.requirementID,
						priority: DEFAULT_CONSTRAINT_PRIORITY,
						isHard: requirement.flexible === false,
						type: "PersonalSequenceConstraint",
						parameters: {
							sequence: {
								patterns: [
									{
										// all days of the week
										daysOfWeek: daysOfWeek.map((_val, idx) => idx + 1),
										// any type of shift
										requiredTypes: [],
										bannedTypes: [],
										// start and end at any time
										startTimeRanges: [["00:00", "23:59"]],
										endTimeRanges: [["00:00", "23:59"]],
										// start to end of the period of interest
										dateTimeRanges: [
											[
												moment(startDate)
													.startOf("day")
													.format("MM/DD/YY HH:mm"),
												// 23:59 on the last day
												moment(endDate)
													.endOf("day")
													.format("MM/DD/YY HH:mm")
											]
										],
										// shifts may be 4 to 12 hours
										lengthRanges: [[4 * 60, 12 * 60]],
										allowedLocations: locations
									}
								],
								// Shifts must start at least 24 hours apart from one another,
								// and can only be up to 2^31 (max int, basically infinity) apart
								timeDiffs: [[1440, Math.pow(2, 31)]],
								timeDiffMode: "start"
							},
							minMatches: requirement.amount,
							maxMatches: requirement.amount,
							repeatStart: moment(startDate).format("MM/DD/YYYY"),
							repeatEnd: moment(endDate).format("MM/DD/YYYY"),
							repeatPeriod: period
						}
					});
					break;

				case "Hours":
					// hours required constraint
					constraints.push({
						id: requirement.requirementID,
						priority: DEFAULT_CONSTRAINT_PRIORITY,
						isHard: requirement.flexible === false,
						type: "HoursRequiredConstraint",
						parameters: {
							amount: requirement.amount,
							period: period
						}
					});
					break;

				case "Attending weeks":
					constraints.push({
						id: requirement.requirementID,
						priority: DEFAULT_CONSTRAINT_PRIORITY,
						isHard: requirement.flexible === false,
						type: "PersonalSequenceConstraint",
						parameters: {
							sequence: {
								patterns: [
									{
										daysOfWeek: [1],
										startTimeRanges: [["7:00", "7:00"]],
										endTimeRanges: [["15:00", "15:00"]],
										lengthRanges: [[4 * 60, 12 * 60]],
										allowedLocations: ICU_ALIASES
									},
									{
										daysOfWeek: [2],
										startTimeRanges: [["7:00", "7:00"]],
										endTimeRanges: [["15:00", "15:00"]],
										lengthRanges: [[4 * 60, 12 * 60]],
										allowedLocations: ICU_ALIASES
									},
									{
										daysOfWeek: [3],
										startTimeRanges: [["7:00", "7:00"]],
										endTimeRanges: [["15:00", "15:00"]],
										lengthRanges: [[4 * 60, 12 * 60]],
										allowedLocations: ICU_ALIASES
									},
									{
										daysOfWeek: [4],
										startTimeRanges: [["7:00", "7:00"]],
										endTimeRanges: [["15:00", "15:00"]],
										lengthRanges: [[4 * 60, 12 * 60]],
										allowedLocations: ICU_ALIASES
									},
									{
										daysOfWeek: [5],
										startTimeRanges: [["7:00", "7:00"]],
										endTimeRanges: [["15:00", "15:00"]],
										lengthRanges: [[4 * 60, 12 * 60]],
										allowedLocations: ICU_ALIASES
									}
								],
								timeDiffs: [[1440, 1440]],
								timeDiffMode: "start"
							},
							minMatches: requirement.amount,
							maxMatches: requirement.amount,
							repeatStart: moment(startDate).format("MM/DD/YYYY"),
							repeatEnd: moment(endDate).format("MM/DD/YYYY"),
							repeatPeriod: period
						}
					});
					break;

				case "Nursing weeks":
					constraints.push({
						id: requirement.requirementID,
						priority: DEFAULT_CONSTRAINT_PRIORITY,
						isHard: requirement.flexible === false,
						type: "PersonalSequenceConstraint",
						parameters: {
							sequence: {
								patterns: [
									{
										daysOfWeek: [1],
										startTimeRanges: [["7:00", "7:00"]],
										endTimeRanges: [["15:00", "15:00"]],
										lengthRanges: [[4 * 60, 12 * 60]],
										allowedLocations: NURSERY_ALIASES
									},
									{
										daysOfWeek: [2],
										startTimeRanges: [["7:00", "7:00"]],
										endTimeRanges: [["15:00", "15:00"]],
										lengthRanges: [[4 * 60, 12 * 60]],
										allowedLocations: NURSERY_ALIASES
									},
									{
										daysOfWeek: [3],
										startTimeRanges: [["7:00", "7:00"]],
										endTimeRanges: [["15:00", "15:00"]],
										lengthRanges: [[4 * 60, 12 * 60]],
										allowedLocations: NURSERY_ALIASES
									},
									{
										daysOfWeek: [4],
										startTimeRanges: [["7:00", "7:00"]],
										endTimeRanges: [["15:00", "15:00"]],
										lengthRanges: [[4 * 60, 12 * 60]],
										allowedLocations: NURSERY_ALIASES
									},
									{
										daysOfWeek: [5],
										startTimeRanges: [["7:00", "7:00"]],
										endTimeRanges: [["15:00", "15:00"]],
										lengthRanges: [[4 * 60, 12 * 60]],
										allowedLocations: NURSERY_ALIASES
									}
								],
								timeDiffs: [[1440, 1440]],
								timeDiffMode: "start"
							},
							minMatches: requirement.amount,
							maxMatches: requirement.amount,
							repeatStart: moment(startDate).format("MM/DD/YYYY"),
							repeatEnd: moment(endDate).format("MM/DD/YYYY"),
							repeatPeriod: period
						}
					});
					break;

				default:
					break;
			}
		});
	}

	if (employeeData.shifts) {
		employeeData.shifts.forEach(shift => {
			shift.startDays.forEach(day => {
				constraints.push({
					id: Number(
						`${new Date().getTime() % Math.pow(2, 29)}${daysOfWeek.indexOf(
							day
						)}`
					),
					priority: DEFAULT_CONSTRAINT_PRIORITY,
					isHard: false,
					type: "Preference",
					parameters: {
						// positive if it's checked, negative if not
						// we can work on a neutral option at a later point in time
						isPositive: shift.selected.includes(day),
						pattern: {
							daysOfWeek: [daysOfWeek.indexOf(day) + 1],
							requiredTypes: [shift.name],
							bannedTypes: [],
							startTimeRanges: [[shift.startTime, shift.startTime]],
							endTimeRanges: [
								[
									moment(shift.startTime, "HH:mm")
										.add(shift.duration, "minutes")
										.format("HH:mm"),
									moment(shift.startTime, "HH:mm")
										.add(shift.duration, "minutes")
										.format("HH:mm")
								]
							],
							dateTimeRanges: [
								[
									moment(startDate).format("MM/DD/YY HH:mm"),
									// 23:59 on the last day
									moment(endDate)
										.add("1439", "minutes")
										.format("MM/DD/YY HH:mm")
								]
							],
							lengthRanges: [[shift.duration, shift.duration]],
							allowedLocations: [shift.location]
						}
					}
				});
			});
		});
	}

	if (employeeData.preferredDays) {
		employeeData.preferredDays.forEach(day => {
			// don't include neutral dates
			if (
				(day.isPositivePreference !== undefined &&
					day.isPositivePreference !== null) ||
				(day.preferred !== undefined && day.preferred !== null)
			) {
				let dateTimeRanges;
				let dateMoment = moment(day.date ? day.date : day.startDate).startOf(
					"day"
				);
				if (day.repeating) {
					dateMoment.year(moment(startDate).year());
					dateTimeRanges = [
						// a year before
						[
							moment(dateMoment)
								.subtract(1, "year")
								.startOf("day"),
							// 23:59 on the last day
							moment(dateMoment)
								.subtract(1, "year")
								.endOf("day")
								.format("MM/DD/YY HH:mm")
						],
						// the date during that year
						[
							dateMoment.startOf("day").format("MM/DD/YY HH:mm"),
							dateMoment.endOf("day").format("MM/DD/YY HH:mm")
						],
						// a year after
						[
							moment(dateMoment)
								.add(1, "year")
								.startOf("day"),
							// 23:59 on the last day
							moment(dateMoment)
								.add(1, "year")
								.endOf("day")
								.format("MM/DD/YY HH:mm")
						]
					];
				} else {
					// only the explicit date
					dateTimeRanges = [
						[
							dateMoment.format("MM/DD/YY HH:mm"),
							dateMoment.format("MM/DD/YY HH:mm")
						]
					];
				}

				constraints.push({
					id: Number(
						`${new Date().getTime() % Math.pow(2, 16)}${day.dayID %
							Math.pow(2, 16)}`
					),
					priority: day.priority || DEFAULT_CONSTRAINT_PRIORITY,
					isHard: false,
					type: "Preference",
					parameters: {
						isPositive:
							day.isPositivePreference || day.preferred ? true : false,
						pattern: {
							daysOfWeek: daysOfWeek.map((_val, idx) => idx + 1),
							requiredTypes: [],
							bannedTypes: [],
							startTimeRanges: [["00:00", "23:59"]],
							endTimeRanges: [["00:00", "23:59"]],
							dateTimeRanges: dateTimeRanges,
							lengthRanges: [[4 * 60, 12 * 60]],
							allowedLocations: day.location ? [day.location] : locations // location is probably undefined
						}
					}
				});
			}
		});
	}
	return { id, constraints, roles };
}

/**
 * Transforms state data for roles into Optaplanner input for roles
 **/
function createSchedulingRole(roleData, locations, startDate, endDate) {
	const constraints = [];
	const daysOfWeek = [
		"Monday",
		"Tuesday",
		"Wednesday",
		"Thursday",
		"Friday",
		"Saturday",
		"Sunday"
	];

	if (roleData.requirements) {
		roleData.requirements.forEach(requirement => {
			const period =
				requirement.per === "2 weeks"
					? { days: 14, months: 0 }
					: requirement.per === "month"
					? { days: 0, months: 1 }
					: requirement.per === "quarter"
					? { days: 0, months: 3 }
					: requirement.per === "year"
					? { days: 0, months: 12 }
					: { days: 7, months: 0 }; // requirement.per === "week" or anything else

			switch (requirement.type) {
				case "Shifts":
					constraints.push({
						id: requirement.requirementID,
						priority: DEFAULT_CONSTRAINT_PRIORITY,
						isHard: requirement.flexible === false,
						type: "PersonalSequenceConstraint",
						parameters: {
							sequence: {
								patterns: [
									{
										// all days of the week
										daysOfWeek: daysOfWeek.map((_val, idx) => idx + 1),
										// any type of shift
										requiredTypes: [],
										bannedTypes: [],
										// start and end at any time
										startTimeRanges: [["00:00", "23:59"]],
										endTimeRanges: [["00:00", "23:59"]],
										// start to end of the period of interest
										dateTimeRanges: [
											[
												moment(startDate)
													.startOf("day")
													.format("MM/DD/YY HH:mm"),
												// 23:59 on the last day
												moment(endDate)
													.endOf("day")
													.add("1439", "minutes")
													.format("MM/DD/YY HH:mm")
											]
										],
										// shifts may be 4 to 12 hours
										lengthRanges: [[4 * 60, 12 * 60]],
										allowedLocations: locations
									}
								],
								// Shifts must start at least 24 hours apart from one another,
								// and can only be up to 2^31 (max int, basically infinity) apart
								timeDiffs: [[1440, Math.pow(2, 31)]],
								timeDiffMode: "start"
							},
							minMatches: requirement.amount,
							maxMatches: requirement.amount,
							repeatStart: moment(startDate).format("MM/DD/YYYY"),
							repeatEnd: moment(endDate).format("MM/DD/YYYY"),
							repeatPeriod: period
						}
					});
					break;

				case "Hours":
					// hours required constraint
					constraints.push({
						id: requirement.requirementID,
						priority: DEFAULT_CONSTRAINT_PRIORITY,
						isHard: requirement.flexible === false,
						type: "HoursRequiredConstraint",
						parameters: {
							amount: requirement.amount,
							period: period
						}
					});
					break;

				case "Attending weeks":
					constraints.push({
						id: requirement.requirementID,
						priority: DEFAULT_CONSTRAINT_PRIORITY,
						isHard: requirement.flexible === false,
						type: "PersonalSequenceConstraint",
						parameters: {
							sequence: {
								patterns: [
									{
										daysOfWeek: [1],
										startTimeRanges: [["7:00", "7:00"]],
										endTimeRanges: [["15:00", "15:00"]],
										lengthRanges: [[4 * 60, 12 * 60]],
										allowedLocations: ICU_ALIASES
									},
									{
										daysOfWeek: [2],
										startTimeRanges: [["7:00", "7:00"]],
										endTimeRanges: [["15:00", "15:00"]],
										lengthRanges: [[4 * 60, 12 * 60]],
										allowedLocations: ICU_ALIASES
									},
									{
										daysOfWeek: [3],
										startTimeRanges: [["7:00", "7:00"]],
										endTimeRanges: [["15:00", "15:00"]],
										lengthRanges: [[4 * 60, 12 * 60]],
										allowedLocations: ICU_ALIASES
									},
									{
										daysOfWeek: [4],
										startTimeRanges: [["7:00", "7:00"]],
										endTimeRanges: [["15:00", "15:00"]],
										lengthRanges: [[4 * 60, 12 * 60]],
										allowedLocations: ICU_ALIASES
									},
									{
										daysOfWeek: [5],
										startTimeRanges: [["7:00", "7:00"]],
										endTimeRanges: [["15:00", "15:00"]],
										lengthRanges: [[4 * 60, 12 * 60]],
										allowedLocations: ICU_ALIASES
									}
								],
								timeDiffs: [[1440, 1440]],
								timeDiffMode: "start"
							},
							minMatches: requirement.amount,
							maxMatches: requirement.amount,
							repeatStart: moment(startDate).format("MM/DD/YYYY"),
							repeatEnd: moment(endDate).format("MM/DD/YYYY"),
							repeatPeriod: period
						}
					});
					break;

				case "Nursing weeks":
					constraints.push({
						id: requirement.requirementID,
						priority: DEFAULT_CONSTRAINT_PRIORITY,
						isHard: requirement.flexible === false,
						type: "PersonalSequenceConstraint",
						parameters: {
							sequence: {
								patterns: [
									{
										daysOfWeek: [1],
										startTimeRanges: [["7:00", "7:00"]],
										endTimeRanges: [["15:00", "15:00"]],
										lengthRanges: [[4 * 60, 12 * 60]],
										allowedLocations: NURSERY_ALIASES
									},
									{
										daysOfWeek: [2],
										startTimeRanges: [["7:00", "7:00"]],
										endTimeRanges: [["15:00", "15:00"]],
										lengthRanges: [[4 * 60, 12 * 60]],
										allowedLocations: NURSERY_ALIASES
									},
									{
										daysOfWeek: [3],
										startTimeRanges: [["7:00", "7:00"]],
										endTimeRanges: [["15:00", "15:00"]],
										lengthRanges: [[4 * 60, 12 * 60]],
										allowedLocations: NURSERY_ALIASES
									},
									{
										daysOfWeek: [4],
										startTimeRanges: [["7:00", "7:00"]],
										endTimeRanges: [["15:00", "15:00"]],
										lengthRanges: [[4 * 60, 12 * 60]],
										allowedLocations: NURSERY_ALIASES
									},
									{
										daysOfWeek: [5],
										startTimeRanges: [["7:00", "7:00"]],
										endTimeRanges: [["15:00", "15:00"]],
										lengthRanges: [[4 * 60, 12 * 60]],
										allowedLocations: NURSERY_ALIASES
									}
								],
								timeDiffs: [[1440, 1440]],
								timeDiffMode: "start"
							},
							minMatches: requirement.amount,
							maxMatches: requirement.amount,
							repeatStart: moment(startDate).format("MM/DD/YYYY"),
							repeatEnd: moment(endDate).format("MM/DD/YYYY"),
							repeatPeriod: period
						}
					});
					break;

				default:
					break;
			}
		});
	}

	if (roleData.shifts) {
		roleData.shifts.forEach(shift => {
			shift.startDays.forEach(day => {
				constraints.push({
					id: Number(
						`${new Date().getTime() % Math.pow(2, 29)}${daysOfWeek.indexOf(
							day
						)}`
					),
					priority: DEFAULT_CONSTRAINT_PRIORITY,
					isHard: false,
					type: "Preference",
					parameters: {
						// positive if it's checked, negative if not
						// we can work on a neutral option at a later point in time
						isPositive: shift.selected.includes(day),
						pattern: {
							daysOfWeek: [daysOfWeek.indexOf(day) + 1],
							requiredTypes: [shift.name],
							bannedTypes: [],
							startTimeRanges: [[shift.startTime, shift.startTime]],
							endTimeRanges: [
								[
									moment(shift.startTime, "HH:mm")
										.add(shift.duration, "minutes")
										.format("HH:mm"),
									moment(shift.startTime, "HH:mm")
										.add(shift.duration, "minutes")
										.format("HH:mm")
								]
							],
							dateTimeRanges: [
								[
									moment(startDate)
										.startOf("day")
										.format("MM/DD/YY HH:mm"),
									// 23:59 on the last day
									moment(endDate)
										.endOf("day")
										.format("MM/DD/YY HH:mm")
								]
							],
							lengthRanges: [[shift.duration, shift.duration]],
							allowedLocations: [shift.location]
						}
					}
				});
			});
		});
	}

	return { id: roleData.roleID, constraints };
}

/**
 * Transforms state data for roles into Optaplanner input for roles
 **/
function createSchedulingGlobalConstraint(state) {
	let numberOfConstraints = 1;

	const RolePriorityConstraint = {
		id: numberOfConstraints,
		priority: 1,
		isHard: false,
		type: "RolePriorityConstraint",
		parameters: {
			types: Array.from(
				new Set(
					state.data.scheduleBlocks.reduce(
						(total1, sb) =>
							total1.concat(
								sb.shifts.reduce(
									(total2, shift) =>
										shift.tags
											? [shift.name, ...shift.tags, ...total2]
											: [shift.name, ...total2],
									[]
								)
							),
						[]
					)
				)
			),
			roles: state.data.roles.map(r => r.roleID),
			priorities: state.data.roles.map(r =>
				// negative if it's a moonlighter, 1 if otherwise
				r.isMoonlighter ||
				moonlighterAliases.some(alias => r.name.toLowerCase() === alias)
					? -1
					: 1
			)
		}
	};
	numberOfConstraints++;

	return [RolePriorityConstraint];
}

async function generateSchedule(
	state,
	{ forCurrentFiscalYear, forCurrentScheduleBlock },
	directory = __dirname,
	fs = require("fs"),
	cp = require("child_process"),
	path = require("path")
) {
	let periodOfInterest;
	if (forCurrentFiscalYear) {
		periodOfInterest = state.data.fiscalYears.filter(
			fy => state.data.currentFiscalYear === fy.fyID
		)[0];
	} else if (forCurrentScheduleBlock) {
		periodOfInterest = state.data.scheduleBlocks.filter(
			sb => state.data.currentScheduleBlock.sbID === sb.sbID
		)[0];
	} else {
		throw new Error(
			"forCurrentFiscalYear or forCurrentScheduleBlock are required arguments"
		);
	}

	const shifts = state.data.scheduleBlocks.reduce((total1, sb) => {
		if (
			moment(sb.startDate).isSameOrAfter(moment(periodOfInterest.startDate)) &&
			moment(sb.endDate).isSameOrBefore(moment(periodOfInterest.endDate))
		) {
			return total1.concat(
				sb.shifts.reduce((total2, shift) => {
					const newShifts = createSchedulingShifts(
						shift,
						periodOfInterest.startDate,
						periodOfInterest.endDate
					);
					return total2.concat(newShifts);
				}, [])
			);
		} else {
			return total1;
		}
	}, []);

	const employees = state.data.employees.map(employee =>
		createSchedulingEmployee(
			employee,
			state.data.locations,
			periodOfInterest.startDate,
			periodOfInterest.endDate
		)
	);

	/*
	// dummy employees implementation
	const employees = state.data.employees.map(e => ({
		id: e.employeeID,
		constraints: [],
		roles: e.roles.map(r => r.roleID)
	}));
	*/

	const roles = state.data.roles.map(role =>
		createSchedulingRole(
			role,
			state.data.locations,
			periodOfInterest.startDate,
			periodOfInterest.endDate
		)
	);
	/*
	// dummy roles implementation
	const roles = state.data.roles.map(r => ({
		id: r.roleID,
		constraints: []
	}));
	*/

	// there is currently no scheduleConstraints property for UI state
	const scheduleConstraints = createSchedulingGlobalConstraint(state);
	/*
	// dummy scheduleConstraints
	const scheduleConstraints = [];
	*/

	// provide input data for the scheduling program
	const fileToWrite = `input_constraints${new Date().getTime()}.json`;
	let wd = path.join(directory, "..", "..", "inputs");

	if (!fs.existsSync(wd)) {
		fs.mkdirSync(wd);
	}

	const pathToWrite = path.join(wd, fileToWrite);
	fs.writeFileSync(
		pathToWrite,
		JSON.stringify({
			shifts,
			roles,
			employees,
			scheduleConstraints
		})
	);

	// Run the scheduling program and wait for results
	// It may be necessary to use a .bat batch file instead of directly running the .jar
	// in the case of process.platform === "win32"
	const executable = "roster-1.0.jar";
	// command line execution of the form "c:downloads/create_schedule.jar input_constraints1255556850.json"
	const outputFile = `output_schedule${new Date().getTime()}.json`;
	wd = path.join(directory, "..", "..", "outputs");
	const command = `java -jar "${path.join(
		directory,
		"..",
		"..",
		executable
	)}" "${pathToWrite}" "${path.join(wd, outputFile)}" "${path.join(
		wd,
		"..",
		"nurseRosterSolverConfig.xml"
	)}"`;
	// console.log(command);
	cp.execSync(command);

	// get all files in the schedules folder
	wd = path.join(directory, "..", "..", "schedules");

	if (!fs.existsSync(wd)) {
		fs.mkdirSync(wd);
	}

	const files = fs.readdirSync(wd);

	// get the last file edited
	let correctFile = files.sort((f1, f2) => {
		return (
			new Date(fs.statSync(path.join(wd, f2)).mtime).getTime() -
			new Date(fs.statSync(path.join(wd, f1)).mtime).getTime()
		);
	})[0];

	// return evaluated JSON
	// console.log(path.join(wd, correctFile));
	return require(path.join(wd, correctFile));
}

export {
	generateICal,
	generateSchedule,
	createSchedulingRole,
	createSchedulingShifts,
	createSchedulingEmployee,
	createSchedulingGlobalConstraint
};
