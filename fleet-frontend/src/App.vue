
<template>
  <v-app>
    <!-- Meniul lateral curat, fără fundal albastru, aliniat perfect -->
    <v-navigation-drawer permanent elevation="2">
      <v-list-item
        prepend-icon="mdi-car-side"
        title="Fleet Management"
        subtitle="Panou de Control"
        class="pt-3 pb-3"
      ></v-list-item>
      
      <v-divider></v-divider>

      <v-list density="compact" nav class="mt-2">
        <v-list-item 
          prepend-icon="mdi-view-dashboard" 
          title="Dashboard & KPI" 
          value="dashboard" 
          @click="currentTab = 'DashboardWidget'" 
          :active="currentTab === 'DashboardWidget'"
          color="blue-grey-darken-4"
        ></v-list-item>
        
        <v-list-item 
          prepend-icon="mdi-domain" 
          title="Flote" 
          value="fleets" 
          @click="currentTab = 'FleetList'" 
          :active="currentTab === 'FleetList'"
          color="blue-grey-darken-4"
        ></v-list-item>
        
        <v-list-item 
          prepend-icon="mdi-car" 
          title="Vehicule" 
          value="vehicles" 
          @click="currentTab = 'VehicleManager'" 
          :active="currentTab === 'VehicleManager'"
          color="blue-grey-darken-4"
        ></v-list-item>
        
        <v-list-item 
          prepend-icon="mdi-account-group" 
          title="Șoferi" 
          value="drivers" 
          @click="currentTab = 'DriverManager'" 
          :active="currentTab === 'DriverManager'"
          color="blue-grey-darken-4"
        ></v-list-item>
        
        <v-list-item 
          prepend-icon="mdi-fuel" 
          title="Alimentări" 
          value="fuel" 
          @click="currentTab = 'FuelManager'" 
          :active="currentTab === 'FuelManager'"
          color="blue-grey-darken-4"
        ></v-list-item>

        <v-list-item 
          prepend-icon="mdi-tools" 
          title="Mentenanță" 
          value="maintenance" 
          @click="currentTab = 'MaintenanceManager'" 
          :active="currentTab === 'MaintenanceManager'"
          color="blue-grey-darken-4"
        ></v-list-item>
        
        <v-list-item 
          prepend-icon="mdi-map-marker-path" 
          title="Curse" 
          value="trips" 
          @click="currentTab = 'TripManager'" 
          :active="currentTab === 'TripManager'"
          color="blue-grey-darken-4"
        ></v-list-item>
        
        <v-list-item 
          prepend-icon="mdi-shield-car" 
          title="Asigurări" 
          value="insurance" 
          @click="currentTab = 'InsuranceManager'" 
          :active="currentTab === 'InsuranceManager'"
          color="blue-grey-darken-4"
        ></v-list-item>
        
        <v-list-item 
          prepend-icon="mdi-clipboard-account" 
          title="Alocări" 
          value="assignments" 
          @click="currentTab = 'AssignmentManager'" 
          :active="currentTab === 'AssignmentManager'"
          color="blue-grey-darken-4"
        ></v-list-item>
      </v-list>
    </v-navigation-drawer>

    <!-- Bara de sus: Culoare Enterprise (Blue-Grey-Darken-4), text subtil îngroșat -->
    <v-app-bar color="blue-grey-darken-4" elevation="1">
      <v-app-bar-title class="font-weight-medium">Sistem Management Flotă Auto</v-app-bar-title>
    </v-app-bar>

    <!-- Zona principală -->
    <v-main class="bg-grey-lighten-4">
      <v-container fluid class="pa-6">
        <component :is="currentTab" />
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
import DashboardWidget from './components/DashboardWidget.vue';
import FleetList from './components/FleetList.vue';
import VehicleManager from './components/VehicleManager.vue';
import DriverManager from './components/DriverManager.vue';
import FuelManager from './components/FuelManager.vue';
import MaintenanceManager from './components/MaintenanceManager.vue';
import TripManager from './components/TripManager.vue';
import InsuranceManager from './components/InsuranceManager.vue';
import AssignmentManager from './components/AssignmentManager.vue';


export default {
  name: 'App',
  components: {
    DashboardWidget,
    FleetList,
    VehicleManager,
    DriverManager,
    FuelManager,
    MaintenanceManager,
    TripManager,
    InsuranceManager,
    AssignmentManager
  },
  data() {
    return {
      currentTab: 'DashboardWidget'
    }
  }
}
</script>