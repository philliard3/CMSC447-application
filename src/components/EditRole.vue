<template>
	<v-container class="title font-weight-medium"
		>Role id: {{ roleData.roleID }}</v-container
	>
</template>

<script>
export default {
	name: "EditRole",
	data() {
		const roleID = this.$route.params.roleID;
		return {
			roleData: {
				roleID
			}
		};
	}
};
</script>
