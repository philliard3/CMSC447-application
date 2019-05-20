<template>
	<v-container>
		<v-card>
			<v-card-text>
				<div class="display-1 font-weight-light">Work Day Preferences</div>
				<v-container v-if="preferredDays && preferredDays.length === 0">
					<v-layout>
						<v-flex>
							<v-btn color="success" @click="addDefaults"
								>Add Default Days as Options</v-btn
							>
						</v-flex>
					</v-layout>
				</v-container>
				<v-container>
					<v-layout>
						<v-flex>
							<v-text-field
								v-model="dayToAdd.name"
								label="Day name"
								:rules="nameRules"
								required
							></v-text-field>
						</v-flex>
						<v-flex xs12 sm6 md4>
							<v-menu
								v-model="dayToAdd.startDatePicker"
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
									v-model="dayToAdd.startDate"
									label="Select start date"
									prepend-icon="event"
									readonly
								></v-text-field>
								<v-date-picker
									v-model="dayToAdd.startDate"
									@input="dayToAdd.startDatePicker = false"
								></v-date-picker>
							</v-menu>
						</v-flex>
						<v-flex>
							<v-checkbox
								v-model="dayToAdd.repeating"
								label="Repeats annually"
							></v-checkbox>
						</v-flex>
						<v-flex>
							<v-btn color="success" @click="addDay">
								<v-icon>add</v-icon>
							</v-btn>
						</v-flex>
					</v-layout>
				</v-container>
				<v-container>
					<v-layout v-for="(day, index) in preferredDays" :key="day.dayID">
						<v-flex class="title">{{ day.name }}</v-flex>
						<v-flex class="subheading">
							{{ formatDate(day) }}
						</v-flex>
						<v-flex>
							<v-checkbox
								:value="day.preferred === false ? true : false"
								@change="
									day.preferred = $event ? false : null;
									$emit('input', preferredDays);
								"
								label="Prefer not to work"
							></v-checkbox>
						</v-flex>
						<v-flex>
							<v-checkbox
								:value="day.preferred === true ? true : false"
								@change="
									day.preferred = $event ? true : null;
									$emit('input', preferredDays);
								"
								label="Prefer to work"
							></v-checkbox>
						</v-flex>
						<v-flex>
							<v-btn color="error" @click="removeDay(index)">
								<v-icon>remove</v-icon>
							</v-btn>
						</v-flex>
					</v-layout>
				</v-container>
			</v-card-text>
		</v-card>
	</v-container>
</template>

<script>
import moment from "moment";

export default {
	name: "WorkPreferenceTable",
	props: { preferredDays: Array, defaultDays: Array },
	data() {
		return {
			dayToAdd: {
				name: "",
				repeating: false,
				location: null,
				startDatePicker: false,
				startDate: new Date().toISOString().substr(0, 10)
			},
			nameRules: [
				v => !!v || "Name is required to tell days apart.",
				v =>
					!this.preferredDays.some(day => day.name === v) ||
					"Name cannot be already taken"
			]
		};
	},
	methods: {
		addDay() {
			const dayToAdd = {
				name: this.dayToAdd.name,
				startDate: moment(this.dayToAdd.startDate)
					.toDate()
					.getTime(),
				location: this.dayToAdd.location ? this.dayToAdd.location : undefined
			};
			this.preferredDays.push(dayToAdd);
			this.dayToAdd = {
				name: "",
				location: null,
				startDatePicker: false,
				startDate: new Date().toISOString().substr(0, 10)
			};
			this.$emit("input", this.preferredDays);
		},
		removeDay(index) {
			this.preferredDays.splice(index, 1);
			this.$emit("input", this.preferredDays);
		},
		moment,
		addDefaults() {
			let changed = false;
			for (let day of this.defaultDays) {
				if (!this.preferredDays.some(d => d.name === day.name)) {
					this.preferredDays.push(day);
					changed = true;
				}
			}
			if (changed) {
				this.$emit("input", this.preferredDays);
			}
		},
		formatDate(day) {
			let formatString = "MM/DD/YY";
			if (day.repeating) {
				formatString = "MM/DD";
			}

			if (day.endDate && day.startDate !== day.endDate) {
				return `${moment(day.startDate).format()}-${moment(day.endDate).format(
					formatString
				)}`;
			} else {
				return moment(day.startDate).format(formatString);
			}
		}
	}
};
</script>
