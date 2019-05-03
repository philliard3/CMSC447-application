<template>
	<v-container>
		<v-card>
			<v-card-text>
				<div class="display-1 font-weight-light">Work Day Preferences</div>
				<v-container>
					<v-layout>
						<v-text-field
							v-model="dayName"
							label="Day name"
							:rules="dayNameRules"
							required
						></v-text-field>
					</v-layout>
				</v-container>
			</v-card-text>
		</v-card>
	</v-container>
</template>

<script>
export default {
	name: "WorkPreferenceTable",
	props: { dayName: String, preferredDays: Array },
	data() {
		return {
			dayNameRules: [
				v => !!v || "Name is required to tell days apart.",
				v =>
					!this.preferredDays.some(day => day.name === v) ||
					"Name cannot be already taken"
			]
		};
	}
};
</script>
