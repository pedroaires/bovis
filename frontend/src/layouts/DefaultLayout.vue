<template>
  <v-app>
    <!-- Menu lateral -->
    <v-navigation-drawer v-model="drawer">
      <!-- Conteúdo do Drawer -->
    </v-navigation-drawer>

    <!-- Barra de navegação superior -->
    <v-app-bar>
      <v-app-bar-nav-icon @click="drawer = !drawer"></v-app-bar-nav-icon>
      <v-app-bar-title>Application</v-app-bar-title>
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
