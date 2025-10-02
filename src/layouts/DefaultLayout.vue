<template>
  <v-app>
    <!-- Menu lateral -->
    <v-navigation-drawer v-model="drawer">
      <v-list>
        <v-list-item
          link
          to="/"
          title="Início"
          prepend-icon="mdi-home"
        />
        <v-list-item
          link
          to="/cattle-visualization"
          title="Visualização dos Bois"
          prepend-icon="mdi-chart-line"
        />
          <v-list-item
          link
          to="/transactions-visualization"
          title="Visualização dos Transações"
          prepend-icon="mdi-cash-multiple"
          />
          <v-list-item
              link
              to="/register-purchase"
              title="Cadastro de Compras"
              prepend-icon="mdi-basket-plus"
            />
            <v-list-item
              link
              to="/register-sale"
              title="Cadastro de vendas"
              prepend-icon="mdi-cash-register"
            />
      </v-list>
    </v-navigation-drawer>

    <!-- Barra de navegação superior -->
    <v-app-bar>
      <v-app-bar-nav-icon @click="drawer = !drawer"></v-app-bar-nav-icon>
      <v-app-bar-title>BovisApp</v-app-bar-title>
            <!-- Botão de cadastro -->
      <v-menu>
        <template #activator="{ props }">
          <v-btn color="primary" v-bind="props" prepend-icon="mdi-plus">Novo Cadastro</v-btn>
        </template>
        <v-list>
          <v-list-item link to="/register-purchase" title="Cadastro de Compra" prepend-icon="mdi-basket-plus" />
          <v-list-item link to="/register-sale" title="Cadastro de Venda" prepend-icon="mdi-cash-register" />
        </v-list>
      </v-menu>
    </v-app-bar>

    <!-- Conteúdo principal -->
    <v-main>
      <v-container>
        <router-view /> <!-- Carrega a página correta dentro do layout -->
      </v-container>
    </v-main>

    <!-- Lista de gado na parte inferior -->
    <v-footer app>
      
    </v-footer>
  </v-app>
</template>

<script setup>
import { ref } from 'vue';

const drawer = ref(null);
</script>
