<template>
	<v-layout>
		<v-flex>
			<v-sheet height="400">
				<!-- now is normally calculated by itself, but to keep the calendar in this date range to view events -->
				<v-calendar
					ref="calendar"
					:now="today"
					:value="today"
					color="primary"
					type="week"
				>
					<!-- the events at the top (all-day) -->
					<template v-slot:dayHeader="{ date }">
						<template v-for="event in eventsMap[date]">
							<!-- all day events don't have time -->
							<div
								v-if="!event.time"
								:key="event.title"
								class="my-event"
								@click="open(event)"
								v-html="event.title"
							></div>
						</template>
					</template>
					<!-- the events at the bottom (timed) -->
					<template v-slot:dayBody="{ date, timeToY, minutesToPixels }">
						<template v-for="event in eventsMap[date]">
							<!-- timed events -->
							<div
								v-if="event.time"
								:key="event.title"
								:style="{
									top: timeToY(event.time) + 'px',
									height: minutesToPixels(event.duration) - 3 + 'px'
								}"
								class="my-event with-time"
								@click="open(event)"
								v-html="event.title"
							></div>
						</template>
					</template>
				</v-calendar>
			</v-sheet>
		</v-flex>
	</v-layout>
</template>

<script>
import moment from "moment";

export default {
	name: "CalendarView",
	data: () => ({
		today: "2019-01-08",
		events: [
			{
				title: "Day Shift",
				date: "2019-01-07",
				assignee: "John Doe",
				time: "07:00",
				duration: 480
			},
			{
				title: "Late Shift",
				date: "2019-01-07",
				time: "15:00",
				duration: 480
			},
			{
				title: "Night Shift",
				date: "2019-01-08",
				time: "23:00",
				duration: 480
			},
			{
				title: "Jan's Birthday",
				date: "2019-01-08",
				duration: 480
			}
		]
	}),
	computed: {
		// convert the list of events into a map of lists keyed by date
		eventsMap() {
			const map = {};
			this.events.forEach(e => {
				(map[e.date] = map[e.date] || []).push(e);
				let endMoment = moment(e.date + " " + e.time).add(
					e.duration,
					"minutes"
				);

				if (endMoment.day() != moment(e.date).day()) {
					let endArray = endMoment.format("YYYY-MM-DD HH:MM").split(" ");

					let endDate = endArray[0];

					let endDuration =
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
		}
	},
	mounted() {
		this.$refs.calendar.scrollToTime("08:00");
	},
	methods: {
		open(event) {
			alert(event.assignee || "No one assigned yet.");
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
  border: 1px solid #0040a0;
  font-size: 12px;
  padding: 3px;
  cursor: pointer;
  margin-bottom: 1px;
  left: 4px;
  margin-right: 8px;
  position: relative;

  &.with-time {
    position: absolute;
    right: 4px;
    margin-right: 0px;
  }
}
</style>