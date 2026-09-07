<template>
    <v-container>
      <v-data-table :headers="headers" :items="items"></v-data-table>
    </v-container>
  </template>
<script setup>
defineProps({
  headers: Array,
  items: Array
})
</script>