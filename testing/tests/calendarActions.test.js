import {
	// generateICal,
	// generateSchedule,
	// createSchedulingRole,
	createSchedulingShifts
	// createSchedulingEmployee,
	// createSchedulingGlobalConstraint
} from "../../src/calendarActions";
import moment from "moment";

test("Shifts contain the correct types", () => {
	const shift = {
		name: "Morning Weekday",
		startDays: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"],
		startTime: "07:00",
		location: "Nursery",
		duration: 480,
		roles: [
			{
				permittedRoles: [1, 4, 5],
				min: 1,
				max: 1
			},
			{
				permittedRoles: [1, 2, 3],
				min: 1,
				max: 2
			}
		]
	};
	const shiftOutput = createSchedulingShifts(
		shift,
		moment("2019-04-28", "YYYY-MM-DD")
			.toDate()
			.getTime(),
		moment("2019-05-04", "YYYY-MM-DD")
			.toDate()
			.getTime()
	);
	// console.log(shiftOutput);
	expect(shiftOutput).toBeInstanceOf(Array);
	for (let s of shiftOutput) {
		expect(s).toBeInstanceOf(Object);
		expect(s.shiftTypes).toBeInstanceOf(Array);
		expect(s.permittedRoles).toBeInstanceOf(Array);
	}
});

test("Shifts contain the correct values", () => {
	const shift = {
		name: "Morning Weekday",
		startDays: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"],
		startTime: "07:00",
		location: "Nursery",
		duration: 480,
		roles: [
			{
				permittedRoles: [1, 4, 5],
				min: 1,
				max: 1
			},
			{
				permittedRoles: [1, 2, 3],
				min: 1,
				max: 2
			}
		]
	};
	const shiftOutput = createSchedulingShifts(
		shift,
		moment("2019-04-28", "YYYY-MM-DD")
			.toDate()
			.getTime(),
		moment("2019-05-04", "YYYY-MM-DD")
			.toDate()
			.getTime()
	);
	expect(shiftOutput).toBeInstanceOf(Array);
	for (let s of shiftOutput) {
		expect(s.shiftTypes.length).toBe(1);
		expect(s.shiftTypes).toEqual(expect.arrayContaining(["Morning Weekday"]));
		expect(s.permittedRoles.length).toBeGreaterThan(0);
		expect(s.location).toBe("Nursery");
		expect(s.endTime).toEqual(expect.stringMatching(/(0[45]\/).*\w+/));
		expect(s.mandatory).toBeDefined();
		expect(s.mandatory).not.toBeNull();
	}
});
