<template>
	<v-container>
		<v-card>
			<v-card-text>
				<div class="display-1 font-weight-light">Shift Preferences</div>
				<div v-if="!shifts || !shifts.length">
					<v-container>
						<v-layout>
							<v-flex class="title"
								>Please select a role in order to use preferences for that
								role.</v-flex
							>
						</v-layout>
					</v-container>
				</div>
				<div v-else>
					<v-container
						fluid
						v-for="(shift, shiftIndex) in shifts"
						:key="shift.name + shift.location + shift.time"
						class="headline font-weight-medium"
					>
						{{ shift.name }} (Starts at {{ shift.startTime }} at location "{{
							shift.location
						}}")
						<v-layout row wrap>
							<v-flex
								v-for="(dayList, dayListIndex) in shift.startDays.reduce(
									(dayList, day, i) => {
										if (i % 2) {
											dayList[dayList.length - 1].push(day);
										} else {
											dayList.push([day]);
										}
										return dayList.slice();
									},
									[]
								)"
								:key="dayListIndex"
								xs12
								sm4
								md4
							>
								<v-checkbox
									v-for="day in dayList"
									v-model="shifts[shiftIndex].selected"
									@change="triggerShiftUpdate"
									:key="day"
									:label="day"
									:value="day"
									hide-details
								></v-checkbox>
							</v-flex>
						</v-layout>
					</v-container>
				</div>
			</v-card-text>
		</v-card>
	</v-container>
</template>

<script>
export default {
	name: "ShiftSelection",
	props: { shifts: Array },
	data() {
		return {};
	},
	methods: {
		triggerShiftUpdate() {
			this.$emit("input", this.shifts);
		}
	}
};
</script>
