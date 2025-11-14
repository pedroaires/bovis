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
              <ClientDataTable
              title="Lista de gado"
              :items="cattle"
              :headers="headers"
              :loading="loading"
              :enable-search="true"
              :selectable="false"
              >
                <template #[`item.estadoBoi`]="{ item }">
                <v-chip :color="getColor(item.estadoBoi)" size="small">
                  {{ item.estadoBoi }}
                </v-chip>
              </template>
              <template #[`item.dataUltimaPesagem`]="{ item }">
                  {{ formatDate(item.dataUltimaPesagem) }}
              </template>
              </ClientDataTable>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { useActiveCattle } from '../composables/useActiveCattle'
import { useCattleStatus } from '@/shared/composables/useCattleStatus';
import { useWeightFormatting } from '@/features/weighing/composables/useWeightFormatting';
import ClientDataTable from '@/shared/components/ClientDataTable.vue';
const { getColor } = useCattleStatus();
const { cattle, loading, error } = useActiveCattle();
const { formatDate } = useWeightFormatting();
const headers = [
  { title: 'ID', key: 'id' },
  { title: 'Número', key: 'numero' },
  { title: 'Raça', key: 'raca' },
  { title: 'Estado do Boi', key: 'estadoBoi' },
  { title: 'Data Última Pesagem', key: 'dataUltimaPesagem' },
  { title: 'Último Peso', key: 'peso' },
];
</script>
