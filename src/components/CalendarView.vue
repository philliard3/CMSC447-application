<template>
	<div>
		<v-container>
			<v-card>
				<v-card-text>
					<FiscalYearSelector />
					<ScheduleBlockSelector />
				</v-card-text>
			</v-card>
		</v-container>
		<v-container>
			<v-card v-if="electron">
				<v-card-text v-if="tentative">
					<v-progress-linear indeterminate color="primary"></v-progress-linear>
				</v-card-text>
				<v-card-text v-else-if="loading">
					<v-progress-linear
						v-model="progress"
						color="primary"
					></v-progress-linear>
				</v-card-text>
				<v-card-text v-else>
					<v-btn @click="generateSchedule">Generate Schedule</v-btn>
					<v-btn v-if="schedule" @click="downloadICal">Export Calendar</v-btn>
					<a
						id="downloadForm"
						:href="calendarFile || ''"
						ref="downloadCalendarButton"
						download
						>download</a
					>
				</v-card-text>
			</v-card>
		</v-container>
		<v-container>
			<v-card>
				<v-card-text>
					<v-layout class="display-1 font-weight-light">
						<v-flex>{{
							moment(start, "YYYY-MM-DD").format("MMMM YYYY")
						}}</v-flex>
					</v-layout>
					<v-layout>
						<v-flex>
							<v-sheet :height="calendarHeight">
								<!-- now is normally calculated by itself, but to keep the calendar in this date range to view events -->
								<v-calendar
									ref="calendar"
									v-model="start"
									:end="end"
									color="primary"
									type="month"
									v-on:[clickDateEvent]="clickDate"
								>
									<template v-slot:day="{ date }">
										<template
											v-for="(event, index) in eventsMap[date]
												? [...[...eventsMap[date]].splice(0, 2)]
												: []"
										>
											<v-menu
												:key="event.title + String(index)"
												v-model="event.open"
												full-width
												offset-x
											>
												<template v-slot:activator="{ on }">
													<div
														v-if="!event.time"
														v-ripple
														class="my-event"
														v-on="on"
														v-html="event.title"
													></div>
												</template>
												<v-card color="grey lighten-4" min-width="350px" flat>
													<v-toolbar color="primary" dark>
														<!--
                            <v-btn icon>
                              <v-icon>edit</v-icon>
                            </v-btn>
                            -->
													</v-toolbar>
													<v-card-title primary-title>
														<span
															v-html="event.details || 'No info available.'"
														></span>
													</v-card-title>
													<v-card-actions>
														<v-btn flat color="secondary">close</v-btn>
													</v-card-actions>
												</v-card>
											</v-menu>
										</template>
										<div
											v-if="eventsMap[date] && eventsMap[date].length > 2"
											v-ripple
											class="my-event"
											v-html="'...'"
										></div>
									</template>
								</v-calendar>
							</v-sheet>
						</v-flex>
						<v-flex
							v-if="
								selectedDay &&
									eventsMap[selectedDay] &&
									eventsMap[selectedDay].length
							"
							xs12
							sm3
						>
							<v-container>
								<v-layout>
									<v-flex class="display-1 font-weight-light">{{
										`Events for ${moment(selectedDay, "YYYY-MM-DD").format(
											"MMM DD"
										)}`
									}}</v-flex>
								</v-layout>
								<v-layout
									v-for="(event, index) in eventsMap[selectedDay]"
									:key="event.title + String(index)"
								>
									<v-flex>
										<v-card color="primary" class="white--text" v-ripple>
											{{
												`${event.employee.employeeID === -1 ? " " : ""} ${
													event.title
												} ${event.employee.employeeID === -1 ? " " : ""}`
											}}
										</v-card>
									</v-flex>
								</v-layout>
							</v-container>
						</v-flex>
					</v-layout>
					<v-layout>
						<v-flex xs12 sm2>
							<v-btn @click="$refs.calendar.prev()">
								<v-icon>arrow_back_ios</v-icon>
							</v-btn>
						</v-flex>
						<v-flex xs12 sm8></v-flex>
						<v-flex xs12 sm2>
							<v-btn @click="$refs.calendar.next()">
								<v-icon>arrow_forward_ios</v-icon>
							</v-btn>
						</v-flex>
					</v-layout>
				</v-card-text>
			</v-card>
		</v-container>
	</div>
</template>

<script>
import moment from "moment";
import { setTimeout } from "timers";
import FiscalYearSelector from "./FiscalYearSelector";
import ScheduleBlockSelector from "./ScheduleBlockSelector";

export default {
	name: "CalendarView",
	components: {
		ScheduleBlockSelector,
		FiscalYearSelector
	},
	data() {
		const sb = this.$store.getters.currentScheduleBlock;
		const today = new Date();
		let startDate = today;
		if (sb) {
			startDate = sb.startDate;
		}

		return {
			electron: !(process.title === "browser"),
			today: moment(today).format("YYYY-MM-DD"),
			start: moment(startDate)
				.startOf("month")
				.format("YYYY-MM-DD"),
			end: moment(startDate)
				.endOf("month")
				.format("YYYY-MM-DD"),
			progress: 0,
			loading: false,
			tentative: false,
			calendarFile: null,
			selectedDay: null,
			// time at which optaplanner cuts off optimization and gives its best solution so far
			// in minutes
			planningCutoffTime: 1
		};
	},
	computed: {
		clickDateEvent() {
			return "click:date";
		},
		schedule() {
			return this.$store.getters.generatedSchedule;
		},
		events() {
			const employees = this.$store.getters.employees;
			const schedule = this.schedule;
			let assignments;
			if (!schedule) {
				const periodOfInterest = this.$store.getters.currentFiscalYear;
				const createSchedulingShifts = require("../calendarActions")
					.createSchedulingShifts;

				const shifts = this.$store.getters.scheduleBlocks.reduce(
					(total1, sb) => {
						if (
							moment(sb.startDate).isSameOrAfter(
								moment(periodOfInterest.startDate)
							) &&
							moment(sb.endDate).isSameOrBefore(
								moment(periodOfInterest.endDate)
							)
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
					},
					[]
				);

				assignments = shifts.map(shift => ({
					employee: -1,
					shift: {
						startTime: shift.startTime,
						endTime: shift.endTime,
						shiftTypes: shift.shiftTypes,
						location: shift.location
					}
				}));
			} else {
				assignments = schedule.assignments;
			}

			const events = assignments
				// only show the current month
				.filter(
					assignment =>
						moment(
							assignment.shift.startTime,
							"MM/DD/YYYY HH:mm"
						).isSameOrAfter(
							moment(this.start, "YYYY-MM-DD").startOf("month")
						) &&
						moment(
							assignment.shift.startTime,
							"MM/DD/YYYY HH:mm"
						).isSameOrBefore(moment(this.start, "YYYY-MM-DD").endOf("month"))
				)
				.map(assignment => {
					let employee;
					if (assignment.employee === -1) {
						employee = {
							employeeID: -1,
							name: " ",
							roles: [],
							email: ""
						};
					} else {
						employee = employees.filter(
							employee => employee.employeeID === assignment.employee
						)[0];
					}
					return {
						employee,
						title: `${employee.name
							.split(" ")
							.map(s => s.charAt(0).toUpperCase())
							.join("")} - ${moment(
							assignment.shift.startTime,
							"MM/DD/YYYY HH:mm"
						).format("hh:mm a")} @${assignment.shift.location}`,
						date: moment(assignment.shift.startTime, "MM/DD/YYYY HH:mm").format(
							"YYYY-MM-DD"
						),
						details:
							employee.employeeID === -1
								? ""
								: `${employee.name} (${employee.roles
										.map(role => role.name)
										.join(", ")}) working ${assignment.shift.shiftTypes.join(
										", "
								  )} at ${assignment.shift.location}${
										employee.email
											? `\nemployee's email: ${employee.email}`
											: ""
								  }`,
						open: false
					};
				});

			return events;
		},
		eventsMap() {
			const map = {};
			this.events.forEach(e => (map[e.date] = map[e.date] || []).push(e));
			return map;
		},
		calendarHeight() {
			return (
				150 *
				moment(this.start, "YYYY-MM-DD")
					.endOf("month")
					.endOf("week")
					.diff(
						moment(this.start, "YYYY-MM-DD")
							.startOf("month")
							.startOf("week"),
						"weeks"
					)
			);
		}
	},
	methods: {
		moment,
		clickDate(e) {
			const $emit = e;
			this.selectedDay = this.selectedDay === $emit.date ? null : $emit.date;
		},
		/*
    open(event) {
      alert(event.assignee || "No one assigned yet.");
    },*/

		async saveAsICal() {
			if (this.electron && this.schedule) {
				// There are two feasible locations that the executables could be stored, both of which can be reached programmatically
				// location 1
				// const distDirectory = process.execPath.split(/[\\/]electron\.exe/)[0];
				// location 2
				const distDirectory = require("electron").remote.app.getAppPath();

				// establish connection between background (Electron) and render (Vue) processes
				const remote = require("electron").remote;

				const fs = remote.require("fs");
				const path = remote.require("path");

				const {
					file,
					text
				} = await require("../calendarActions.js").generateICal(
					{
						schedule: this.schedule,
						employees: this.$store.getters.employees
					},
					distDirectory,
					fs,
					path
				);

				this.calendarFile = URL.createObjectURL(file);
				return text;
			}
		},

		async generateSchedule() {
			if (this.electron) {
				this.loading = true;
				this.progress = 0;

				for (let i = 0; i < 100; i++) {
					setTimeout(() => {
						if (this.progress < 100) {
							this.progress++;
							if (this.progress >= 100) {
								this.tentative = true;
							}
						}
					}, 600 * this.planningCutoffTime * i);
				}
				await this.$store.dispatch("generateSchedule");
				this.loading = false;
				this.tentative = false;
				this.progress = 0;
			}
		},

		async downloadICal() {
			const text = await this.saveAsICal();
			let path = require("electron").remote.dialog.showSaveDialog();
			if (!path.includes(".ics")) {
				path = path + ".ics";
			}
			require("electron")
				.remote.require("fs")
				.writeFileSync(path, text);
			// this.$refs.downloadCalendarButton.click();
		}
	}
};
</script>

<style lang="stylus" scoped>
.my-event {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  border-radius: 2px;
  background-color: #1867c0;
  color: #ffffff;
  border: 1px solid #1867c0;
  width: 100%;
  font-size: 12px;
  padding: 3px;
  cursor: pointer;
  margin-bottom: 1px;
}

#downloadForm {
  display: none;
}
</style>
