import Vue from "vue";
import Vuex from "vuex";

Vue.use(Vuex);

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
	state: {
		settings: {
			activeFile: null,
			exportFormat: "ICS"
		},
		data: {
			scheduleBlocks: [{ sbID: "empty" }],
			fiscalYears: [
				{ fyID: "empty", scheduleBlocks: ["empty"], startDate: 0, endDate: 0 }
			],
			currentScheduleBlock: "empty",
			currentFiscalYear: "empty"
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
				sbID: new Date().getTime(),
				startDate: scheduleBlockData.startDate.getTime(),
				endDate: scheduleBlockData.endDate.getTime(),
				...scheduleBlockData
			};
			state.data.scheduleBlocks[0] = scheduleBlockToCreate;
			state.data.currentScheduleBlock = scheduleBlockToCreate.sbID;

			const fiscalYearToCreate = {
				fyID: new Date().getTime(),
				startDate: fiscalYearData.startDate.getTime(),
				endDate: fiscalYearData.endDate.getTime(),
				scheduleBlocks: [scheduleBlockToCreate.sbID],
				...fiscalYearData
			};
			state.data.fiscalYears[0] = fiscalYearToCreate;
			state.data.currentFiscalYear = fiscalYearToCreate.fyID;
		},
		/**
		 * Insert a schedule block into the current state.
		 * @param {object} state
		 * @param {object} scheduleBlockData
		 */
		addScheduleBlock(state, scheduleBlockData) {
			// first check that this schedule block does not conflict with existing ones

			// create schedule block
			const scheduleBlockToCreate = {
				sbID: new Date().getTime(),
				startDate: scheduleBlockData.startDate.getTime(),
				endDate: scheduleBlockData.endDate.getTime(),
				...scheduleBlockData
			};
			state.data.scheduleBlocks.push(scheduleBlockToCreate);
			state.data.currentScheduleBlock = scheduleBlockToCreate.sbID;
			// then update the fiscal year that the block should belong to
			// const fiscalYearToUpdate = null;
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
				sbID: new Date().getTime(),
				startDate: scheduleBlockData.startDate.getTime(),
				endDate: scheduleBlockData.endDate.getTime(),
				...scheduleBlockData
			};
			state.data.scheduleBlocks.push(scheduleBlockToCreate);
			state.data.currentScheduleBlock = scheduleBlockToCreate.sbID;

			// first check that this fiscal year does not conflict with existing ones

			// create fiscal year
			const fiscalYearToCreate = {
				fyID: new Date().getTime(),
				startDate: fiscalYearData.startDate.getTime(),
				endDate: fiscalYearData.endDate.getTime(),
				scheduleBlocks: [scheduleBlockToCreate.sbID],
				...fiscalYearData
			};
			state.data.fiscalYears.push(fiscalYearToCreate);
			state.data.currentFiscalYear = fiscalYearToCreate.fyID;
		}
	},
	actions: {},
	getters: {
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
				el => el.sbID === sbID
			);
			if (filteredScheduleBlocks.length < 1) {
				return null;
				// Error: stored current schedule block not found in stored schedule blocks
			} else if (filteredScheduleBlocks.length > 1) {
				return null;
				// Error: stored current schedule block matches more than one stored schedule block
			} else {
				return filteredScheduleBlocks[0];
			}
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
		}
	}
});
