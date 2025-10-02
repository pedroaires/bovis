<template>
  <v-container>
    <!-- Linha superior com 4 cards -->
    <v-row>
      <v-col cols="12" sm="6" md="3">
        <v-card class="pa-4" height="120px">
          <v-card-title>Card 1</v-card-title>
        </v-card>
      </v-col>
      <v-col cols="12" sm="6" md="3">
        <v-card class="pa-4" height="120px">
          <v-card-title>Card 2</v-card-title>
        </v-card>
      </v-col>
      <v-col cols="12" sm="6" md="3">
        <v-card class="pa-4" height="120px">
          <v-card-title>Card 3</v-card-title>
        </v-card>
      </v-col>
      <v-col cols="12" sm="6" md="3">
        <v-card class="pa-4" height="120px">
          <v-card-title>Card 4</v-card-title>
        </v-card>
      </v-col>
    </v-row>

    <!-- Espaço entre os cards e a tabela -->
    <v-spacer></v-spacer>

    <!-- Tabela em um quadrado maior -->
    <v-row>
      <v-col cols="12">
        <v-card class="pa-4">
          <v-card-text>
             <CattleList
              title="Lista de Gado"
              :items="cattle"
              :loading="loading"
              :enable-search="true"
              :selectable="false"
              />
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import CattleList from '@/components/templates/CattleList.vue'
import api from '@/services/api.js'

const loading = ref(false)
const cattle = ref([])

onMounted(async () => {
  loading.value = true
  try {
    const resp = await api.getActiveCattleWithWeight()
    cattle.value = resp.data ?? []
  } finally {
    loading.value = false
  }
})
</script>
