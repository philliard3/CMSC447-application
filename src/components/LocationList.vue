<template>
	<v-container>
		<v-card>
			<v-card-text>
				<div class="display-1 font-weight-light" id="header-row-1">
					Locations
				</div>
				<v-container>
					<v-layout>
						<v-flex>
							<v-text-field
								v-model="newLocationData.name"
								name="Location Name"
								label="Location Name"
								@keypress="enterCheck"
							></v-text-field>
						</v-flex>
						<v-flex>
							<v-btn color="success" @click="addLocation">
								<v-icon>add_location</v-icon>&nbsp;New Location
							</v-btn>
						</v-flex>
					</v-layout>
				</v-container>
				<!--- location table -->
				<v-layout>
					<v-flex>
						<v-container v-for="name in locations" :key="name">
							<v-layout>
								<v-flex class="title">{{ name }}</v-flex>
								<v-flex>
									<v-btn color="error" @click="removeLocation(name)">
										<v-icon>remove_circle</v-icon>&nbsp;Remove
									</v-btn>
								</v-flex>
							</v-layout>
						</v-container>
					</v-flex>
				</v-layout>
			</v-card-text>
		</v-card>
	</v-container>
</template>
<script>
export default {
	name: "LocationList",
	data() {
		return {
			newLocationData: {
				name: ""
			}
		};
	},
	computed: {
		locations() {
			return this.$store.getters.locations;
		}
	},
	methods: {
		enterCheck(e) {
			if (e.key === "Enter" && this.newLocationData.name) {
				this.addLocation();
			}
		},
		addLocation() {
			if (
				this.newLocationData.name.length > 0 &&
				!this.locations.includes(this.newLocationData.name)
			) {
				this.$store.commit("addLocation", this.newLocationData.name);
				this.newLocationData.name = "";
			}
		},
		removeLocation(locationName) {
			this.$store.commit("removeLocation", locationName);
		}
	}
};
</script>
