<template>
	<div>
		<v-container>
			<div>
				<h2 class="title">{{ title }}</h2>
			</div>
			<v-layout row wrap>
				<v-flex xs12 sm6 md3>
					<v-text-field
						v-model="name"
						label="Name"
						:rules="nameRules"
						required
					></v-text-field>
				</v-flex>
			</v-layout>
			<v-layout row wrap>
				<v-flex xs12 sm6 md4>
					<v-menu
						v-model="startDatePicker"
						:close-on-content-click="false"
						:nudge-right="40"
						lazy
						transition="scale-transition"
						offset-y
						full-width
						min-width="290px"
					>
						<v-text-field
							slot="activator"
							v-model="startDate"
							label="Select start date"
							prepend-icon="event"
							readonly
						></v-text-field>
						<v-date-picker
							v-model="startDate"
							@input="closeStartDatePicker"
						></v-date-picker>
					</v-menu>
				</v-flex>
				<v-flex xs12 sm6 md4>
					<v-menu
						v-model="endDatePicker"
						:close-on-content-click="false"
						:nudge-right="40"
						lazy
						transition="scale-transition"
						offset-y
						full-width
						min-width="290px"
					>
						<v-text-field
							slot="activator"
							v-model="endDate"
							label="Select end date"
							prepend-icon="event"
							readonly
						></v-text-field>
						<v-date-picker
							v-model="endDate"
							@input="closeEndDatePicker"
						></v-date-picker>
					</v-menu>
				</v-flex>
			</v-layout>
		</v-container>
	</div>
</template>

<script>
import moment from "moment";

export default {
	name: "StartEndPicker",
	props: {
		title: String
	},
	data() {
		return {
			startDate: moment(new Date())
				.toDate()
				.toISOString()
				.substr(0, 10),
			endDate: moment(new Date())
				.add(1, "year")
				.toDate()
				.toISOString()
				.substr(0, 10),
			startDatePicker: false,
			endDatePicker: false,
			name: "",
			nameRules: [v => Boolean(v) || "Name is required"]
		};
	},
	mounted() {
		this.$emit("picked", {
			startDate: this.convertStringToLocalDate(this.startDate),
			endDate: this.convertStringToLocalDate(this.endDate),
			name: this.name
		});
	},
	methods: {
		convertStringToLocalDate(dateString) {
			// consider changing this to use Moment.js
			const offsetHours = new Date().getTimezoneOffset() / 60;
			let stringOffsetHours;
			if (Math.abs(offsetHours) < 10) {
				stringOffsetHours = "0" + Math.abs(String(offsetHours));
			} else {
				stringOffsetHours = String(Math.abs(offsetHours));
			}
			return new Date(
				Date.parse(
					`${dateString}T00:00:00${
						offsetHours < 0 ? "+" : "-"
					}${stringOffsetHours}:00`
				)
			);
		},
		closeStartDatePicker() {
			this.startDatePicker = false;
			this.$emit("picked", {
				startDate: this.convertStringToLocalDate(this.startDate),
				endDate: this.convertStringToLocalDate(this.endDate),
				name: this.name
			});
		},
		closeEndDatePicker() {
			this.endDatePicker = false;
			this.$emit("picked", {
				startDate: this.convertStringToLocalDate(this.startDate),
				endDate: this.convertStringToLocalDate(this.endDate),
				name: this.name
			});
		}
	},
	watch: {
		name: function() {
			this.$emit("picked", {
				startDate: this.convertStringToLocalDate(this.startDate),
				endDate: this.convertStringToLocalDate(this.endDate),
				name: this.name
			});
		}
	}
};
</script>

<style>
.title {
	text-align: left;
}
</style>
