<template>
  <v-container>
    <v-stepper v-model="step">
      <v-stepper-header>
        <v-stepper-item :value="1" title="Selecionar bois" />
        <v-stepper-item :value="2" title="Dados da pesagem" />
      </v-stepper-header>

      <v-stepper-window>
        <v-stepper-window-item :value="1">
          <v-card>
            <v-card-text>
              <ClientDataTable
                title="Selecionar bois para pesagem"
                :items="cattle"
                :headers="headers"
                :loading="loading"
                selectable
                v-model="selected">
                <template #actions>
                  <div class="mt-4 text-right">
                    <v-btn color="primary" :disabled="!selected.length" @click="toStep2">
                      Continuar
                    </v-btn>
                  </div>
                </template>
                <template #[`item.estadoBoi`]="{ item }">
                  <v-chip
                  :color="getColor(item.estadoBoi)"
                  size="small">
                    {{ item.estadoBoi }}
                  </v-chip>
                </template>
                <template #[`item.dataUltimaPesagem`]="{ item }">
                  {{ formatDate(item.dataUltimaPesagem) }}
                </template>
              </ClientDataTable>
            </v-card-text>
          </v-card>
        </v-stepper-window-item>

        <v-stepper-window-item :value="2">
          <v-card>
            <v-card-text>
              <v-alert
                v-if="errorMsg"
                type="error"
                class="mb-4"
                dismissible
                @click:close="errorMsg = ''"
              >
                {{ errorMsg }}
              </v-alert>

              <v-row>
                <v-col cols="12" sm="4">
                  <v-text-field
                    v-model="weighingDate"
                    label="Data da Pesagem"
                    type="date"
                    variant="outlined"
                    :max="today"
                  />
                </v-col>
              </v-row>

              <div class="mt-6">
                <div class="text-subtitle-2 mb-2">
                  Bois selecionados (informe os dados da pesagem):
                </div>

                <v-data-table
                  :headers="itemHeaders"
                  :items="weighingItems"
                  item-key="boiId"
                  :items-per-page="-1"
                  hide-default-footer
                >
                  <template #[`item.peso`]="{ item }">
                    <v-text-field
                      v-model="item.pesoStr"
                      density="compact"
                      variant="outlined"
                      inputmode="decimal"
                      hide-details
                      placeholder="Ex: 450,5"
                      @blur="normalizePeso(item)"
                    />
                  </template>

                  <template #[`item.descricao`]="{ item }">
                    <v-text-field
                      v-model="item.descricao"
                      density="compact"
                      variant="outlined"
                      hide-details
                      placeholder="Opcional"
                    />
                  </template>

                  <template #[`item.medicacoes`]="{ item }">
                    <v-combobox
                      v-model="item.medicacoes"
                      chips
                      multiple
                      deletable-chips
                      density="compact"
                      variant="outlined"
                      hide-details
                      placeholder="Digite e pressione Enter"
                    />
                  </template>
                </v-data-table>
              </div>
            </v-card-text>

            <v-card-actions>
              <v-btn variant="text" @click="step = 1">Voltar</v-btn>
              <v-spacer />
              <v-btn
                color="primary"
                :loading="submitting"
                :disabled="!canSubmit"
                @click="submitWeighing"
              >
                Confirmar Pesagem
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-stepper-window-item>
      </v-stepper-window>
    </v-stepper>

    <v-snackbar v-model="snackbar.show" :timeout="3000" color="success">
      {{ snackbar.text }}
    </v-snackbar>
  </v-container>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import ClientDataTable from '@/shared/components/ClientDataTable.vue'
import { useCattleStatus } from '@/shared/composables/useCattleStatus'
import { formatDate } from '@/shared/utils/formatters/dateFormatter';
import { normalizePeso } from '@/shared/utils/formatters/weightFormatter';
import { useActiveCattle } from '@/features/cattle/composables/useActiveCattle';
import { useRegisterWeighing } from '../composables/useRegisterWeighing';

const headers = [
  { title: 'ID', key: 'id' },
  { title: 'Número', key: 'numero' },
  { title: 'Raça', key: 'raca' },
  { title: 'Estado do Boi', key: 'estadoBoi' },
  { title: 'Data Última Pesagem', key: 'dataUltimaPesagem' },
  { title: 'Último Peso', key: 'peso' },
];

const { getColor } = useCattleStatus();

const { 
  fetchActiveCattle, 
  loading, 
  cattle,
  error: fetchCattleError
} = useActiveCattle();
const{
  weighingDate,
  weighingItems,
  submitting,
  error: submitError,
  canSubmit,
  createFromSelection,
  submit
} = useRegisterWeighing();

const step = ref(1)
const selected = ref([])

const snackbar = ref({ show: false, text: '' })
const errorMsg = ref('')
const today = new Date().toISOString().slice(0, 10)

const itemHeaders = [
  { title: 'ID', key: 'boiId', sortable: false },
  { title: 'Número', key: 'numero', sortable: false },
  { title: 'Raça', key: 'raca', sortable: false },
  { title: 'Peso (kg)', key: 'peso', sortable: false, width: '150px' },
  { title: 'Descrição', key: 'descricao', sortable: false, width: '200px' },
  { title: 'Medicações Aplicadas', key: 'medicacoes', sortable: false },
]

watch(fetchCattleError, (newError) => {
  if (newError) {
    errorMsg.value = newError
  }
})
watch(submitError, (newError) => {
  if (newError) {
    errorMsg.value = newError
  }
})

onMounted(async () => {
  await fetchActiveCattle()
})

function toStep2() {
  if (!createFromSelection(selected, cattle)) {
    errorMsg.value = 'Seleção inválida. Tente selecionar novamente.'
    step.value = 1
    return
  }

  step.value = 2
}

async function submitWeighing() {
  if (!canSubmit.value) return
  submit(() => {
    snackbar.value = { show: true, text: 'Pesagem(ns) cadastrada(s) com sucesso!' }
    step.value = 1
    selected.value = []
    fetchActiveCattle()
  })
  
}

</script>