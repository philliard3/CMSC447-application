<template>
	<v-form>
		<v-container>
			<v-card>
				<v-card-text>
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
														<v-btn icon>
															<v-icon>edit</v-icon>
														</v-btn>
														<v-toolbar-title
															v-html="event.title"
														></v-toolbar-title>
														<v-spacer></v-spacer>
														<v-btn icon>
															<v-icon>favorite</v-icon>
														</v-btn>
														<v-btn icon>
															<v-icon>more_vert</v-icon>
														</v-btn>
													</v-toolbar>
													<v-card-title primary-title>
														<span v-html="event.details"></span>
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
		<v-container>
			<v-card v-if="electron">
				<v-card-text>
					<v-btn @click="saveAsICal">Export Calendar</v-btn>
					<v-btn @click="generateSchedule">Generate Schedule</v-btn>
				</v-card-text>
			</v-card>
		</v-container>
	</v-form>
</template>

<script>
import moment from "moment";

export default {
	name: "CalendarView",
	data: () => ({
		electron: !(process.title === "browser"),
		today: moment(new Date()).format("YYYY-MM-DD"),
		start: moment(new Date())
			.startOf("month")
			.format("YYYY-MM-DD"),
		end: moment(new Date())
			.endOf("month")
			.format("YYYY-MM-DD")
	}),
	computed: {
		events() {
			const employees = this.$store.getters.employees;
			const schedule = this.$store.getters.generatedSchedule;
			const events = schedule.assignments
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
					const employee = employees.filter(
						employee => employee.employeeID === assignment.employee
					)[0];
					return {
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
						details: `${employee.name} (${employee.roles
							.map(role => role.name)
							.join(", ")}) working ${assignment.shift.shiftTypes.join(
							", "
						)} at ${assignment.shift.location}${
							employee.email ? `\nemployee's email: ${employee.email}` : ""
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
		/*
    // convert the list of events into a map of lists keyed by date
    eventsMap() {
      const map = {};
      this.events.forEach(e => {
        (map[e.date] = map[e.date] || []).push(e);

        // calculate whether the event ends on the same day
        const endMoment = moment(e.date + " " + e.time).add(
          e.duration,
          "minutes"
        );

        // handle events that last past midnight
        if (endMoment.day() != moment(e.date).day()) {
          const endArray = endMoment.format("YYYY-MM-DD HH:MM").split(" ");

          const endDate = endArray[0];

          // calculate the correct duration after midnight
          const endDuration =
            e.duration -
            (24 - moment((e.date + " " + (e.time || "")).trim()).hour()) * 60;

          (map[endDate] = map[endDate] || []).push(
            Object.assign(
              { ...e },
              { date: endDate, time: "00:00", duration: endDuration }
            )
          );
        }
      });
      return map;
	}*/
	},
	methods: {
		/*
    open(event) {
      alert(event.assignee || "No one assigned yet.");
    },*/

		async saveAsICal() {
			if (this.electron) {
				// There are two feasible locations that the executables could be stored, both of which can be reached programmatically
				// location 1
				const distDirectory = process.execPath.split(/[\\/]electron\.exe/)[0];
				// location 2
				// const distDirectory = require("electron").remote.app.getAppPath()

				// establish connection between background (Electron) and render (Vue) processes
				const remote = require("electron").remote;

				const fs = remote.require("fs");
				const path = remote.require("path");

				const fileCreated = await require("../calendarActions.js").generateICal(
					{
						schedule: this.$store.getters.generatedSchedule,
						employees: this.$store.getters.employees
					},
					distDirectory,
					fs,
					path
				);
				this.calendarFile = fileCreated;
			}
		},

		async generateSchedule() {
			if (this.electron) {
				await this.$store.dispatch("generateSchedule");
			}
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
</style>
