import Vue from "vue";
import Vuex from "vuex";
import moment from "moment";

Vue.use(Vuex);

// set this as an environment variable in later iterations
const IS_TEST = true;
const initial = "sample" || "empty";

const createShifts = (shift, startDate, endDate) => {
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
				startTime: startTime.format("mm/dd/yy hh:mm"),
				endTime: startTime
					.add(shift.duration, "minutes")
					.format("mm/dd/yy hh:mm")
			};
		});

	return shifts;
};

export default new Vuex.Store({
	/**
	 * @namespace
	 *  @property {object} state
	 * @property {object} state.settings
	 * @property {string} [state.settings.activeFile]
	 * @property {string} state.settings.exportFormat
	 * @property {object} state.data
	 * @property {array} state.data.scheduleBlocks
	 * @property {array} state.data.fiscalYears
	 * @property {any} state.data.currentScheduleBlock
	 * @property {any} state.data.currentFiscalYear
	 */
	state: IS_TEST
		? require("./SampleState.json")
		: {
				settings: {
					activeFile: null,
					exportFormat: "ICS"
				},
				data: {
					scheduleBlocks: [
						{
							sbID: initial,
							startDate: 0,
							endDate: 0,
							name: initial
						}
					],
					fiscalYears: [
						{
							fyID: initial,
							name: initial,
							scheduleBlocks: [initial],
							startDate: 0,
							endDate: 0,
							shifts: []
						}
					],
					currentScheduleBlock: initial,
					currentFiscalYear: initial,
					employees: [],
					roles: []
				}
		  },

	mutations: {
		/**
		 * Inserts into the current application state a past state, usually loaded from a file.
		 * @param {object} state
		 * @param {object} loadedState
		 */
		insertLoadedState(state, loadedState) {
			state.data = loadedState.data || state.data;
			state.settings = loadedState.settings || state.settings;
		},
		/**
		 * Insert an initial schedule block and fiscal year to start the schedule.
		 * @param {object} state
		 * @param {object} scheduleBlockData
		 * @param {object} fiscalYearData
		 */
		initialize(state, { scheduleBlockData, fiscalYearData }) {
			const scheduleBlockToCreate = {
				sbID: scheduleBlockData.name || new Date().getTime(),
				startDate: scheduleBlockData.startDate.getTime(),
				endDate: scheduleBlockData.endDate.getTime(),
				...scheduleBlockData
			};
			state.data.scheduleBlocks[0] = scheduleBlockToCreate;
			state.data.currentScheduleBlock = scheduleBlockToCreate.sbID;

			const fiscalYearToCreate = {
				fyID: fiscalYearData.name || new Date().getTime(),
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
		 * @param {object} state
		 * @param {object} scheduleBlockData
		 */
		addScheduleBlock(state, { scheduleBlockData, fiscalYear }) {
			// first check that this schedule block does not conflict with existing ones

			// create schedule block
			const scheduleBlockToCreate = {
				sbID: scheduleBlockData.name || new Date().getTime(),
				startDate: scheduleBlockData.startDate.getTime(),
				endDate: scheduleBlockData.endDate.getTime(),
				...scheduleBlockData
			};
			state.data.scheduleBlocks.push(scheduleBlockToCreate);
			state.data.currentScheduleBlock = scheduleBlockToCreate.sbID;

			const fiscalYearToUpdate = fiscalYear || state.data.currentFiscalYear;
			// then update the fiscal year that the block should belong to
			state.data.fiscalYears
				.filter(fy => fy.fyID === fiscalYearToUpdate)[0]
				.scheduleBlocks.push(scheduleBlockToCreate.sbID);
		},
		/**
		 * Insert a fiscal year into the current state.
		 * @param {object} state
		 * @param {object} scheduleBlockData
		 * @param {object} fiscalYearData
		 */
		addFiscalYear(state, { scheduleBlockData, fiscalYearData }) {
			// create schedule block
			const scheduleBlockToCreate = {
				sbID: scheduleBlockData.name || new Date().getTime(),
				startDate: scheduleBlockData.startDate.getTime(),
				endDate: scheduleBlockData.endDate.getTime(),
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

		/** **/
		addRole(state, roleData) {
			if (!roleData.roleID) {
				return;
			}
			state.data.roles.push({ ...roleData });
		},

		/** **/
		addShift(state, shiftData) {
			if (!shiftData.shiftID) {
				return;
			}

			if (
				!state.data.currentScheduleBlock ||
				state.data.currentScheduleBlock === "empty"
			) {
				return;
			}

			const currentScheduleBlock = state.data.fiscalYears.filter(
				fy => fy.fyID === state.data.currentScheduleBlock
			)[0];

			currentScheduleBlock.shifts.push({ ...shiftData });
		},

		/** **/
		addEmployee(state, employeeData) {
			if (!employeeData.employeeID) {
				return;
			}
			state.data.employees.push({ ...employeeData });
		},

		/** **/
		createSchedule(state, schedule) {
			state.schedule = schedule;
		}
	},

	actions: {
		async generateSchedule(state) {
			this.commit("createSchedule", {
				shifts: state.shifts.map(shift => createShifts(shift, 0, 0))
			});
		}
	},

	getters: {
		currentFiscalYear(state) {
			const fyID = state.data.currentFiscalYear;
			if (state.data.fiscalYears[0].fyID === "empty") {
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
		 * @param {object} state
		 * @returns {object}
		 */
		currentScheduleBlock(state) {
			const sbID = state.data.currentScheduleBlock;
			if (state.data.scheduleBlocks[0].sbID === "empty") {
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

			if (years[0].fyID === "empty") {
				return [];
			}

			const newYears = years.map(fy => ({
				current: fy.fyID === currentFiscalYear,
				...fy
			}));

			return newYears;
		},

		fiscalYearExists(state) {
			return fyID =>
				state.data.fiscalYears.filter(fy => fyID === fy.fyID).length > 0;
		},

		scheduleBlockExists(state) {
			return sbID =>
				state.data.scheduleBlocks.filter(sb => sbID === sb.sbID).length > 0;
		},

		roles(state) {
			return state.data.roles.map(role => ({ ...role }));
		}
	}
});
