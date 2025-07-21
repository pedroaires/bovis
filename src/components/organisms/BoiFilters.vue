<template>
  <v-card flat class="pa-4 mb-4">
    <v-row dense>
      <v-col cols="12" md="2">
        <v-text-field
          v-model="internalFilters.numeroBoi"
          label="Número do Boi"
          type="number"
          variant="outlined"
          dense
          clearable
        />
      </v-col>

      <v-col cols="12" md="2">
        <v-select
          v-model="internalFilters.estadoBoi"
          :items="estados"
          label="Estado do Boi"
          variant="outlined"
          dense
          clearable
        />
      </v-col>

      <v-col cols="12" md="2">
        <v-text-field
          v-model="internalFilters.pesoMin"
          label="Peso mínimo"
          type="number"
          variant="outlined"
          dense
          clearable
        />
      </v-col>

      <v-col cols="12" md="2">
        <v-text-field
          v-model="internalFilters.pesoMax"
          label="Peso máximo"
          type="number"
          variant="outlined"
          dense
          clearable
        />
      </v-col>

      <v-col cols="12" md="2">
        <v-menu
          v-model="menu1"
          :close-on-content-click="false"
          transition="scale-transition"
          offset-y
        >
          <template #activator="{ on, attrs }">
            <v-text-field
              v-model="internalFilters.dataPesagemInicio"
              label="Pesagem início"
              readonly
              clearable
              dense
              variant="outlined"
              v-bind="attrs"
              v-on="on"
            />
          </template>
          <v-date-picker
            v-model="internalFilters.dataPesagemInicio"
            locale="pt-br"
            @input="menu1 = false"
          />
        </v-menu>
      </v-col>

      <v-col cols="12" md="2">
        <v-menu
          v-model="menu2"
          :close-on-content-click="false"
          transition="scale-transition"
          offset-y
        >
          <template #activator="{ on, attrs }">
            <v-text-field
              v-model="internalFilters.dataPesagemFim"
              label="Pesagem fim"
              readonly
              clearable
              dense
              variant="outlined"
              v-bind="attrs"
              v-on="on"
            />
          </template>
          <v-date-picker
            v-model="internalFilters.dataPesagemFim"
            locale="pt-br"
            @input="menu2 = false"
          />
        </v-menu>
      </v-col>

      <v-col cols="12" class="d-flex justify-end">
        <v-btn color="primary" class="mr-2" @click="$emit('apply', internalFilters)">
          Aplicar
        </v-btn>
        <v-btn variant="outlined" @click="resetFilters">
          Limpar
        </v-btn>
      </v-col>
    </v-row>
  </v-card>
</template>

<script>
export default {
  name: 'BoiFilters',
  props: {
    filters: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      internalFilters: { ...this.filters },
      estados: ['NA_FAZENDA', 'MORTO', 'VENDIDO'],
      menu1: false,
      menu2: false,
    };
  },
  watch: {
    filters: {
      handler(newVal) {
        this.internalFilters = { ...newVal };
      },
      deep: true,
    },
  },
  methods: {
    resetFilters() {
      this.internalFilters = {
        numeroBoi: null,
        estadoBoi: null,
        pesoMin: null,
        pesoMax: null,
        dataPesagemInicio: null,
        dataPesagemFim: null,
        dataCompraInicio: null,
        dataCompraFim: null,
      };
    },
  },
};
</script>
