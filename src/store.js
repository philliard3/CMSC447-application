import Vue from "vue";
import Vuex from "vuex";

Vue.use(Vuex);

// set this as an environment variable in later iterations
const USE_TEST_DATA = true;

const emptyState = {
	settings: {
		activeFile: null,
		exportFormat: "ICS"
	},
	data: {
		scheduleBlocks: [],
		fiscalYears: [],
		currentScheduleBlock: null,
		currentFiscalYear: null,
		employees: [],
		roles: [],
		locations: []
	},
	generatedSchedule: USE_TEST_DATA
		? require("./SamplePlanningOutput.json")
		: undefined
};

export default new Vuex.Store({
	/**
	 * Initialize State
	 */
	state: USE_TEST_DATA
		? {
				generatedSchedule: require("./SamplePlanningOutput.json"),
				...require("./SampleState.json")
		  }
		: emptyState,

	mutations: {
		clearStore(state) {
			state.data = { ...emptyState.data };
			state.settings = { ...emptyState.settings };
			state.generateSchedule = undefined;
		},
		/**
		 * Inserts into the current application state a past state, usually loaded from a file.
		 */
		insertLoadedState(state, { loadedState, sourceFile }) {
			state.data = loadedState.data || state.data;

			state.settings = loadedState.settings || state.settings;
			state.settings.activeFile = sourceFile;

			state.generatedSchedule =
				loadedState.generatedSchedule || state.generatedSchedule;
		},
		/**
		 * Insert an initial schedule block and fiscal year to start the schedule.
		 */
		initialize(state, { scheduleBlockData, fiscalYearData }) {
			const scheduleBlockToCreate = {
				name: scheduleBlockData.name,
				sbID: new Date().getTime(),
				startDate: scheduleBlockData.startDate.getTime(),
				endDate: scheduleBlockData.endDate.getTime(),
				...scheduleBlockData
			};
			state.data.scheduleBlocks[0] = scheduleBlockToCreate;
			state.data.currentScheduleBlock = scheduleBlockToCreate.sbID;

			const fiscalYearToCreate = {
				name: fiscalYearData.name,
				fyID: new Date().getTime(),
				startDate: fiscalYearData.startDate.getTime(),
				endDate: fiscalYearData.endDate.getTime(),
				scheduleBlocks: [scheduleBlockToCreate.sbID],
				...fiscalYearData
			};
			state.data.fiscalYears[0] = fiscalYearToCreate;
			state.data.currentFiscalYear = fiscalYearToCreate.fyID;
			return {
				currentFiscalYear: { ...state.data.currentFiscalYear },
				currentScheduleBlock: { ...state.data.currentScheduleBlock }
			};
		},
		/**
		 * Insert a schedule block into the current state.
		 */
		addScheduleBlock(state, { scheduleBlockData, fiscalYear }) {
			// first check that this schedule block does not conflict with existing ones
			if (
				state.data.scheduleBlocks.filter(
					sb => scheduleBlockData.sbID === sb.sbID
				).length > 0
			) {
				return;
			}

			// create schedule block
			const scheduleBlockToCreate = {
				name: scheduleBlockData.name,
				sbID: new Date().getTime(),
				startDate: scheduleBlockData.startDate.getTime(),
				endDate: scheduleBlockData.endDate.getTime(),
				shifts: [],
				...scheduleBlockData
			};
			state.data.scheduleBlocks.push(scheduleBlockToCreate);
			state.data.currentScheduleBlock = scheduleBlockToCreate.sbID;

			const fiscalYearToUpdate = fiscalYear || state.data.currentFiscalYear;
			// then update the fiscal year that the block should belong to
			state.data.fiscalYears
				.filter(
					fy => fy.fyID === fiscalYearToUpdate || fy.name === fiscalYearToUpdate
				)[0]
				.scheduleBlocks.push(scheduleBlockToCreate.sbID);
		},
		/**
		 * Removes the schedule block with the matching sbID
		 **/
		removeScheduleBlock(state, { scheduleBlockData }) {
			const matchingBlocks = state.data.scheduleBlocks.map(
				sb => sb.sbID === scheduleBlockData.sbID
			);
			if (matchingBlocks.includes(true)) {
				// remove the block
				const newBlocks = [...state.data.scheduleBlocks];
				newBlocks.splice(matchingBlocks.indexOf(true), 1);
				state.data.scheduleBlocks = newBlocks;

				// Remove the schedule block from any fiscal years that reference it.
				state.data.fiscalYears.forEach(fy => {
					if (fy.scheduleBlocks.includes(scheduleBlockData.sbID)) {
						const newBlocks = [...fy.scheduleBlocks];
						newBlocks.splice(
							fy.scheduleBlocks.indexOf(scheduleBlockData.sbID),
							1
						);

						fy.scheduleBlocks = newBlocks;
					}
				});

				// address the case that the removed block was the current schedule block
				if (state.data.currentScheduleBlocks === scheduleBlockData.sbID) {
					if (state.data.scheduleBlocks.length > 0) {
						state.data.currentScheduleBlock = state.data.scheduleBlocks[0].sbID;
					} else {
						state.data.currentScheduleBlock = null;
					}
				}
			}
		},

		setCurrentScheduleBlock(state, { scheduleBlockData, fiscalYearData }) {
			if (fiscalYearData) {
				const filteredFiscalYears = state.data.fiscalYears.filter(
					fy =>
						fy.fyID === fiscalYearData.fyID || fy.name === fiscalYearData.name
				);
				if (filteredFiscalYears.length === 1) {
					const filteredBlocks = state.data.scheduleBlocks.filter(
						sb =>
							filteredFiscalYears[0].scheduleBlocks.includes(sb.sbID) &&
							(sb.sbID === scheduleBlockData.sbID ||
								sb.name === scheduleBlockData.name)
					);
					if (filteredBlocks.length === 1) {
						state.data.currentScheduleBlock = filteredBlocks[0].sbID;
					}
				}
			} else {
				const filteredBlocks = state.data.scheduleBlocks.filter(
					sb => sb.sbID === scheduleBlockData.sbID
				);
				if (filteredBlocks.length === 1) {
					state.data.currentScheduleBlock = filteredBlocks[0].sbID;
				}
			}
		},

		/**
		 * Insert a fiscal year into the current state.
		 */
		addFiscalYear(state, { scheduleBlockData, fiscalYearData }) {
			if (
				state.data.fiscalYears.filter(fy => fiscalYearData.fyID === fy.fyID)
					.length > 0
			) {
				return;
			}

			// create schedule block
			const scheduleBlockToCreate = {
				name: scheduleBlockData.name,
				sbID: new Date().getTime(),
				startDate: scheduleBlockData.startDate.getTime(),
				endDate: scheduleBlockData.endDate.getTime(),
				shifts: [],
				...scheduleBlockData
			};
			state.data.scheduleBlocks.push(scheduleBlockToCreate);
			state.data.currentScheduleBlock = scheduleBlockToCreate.sbID;

			// first check that this fiscal year does not conflict with existing ones

			// create fiscal year
			const fiscalYearToCreate = {
				fyID: fiscalYearData.name || new Date().getTime(),
				startDate: fiscalYearData.startDate.getTime(),
				endDate: fiscalYearData.endDate.getTime(),
				scheduleBlocks: [scheduleBlockToCreate.sbID],
				...fiscalYearData
			};
			state.data.fiscalYears.push(fiscalYearToCreate);
			state.data.currentFiscalYear = fiscalYearToCreate.fyID;
		},

		removeFiscalYear(state, { fiscalYearData }) {
			const matchingYears = state.data.fiscalYears.map(
				fy => fy.fyID === fiscalYearData.fyID
			);
			if (matchingYears.includes(true)) {
				const newYears = [...state.data.fiscalYears].splice(
					matchingYears.indexOf(true),
					1
				);

				state.data.fiscalYears = newYears;

				// address the case that the removed year was the current fiscal year
				if (state.data.currentFiscalYear === fiscalYearData.fyID) {
					if (state.data.fiscalYears.length > 0) {
						state.data.currentFiscalYear = state.data.fiscalYears[0].fyID;
					} else {
						state.data.currentFiscalYear = null;
					}
				}
			}
		},

		setCurrentFiscalYear(state, { fiscalYearData }) {
			const filteredFiscalYears = state.data.fiscalYears.filter(
				fy => fy.fyID === fiscalYearData.fyID || fy.name === fiscalYearData.name
			);
			if (filteredFiscalYears.length === 1) {
				state.data.currentFiscalYear = filteredFiscalYears[0].fyID;
			}
		},

		/** **/
		addRole(state, roleData) {
			if (!roleData.roleID) {
				return;
			}
			if (state.data.roles.some(role => role.roleID === roleData.roleID)) {
				return;
			}
			state.data.roles.push({ ...roleData });
		},

		removeRole(state, { roleData }) {
			const matchingRoles = state.data.roles.map(
				role => role.roleID === roleData.roleID
			);
			if (matchingRoles.includes(true)) {
				const newRoles = [...state.data.roles];
				newRoles.splice(matchingRoles.indexOf(true), 1);
				state.data.roles = newRoles;

				// Remove the role from any employees that reference it.
				state.data.employees.forEach(employee => {
					employee.roles = employee.roles.filter(
						role => role.roleID !== roleData.roleID
					);
				});

				// Remove the role from any constraints that reference it
			}
		},

		/** **/
		addShift(state, { shiftData, scheduleBlockData }) {
			if (!shiftData.shiftID) {
				return;
			}
			// disallow repeat shiftID values
			if (
				state.data.scheduleBlocks.some(
					sb =>
						// disallow shifts with the same shiftID
						sb.shifts.some(shift => shift.shiftID === shiftData.shiftID) ||
						// disallow shifts with the same name in the same schedule block
						(!!scheduleBlockData &&
							scheduleBlockData.sbID === sb.sbID &&
							sb.shifts.some(shift => shift.name === shiftData.name))
				)
			) {
				return;
			}

			if (state.data.currentScheduleBlock === null) {
				return;
			}

			const scheduleBlockIDToUpdate = scheduleBlockData
				? scheduleBlockData.sbID
				: state.data.currentScheduleBlock;

			const scheduleBlockToUpdate = state.data.scheduleBlocks.filter(
				sb => sb.sbID === scheduleBlockIDToUpdate
			)[0];

			scheduleBlockToUpdate.shifts.push({ ...shiftData });
		},

		removeShift(state, { shiftData, scheduleBlock }) {
			const filteredBlocks = state.data.scheduleBlocks.filter(
				sb => scheduleBlock.sbID === sb.sbID
			);
			if (filteredBlocks.length > 0) {
				const scheduleBlockOfInterest = filteredBlocks[0];
				const matchingShifts = scheduleBlockOfInterest.shifts.map(
					shift => shift.shiftID === shiftData.shiftID
				);
				if (matchingShifts.includes(true)) {
					const newShifts = [...scheduleBlockOfInterest.shifts];
					newShifts.splice(matchingShifts.indexOf(true), 1);
					scheduleBlockOfInterest.shifts = newShifts;
				}
			}
		},

		/** **/
		addEmployee(state, employeeData) {
			if (!employeeData.employeeID) {
				return;
			}
			// disallow repeat employeeID values
			if (
				state.data.employees.some(
					employee => employee.employeeID === employeeData.employeeID
				)
			) {
				return;
			}
			state.data.employees.push({ ...employeeData });
		},

		removeEmployee(state, { employeeData }) {
			// make sure the employee exists
			const matchingEmployees = state.data.employees.map(
				employee => employee.employeeID === employeeData.employeeID
			);
			if (matchingEmployees.includes(true)) {
				// remove the employee
				const newEmployees = [...state.data.employees];
				newEmployees.splice(matchingEmployees.indexOf(true), 1);
				state.data.employees = newEmployees;
			}
		},

		addLocation(state, location) {
			if (!state.data.locations.includes(location)) {
				state.data.locations.push(location);
			}
		},

		removeLocation(state, location) {
			const locations = [...state.data.locations];
			if (locations.includes(location)) {
				locations.splice(locations.indexOf(location), 1);
				state.data.locations = locations;
			}
		},

		/** schedule gets replaced every time the Java process is called **/
		replaceSchedule(state, schedule) {
			state.generatedSchedule = schedule;
		}
	},

	actions: {
		async generateSchedule(state) {
			const isElectron = !(process.title === "browser");
			if (isElectron) {
				// There are two feasible locations that the executables could be stored, both of which can be reached programmatically
				// location 1
				const distDirectory = process.execPath.split(/[\\/]electron\.exe/)[0];
				// location 2
				// const distDirectory = require("electron").remote.app.getAppPath()

				// establish connection between background (Electron) and render (Vue) processes
				const remote = require("electron").remote;

				const fs = remote.require("fs");
				const cp = remote.require("child_process");
				const schedule = await require("./calendarActions.js").generateSchedule(
					state,
					{ forCurrentFiscalYear: true, forCurrentScheduleBlock: false },
					distDirectory,
					fs,
					cp
				);
				this.commit("replaceSchedule", schedule);
			}
		}
	},

	getters: {
		fullState(state) {
			return { ...state };
		},

		currentFiscalYear(state) {
			const fyID = state.data.currentFiscalYear;
			if (state.data.fiscalYears.length === 0) {
				if (state.data.currentFiscalYear !== null) {
					console.error(
						"FiscalYears are empty but the current fiscal year is not null"
					);
				}
				return null;
			}

			const filteredFiscalYears = state.data.fiscalYears.filter(
				fy => fy.fyID === fyID
			);

			if (filteredFiscalYears.length < 1) {
				// Error: stored current fiscal year doesn't match any stored fiscal year
				return null;
			} else if (filteredFiscalYears.length > 1) {
				// Error: stored current fiscal year matches more than one stored fiscal year
				return null;
			} else {
				return filteredFiscalYears[0];
			}
		},

		/**
		 * Returns the stored schedule block that matches the stored current block.
		 */
		currentScheduleBlock(state) {
			const sbID = state.data.currentScheduleBlock;
			if (state.data.scheduleBlocks.length === 0) {
				if (state.data.currentScheduleBlock !== null) {
					console.error(
						"ScheduleBlocks are empty but the current schedule block is not null."
					);
				}
				return null;
			}
			const filteredScheduleBlocks = state.data.scheduleBlocks.filter(
				sb => sb.sbID === sbID
			);
			if (filteredScheduleBlocks.length < 1) {
				// Error: stored current schedule block not found in stored schedule blocks
				return null;
			} else if (filteredScheduleBlocks.length > 1) {
				// Error: stored current schedule block matches more than one stored schedule block
				return null;
			} else {
				return filteredScheduleBlocks[0];
			}
		},

		employees(state) {
			return state.data.employees.map(employee => ({ ...employee }));
		},

		fiscalYears(state) {
			const currentFiscalYear = state.data.currentFiscalYear;
			const years = state.data.fiscalYears;

			const newYears = years.map(fy => ({
				current: fy.fyID === currentFiscalYear,
				...fy
			}));

			return newYears;
		},

		scheduleBlocks(state) {
			const currentScheduleBlock = state.data.currentScheduleBlock;
			const scheduleBlocks = state.data.scheduleBlocks;

			const newScheduleBLocks = scheduleBlocks.map(sb => ({
				current: sb.sbID === currentScheduleBlock,
				...sb
			}));

			return newScheduleBLocks;
		},

		fiscalYearExists(state) {
			return fyID =>
				state.data.fiscalYears.filter(fy => fyID === fy.fyID).length > 0;
		},

		fiscalYearExistsWithName(state) {
			return name =>
				state.data.fiscalYears.filter(fy => name === fy.name).length > 0;
		},

		scheduleBlockExists(state) {
			return sbID =>
				state.data.scheduleBlocks.filter(sb => sbID === sb.sbID).length > 0;
		},

		scheduleBlockExistsWithName(state) {
			return (name, fyName) => {
				const filteredYears = state.data.fiscalYears.filter(
					fy => fy.name === fyName
				);
				if (filteredYears.length === 1) {
					return (
						state.data.scheduleBlocks.filter(
							sb =>
								filteredYears[0].scheduleBlocks.includes(sb.sbID) &&
								sb.name === name
						).length > 0
					);
				}
				return null;
			};
		},

		roleExists(state) {
			return roleID => state.data.roles.filter(r => r.roleID === roleID) > 0;
		},

		roles(state) {
			return state.data.roles.map(role => ({ ...role }));
		},

		shiftExists(state) {
			return (shiftData, scheduleBlockData) =>
				state.data.scheduleBlocks.some(
					sb =>
						// disallow shifts with the same shiftID
						sb.shifts.some(shift => shift.shiftID === shiftData.shiftID) ||
						// disallow shifts with the same name in the same schedule block
						(!!scheduleBlockData &&
							scheduleBlockData.sbID === sb.sbID &&
							sb.shifts.some(shift => shift.name === shiftData.name))
				);
		},

		locations(state) {
			return [...state.data.locations];
		},

		generatedSchedule(state) {
			if (state.generatedSchedule) {
				return { ...state.generatedSchedule };
			}
		},

		settings(state) {
			return { ...state.settings };
		}
	}
});
