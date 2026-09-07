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
          <v-card-title>Lista de Gado</v-card-title>
          <v-card-text>
            <CattleList />
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import CattleList from '@/components/templates/CattleList.vue';
</script>
