<template>
	<v-form>
		<v-container class="title font-weight-medium">
			<v-card v-if="error">
				<v-card-text>{{ error }}</v-card-text>
			</v-card>
			<v-card v-else>
				<v-card-text>
					<v-container>Employee Role: {{ roleData.name }}</v-container>
					<v-container>
						<swatches v-model="roleData.color" colors="basic" inline></swatches>
					</v-container>
					<v-container id="bad"></v-container>
				</v-card-text>
			</v-card>
		</v-container>
	</v-form>
</template>

<script>
import Swatches from "vue-swatches";

export default {
	name: "EditRole",
	components: { Swatches },
	data() {
		return {};
	},
	computed: {
		roleData() {
			const roleID = Number(this.$route.params.roleID);
			const roles = this.$store.getters.roles.filter(
				role => role.roleID === roleID
			);
			let roleData = null;
			if (roles.length) {
				roleData = roles[0];
			} else {
				return null;
			}

			if (!roleData.color) {
				roleData.color = "#1FBC9C";
			}
			return roleData;
		},

		error() {
			return this.roleData ? null : "We couldn't find that role.";
		}
	}
};
</script>

<style>
*:focus {
	outline: none;
}
/*
#bad {
  height: 500px;
}*/
</style>
