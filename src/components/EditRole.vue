<template>
	<v-form>
		<v-container class="title font-weight-medium">
			<v-card v-if="error">
				<v-card-text>{{ error }}</v-card-text>
			</v-card>
			<v-card v-else>
				<v-card-text>
					<v-container>Role id: {{ roleData.roleID }}</v-container>
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
		const roleID = this.$route.params.roleID;
		const roles = this.$store.getters.roles.filter(
			role => role.roleID === roleID
		);
		let roleData = null;
		if (roles.length) {
			roleData = roles[0];
		}

		if (!roleData.color) {
			roleData.color = "#1FBC9C";
		}

		return {
			roleData: roleData,
			error: roleData ? null : "We couldn't find that role."
		};
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
