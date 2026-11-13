<template>
  <v-container fluid>
    <!-- Antet Dashboard -->
    <v-row>
      <v-col cols="12" class="pb-0">
        <h2 class="text-h4 font-weight-bold text-primary mb-1">Panou de Control (KPIs)</h2>
        <p class="text-subtitle-1 text-grey-darken-1 mb-4">
          O privire de ansamblu în timp real asupra performanței și stării flotei auto.
        </p>
      </v-col>
    </v-row>
    
    <!-- Rândul 1: Cardurile KPI Principale -->
    <v-row>
      <!-- KPI 1: Costul Mediu la Combustibil -->
      <v-col cols="12" md="4">
        <v-card elevation="4" class="rounded-lg kpi-card gradient-blue">
          <v-card-text class="d-flex align-center pa-5">
            <div>
              <div class="text-subtitle-1 text-white font-weight-medium opacity-90">Cost Mediu Combustibil</div>
              <div class="text-h3 font-weight-bold text-white mt-1">
                {{ avgFuelCost ? avgFuelCost.toFixed(2) : '0.00' }} <span class="text-h6">RON/L</span>
              </div>
            </div>
            <v-spacer></v-spacer>
            <v-icon size="72" color="white" class="kpi-icon">mdi-gas-station</v-icon>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- KPI 2: Experiența medie a șoferilor -->
      <v-col cols="12" md="4">
        <v-card elevation="4" class="rounded-lg kpi-card gradient-green">
          <v-card-text class="d-flex align-center pa-5">
            <div>
              <div class="text-subtitle-1 text-white font-weight-medium opacity-90">Vechime Medie Șoferi</div>
              <div class="text-h3 font-weight-bold text-white mt-1">
                {{ avgDriverExp ? avgDriverExp.toFixed(1) : '0.0' }} <span class="text-h6">Ani</span>
              </div>
            </div>
            <v-spacer></v-spacer>
            <v-icon size="72" color="white" class="kpi-icon">mdi-account-star</v-icon>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- KPI 3: Mașini pe traseu (Active Assignments) -->
      <v-col cols="12" md="4">
        <v-card elevation="4" class="rounded-lg kpi-card gradient-orange">
          <v-card-text class="d-flex align-center pa-5">
            <div>
              <div class="text-subtitle-1 text-white font-weight-medium opacity-90">Vehicule pe Traseu</div>
              <div class="text-h3 font-weight-bold text-white mt-1">
                {{ activeCarsCount }} <span class="text-h6">Active</span>
              </div>
            </div>
            <v-spacer></v-spacer>
            <v-icon size="72" color="white" class="kpi-icon">mdi-car-clock</v-icon>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Rândul 2: Alerte și Informații Suplimentare -->
    <v-row class="mt-4">
      <!-- Panou Alerte Asigurări (Folosește Endpoint Complex 1) -->
      <v-col cols="12" md="6">
        <v-card elevation="3" class="rounded-lg h-100 border-top-error">
          <v-card-title class="d-flex align-center pa-4 bg-grey-lighten-4">
            <v-icon color="error" class="me-2">mdi-alert-decagram</v-icon>
            <span class="text-error font-weight-bold">Alerte: Asigurări Scadente (&lt; 30 Zile)</span>
          </v-card-title>
          <v-divider></v-divider>
          
          <!-- Lista dacă există alerte -->
          <v-list v-if="expiringInsurances.length > 0" lines="two">
            <v-list-item v-for="ins in expiringInsurances" :key="ins.id">
              <template v-slot:prepend>
                <v-icon color="error" size="large">mdi-shield-alert</v-icon>
              </template>
              <v-list-item-title class="font-weight-bold text-subtitle-1">
                {{ ins.vehicle ? ins.vehicle.licensePlate : 'N/A' }} - {{ ins.provider }}
              </v-list-item-title>
              <v-list-item-subtitle>
                Expiră la data de: <strong class="text-error">{{ ins.endDate }}</strong>
              </v-list-item-subtitle>
            </v-list-item>
          </v-list>

          <!-- Mesaj de succes dacă totul e ok -->
          <v-card-text v-else class="text-center pa-8 text-grey-darken-1">
            <v-icon size="64" color="success" class="mb-3 opacity-80">mdi-shield-check</v-icon>
            <h3 class="text-h6 font-weight-medium">Toate asigurările sunt valabile!</h3>
            <p>Nicio poliță nu expiră în următoarele 30 de zile.</p>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- Spațiu pentru viitoarea integrare AI (Ollama Chatbot) -->
      <v-col cols="12" md="6">
        <v-card elevation="3" class="rounded-lg h-100 border-top-primary bg-grey-lighten-5 d-flex flex-column align-center justify-center pa-6">
          <v-icon size="80" color="primary" class="mb-4 opacity-50">mdi-robot-outline</v-icon>
          <h3 class="text-h5 text-primary font-weight-bold text-center">AI Fleet Assistant</h3>
          <p class="text-center text-grey-darken-1 mt-2">
            Aici vom integra asistentul inteligent Ollama. Va putea analiza datele flotei și răspunde la întrebări în timp real.
          </p>
          <v-btn color="primary" variant="outlined" class="mt-4" disabled>
            Integrare în curând
          </v-btn>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import axios from 'axios';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

export default {
  name: 'DashboardWidget',
  data() {
    return {
      avgFuelCost: 0,
      avgDriverExp: 0,
      activeCarsCount: 0,
      expiringInsurances: [],
      stompClient: null // <--- Adaugă asta
    }
  },
  mounted() {
    this.fetchKPIs();
    this.fetchExpiringInsurances();
    this.connectWebSocket(); // <--- Adaugă asta
  },
  methods: {
    fetchKPIs() {
      // Endpoint complex 3
      axios.get('http://localhost:8083/api/fuel/average-cost')
        .then(res => this.avgFuelCost = res.data)
        .catch(err => console.error(err));

      // Endpoint complex 4
      axios.get('http://localhost:8083/api/drivers/average-experience')
        .then(res => this.avgDriverExp = res.data)
        .catch(err => console.error(err));

      // Endpoint complex 5
      axios.get('http://localhost:8083/api/assignments/active')
        .then(res => this.activeCarsCount = res.data.length)
        .catch(err => console.error(err));
    },
    fetchExpiringInsurances() {
      // Endpoint complex 1
      axios.get('http://localhost:8083/api/insurance/expiring-soon')
        .then(res => this.expiringInsurances = res.data)
        .catch(err => console.error(err));
    },
    connectWebSocket() {
      // Ne conectăm la endpoint-ul tău '/ws' din Spring Boot
      const socket = new SockJS('http://localhost:8083/ws');
      this.stompClient = Stomp.over(socket);
      
      // Oprim log-urile masive în consolă (opțional, dar recomandat)
      this.stompClient.debug = () => {};

      this.stompClient.connect({}, (frame) => {
        console.log('WebSocket Conectat: ' + frame);
        
        // Ne abonăm la canalul pe care trimite Controller-ul
        this.stompClient.subscribe('/topic/assignments', (message) => {
          console.log('S-a detectat o schimbare pe backend!', message.body);
          
          // Magia Real-Time: Reapelăm datele automat fără refresh!
          this.fetchKPIs();
        });
      }, (error) => {
        console.error('Eroare conexiune WebSocket:', error);
      });
    }
  }
}
</script>

<style scoped>
/* CSS pentru a adăuga gradiente și efecte vizuale de tip enterprise */
.kpi-card {
  position: relative;
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.kpi-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0,0,0,0.15) !important;
}

.kpi-icon {
  position: absolute;
  right: -10px;
  bottom: -15px;
  opacity: 0.2; /* Iconiță mare și transparentă pe fundal */
  transform: rotate(-15deg);
}

.opacity-90 {
  opacity: 0.9;
}

.gradient-blue {
  background: linear-gradient(135deg, #1976D2 0%, #42A5F5 100%);
}

.gradient-green {
  background: linear-gradient(135deg, #2E7D32 0%, #66BB6A 100%);
}

.gradient-orange {
  background: linear-gradient(135deg, #E65100 0%, #FFA726 100%);
}

.border-top-error {
  border-top: 4px solid #F44336;
}

.border-top-primary {
  border-top: 4px solid #1976D2;
}
</style>