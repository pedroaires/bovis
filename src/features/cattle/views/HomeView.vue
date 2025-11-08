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
            <v-alert
              v-if="error"
              type="error"
              class="mb-4"
              title="Erro ao carregar"
            >
              Não foi possível buscar os dados do gado. Por favor, tente novamente mais tarde.
            </v-alert>
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
import CattleList from '@/shared/components/CattleList.vue'
import { useActiveCattle } from '../composables/useActiveCattle'

const { cattle, loading, error } = useActiveCattle()
</script>
